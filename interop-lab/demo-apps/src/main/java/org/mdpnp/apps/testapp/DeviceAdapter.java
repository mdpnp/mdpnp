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
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

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
import org.mdpnp.apps.testapp.device.DeviceView;
import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.DeviceDriverProvider;
import org.mdpnp.devices.DeviceDriverProvider.DeviceType;
import org.mdpnp.devices.connected.AbstractConnectedDevice;
import org.mdpnp.devices.serial.SerialProviderFactory;
import org.mdpnp.devices.serial.TCPSerialProvider;
import org.mdpnp.rtiapi.data.DeviceDataMonitor;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;


public abstract class DeviceAdapter extends Observable {

    enum AdapterState { init, connected, running, stopped };

    private static final Logger log = LoggerFactory.getLogger(DeviceAdapter.class);

    public static DeviceAdapter newHeadlessAdapter(DeviceDriverProvider deviceFactory, AbstractApplicationContext context) throws Exception {
        DeviceAdapter da = new HeadlessAdapter(deviceFactory, context, true);
        return da;
    }

    public static DeviceAdapter newGUIAdapter(DeviceDriverProvider deviceFactory, AbstractApplicationContext context) throws Exception {
        DeviceAdapter da = new GUIAdapter(deviceFactory, context);
        return da;
    }

    public static class DeviceAdapterCommand implements Configuration.Command {
        @Override
        public int execute(final Configuration conf) throws Exception
        {
            DeviceDriverProvider ddp = conf.getDeviceFactory();
            if(null == ddp) {
                log.error("Unknown device type was specified");
                throw new Exception("Unknown device type was specified");
            }

            final AbstractApplicationContext context = conf.createContext("DriverContext.xml");

            DeviceAdapter da;
            if(conf.isHeadless())
                da = DeviceAdapter.newHeadlessAdapter(ddp, context);
            else
                da = DeviceAdapter.newGUIAdapter(ddp, context);

            da.start(conf.getAddress());

            // this will block until killAdapter stops everything.
            context.destroy();
            
            return 0;
        }
    }

    abstract AbstractDevice initializeDevice() throws Exception;

    protected void update(String msg, int pct) {
        log.info(pct + "% " + msg);
    }

    protected AbstractDevice device;
    protected String[] initialPartition;

    private final CountDownLatch stopOk = new CountDownLatch(1);

    protected final AbstractApplicationContext context;
    protected final DeviceDriverProvider deviceFactory;

    protected DeviceAdapter(DeviceDriverProvider df, AbstractApplicationContext ctx) {
        deviceFactory = df;
        context = ctx;
    }

    public void setInitialPartition(String[] v) {
        initialPartition = v;
    }

    public AbstractDevice getDevice() {
        return device;
    }

    /**
     * blocking call to start adapter's listening loop. It is expected that stop API will be called on another thread
     * @param address
     * @throws Exception
     */
    public void start(String address) throws Exception {

        initializeDevice();

        run(address);
    }

    protected void run(String address) throws Exception {

        if (null != device && device instanceof AbstractConnectedDevice) {
            if (!((AbstractConnectedDevice)device).connect(address)) {
                stopOk.countDown();
            }
            setChanged();
            notifyObservers(AdapterState.connected);
        }

        setChanged();
        notifyObservers(AdapterState.running);

        // Wait until killAdapter
        stopOk.await();
    }

    public void stop()
    {
        stopOk.countDown();
        setChanged();
        notifyObservers(AdapterState.stopped);
    }

    static class HeadlessAdapter extends DeviceAdapter {

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

        AbstractDevice initializeDevice() throws Exception {

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

            return device;
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

        public synchronized void stop() {

            Metrics metrics = new Metrics();
            try {
                long tm = metrics.start();

                if (null != device && device instanceof AbstractConnectedDevice) {
                    AbstractConnectedDevice cDevice = (AbstractConnectedDevice) device;
                    update("Ask the device to disconnect from the ICE", 50);
                    cDevice.disconnect();
                    if (!cDevice.awaitState(ice.ConnectionState.Disconnected, 5000L)) {
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


    static class GUIAdapter extends HeadlessAdapter {

        private DeviceDataMonitor    deviceMonitor;

        final ProgressBar      progressBar = new ProgressBar();
        final DeviceView       deviceViewController = new DeviceView();
        private Stage          deviceFxStage;

        public GUIAdapter(DeviceDriverProvider deviceFactory, AbstractApplicationContext context) {
            super(deviceFactory, context, false);
        }

        public void stop() {

            Callable<Boolean> uiStop = new Callable<Boolean>() {
                public Boolean call() throws Exception {
                    deviceFxStage.close();
                    return true;
                }
            };

            runOnFxThread(uiStop);

            stopHeadlessComponents();
        }

        private synchronized void stopHeadlessComponents() {

            update("Shut down local monitoring client", 10);
            deviceMonitor.stop();
            update("Shut down local user interface", 20);

            super.stop();
        }


        AbstractDevice initializeDevice() throws Exception {

            DeviceType type = deviceFactory.getDeviceType();

            AbstractDevice device = super.initializeDevice();

            deviceMonitor = new DeviceDataMonitor(device.getDeviceIdentity().ice_id);

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
            EventLoop eventLoop = (EventLoop) context.getBean("eventLoop");
            deviceMonitor.start(device.getSubscriber(), eventLoop);

            TextArea descriptionText = new TextArea();
            descriptionText.setEditable(false);
            descriptionText.setWrapText(true);
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
                    descriptionText.setText(sb.toString().replaceAll("\\%\\%DEVICE\\_TYPE\\%\\%", type.toString()));
                } catch (IOException e) {
                    log.error("Error getting window text", e);
                }
            }
            BorderPane root = new BorderPane();
            descriptionText.setPrefColumnCount(1);
            descriptionText.setPrefRowCount(1);
            ScrollPane scrollPane = new ScrollPane(descriptionText);
            scrollPane.setFitToHeight(true);
            scrollPane.setFitToWidth(true);
            root.setTop(scrollPane);
            root.setCenter(node);

            Callable<Stage>  uiStart = new Callable<Stage>() {
                public Stage call() throws Exception {
                    Stage stage = new Stage(StageStyle.DECORATED);
                    stage.setOnHiding(new EventHandler<WindowEvent>() {
        
                        @Override
                        public void handle(WindowEvent event) {
                            progressBar.setProgress(0.0);
                            update("Shutting down", 1);
                            root.getChildren().clear();
                            root.setTop(progressBar);
                            // Required to trigger destruction of animated DevicePanels
                            deviceViewController.set(null);
                            Runnable r = new Runnable() {
                                public void run() {
                                    stopHeadlessComponents();
                                }
                            };
                            new Thread(r, "Device shutdown thread").start();
                        }
                        
                    });
                    stage.setScene(new Scene(root));
                    stage.setWidth(640);
                    stage.setHeight(480);
                    stage.centerOnScreen();

                    stage.show();
                    return stage;
                }
            };
            deviceFxStage = runOnFxThread(uiStart);
            if(deviceFxStage == null)
                throw new IllegalStateException("Failed to create a stage");

            setChanged();
            notifyObservers(AdapterState.init);
            return device;
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

        public <T> T runOnFxThread(Callable<T> callable) {
            try {
                if(Platform.isFxApplicationThread()) {
                    return callable.call();
                } else {
                    FutureTask<T> future = new FutureTask<>(callable);
                    Platform.runLater(future);
                    return future.get();
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException("Can't execute callable", e);
            }
        }
    }
}
