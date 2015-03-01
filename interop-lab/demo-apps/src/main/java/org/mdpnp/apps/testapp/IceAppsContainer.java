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

import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.mdpnp.apps.testapp.IceApplicationProvider.AppType;
import org.mdpnp.devices.BuildInfo;
import org.mdpnp.guis.swing.CompositeDevicePanel;
import org.mdpnp.rtiapi.data.DeviceDataMonitor;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

/**
 * Container responsible for discovery and hosting of ICE applications. Its main purpose is enforcement of
 * standard life-cycle of the participating components.
 *
 * Any application that implements IceApplicationProvider interface and complies with java's
 * <a href="http://docs.oracle.com/javase/7/docs/api/java/util/ServiceLoader.html">ServiceLoader</a> pattern
 * will be discovered by this container. The application could be either swing UI or headless background
 * thread (but not a console-based green-screen). For the UI apps, it could be either standalone frames or
 * embeddable panels.
 *
 * Regardless of the implementation details, all apps will be started the same way via
 * {@link IceApplicationProvider.IceApp#activate} API, and shut down via call to {@link IceApplicationProvider.IceApp#stop}.
 * It is assumed that the apps could be stateful - i.e when created at the activate of the container they
 * could span thread, keep references to resources as long as they dispose of them properly in the
 * implementation of {@link IceApplicationProvider.IceApp#destroy} API.
 *
 * @see IceApplicationProvider
 *
 */
@SuppressWarnings("serial")
public class IceAppsContainer extends IceApplication {

    private static final Logger log = LoggerFactory.getLogger(IceAppsContainer.class);

    private static IceApplicationProvider.AppType Main   = new IceApplicationProvider.AppType("Main Menu", null, (URL) null, 0);
    private static IceApplicationProvider.AppType Device = new IceApplicationProvider.AppType("Device Info", null, (URL) null, 0);

    DeviceApp driverWrapper = new DeviceApp();
    
    final Map<IceApplicationProvider.AppType, IceApplicationProvider.IceApp> activeApps = new HashMap<>();

    private DemoPanel panelController;
    private Parent panelRoot;
    
    private MainMenu mainMenuController;
    private Parent mainMenuRoot;
//    private CardLayout ol;

    private PartitionChooserModel partitionChooserModel;
//    private DiscoveryPeers   discoveryPeers;
    
    public IceAppsContainer() {
        
    }

    private void activateGoBack(final IceApplicationProvider.IceApp app) {

        final String appName = app.getDescriptor().getName();

        Runnable goBackAction = new Runnable() {
            public void run() {
                try {
                    app.stop();
                } catch (Exception ex) {
                    log.error("Failed to stop " + appName, ex);
                }
            }
        };
        panelController.bedLabel.setText(appName);
        panelController.back.setVisible(true);
        panelController.content.setCenter(app.getUI());
        panelController.getBack().setOnAction(new GoBackAction(goBackAction));
    }

    /**
     * Utility class to be installed as an action handler on the 'back' button
     * of the main panel to stop the current app and bring the pre-defined 'main'
     * screen back forward.
     */
    private class GoBackAction implements EventHandler<ActionEvent> {
        final Runnable cmdOnExit;

        public GoBackAction(Runnable r) {
            cmdOnExit = r;
        }

        @Override
        public void handle(ActionEvent event) {
            if (null != cmdOnExit) {
                try {
                    cmdOnExit.run();
                } catch (Throwable t) {
                    log.error("Error in 'go back' logic", t);
                }
            }
            panelController.content.setCenter(mainMenuRoot);
            ((Button)event.getSource()).setVisible(false);
            ((Button)event.getSource()).setOnAction(null);
        }
    }

    /**
     * Utility class to wrap the driver object and preset it as a full-fledged
     * participating application. The main purpose is to enable a formal life-cycle
     * for startup and shutdown of the container.
     */
    private static class DeviceApp implements IceApplicationProvider.IceApp {

        CompositeDevicePanel devicePanel = new CompositeDevicePanel();

        @Override
        public IceApplicationProvider.AppType getDescriptor() {
            return Device;
        }

        @Override
        public Parent getUI() {
            // TODO this
            return null;
        }

        @Override
        public void activate(ApplicationContext context) {
            throw new IllegalStateException("Internal activate(context,driver) API should be called for driver wrapper");
        }

        public void start(ApplicationContext context, final Device device) {

            final EventLoop  eventLoop = (EventLoop)context.getBean("eventLoop");
            final Subscriber subscriber= (Subscriber)context.getBean("subscriber");

            // TODO threading model needs to be revisited but here this
            // will ultimately deadlock on this AWT EventQueue thread
            Thread t = new Thread(threadGroup, new Runnable() {
                public void run() {
                    DeviceDataMonitor deviceMonitor = devicePanel.getModel();
                    if (null != deviceMonitor) {
                        deviceMonitor.stop();
                    }
                    deviceMonitor = new DeviceDataMonitor(device.getUDI());
                    devicePanel.setModel(deviceMonitor);
                    deviceMonitor.start(subscriber, eventLoop);
                }
            }, device.getMakeAndModel());

            t.setDaemon(true);
            t.start();
        }

        @Override
        public void stop() {
            DeviceDataMonitor deviceMonitor = devicePanel.getModel();
            if (null != deviceMonitor) {
                deviceMonitor.stop();
            }
            devicePanel.setModel(null);
        }

        @Override
        public void destroy() {
        }

        static final ThreadGroup threadGroup = new ThreadGroup("DeviceApp");
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("OpenICE");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);

        Scene panelScene = new Scene(panelRoot);
        primaryStage.setOnHiding(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                final ExecutorService refreshScheduler = (ExecutorService) context.getBean("refreshScheduler");
                refreshScheduler.shutdownNow();

                for (IceApplicationProvider.IceApp a : activeApps.values()) {
                    String aName = a.getDescriptor().getName();
                    try {
                        log.info("Shutting down " + aName + "...");
                        a.stop();
                        a.destroy();
                        log.info("Shut down " + aName + " OK");
                    } catch (Exception ex) {
                        // continue as there is nothing mich that can be done,
                        // but print the error out to the log.
                        log.error("Failed to stop/destroy " + aName, ex);
                    }
                }

                stopOk.countDown();
            }
            
        });
        
        
        primaryStage.setScene(panelScene);
        primaryStage.show();
    }
    
    private CountDownLatch stopOk;
    private AbstractApplicationContext context;
    
    @Override
    public void init() throws Exception {
        super.init();
        stopOk = new CountDownLatch(1);
        context = getConfiguration().createContext("IceAppContainerContext.xml");
        context.registerShutdownHook();

        RtConfig rtConfig = (RtConfig) context.getBean("rtConfig");
        final Publisher         publisher   = rtConfig.getPublisher();
        final Subscriber        subscriber  = rtConfig.getSubscriber();
        final DomainParticipant participant = rtConfig.getParticipant();
        final String            udi         = (String) context.getBean("supervisorUdi");

        
        
        final DeviceListModel nc = (DeviceListModel) context.getBean("deviceListModel");

//        setIconImage(ImageIO.read(getClass().getResource("icon.png")));
        partitionChooserModel = new PartitionChooserModel(subscriber, publisher);
        
        FXMLLoader loader = new FXMLLoader(DemoPanel.class.getResource("DemoPanel.fxml"));
        panelRoot = loader.load();
        panelController = ((DemoPanel)loader.getController())
            .setModel(partitionChooserModel)
            .setUdi(udi)
            .setVersion(BuildInfo.getDescriptor());
        panelRoot.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
//
//        discoveryPeers = new DiscoveryPeers(this);
//        discoveryPeers.setSize(320, 240);
//        discoveryPeers.set(participant);

//        getContentPane().add(panel);
//        ol = new CardLayout();
//        panel.getContent().setLayout(ol);


        // Locate all available ice application via the service loader.
        // For documentation refer to http://docs.oracle.com/javase/7/docs/api/java/util/ServiceLoader.html
        //
        ServiceLoader<IceApplicationProvider> l = ServiceLoader.load(IceApplicationProvider.class);

        final Iterator<IceApplicationProvider> iter = l.iterator();
        while (iter.hasNext()) {
            IceApplicationProvider ap = iter.next();
            if (ap.getAppType().isDisabled())
                continue;

            try {
                IceApplicationProvider.IceApp a = ap.create(context);
                activeApps.put(ap.getAppType(), a);
//                Component ui = a.getUI();
//                if (ui instanceof JFrame) {
                    // not a part of tabbed panel; do nothing
//                } else if (ui != null) {
//                    panel.getContent().add(ui, a.getDescriptor().getId());
//                } else {
//                    log.info("No UI component for " + a.getDescriptor().getName());
//                }
            } catch (Exception ex) {
                // continue as there is nothing mich that can be done,
                // but print the error out to the log.
                log.error("Failed to create " + ap.getAppType(), ex);
            }
        }


        // Now that we have a list of all active components, build up a menu and add it to the app
        IceApplicationProvider.AppType[] at = activeApps.keySet().toArray(new IceApplicationProvider.AppType[activeApps.size()]);
        Arrays.sort(at, new Comparator<IceApplicationProvider.AppType>() {
            @Override
            public int compare(IceApplicationProvider.AppType o1, IceApplicationProvider.AppType o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        
        loader = new FXMLLoader(MainMenu.class.getResource("MainMenu.fxml"));
        mainMenuRoot = loader.load();
        final MainMenu mainMenuController = loader.getController();
        mainMenuController.setTypes(at).setDevices(nc.getContents());
        panelController.getContent().setCenter(mainMenuRoot);
        mainMenuController.getAppList().setCellFactory(new AppTypeCellFactory(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                AppTypeGridCell cell = (AppTypeGridCell) event.getSource();
                AppType appType = (AppType) cell.getUserData();
                final IceApplicationProvider.IceApp app = activeApps.get(appType);
                if (app != null) {
                    // TODO top level "apps" (devices)
//                    if (app.getUI() instanceof JFrame) {
//                        JFrame a = (JFrame) app.getUI();
    //                                            a.setLocationRelativeTo(IceAppsContainer.this);
//                        app.activate(context);
//                        a.setVisible(true);
//                    } else {
                        activateGoBack(app);
                        app.activate(context);
//                    }
                }
                
            }
            
        }));
        mainMenuController.getDeviceList().setCellFactory(new DeviceCellFactory());
        
        
//        panel.getContent().add(mainMenuPanel, Main.getId());

        // Add a wrapper for the driver adapter display. This is so that the stop logic could
        // shut it down properly.
        activeApps.put(Device, driverWrapper);
//        panel.getContent().add(driverWrapper.getUI(), Device.getId());


//        panel.getBack().setVisible(false);

//        panel.getCreateAdapter().addActionListener(new ActionListener() {
//
//            @SuppressWarnings({"rawtypes"})
//            @Override
//            public void actionPerformed(ActionEvent e) {
//
//                DefaultComboBoxModel model = new DefaultComboBoxModel(new Configuration.Application[]{Configuration.Application.ICE_Device_Interface});
//
//                ConfigurationDialog dia = new ConfigurationDialog(null, null);
//                dia.setTitle("Create a local ICE Device Adapter");
//                dia.getApplications().setModel(model);
//                dia.set(Configuration.Application.ICE_Device_Interface, null);
//                dia.remove(dia.getDomainId());
//                dia.remove(dia.getDomainIdLabel());
//                dia.remove(dia.getApplications());
//                dia.remove(dia.getApplicationsLabel());
//                dia.getWelcomeText().setRows(4);
//                dia.getWelcomeText().setColumns(40);
//                // dia.remove(dia.getWelcomeScroll());
//                dia.getWelcomeText()
//                        .setText("Typically ICE Device Adapters do not run directly within the ICE Supervisor.  " +
//                                 "This option is provided for convenient testing.  A window will be created for the device adapter. " +
//                                 " To terminate the adapter close that window.  To exit this application you must close the supervisory window.");
//
//                dia.getQuit().setText("Close");
//                dia.pack();
//                dia.setLocationRelativeTo(panel);
//
//                final Configuration c = dia.showDialog();
//                if (null != c) {
//                    Thread t = new Thread(new Runnable() {
//                        public void run() {
//                            try {
//                                DomainParticipantQos pQos = new DomainParticipantQos();
//                                DomainParticipantFactory.get_instance().get_default_participant_qos(pQos);
//                                pQos.discovery.initial_peers.clear();
////                                for (int i = 0; i < discoveryPeers.peers.getSize(); i++) {
////                                    pQos.discovery.initial_peers.add(discoveryPeers.peers.getElementAt(i));
////                                    System.err.println("PEER:" + discoveryPeers.peers.getElementAt(i));
////                                }
//                                DomainParticipantFactory.get_instance().set_default_participant_qos(pQos);
//                                SubscriberQos qos = new SubscriberQos();
//                                subscriber.get_qos(qos);
//                                List<String> partition = new ArrayList<String>();
//                                for (int i = 0; i < qos.partition.name.size(); i++) {
//                                    partition.add((String) qos.partition.name.get(i));
//                                }
//                                DeviceAdapter da = DeviceAdapter.newGUIAdapter(c.getDeviceFactory(), context);
//                                da.setInitialPartition(partition.toArray(new String[0]));
//                                da.start(c.getAddress());
//
//                                log.info("DeviceAdapter ended");
//                            } catch (Exception e) {
//                                log.error("Error in spawned DeviceAdapter", e);
//                            }
//                        }
//                    });
//                    t.setDaemon(true);
//                    t.start();
//                }
//            }
//
//        });


//        mainMenuPanel.getDeviceList().addMouseListener(new MouseAdapter() {
//            @Override
//            public void mouseClicked(MouseEvent e) {
//                int idx = mainMenuPanel.getDeviceList().locationToIndex(e.getPoint());
//                if (idx >= 0 && mainMenuPanel.getDeviceList().getCellBounds(idx, idx).contains(e.getPoint())) {
//                    final Device device = (Device) mainMenuPanel.getDeviceList().getModel().getElementAt(idx);
//
//                    activateGoBack(driverWrapper);
//                    driverWrapper.start(context, device);
//                }
//                super.mouseClicked(e);
//            }
//        });


    }
    
    @Override
    public void stop() throws Exception {
        // this will block until the frame is killed
        stopOk.await();
        context.destroy();
        super.stop();
    }
}
