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
package org.mdpnp.apps.testapp;

import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class DeviceIdentityTableModel extends AbstractTableModel implements ListDataListener {
    @SuppressWarnings("rawtypes")
    private final ListModel model;

    public DeviceIdentityTableModel(@SuppressWarnings("rawtypes") ListModel model) {
        this.model = model;
        model.addListDataListener(this);
    }

    @Override
    public int getRowCount() {
        return model.getSize();
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        ice.DeviceIdentity d = (ice.DeviceIdentity) model.getElementAt(rowIndex);
        switch (columnIndex) {
        case 0:
            return d.unique_device_identifier;
        case 1:
            return d.manufacturer;
        case 2:
            return d.model;
        default:
            return null;
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
        case 0:
            return "UDI";
        case 1:
            return "Manufacturer";
        case 2:
            return "Model";
        default:
            return null;
        }
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        fireTableRowsDeleted(e.getIndex0(), e.getIndex1());
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
    }
}
