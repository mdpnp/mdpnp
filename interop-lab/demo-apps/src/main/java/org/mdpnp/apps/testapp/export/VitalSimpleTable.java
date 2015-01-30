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
package org.mdpnp.apps.testapp.export;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.RtConfig;
import org.mdpnp.apps.testapp.pca.VitalModelContainer;
import org.mdpnp.apps.testapp.pca.VitalSign;
import org.mdpnp.apps.testapp.vital.*;
import org.mdpnp.devices.EventLoopHandler;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("serial")
public class VitalSimpleTable extends JComponent implements VitalModelListener, VitalModelContainer, Runnable {

    private static final Logger log = LoggerFactory.getLogger(VitalSimpleTable.class);

    DefaultTableModel tblModel = new DefaultTableModel(
            new Object[][] {  },
            new Object[] { "MetricId", "Time", "InstanceId", "Value" });


    static ThreadLocal<SimpleDateFormat> dateFormats = new ThreadLocal<SimpleDateFormat>()
    {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmssZ");
        }
    };

    private static final long REFRESH_RATE_MS = 100L;

    private final ScheduledExecutorService executor;
    private ScheduledFuture<?> future;
    private Persister          persister;
    private JPanel             persisterContainer = new JPanel();

    private Map<String, Persister> supportedPersisters = new HashMap<>();

    public VitalSimpleTable() {
        this(null);
    }

    public VitalSimpleTable(ScheduledExecutorService executor) {
        this.executor = executor;

        JTable table = new JTable(tblModel);
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        supportedPersisters.put("csv",  new CSVPersister());
        supportedPersisters.put("jdbc", new JdbcPersister());

        final CardLayout cl = new CardLayout();
        final JPanel cards = new JPanel(cl);
        cards.setBorder(BorderFactory.createLineBorder(Color.gray, 2, true));

        JPanel btns = new JPanel();
        btns.setLayout(new GridLayout(0,1));
        btns.setBorder(BorderFactory.createLineBorder(Color.gray, 2, true));

        ButtonGroup group = new ButtonGroup();

        for (Map.Entry<String, Persister> entry : supportedPersisters.entrySet()) {
            Persister p = entry.getValue();
            cards.add(p, entry.getKey());
            JRadioButton btn  = new JRadioButton(entry.getKey());
            btns.add(btn);
            group.add(btn);
            btn.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    String name = ((JRadioButton)e.getItem()).getActionCommand();
                    try {
                        // lock on the main object so that the change of the
                        // persister is handled in a synchronized way.
                        //
                        synchronized(VitalSimpleTable.this) {
                            Persister p = supportedPersisters.get(name);
                            if (e.getStateChange() == ItemEvent.DESELECTED) {
                                p.stop();
                                persister = null;
                            }
                            else if (e.getStateChange() == ItemEvent.SELECTED) {
                                boolean v = p.start();
                                if (v) {
                                    persister = p;
                                    p.setBackground(Color.lightGray);
                                } else {
                                    persister = null;
                                    p.setBackground(Color.red);
                                }
                            }
                        }
                        cl.show(cards, name);
                    } catch(Exception ex) {
                        JOptionPane.showMessageDialog(null,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                    }
                }
            });
            p.putClientProperty("mdpnp.appender", btn);
        }

        persisterContainer.setLayout(new BorderLayout());
        persisterContainer.add(cards, BorderLayout.CENTER);
        persisterContainer.add(btns,  BorderLayout.WEST);

        JRadioButton btn = (JRadioButton)supportedPersisters.get("csv").getClientProperty("mdpnp.appender");
        group.setSelected(btn.getModel(), true);

        add(persisterContainer, BorderLayout.SOUTH);
    }

    static abstract class Persister extends JPanel
    {
        public abstract void persist(Value vital) throws Exception;
        public abstract void stop()  throws Exception;
        public abstract boolean start()  throws Exception;
    }

    public void run() {
        if (isVisible()) {
            repaint();
        }
    }

    private VitalModel model;

    public void setModel(VitalModel model) {
        if (null != this.model) {
            if (future != null) {
                future.cancel(true);
                future = null;
            }
            this.model.removeListener(this);
        }
        this.model = model;
        if (null != this.model) {
            this.model.addListener(this);
            if (executor != null) {
                future = executor.scheduleAtFixedRate(this, 0L, REFRESH_RATE_MS, TimeUnit.MILLISECONDS);
            }
        }
    }

    public VitalModel getModel() {
        return model;
    }


    private synchronized void handleVitalChanged(VitalModel model, Vital vital) {

        for (Value value : vital.getValues()) {

            // save it for real
            try {
                persister.persist(value) ;
            } catch (Exception ex) {
                log.error("Failed to save data", ex);
            }

            // and add to the screen for visual.
            String devTime = dateFormats.get().format(new Date(value.getNumeric().device_time.sec * 1000));
            Object[] row = new Object[]{
                    value.getMetricId(),
                    devTime,
                    value.getInstanceId(),
                    value.getNumeric().value
            };
            tblModel.insertRow(0, row);
            tblModel.setRowCount(25);
        }
    }

    @Override
    public void vitalChanged(VitalModel model, Vital vital) {

        if(vital != null && persister != null) {
            handleVitalChanged(model, vital);
        }

        if (null == executor) {
            repaint();
        }
    }

    @Override
    public void vitalRemoved(VitalModel model, Vital vital) {
        if (null == executor) {
            repaint();
        }
    }

    @Override
    public void vitalAdded(VitalModel model, Vital vital) {
        if (null == executor) {
            repaint();
        }
    }

    public static void main(String[] args) {

        RtConfig.loadAndSetIceQos();

        final RtConfig rtSetup = RtConfig.setupDDS(0);
        final EventLoop eventLoop=rtSetup.getEventLoop();
        final Publisher pub=rtSetup.getPublisher();
        final Subscriber s=rtSetup.getSubscriber();
        final DeviceListModel nc = rtSetup.getDeviceListModel();

        final VitalModel vm = new VitalModelImpl(nc);

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        final EventLoopHandler eventLoopHandler = new EventLoopHandler(eventLoop);

        vm.start(s, pub, eventLoop);

        eventLoop.doLater(new Runnable() {
            public void run() {
                VitalSign.SpO2.addToModel(vm);
                VitalSign.RespiratoryRate.addToModel(vm);
                VitalSign.EndTidalCO2.addToModel(vm);
            }
        });

        JFrame frame = new JFrame("UITest");
        frame.getContentPane().setBackground(Color.white);
        final VitalSimpleTable uiTest = new VitalSimpleTable(null);
        uiTest.setModel(vm);
        executor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                uiTest.repaint();
            }
        }, 0L, 500L, TimeUnit.MILLISECONDS);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                vm.stop();
                try {
                    eventLoopHandler.shutdown();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                super.windowClosing(e);
            }
        });
        frame.getContentPane().setLayout(new BorderLayout());
        frame.getContentPane().add(uiTest, BorderLayout.CENTER);
        // frame.getContentPane().add(slider, BorderLayout.SOUTH);
        frame.setSize(640, 480);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
