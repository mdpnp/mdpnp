/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.apps.device;

import java.awt.GridLayout;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public final class PacketPanel extends javax.swing.JPanel implements TableModel, Runnable {
    private Method[] getters;
    private JLabel[] labels;

    private Object lastClass;
    private boolean dynamic;

    private String[] names = new String[0];
    private Object[] values = new Object[0];
    private Class<?>[] types = new Class<?>[0];

    private final Set<TableModelListener> listeners = new java.util.concurrent.CopyOnWriteArraySet<TableModelListener>();

    private final TableModelEvent dataChanged = new TableModelEvent(this);
    private final TableModelEvent structChanged = new TableModelEvent(this, TableModelEvent.HEADER_ROW);

    private void setup(Class<?> clazz) {
        List<Method> getters = new ArrayList<Method>();
        List<JLabel> names = new ArrayList<JLabel>();
        List<JLabel> labels = new ArrayList<JLabel>();

        List<String> names_ = new ArrayList<String>();
        List<Class<?>> classes_ = new ArrayList<Class<?>>();

        for (Method m : clazz.getMethods()) {
            String name = m.getName();
            if (m.getParameterTypes().length == 0) {
                boolean yes = false;
                if (name.startsWith("get")) {
                    yes = true;
                    name = name.substring(3, name.length());
                } else if (name.startsWith("is")) {
                    yes = true;
                    name = name.substring(2, name.length());
                }
                if (yes) {
                    getters.add(m);
                    labels.add(new JLabel());
                    names.add(new JLabel(name));
                    names_.add(name);
                    classes_.add(m.getReturnType());
                }
            }
        }
        removeAll();
        setLayout(new GridLayout(getters.size(), 2));
        this.names = names_.toArray(new String[0]);
        this.values = new Object[this.names.length];
        this.types = classes_.toArray(new Class<?>[0]);
        fire(structChanged);

        for (int i = 0; i < getters.size(); i++) {
            add(names.get(i));
            add(labels.get(i));
        }

        this.getters = getters.toArray(new Method[0]);
        this.labels = labels.toArray(new JLabel[0]);
        revalidate();
    }

    public PacketPanel() {
        this(null);
    }

    public PacketPanel(Class<?> clazz) {

        if (null == clazz) {
            dynamic = true;
        } else {
            setup(clazz);
        }
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    public void run() {
        while (true) {
            Object o;
            synchronized (this) {
                while (this.o == null) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                o = this.o;
                this.o = null;
            }
            _set(o);
        }
    }

    private Object o;

    public synchronized void set(Object o) {
        this.o = o;
        this.notify();
    }

    private void _set(Object o) {
        if (null == o) {
            return;
        }
        if (dynamic && (lastClass == null || !lastClass.equals(o.getClass()))) {
            Class<?> cls = o.getClass();
            setup(cls);
            lastClass = cls;
        }

        for (int i = 0; i < getters.length; i++) {
            Object r;
            try {
                r = getters[i].invoke(o);
                if (r != null) {
                    // if(r.getClass().isArray()) {
                    if (r instanceof Object[]) {
                        labels[i].setText(Arrays.toString((Object[]) r));
                        values[i] = Arrays.toString((Object[]) r);
                    } else {
                        labels[i].setText(r.toString());
                        values[i] = r.toString();
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }

        }
        fire(dataChanged);
    }

    @Override
    public int getRowCount() {
        return names.length;
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return "Field";
        case 1:
            return "Value";
        default:
            return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return String.class;
        case 1:
            return Object.class;
        default:
            return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
        case 0:
            String[] names = this.names;
            if (names.length > rowIndex) {
                return names[rowIndex];
            } else {
                return null;
            }
        case 1:
            Object[] values = this.values;
            if (values.length > rowIndex) {
                return values[rowIndex];
            } else {
                return null;
            }
        default:
            return null;
        }

    }

    protected void fire(TableModelEvent tme) {
        TableModelListener[] listeners = this.listeners.toArray(new TableModelListener[0]);
        for (TableModelListener l : listeners) {
            l.tableChanged(tme);
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {

    }

    @Override
    public void addTableModelListener(TableModelListener l) {
        listeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
        listeners.remove(l);
    }
}
