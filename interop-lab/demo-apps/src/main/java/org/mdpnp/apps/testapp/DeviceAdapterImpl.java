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

import ice.ConnectionType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

import org.mdpnp.apps.device.DeviceDataMonitor;
import org.mdpnp.apps.fxbeans.InfusionStatusFxList;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.mdpnp.apps.testapp.device.DeviceView;
import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.DeviceDriverProvider;
import org.mdpnp.devices.DeviceDriverProvider.DeviceType;
import org.mdpnp.devices.connected.AbstractConnectedDevice;
import org.mdpnp.devices.serial.SerialProviderFactory;
import org.mdpnp.devices.serial.TCPSerialProvider;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;


public abstract class DeviceAdapterImpl extends Observable implements DeviceAdapter {

    private static final Logger log = LoggerFactory.getLogger(DeviceAdapterImpl.class);

    public static class DeviceAdapterCommand implements Configuration.HeadlessCommand, Configuration.GUICommand {

        @Override
        public int execute(final Configuration config) throws Exception
        {
            // TODO revisit check for headless and check for FX Application Thread
            // This attempts to initialize the default Toolkit which will fail in truly headless
            // environments.  Is there another precheck for a graphical display that can be called before this?
            // or is it possible to substitute a different Toolkit?
//            if(Platform.isFxApplicationThread())
//                throw new IllegalStateException("Trying to start headless blocking device adapter on UI thread");

            DeviceDriverProvider ddp = config.getDeviceFactory();
            if(null == ddp) {
                log.error("Unknown device type was specified");
                throw new Exception("Unknown device type was specified");
            }

            final AbstractApplicationContext context = config.createContext("DriverContext.xml");

            DeviceAdapter da = new DeviceAdapterImpl.HeadlessAdapter(ddp, context, true);

            da.init();
            da.setAddress(config.getAddress());

            // this will block until stops kills everything from another thread or a
            // VM's shutdown hook
            da.run();

            // will only get here once the controller loop is stopped
            context.destroy();

            return 0;
        }


        @Override
        public IceApplication create(Configuration config) throws Exception {

            if(Platform.isFxApplicationThread() && config.isHeadless())
                throw new IllegalStateException("Attempting to start headless app on the UI thread");

            DeviceDriverProvider ddp = config.getDeviceFactory();
            if(null == ddp) {
                log.error("Unknown device type was specified");
                throw new Exception("Unknown device type was specified");
            }

            final AbstractApplicationContext context = config.createContext("DriverContext.xml");

            GUIAdapter da = new GUIAdapter(ddp, context) {
                @Override
                public void stop() throws Exception {
                    super.stop();
                    // at the very end; kill the context that was created here.
                    log.info("Shut down spring context");
                    context.destroy();
                }
            };

            
            da.setAddress(config.getAddress());
            
            return da;
        }
    }

    protected void update(String msg, int pct) {
        log.info(pct + "% " + msg);
    }

    protected AbstractDevice device;
    protected String[]       initialPartition;
    private   String         address=null;

    private final CountDownLatch stopOk = new CountDownLatch(1);

    protected final AbstractApplicationContext context;
    protected final DeviceDriverProvider deviceFactory;

    protected DeviceAdapterImpl(DeviceDriverProvider df, AbstractApplicationContext ctx) {
        deviceFactory = df;
        context = ctx;
    }

    @Override
    public void setInitialPartition(String[] v) {
        initialPartition = v;
    }

    @Override
    public void setAddress(String v) {
        address = v;
    }

    @Override
    public AbstractDevice getDevice() {
        return device;
    }

    protected AbstractApplicationContext getContext() {
        return context;
    }

    protected DeviceType getDeviceType() {
        return deviceFactory.getDeviceType();
    }


    /**
     * blocking call to start adapter's listening loop. It is expected that stop API will be called on another thread
     */
    @Override
    public void run() {

        if (null != device && device instanceof AbstractConnectedDevice) {
            log.info("Connecting to >" + address + "<");
            if (!((AbstractConnectedDevice)device).connect(address)) {
                stopOk.countDown();
            }
            setChanged();
            notifyObservers(AdapterState.connected);
        }

        setChanged();
        notifyObservers(AdapterState.running);

        // Wait until killAdapter
        try {
            stopOk.await();
        } catch (InterruptedException ex) {
            log.error("Device adapter run failed to block on start/stop latch", ex);
            throw new RuntimeException("Device adapter run failed to block on start/stop latch", ex);
        }
    }

    @Override
    public void stop()
    {
        stopOk.countDown();
        setChanged();
        notifyObservers(AdapterState.stopped);
    }

    static class HeadlessAdapter extends DeviceAdapterImpl  {

        HeadlessAdapter(DeviceDriverProvider deviceFactory, AbstractApplicationContext context, boolean isStandalone) {

            super(deviceFactory, context);

            if(isStandalone) {
                Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                    public void run() {
                        log.info("Calling killAdapter from shutdown hook");
                        stop();
                    }
                }));
            }
        }

        @Override
        public void init() throws Exception {

            DeviceType type = deviceFactory.getDeviceType();

            log.trace("Starting DeviceAdapter with type=" + type);
            if (ConnectionType.Network.equals(type.getConnectionType())) {
                SerialProviderFactory.setDefaultProvider(new TCPSerialProvider());
                log.info("Using the TCPSerialProvider, be sure you provided a host:port target");
            }

            device = deviceFactory.create(context);

            if (null != initialPartition) {
                device.setPartition(initialPartition);
            }

            setChanged();
            notifyObservers(AdapterState.init);
        }

        private static class Metrics {
            long start() {
                return System.currentTimeMillis();
            }

            long stop(String s, long tm) {
                log.trace(s + " took " + (System.currentTimeMillis() - tm) + "ms");
                return start();
            }
        }

        @Override
        public synchronized void stop() {

            Metrics metrics = new Metrics();
            try {
                long tm = metrics.start();

                if (null != device && device instanceof AbstractConnectedDevice) {
                    AbstractConnectedDevice cDevice = (AbstractConnectedDevice) device;
                    update("Ask the device to disconnect from the ICE", 50);
                    cDevice.disconnect();
                    if (!cDevice.awaitState(ice.ConnectionState.Terminal, 5000L)) {
                        log.warn("ConnectedDevice ended in State:" + cDevice.getState());
                    }
                    metrics.stop("disconnect", tm);
                }

                tm = metrics.start();
                if (device != null) {
                    update("Shutting down the device", 75);
                    device.shutdown();
                    metrics.stop("device.shutdown", tm);
                    device = null;
                }
            }
            catch(Exception ex) {
                log.error("Failed to stop", ex);
                throw ex;
            }
            finally {
                device = null;
                super.stop();
            }
        }

    }


    public static class GUIAdapter extends IceApplication implements DeviceAdapter {

        private DeviceDataMonitor      deviceMonitor;
        private final ProgressBar      progressBar = new ProgressBar();
        private final DeviceView       deviceViewController = new DeviceView();

        private final DeviceAdapterImpl controller;

        public GUIAdapter(DeviceDriverProvider deviceFactory, AbstractApplicationContext context) {
            controller = new HeadlessAdapter(deviceFactory, context, false);
        }

        @Override
        public void stop() throws Exception {

            if(!Platform.isFxApplicationThread())
                throw new IllegalStateException("Sneaky developer! Trying to stop ui outside of FX thread");

            // Required to trigger destruction of animated DevicePanels
            deviceViewController.set(null);

            update("Shut down local monitoring client", 10);
            deviceMonitor.stop();
            update("Shut down local user interface", 20);

            controller.stop();

            super.stop();
        }

        @Override
        public void init() throws Exception {
            super.init();
            controller.init();
        }

        @Override
        public void start(final Stage primaryStage) throws Exception {

            if(!Platform.isFxApplicationThread())
                throw new IllegalStateException("Sneaky developer! Trying to start ui outside of FX thread");

            AbstractDevice device = controller.getDevice();
            DeviceType deviceType = controller.getDeviceType();

            final NumericFxList numericList = controller.getContext().getBean("numericList", NumericFxList.class);
            final SampleArrayFxList sampleArrayList = controller.getContext().getBean("sampleArrayList", SampleArrayFxList.class);
            final InfusionStatusFxList infusionStatusList = controller.getContext().getBean("infusionStatusList", InfusionStatusFxList.class);
            final DeviceListModel deviceListModel = controller.getContext().getBean("deviceListModel", DeviceListModel.class);
            
            deviceMonitor = new DeviceDataMonitor(device.getDeviceIdentity().unique_device_identifier, deviceListModel, numericList, sampleArrayList, infusionStatusList);

            Callback<Class<?>, Object> factory = new Callback<Class<?>, Object>()
            {
                public Object call(Class<?> type) {
                    return deviceViewController;
                }
            };

            FXMLLoader loader = new FXMLLoader(DeviceView.class.getResource("DeviceView.fxml"));
            loader.setControllerFactory(factory);
            Parent node = loader.load();
            deviceViewController.set(deviceMonitor);

            // Use the device subscriber so that we
            // automatically maintain the same partition as the device
            EventLoop eventLoop = (EventLoop) controller.getContext().getBean("eventLoop");

            TextArea descriptionText = new TextArea();
            descriptionText.setEditable(false);
            descriptionText.setWrapText(true);
            descriptionText.setText(getDeviceTypeDescription(deviceType));
            BorderPane root = new BorderPane();
            descriptionText.setPrefColumnCount(1);
            descriptionText.setPrefRowCount(1);
            ScrollPane scrollPane = new ScrollPane(descriptionText);
            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);
            root.setTop(scrollPane);
            root.setCenter(node);

            Stage stage = primaryStage == null ? new Stage(StageStyle.DECORATED) : primaryStage;

            stage.setTitle("ICE Device Interface - "+deviceType.toString());
            
            stage.setOnHiding(new EventHandler<WindowEvent>() {

                @Override
                public void handle(WindowEvent event) {
                    progressBar.setProgress(0.0);
                    update("Shutting down", 1);
                    root.getChildren().clear();
                    root.setTop(progressBar);

                    // this is a dialog - the application's 'close' event
                    // wont happen
                    if(primaryStage == null) {
                        try {
                            GUIAdapter.this.stop();
                        } catch (Exception e) {
                            log.error("Failed to stop device adapter");
                        }
                    }
                    // In case of this being a 'real' application,  stop will be called
                    // by the fx framework
                }

            });
            stage.setScene(new Scene(root));
            stage.setWidth(640);
            stage.setHeight(480);
            stage.centerOnScreen();

            controller.setChanged();
            controller.notifyObservers(AdapterState.init);

            Thread deviceRunner = device.newThread(this);
            deviceRunner.start();

            stage.show();
        }

        private String getDeviceTypeDescription(DeviceType deviceType) {
            InputStream is = ConfigurationDialog.class.getResourceAsStream("device-adapter");
            if (null != is) {
                try {

                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    StringBuffer sb = new StringBuffer();
                    String line = null;
                    while (null != (line = br.readLine())) {
                        sb.append(line).append("\n");
                    }
                    br.close();
                    String s = sb.toString().replaceAll("\\%\\%DEVICE\\_TYPE\\%\\%", deviceType.toString());
                    return s;
                } catch (IOException e) {
                    log.error("Error getting window text", e);
                }
            }
            return "";
        }

        @Override
        public void setInitialPartition(String[] v) {
            controller.setInitialPartition(v);
        }

        @Override
        public void addObserver(Observer v) {
            controller.addObserver(v);
        }

        @Override
        public void deleteObserver(Observer v) {
            controller.deleteObserver(v);
        }

        @Override
        public AbstractDevice getDevice() {
            return controller.getDevice();
        }

        @Override
        public void run() {
            controller.run();
        }

        @Override
        public void setAddress(String address) {
            controller.setAddress(address);
        }

        protected void update(final String msg, final int pct) {
            log.info(pct + "% " + msg);

            Runnable r = new Runnable() {
                public void run() {
                    progressBar.setProgress(pct / 100.0);
                }
            };

            if(Platform.isFxApplicationThread()) {
                r.run();
            } else {
                Platform.runLater(r);
            }

        }
    }
}
