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

import ice.InfusionObjectiveDataWriter;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;

import org.mdpnp.apps.testapp.pca.PCAPanel;
import org.mdpnp.apps.testapp.pca.VitalSign;
import org.mdpnp.apps.testapp.rrr.RapidRespiratoryRate;
import org.mdpnp.apps.testapp.sim.SimControl;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.apps.testapp.vital.VitalModelImpl;
import org.mdpnp.apps.testapp.xray.XRayVentPanel;
import org.mdpnp.devices.BuildInfo;
import org.mdpnp.devices.EventLoopHandler;
import org.mdpnp.devices.TimeManager;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.guis.swing.CompositeDevicePanel;
import org.mdpnp.rtiapi.data.DeviceDataMonitor;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.InfusionStatusInstanceModel;
import org.mdpnp.rtiapi.data.InfusionStatusInstanceModelImpl;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.SampleArrayInstanceModel;
import org.mdpnp.rtiapi.data.SampleArrayInstanceModelImpl;
import org.mdpnp.rtiapi.data.TopicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantFactoryQos;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.topic.Topic;
import com.rti.dds.topic.TopicDescription;

//
/**
 * @author Jeff Plourde
 *
 */
public class DemoApp {

    private static String goback = null;
    private static Runnable goBackAction = null;
    private static DemoPanel panel;
    private static String gobackPatient, gobackBed;
    private static Font gobackPatientFont;
    private static Color gobackPatientColor;
    private static int verticalAlignment, verticalTextAlignment;
    private static CardLayout ol;

    private static void setGoBack(String goback, Runnable goBackAction) {
        DemoApp.goback = goback;
        DemoApp.goBackAction = goBackAction;
        DemoApp.gobackPatient = panel.getPatientLabel().getText();
        DemoApp.gobackBed = panel.getBedLabel().getText();
        DemoApp.gobackPatientFont = panel.getPatientLabel().getFont();
        DemoApp.gobackPatientColor = panel.getPatientLabel().getForeground();
        DemoApp.verticalAlignment = panel.getPatientLabel().getVerticalAlignment();
        DemoApp.verticalTextAlignment = panel.getPatientLabel().getVerticalTextPosition();
        panel.getBack().setVisible(null != goback);
    }

    private static void goback() {
        if (null != goBackAction) {
            goBackAction.run();
            goBackAction = null;
        }
        panel.getPatientLabel().setFont(gobackPatientFont);
        panel.getPatientLabel().setForeground(gobackPatientColor);
        panel.getPatientLabel().setText(DemoApp.gobackPatient);
        panel.getBedLabel().setText(DemoApp.gobackBed);
        ol.show(panel.getContent(), DemoApp.goback);
        panel.getPatientLabel().setVerticalAlignment(DemoApp.verticalAlignment);
        panel.getPatientLabel().setVerticalTextPosition(DemoApp.verticalTextAlignment);
        panel.getBack().setVisible(false);
    }

    private static final Logger log = LoggerFactory.getLogger(DemoApp.class);

    private abstract static class RunAndDone implements Runnable {
        public boolean done;
        
        public synchronized void waitForIt() {
            while(!done) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        protected synchronized void done() {
            this.done = true;
            this.notifyAll();
        }
    }
    
    @SuppressWarnings("unchecked")
    public static final void start(final int domainId) throws Exception {
        UIManager.setLookAndFeel(new MDPnPLookAndFeel());

        final EventLoop eventLoop = new EventLoop();
        final EventLoopHandler handler = new EventLoopHandler(eventLoop);

        // UIManager.put("List.focusSelectedCellHighlightBorder", null);
        // UIManager.put("List.focusCellHighlightBorder", null);

        // This could prove confusing
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
        final String udi = AbstractSimulatedDevice.randomUDI();

        final DomainParticipantFactoryQos qos = new DomainParticipantFactoryQos();
        DomainParticipantFactory.get_instance().get_qos(qos);
        qos.entity_factory.autoenable_created_entities = false;
        DomainParticipantFactory.get_instance().set_qos(qos);
        final DomainParticipant participant = DomainParticipantFactory.get_instance().create_participant(domainId, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null,
                StatusKind.STATUS_MASK_NONE);
        final Subscriber subscriber = participant.get_implicit_subscriber();
        final Publisher publisher = participant.get_implicit_publisher();
        final TimeManager timeManager = new TimeManager(publisher, subscriber, udi, "Supervisor");

        @SuppressWarnings("serial")
        final DeviceListModel nc = new DeviceListModel(subscriber, eventLoop, timeManager) {
            @Override
            protected void notADevice(ice.HeartBeat heartbeat, boolean alive) {
                if ("Supervisor".equals(heartbeat.type) && alive) {
                    // TODO Temporarily removing this; is there a requirement that only one Supervisor runs at a time?
//                    JOptionPane.showMessageDialog(panel, "Another supervisor has been detected on the domain", "Multiple Supervisors",
//                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        RunAndDone enable = new RunAndDone() {
            public void run() {
                nc.start();
                participant.enable();
                timeManager.start();
                qos.entity_factory.autoenable_created_entities = true;
                DomainParticipantFactory.get_instance().set_qos(qos);
                done();
            }
        };
        
        eventLoop.doLater(enable);
        
        enable.waitForIt();
        
        final DeviceListCellRenderer deviceCellRenderer = new DeviceListCellRenderer(nc);


        final ScheduledExecutorService refreshScheduler = Executors.newSingleThreadScheduledExecutor();

        final DemoFrame frame = new DemoFrame("ICE Supervisor");
        frame.setIconImage(ImageIO.read(DemoApp.class.getResource("icon.png")));
        panel = new DemoPanel();
        panel.getPartitionChooser().add(subscriber);
        switch (domainId) {
        case 0:
            panel.getBedLabel().setText("ICE Test Domain " + domainId);
            break;
        case 3:
            panel.getBedLabel().setText("Operating Room " + domainId);
            break;
        default:
            panel.getBedLabel().setText("Intensive Care " + domainId);
            break;
        }
        panel.getVersion().setText(BuildInfo.getDescriptor());

        frame.getContentPane().add(panel);
        ol = new CardLayout();
        panel.getContent().setLayout(ol);
        panel.getUdi().setText(udi);

        final MainMenuPanel mainMenuPanel = new MainMenuPanel(AppType.getListedTypes());
        mainMenuPanel.setOpaque(false);
        panel.getContent().add(mainMenuPanel, AppType.Main.getId());
        ol.show(panel.getContent(), AppType.Main.getId());

        final CompositeDevicePanel devicePanel = new CompositeDevicePanel();
        panel.getContent().add(devicePanel, AppType.Device.getId());

        final VitalModel vitalModel = new VitalModelImpl(nc);
        final InfusionStatusInstanceModel pumpModel = 
                new InfusionStatusInstanceModelImpl(ice.InfusionStatusTopic.VALUE);
        ice.InfusionObjectiveDataWriter objectiveWriter = null;
        final SampleArrayInstanceModel capnoModel = 
                new SampleArrayInstanceModelImpl(ice.SampleArrayTopic.VALUE);

        // VitalSign.EndTidalCO2.addToModel(vitalModel);
        if(!AppType.PCA.isDisabled() || !AppType.PCAViz.isDisabled()) {
            vitalModel.start(subscriber, eventLoop);
            TopicDescription infusionObjectiveTopic = TopicUtil.lookupOrCreateTopic(participant, ice.InfusionObjectiveTopic.VALUE,  ice.InfusionObjectiveTypeSupport.class);
            objectiveWriter = (InfusionObjectiveDataWriter) publisher.create_datawriter_with_profile((Topic) infusionObjectiveTopic, QosProfiles.ice_library,
                    QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);
            pumpModel.start(subscriber, eventLoop, QosProfiles.ice_library, QosProfiles.state);
            // VitalSign.HeartRate.addToModel(vitalModel);
            VitalSign.SpO2.addToModel(vitalModel);
            VitalSign.RespiratoryRate.addToModel(vitalModel);
            VitalSign.EndTidalCO2.addToModel(vitalModel);
        }

        if(!AppType.RRR.isDisabled()) {
            StringSeq params = new StringSeq();
            params.add("'"+ice.MDC_CAPNOGRAPH.VALUE+"'");
            capnoModel.start(subscriber, eventLoop, "metric_id = %0", params, QosProfiles.ice_library, QosProfiles.waveform_data);
        }

        PCAPanel _pcaPanel = null;
        if (!AppType.PCA.isDisabled()) {
            UIManager.put("TabbedPane.contentOpaque", false);
            _pcaPanel = new PCAPanel(refreshScheduler, objectiveWriter, deviceCellRenderer);
            _pcaPanel.setOpaque(false);
            panel.getContent().add(_pcaPanel, AppType.PCA.getId());
        }
        final PCAPanel pcaPanel = _pcaPanel;

        XRayVentPanel _xrayVentPanel = null;
        if (!AppType.XRay.isDisabled()) {
            _xrayVentPanel = new XRayVentPanel(panel, subscriber, eventLoop, deviceCellRenderer);
            panel.getContent().add(_xrayVentPanel, AppType.XRay.getId());
        }
        final XRayVentPanel xrayVentPanel = _xrayVentPanel;

        DataVisualization _pcaviz = null;
        if (!AppType.PCAViz.isDisabled()) {
            _pcaviz = new DataVisualization(refreshScheduler, objectiveWriter, deviceCellRenderer);
            panel.getContent().add(_pcaviz, AppType.PCAViz.getId());
        }
        final DataVisualization pcaviz = _pcaviz;

        RapidRespiratoryRate _rrr = null;
        if (!AppType.RRR.isDisabled()) {
            _rrr = new RapidRespiratoryRate(domainId, eventLoop, deviceCellRenderer);
            panel.getContent().add(_rrr, AppType.RRR.getId());
        }
        final RapidRespiratoryRate rrr = _rrr;

        JFrame _sim = null;
        if (!AppType.SimControl.isDisabled()) {
            SimControl simControl = new SimControl(participant);
            _sim = new JFrame("Sim Control");
            _sim.getContentPane().add(new JScrollPane(simControl));
            _sim.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            _sim.setAlwaysOnTop(true);
            _sim.pack();
            Dimension d = new Dimension();
            _sim.getSize(d);
            d.width = 2 * d.width;
            _sim.setSize(d);

            // panel.getContent().add(_sim, AppType.SimControl.getId());
        }
        final JFrame sim = _sim;

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                refreshScheduler.shutdownNow();
                if (goBackAction != null) {
                    goBackAction.run();
                    goBackAction = null;
                }
                if (null != sim) {
                    // TODO things
                }
                if (pcaPanel != null) {
                    pcaPanel.setModel(null, null);
                }
                if (null != pcaviz) {
                    pcaviz.setModel(null, null);
                }
                if (null != rrr) {
                    rrr.setModel(null);
                }
                vitalModel.stop();
                pumpModel.stop();
                capnoModel.stop();
                nc.tearDown();
                try {
                    handler.shutdown();
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                super.windowClosing(e);
            }
        });
        panel.getBack().setVisible(false);

        panel.getBack().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                goback();
            }

        });

        mainMenuPanel.getSpawnDeviceAdapter().addActionListener(new ActionListener() {

            @SuppressWarnings({ "rawtypes" })
            @Override
            public void actionPerformed(ActionEvent e) {
                ConfigurationDialog dia = new ConfigurationDialog();
                dia.setTitle("Create a local ICE Device Adapter");
                dia.getApplications().setModel(
                        new DefaultComboBoxModel(new Configuration.Application[] { Configuration.Application.ICE_Device_Interface }));
                dia.set(Configuration.Application.ICE_Device_Interface, Configuration.DeviceType.PO_Simulator);
                dia.remove(dia.getDomainId());
                dia.remove(dia.getDomainIdLabel());
                dia.remove(dia.getApplications());
                dia.remove(dia.getApplicationsLabel());
                dia.getWelcomeText().setRows(4);
                dia.getWelcomeText().setColumns(40);
                // dia.remove(dia.getWelcomeScroll());
                dia.getWelcomeText()
                        .setText(
                                "Typically ICE Device Adapters do not run directly within the ICE Supervisor.  This option is provided for convenient testing.  A window will be created for the device adapter.  To terminate the adapter close that window.  To exit this application you must close the supervisory window.");
                dia.getQuit().setText("Close");
                dia.pack();
                dia.setLocationRelativeTo(panel);
                final Configuration c = dia.showDialog();
                if (null != c) {
                    Thread t = new Thread(new Runnable() {
                        public void run() {
                            try {
                                DeviceAdapter da = new DeviceAdapter();
                                da.start(c.getDeviceType(), domainId, c.getAddress(), true, false, eventLoop);
                                log.info("DeviceAdapter ended");
                            } catch (Exception e) {
                                log.error("Error in spawned DeviceAdapter", e);
                            }
                        }
                    });
                    t.setDaemon(true);
                    t.start();
                }
            }
            
        });

        mainMenuPanel.getDeviceList().setModel(nc);

        mainMenuPanel.getAppList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int idx = mainMenuPanel.getAppList().locationToIndex(e.getPoint());
                if (idx >= 0 && mainMenuPanel.getAppList().getCellBounds(idx, idx).contains(e.getPoint())) {
                    Object o = mainMenuPanel.getAppList().getModel().getElementAt(idx);
                    AppType appType = (AppType) o;

                    switch (appType) {
                    case RRR:
                        if (null != rrr) {
                            setGoBack(AppType.Main.getId(), new Runnable() {
                                public void run() {
                                    // rrr.setModel(null, null);
                                }
                            });
                            panel.getBedLabel().setText(appType.getName());
                            panel.getPatientLabel().setText("");
                            rrr.setModel(capnoModel);
                            ol.show(panel.getContent(), appType.getId());
                        }
                        break;
                    case PCAViz:
                        if (null != pcaviz) {
                            setGoBack(AppType.Main.getId(), new Runnable() {
                                public void run() {
                                    pcaviz.setModel(null, null);
                                }
                            });
                            panel.getBedLabel().setText(appType.getName());
                            panel.getPatientLabel().setText("");
                            pcaviz.setModel(vitalModel, pumpModel);
                            ol.show(panel.getContent(), AppType.PCAViz.getId());
                        }
                        break;
                    case PCA:
                        if (null != pcaPanel) {
                            setGoBack(AppType.Main.getId(), new Runnable() {
                                public void run() {
                                    pcaPanel.setModel(null, null);
                                }
                            });
                            panel.getBedLabel().setText(appType.getName());
                            panel.getPatientLabel().setText("");
                            pcaPanel.setModel(vitalModel, pumpModel);
                            ol.show(panel.getContent(), AppType.PCA.getId());
                        }
                        break;
                    case XRay:
                        if (null != xrayVentPanel) {
                            setGoBack(AppType.Main.getId(), new Runnable() {
                                public void run() {
                                    xrayVentPanel.stop();
                                }
                            });
                            ol.show(panel.getContent(), AppType.XRay.getId());
                            xrayVentPanel.start(subscriber, eventLoop);
                        }
                        break;
                    case SimControl:
                        if (null != sim) {
                            sim.setLocationRelativeTo(frame);
                            sim.setVisible(true);
                            // setGoBack(AppType.Main.getId(), new Runnable() {
                            // public void run() {
                            // sim.stop();
                            // }
                            // });
                            // ol.show(panel.getContent(),
                            // AppType.SimControl.getId());
                            // sim.start();
                        }
                    case Device:
                        break;
                    case Main:
                        break;
                    default:
                        break;

                    }
                }
                super.mouseClicked(e);
            }
        });
        mainMenuPanel.getDeviceList().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int idx = mainMenuPanel.getDeviceList().locationToIndex(e.getPoint());
                if (idx >= 0 && mainMenuPanel.getDeviceList().getCellBounds(idx, idx).contains(e.getPoint())) {
                    final Device device = (Device) mainMenuPanel.getDeviceList().getModel().getElementAt(idx);
                    // TODO threading model needs to be revisited but here this
                    // will ultimately deadlock on this AWT EventQueue thread
                    Thread t = new Thread(new Runnable() {
                        public void run() {
                            DeviceDataMonitor deviceMonitor = devicePanel.getModel();
                            if (null != deviceMonitor) {
                                deviceMonitor.stop();
                                deviceMonitor = null;
                            }
                            deviceMonitor = new DeviceDataMonitor(device.getUDI());
                            devicePanel.setModel(deviceMonitor);
                            deviceMonitor.start(subscriber, eventLoop);
                        }
                    });
                    t.setDaemon(true);
                    t.start();

                    setGoBack(AppType.Main.getId(), new Runnable() {
                        public void run() {
                            DeviceDataMonitor deviceMonitor = devicePanel.getModel();
                            if (null != deviceMonitor) {
                                deviceMonitor.stop();
                            }
                            devicePanel.setModel(null);
                        }
                    });
                    ol.show(panel.getContent(), AppType.Device.getId());
                }
                super.mouseClicked(e);
            }
        });

        // mainMenuPanel.getDeviceList().setModel(nc.getAcceptedDevices());

        // loginPanel.getClinicianId().addActionListener(new ActionListener() {
        //
        // @Override
        // public void actionPerformed(ActionEvent e) {
        // setGoBack("login", null);
        // ol.show(panel.getContent(), "main");
        // }
        //
        // });

        // DemoPanel.setChildrenOpaque(panel, false);

        // panel.setOpaque(true);
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
