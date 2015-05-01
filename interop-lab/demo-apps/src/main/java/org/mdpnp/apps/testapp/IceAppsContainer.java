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

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import org.mdpnp.apps.device.DeviceDataMonitor;
import org.mdpnp.apps.fxbeans.InfusionStatusFxList;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.IceApplicationProvider.AppType;
import org.mdpnp.apps.testapp.comboboxfix.SingleSelectionModel;
import org.mdpnp.apps.testapp.device.DeviceView;
import org.mdpnp.apps.testapp.patient.EMRFacade;
import org.mdpnp.apps.testapp.patient.PatientInfo;
import org.mdpnp.devices.BuildInfo;
import org.mdpnp.devices.TimeManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;
import com.sun.glass.ui.Screen;

/**
 * Container responsible for discovery and hosting of ICE applications. Its main
 * purpose is enforcement of standard life-cycle of the participating
 * components.
 *
 * Any application that implements IceApplicationProvider interface and complies
 * with java's <a href=
 * "http://docs.oracle.com/javase/7/docs/api/java/util/ServiceLoader.html"
 * >ServiceLoader</a> pattern will be discovered by this container. The
 * application could be either JavaFX UI or headless background thread (but not
 * a console-based green-screen). For the UI apps, it could be either standalone
 * frames or embeddable panels.
 *
 * Regardless of the implementation details, all apps will be started the same
 * way via {@link IceApplicationProvider.IceApp#activate} API, and shut down via
 * call to {@link IceApplicationProvider.IceApp#stop}. It is assumed that the
 * apps could be stateful - i.e when created at the activate of the container
 * they could span thread, keep references to resources as long as they dispose
 * of them properly in the implementation of
 * {@link IceApplicationProvider.IceApp#destroy} API.
 *
 * @see IceApplicationProvider
 *
 */
public class IceAppsContainer extends IceApplication {

    private static final Logger log = LoggerFactory.getLogger(IceAppsContainer.class);

    @SuppressWarnings("unused")
    private static IceApplicationProvider.AppType Main = new IceApplicationProvider.AppType("Main Menu", null, (URL) null, 0);
    private static IceApplicationProvider.AppType Device = new IceApplicationProvider.AppType("Device Info", null, (URL) null, 0);

    DeviceApp driverWrapper = new DeviceApp();

    final Map<IceApplicationProvider.AppType, IceApplicationProvider.IceApp> activeApps = new HashMap<>();

    private DemoPanel panelController;
    private Parent panelRoot;

    @SuppressWarnings("unused")
    private MainMenu mainMenuController;
    private Parent mainMenuRoot;

    private PartitionChooserModel partitionChooserModel;

    // private DiscoveryPeers discoveryPeers;

    public IceAppsContainer() {

    }

    private void activateGoBack(final IceApplicationProvider.IceApp app) {

        final String appName = app.getDescriptor().getName();

        Runnable goBackAction = new Runnable() {
            public void run() {
                try {
                    panelController.appTitle.setText("");
                    app.stop();
                } catch (Exception ex) {
                    log.error("Failed to stop " + appName, ex);
                }
            }
        };
        panelController.back.setVisible(true);
        
        panelController.content.setCenter(app.getUI());
        panelController.getBack().setOnAction(new GoBackAction(goBackAction));
    }

    /**
     * Utility class to be installed as an action handler on the 'back' button
     * of the main panel to stop the current app and bring the pre-defined
     * 'main' screen back forward.
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
            // TODO make this more elegant
            ((Button) event.getSource()).setVisible(false);
            ((Button) event.getSource()).setOnAction(null);
        }
    }

    /**
     * Utility class to wrap the driver object and preset it as a full-fledged
     * participating application. The main purpose is to enable a formal
     * life-cycle for startup and shutdown of the container.
     */
    private static class DeviceApp implements IceApplicationProvider.IceApp {
        private Parent ui;
        private DeviceView devicePanel;

        @Override
        public IceApplicationProvider.AppType getDescriptor() {
            return Device;
        }

        @Override
        public Parent getUI() {
            return ui;
        }

        @Override
        public void activate(ApplicationContext context) {
            throw new IllegalStateException("Internal activate(context,driver) API should be called for driver wrapper");
        }

        public void start(ApplicationContext context, final Device device) throws IOException {

            final NumericFxList numericList = context.getBean("numericList", NumericFxList.class);
            final SampleArrayFxList sampleArrayList = context.getBean("sampleArrayList", SampleArrayFxList.class);
            final InfusionStatusFxList infusionStatusList = context.getBean("infusionStatusList", InfusionStatusFxList.class);
            final DeviceListModel deviceListModel = context.getBean("deviceListModel", DeviceListModel.class);
            
            FXMLLoader loader = new FXMLLoader(DeviceView.class.getResource("DeviceView.fxml"));
            ui = loader.load();
            devicePanel = loader.getController();

            DeviceDataMonitor deviceMonitor = devicePanel.getModel();
            if (null != deviceMonitor) {
                deviceMonitor.stop();
            }
            deviceMonitor = new DeviceDataMonitor(device.getUDI(), deviceListModel, numericList, sampleArrayList, infusionStatusList);
            devicePanel.set(deviceMonitor);
        }

        @Override
        public void stop() {
            if (devicePanel != null) {
                DeviceDataMonitor deviceMonitor = devicePanel.getModel();
                if (null != deviceMonitor) {
                    deviceMonitor.stop();
                }
                devicePanel.set((DeviceDataMonitor) null);
            }
        }

        @Override
        public void destroy() {
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("OpenICE");
        
        int visibleWidth  = Screen.getMainScreen().getVisibleWidth();
        int visibleHeight = Screen.getMainScreen().getVisibleHeight();
        
        int width = (int) (0.85 * visibleWidth);
        int height = (int) (0.85 * visibleHeight);

        Scene panelScene = new Scene(panelRoot);
        primaryStage.setOnHiding(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                final ExecutorService refreshScheduler = context.getBean("refreshScheduler", ExecutorService.class);
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

                log.info("All apps closed, stop OK");
                stopOk.countDown();
            }

        });

        primaryStage.setScene(panelScene);
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
        primaryStage.centerOnScreen();
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
        final TimeManager timeManager = context.getBean("timeManager", TimeManager.class);
        final Publisher publisher = context.getBean("publisher", Publisher.class);
        final Subscriber subscriber = context.getBean("subscriber", Subscriber.class);
        final String udi = context.getBean("supervisorUdi", String.class);

        final DeviceListModel nc = context.getBean("deviceListModel", DeviceListModel.class);
        final EMRFacade emr = context.getBean("emr", EMRFacade.class);

        timeManager.start();
        
        // setIconImage(ImageIO.read(getClass().getResource("icon.png")));
        partitionChooserModel = new PartitionChooserModel(subscriber, publisher);

        FXMLLoader loader = new FXMLLoader(DemoPanel.class.getResource("DemoPanel.fxml"));
        panelRoot = loader.load();
        panelController = ((DemoPanel)loader.getController()).setModel(partitionChooserModel).setUdi(udi).setVersion(BuildInfo.getDescriptor())
                .setModel(emr.getPatients()).setDeviceListModel(nc);
        panelRoot.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

        // discoveryPeers = new DiscoveryPeers(this);
        // discoveryPeers.setSize(320, 240);
        // discoveryPeers.set(participant);

        // Locate all available ice application via the service loader.
        // For documentation refer to
        // http://docs.oracle.com/javase/7/docs/api/java/util/ServiceLoader.html
        ServiceLoader<IceApplicationProvider> l = ServiceLoader.load(IceApplicationProvider.class);

        final Iterator<IceApplicationProvider> iter = l.iterator();
        while (iter.hasNext()) {
            IceApplicationProvider ap = iter.next();
            if (ap.getAppType().isDisabled())
                continue;

            try {
                IceApplicationProvider.IceApp a = ap.create(context);
                activeApps.put(ap.getAppType(), a);
            } catch (Throwable ex) {
                // continue as there is nothing mich that can be done,
                // but print the error out to the log.
                log.error("Failed to create " + ap.getAppType(), ex);
            }
        }

        // Now that we have a list of all active components, build up a menu and
        // add it to the app
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
        
        final SingleSelectionModel<PatientInfo> selectionModel = panelController.getPatients().getSelectionModel();
        StringProperty patientNameProperty = new SimpleStringProperty("");
        patientNameProperty.bind(
                Bindings.when(selectionModel.selectedItemProperty().isNotNull())
                .then(selectionModel.selectedItemProperty().asString())
                .otherwise(""));
        
        ListProperty<Device> deviceListProperty = new SimpleListProperty<>(nc.getContents());
        
        mainMenuController.getDevicesEmptyText().textProperty().bind(
                Bindings.when(deviceListProperty.emptyProperty())
                .then(Bindings.concat(
                        "There are no devices associated with ", 
                        patientNameProperty, 
                        ".  Create an ICE_Device_Adapter connected to a physical medical device or a software simulator and associate that device with ", patientNameProperty, "."))
                .otherwise(""));
        mainMenuController.devicesLabel.textProperty().bind(
                Bindings.when(patientNameProperty.isEmpty())
                .then("Devices")
                .otherwise(Bindings.concat("Devices assigned to ", patientNameProperty)));
        
        
        mainMenuController.getAppList().setCellFactory(new AppTypeCellFactory(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                AppTypeGridCell cell = (AppTypeGridCell) event.getSource();
                AppType appType = (AppType) cell.getUserData();
                final IceApplicationProvider.IceApp app = activeApps.get(appType);
                if (app != null) {
                    if (app.getUI() != null) {
                        panelController.appTitle.setText(app.getDescriptor().getName());
                        activateGoBack(app);
                    }

                    app.activate(context);
                }

            }

        }));
        mainMenuController.getDeviceList().setCellFactory(new DeviceCellFactory(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                DeviceGridCell cell = (DeviceGridCell) event.getSource();
                Device device = (org.mdpnp.apps.testapp.Device) cell.getUserData();
                try {
                    driverWrapper.start(context, device);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                activateGoBack(driverWrapper);
            }

        }));

        // Add a wrapper for the driver adapter display. This is so that the
        // stop logic could
        // shut it down properly.
        activeApps.put(Device, driverWrapper);
        mainMenuController.setTypes(at).setDevices(nc.getContents());

        Platform.runLater(new Runnable() {
            public void run() {
                panelController.getContent().setCenter(mainMenuRoot);
            }
        });

    }

    @Override
    public void stop() throws Exception {
        // this will block until the frame is killed
        stopOk.await();
        panelController.stop();
        // kill the spring context that is owned by this component.
        context.destroy();
        super.stop();
    }
}
