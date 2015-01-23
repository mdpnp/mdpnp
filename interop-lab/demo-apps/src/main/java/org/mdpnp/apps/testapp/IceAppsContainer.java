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

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;
import org.mdpnp.devices.BuildInfo;
import org.mdpnp.guis.swing.CompositeDevicePanel;
import org.mdpnp.rtiapi.data.DeviceDataMonitor;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

public class IceAppsContainer implements Configuration.Command {

    private static final Logger log = LoggerFactory.getLogger(IceAppsContainer.class);

    public interface IceApp {

        String getId();
        String getName();
        Icon getIcon();
        Component getUI();

        void start(ApplicationContext context);
        void stop();
    }

    public static class AppType {

        private final String id;
        private final String name;
        private final Icon   icon;
        private final String disableProperty;

        public AppType(final String id, final String name, final String disableProperty, final URL icon, double scale) {
            this(id, name, disableProperty, read(icon), scale);
        }

        public AppType(final String id, final String name, final String disableProperty, final BufferedImage icon, double scale) {
            this.id = id;
            this.name = name;
            this.icon = null == icon ? null : new ImageIcon(scale(icon, scale));
            this.disableProperty = disableProperty;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public Icon getIcon() {
            return icon;
        }

        @Override
        public String toString() {
            return name;
        }
        public boolean isDisabled() {
            return null != disableProperty && Boolean.getBoolean(disableProperty);
        }


        private static BufferedImage read(URL url) {
            try {
                return url==null?null:ImageIO.read(url);
            } catch (IOException e) {
                log.error("Failed to load image url:" + url.toExternalForm(), e);
                return null;
            }
        }

        private static BufferedImage scale(BufferedImage before, double scale) {
            if (null == before) {
                return null;
            }
            if (0 == Double.compare(scale, 0.0)) {
                return before;
            }
            int width = before.getWidth();
            int height = before.getHeight();

            BufferedImage after = new BufferedImage((int) (scale * width), (int) (scale * height), BufferedImage.TYPE_INT_ARGB);
            java.awt.geom.AffineTransform at = new java.awt.geom.AffineTransform();
            at.scale(scale, scale);

            AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            after = scaleOp.filter(before, after);
            return after;
        }
    }

    @Override
    public int execute(Configuration config) throws Exception {

        final Semaphore stopOk = new Semaphore(0);
        final AbstractApplicationContext context = config.createContext("IceAppContainerContext.xml");
        context.registerShutdownHook();

        JFrame f = new IceAppsContainerUI(context, stopOk);
        f.setVisible(true);

        // this will block until the frame is killed
        stopOk.acquire();

        context.destroy();
        return 0;
    }

    static class IceAppsContainerUI extends DemoFrame {

        private static AppType Main = new AppType("main", "Main Menu", null, (URL) null, 0);
        private static AppType Device = new AppType("device", "Device Info", null, (URL) null, 0);

        DeviceApp driverWrapper = new DeviceApp();

        private DemoPanel  panel;
        private CardLayout ol;

        private PartitionChooser partitionChooser;
        private DiscoveryPeers   discoveryPeers;

        public IceAppsContainerUI(final AbstractApplicationContext context, final Semaphore stopOk) throws Exception {
            super("ICE Supervisor");


            RtConfig rtConfig = (RtConfig) context.getBean("rtConfig");
            final Publisher publisher = rtConfig.publisher;
            final Subscriber subscriber = rtConfig.subscriber;
            final DomainParticipant participant = rtConfig.participant;
            final DeviceListModel nc = rtConfig.deviceListModel;
            final String udi = rtConfig.udi;

            //final DemoFrame frame = new DemoFrame("ICE Supervisor");
            setIconImage(ImageIO.read(getClass().getResource("icon.png")));
            panel = new DemoPanel();
            partitionChooser = new PartitionChooser(this);
            partitionChooser.setSize(320, 240);
            partitionChooser.set(subscriber);
            partitionChooser.set(publisher);

            discoveryPeers = new DiscoveryPeers(this);
            discoveryPeers.setSize(320, 240);
            discoveryPeers.set(participant);

            panel.getBedLabel().setText("OpenICE");
            panel.getVersion().setText(BuildInfo.getDescriptor());

            getContentPane().add(panel);
            ol = new CardLayout();
            panel.getContent().setLayout(ol);
            panel.getUdi().setText(udi);

            panel.getChangePartition().addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    partitionChooser.refresh();
                    partitionChooser.setLocationRelativeTo(panel);
                    partitionChooser.setVisible(true);
                }

            });

            // Locate all available ice application via the service loader.
            // For documentation refer to http://docs.oracle.com/javase/7/docs/api/java/util/ServiceLoader.html
            //
            ServiceLoader<IceApplicationProvider> l = ServiceLoader.load(IceApplicationProvider.class);

            final Map<AppType, IceAppsContainer.IceApp> activeApps = new HashMap<>();

            final Iterator<IceApplicationProvider> iter = l.iterator();
            while (iter.hasNext()) {
                IceApplicationProvider ap = iter.next();
                if (ap.getAppType().isDisabled())
                    continue;

                try {
                    IceAppsContainer.IceApp a = ap.create(context);
                    activeApps.put(ap.getAppType(), a);
                    Component ui = a.getUI();
                    if (ui instanceof JFrame) {
                        // not a part of tabbed panel; do nothing
                    } else if (ui != null) {
                        panel.getContent().add(ui, a.getId());
                    } else {
                        log.info("No UI component for " + a.getName());
                    }
                } catch (Exception ex) {
                    // continue as there is nothing mich that can be done,
                    // but print the error out to the log.
                    log.error("Failed to create " + ap.getAppType(), ex);
                }
            }


            // Now that we have a list of all active components, build up a menu and add it to the app
            AppType[] at = activeApps.keySet().toArray(new AppType[activeApps.size()]);
            final MainMenuPanel mainMenuPanel = new MainMenuPanel(at);
            mainMenuPanel.setOpaque(false);
            panel.getContent().add(mainMenuPanel, Main.getId());

            // Add a wrapper for the driver adapter display. This is so that the stop logic could
            // shut it down properly.
            activeApps.put(Device, driverWrapper);
            panel.getContent().add(driverWrapper.getUI(), Device.getId());

            // Start with showing the main panel.
            //
            ol.show(panel.getContent(), Main.getId());


            addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {

                    final ExecutorService refreshScheduler = (ExecutorService) context.getBean("refreshScheduler");
                    refreshScheduler.shutdownNow();

                    for (IceAppsContainer.IceApp a : activeApps.values()) {
                        try {
                            log.info("Shutting down " + a.getName() + "...");
                            a.stop();
                            log.info("Shut down " + a.getName() + " OK");
                        } catch (Exception ex) {
                            // continue as there is nothing mich that can be done,
                            // but print the error out to the log.
                            log.error("Failed to stop " + a.getName(), ex);
                        }
                    }

                    stopOk.release();

                    super.windowClosing(e);
                }
            });
            panel.getBack().setVisible(false);

            panel.getCreateAdapter().addActionListener(new ActionListener() {

                @SuppressWarnings({"rawtypes"})
                @Override
                public void actionPerformed(ActionEvent e) {
                    ConfigurationDialog dia = new ConfigurationDialog(null, IceAppsContainerUI.this);
                    dia.setTitle("Create a local ICE Device Adapter");
                    final DefaultComboBoxModel model = new DefaultComboBoxModel(new Configuration.Application[]{Configuration.Application.ICE_Device_Interface});
                    dia.getApplications().setModel(model);
                    dia.set(Configuration.Application.ICE_Device_Interface, null);
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
                                    DomainParticipantQos pQos = new DomainParticipantQos();
                                    DomainParticipantFactory.get_instance().get_default_participant_qos(pQos);
                                    pQos.discovery.initial_peers.clear();
                                    for (int i = 0; i < discoveryPeers.peers.getSize(); i++) {
                                        pQos.discovery.initial_peers.add(discoveryPeers.peers.getElementAt(i));
                                        System.err.println("PEER:" + discoveryPeers.peers.getElementAt(i));
                                    }
                                    DomainParticipantFactory.get_instance().set_default_participant_qos(pQos);
                                    SubscriberQos qos = new SubscriberQos();
                                    subscriber.get_qos(qos);
                                    List<String> partition = new ArrayList<String>();
                                    for (int i = 0; i < qos.partition.name.size(); i++) {
                                        partition.add((String) qos.partition.name.get(i));
                                    }
                                    DeviceAdapter da = DeviceAdapter.newGUIAdapter(c.getDeviceFactory(), context);
                                    da.setInitialPartition(partition.toArray(new String[0]));
                                    da.start(c.getAddress());

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

                        final IceAppsContainer.IceApp app = activeApps.get(appType);
                        if (app != null) {
                            if (app.getUI() instanceof JFrame) {
                                JFrame a = (JFrame) app.getUI();
                                a.setLocationRelativeTo(IceAppsContainerUI.this);
                                a.setVisible(true);
                            } else {
                                activateGoBack(app);
                                app.start(context);
                            }
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

                        activateGoBack(driverWrapper);
                        driverWrapper.start(context, device);
                    }
                    super.mouseClicked(e);
                }
            });

            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setSize(800, 600);
            setLocationRelativeTo(null);
        }

        private void activateGoBack(final IceAppsContainer.IceApp app) {
            Runnable goBackAction = new Runnable() {
                public void run() { app.stop(); }
            };
            panel.getBedLabel().setText(app.getName());
            panel.getBack().setAction(new GoBackAction(goBackAction));
            ol.show(panel.getContent(), app.getId());
            panel.getBack().setVisible(true);
        }

        /**
         * Utility class to be installed as an action handler on the 'back' button
         * of the main panel to stop the current app and bring the pre-defined 'main'
         * screen back forward.
         */
        private class GoBackAction extends AbstractAction {
            final Runnable cmdOnExit;

            public GoBackAction(Runnable r) {
                super("Exit App");
                cmdOnExit = r;
            }

            @Override
            public void actionPerformed(ActionEvent e) {
                if (null != cmdOnExit) {
                    try {
                        cmdOnExit.run();
                    } catch (Throwable t) {
                        log.error("Error in 'go back' logic", t);
                    }
                }
                ol.show(panel.getContent(), Main.getId());
                ((JButton)e.getSource()).setVisible(false);
                ((JButton)e.getSource()).setAction(null);
            }
        }

        /**
         * Utility class to wrap the driver object and preset it as a full-fledged
         * participating application. The main purpose is to enable a formal life-cycle
         * for startup and shutdown of the container.
         */
        private static class DeviceApp implements IceAppsContainer.IceApp {

            CompositeDevicePanel devicePanel = new CompositeDevicePanel();

            @Override
            public String getId() {
                return Device.getId();
            }

            @Override
            public String getName() {
                return Device.getName();
            }

            @Override
            public Icon getIcon() {
                return null;
            }

            @Override
            public Component getUI() {
                return devicePanel;
            }

            @Override
            public void start(ApplicationContext context) {
                throw new IllegalStateException("Internal start(context,driver) API should be called for driver wrapper");
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

            static final ThreadGroup threadGroup = new ThreadGroup("DeviceApp");
        }
    }
}
