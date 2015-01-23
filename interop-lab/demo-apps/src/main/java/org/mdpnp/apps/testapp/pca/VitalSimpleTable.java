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
package org.mdpnp.apps.testapp.pca;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;
import javafx.scene.layout.Priority;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.apps.testapp.RtConfig;
import org.mdpnp.apps.testapp.vital.*;
import org.mdpnp.devices.EventLoopHandler;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class VitalSimpleTable extends JComponent implements VitalModelListener, VitalModelContainer, Runnable {

    private static final Logger log = LoggerFactory.getLogger(VitalSimpleTable.class);

    DefaultTableModel tblModel = new DefaultTableModel(
            new Object[][] {  },
            new Object[] { "MetricId", "Time", "InstanceId", "Value" });


    ThreadLocal<SimpleDateFormat> dateFormats = new ThreadLocal<SimpleDateFormat>()
    {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmssZ");
        }
    };

    private static final long REFRESH_RATE_MS = 100L;

    private final ScheduledExecutorService executor;
    private ScheduledFuture<?> future;

    public VitalSimpleTable() {
        this(null);
    }

    public VitalSimpleTable(ScheduledExecutorService executor) {
        this.executor = executor;

        JTable table = new JTable(tblModel);
        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel fControl = new JPanel();
        fControl.setLayout(new FlowLayout());

        JComboBox backupIndex = new JComboBox(new String[] { "1", "5", "10", "20"});
        backupIndex.addActionListener(new ActionListener()
                                    {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {

                                            Object o = ((JComboBox)e.getSource()).getSelectedItem();
                                            appender.setMaxBackupIndex(Integer.parseInt(o.toString()));
                                            appender.activateOptions();
                                        }
                                    }
        );
        backupIndex.setSelectedIndex(2);

        JComboBox fSize = new JComboBox(new String[] { "1MB", "5MB", "10MB", "50M"});
        fSize.addActionListener(new ActionListener()
                                      {
                                          @Override
                                          public void actionPerformed(ActionEvent e) {

                                              Object o = ((JComboBox)e.getSource()).getSelectedItem();
                                              appender.setMaxFileSize(o.toString());
                                              appender.activateOptions();
                                          }
                                      }
        );
        fSize.setSelectedIndex(2);

        fControl.add(new JLabel("Number of files to keep around:"));
        fControl.add(backupIndex);
        fControl.add(new JLabel("Max file size:"));
        fControl.add(fSize);

        add(fControl, BorderLayout.SOUTH);

        appender.setFile("demo-app.csv");
        appender.setMaxBackupIndex(Integer.parseInt(backupIndex.getSelectedItem().toString()));
        appender.setMaxFileSize(fSize.getSelectedItem().toString());
        appender.setAppend(true);
        appender.setLayout(new org.apache.log4j.PatternLayout("%m%n"));
        appender.setThreshold(Level.ALL);
        appender.activateOptions();
        cat.setAdditivity(false);
        cat.setLevel(Level.ALL);
        cat.addAppender(appender);

    }


    org.apache.log4j.RollingFileAppender appender = new org.apache.log4j.RollingFileAppender();
    org.apache.log4j.Category cat = org.apache.log4j.Logger.getLogger("VitalSimpleTable.CVS");

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


    public static final void main(String[] args) {

        RtConfig.loadAndSetIceQos();

        RtConfig rtSetup = RtConfig.setupDDS(0);
        final EventLoop eventLoop=rtSetup.getEventLoop();
        final Publisher pub=rtSetup.getPublisher();
        final Subscriber s=rtSetup.getSubscriber();
        final DomainParticipant participant=rtSetup.getParticipant();
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
                participant.delete_subscriber(s);
                participant.delete_contained_entities();
                DomainParticipantFactory.get_instance().delete_participant(participant);
                DomainParticipantFactory.finalize_instance();
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

    @Override
    public void vitalChanged(VitalModel model, Vital vital) {

        StringBuilder sb = new StringBuilder();
        for (Value value : vital.getValues()) {
            if(sb.length()!=0) sb.append('\n');

            String devTime = dateFormats.get().format(new Date(value.getNumeric().device_time.sec * 1000));

            sb.append(value.getMetricId()).append(",").append(devTime).append(",").append(value.getInstanceId()).append(",").append(value.getNumeric().value);

            // LoggingEvent le = new LoggingEvent("", null, Level.ALL, sb.toString(), null);
            cat.info(sb.toString());
            Object[] row=new Object[] {
                    value.getMetricId(),
                    devTime,
                    value.getInstanceId(),
                    value.getNumeric().value
            };
            tblModel.insertRow(0, row);
            tblModel.setRowCount(25);
        }
        log.info(sb.toString());

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



}
