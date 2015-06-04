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

import com.rti.dds.subscription.Subscriber;
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
import org.mdpnp.apps.fxbeans.*;
import org.mdpnp.apps.testapp.device.DeviceView;
import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.DeviceDriverProvider;
import org.mdpnp.devices.DeviceDriverProvider.DeviceType;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;

/**
 * single vm batch command. assumes none of the run-time support available yet - no
 * top-level spring context exists yet.
 */

public class DeviceAdapterCommand implements Configuration.HeadlessCommand, Configuration.GUICommand {

    private static final Logger log = LoggerFactory.getLogger(org.mdpnp.apps.testapp.DeviceAdapterCommand.class);

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

        final AbstractApplicationContext context = config.createContext("DeviceAdapterContext.xml");

        HeadlessAdapter da = new HeadlessAdapter(ddp, context, true);

        da.setAddress(config.getAddress());

        da.init();

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

        final AbstractApplicationContext context = config.createContext("DeviceAdapterContext.xml");

        GUIAdapter da = new GUIAdapter(ddp, context) {
            @Override
            public void stop()  {
                super.stop();
                // at the very end; kill the context that was created here.
                log.info("Shut down spring context");
                context.destroy();
            }
        };


        da.setAddress(config.getAddress());

        return da;
    }

    static class HeadlessAdapter extends Observable implements DeviceDriverProvider.DeviceAdapter, Runnable {

        HeadlessAdapter(DeviceDriverProvider df, AbstractApplicationContext parentContext, boolean isStandalone) throws Exception{

            deviceType    = df.getDeviceType();
            deviceHandle  = df.create(parentContext);

            if(isStandalone) {
                Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                    public void run() {
                        log.info("Calling killAdapter from shutdown hook");
                        stop();
                    }
                }));
            }
        }

        protected void update(String msg, int pct) {
            log.info(pct + "% " + msg);
        }

        public void init() throws Exception {

            if (null != initialPartition) {
                deviceHandle.setPartition(initialPartition);
            }
            if(null != address)
                deviceHandle.setAddress(address);
        }

        /**
         * blocking call to start adapter's listening loop. It is expected that stop API will be called on another thread
         */
        @Override
        public void run() {

            if(!deviceHandle.connect()) {
                stopOk.countDown();
            }

            // Wait until killAdapter
            try {
                stopOk.await();
            } catch (InterruptedException ex) {
                log.error("Device adapter run failed to block on start/stop latch", ex);
                throw new RuntimeException("Device adapter run failed to block on start/stop latch", ex);
            }
        }

        @Override
        public synchronized void stop() {

            Metrics metrics = new Metrics();
            try {
                update("Ask the device to disconnect from the ICE", 50);
                long tm = metrics.start();
                deviceHandle.disconnect();
                metrics.stop("disconnect", tm);

                update("Shutting down the device", 75);
                tm = metrics.start();
                deviceHandle.stop();
                metrics.stop("device.shutdown", tm);
            }
            catch(Exception ex) {
                log.error("Failed to stop", ex);
                throw ex;
            }
            finally {
                stopOk.countDown();
            }
        }
        protected String[]       initialPartition;
        private   String         address=null;

        private final CountDownLatch stopOk = new CountDownLatch(1);

        private final DeviceDriverProvider.DeviceAdapter deviceHandle;
        private final DeviceType deviceType;

        protected DeviceType getDeviceType() {
            return deviceType;
        }

        @Override
        public void setAddress(String v) {
            address = v;
        }

        @Override
        public AbstractDevice getDevice() {
            return deviceHandle.getDevice();
        }

        public <T> T getComponent(String name, Class<T> requiredType) throws Exception {
            return deviceHandle.getComponent(name, requiredType);
        }

        public <T> T getComponent(Class<T> requiredType) throws Exception {
            return deviceHandle.getComponent(requiredType);
        }

        @Override
        public void setPartition(String[] v) {
            initialPartition = v;
        }

        @Override
        public boolean connect() {
            return deviceHandle.connect();
        }

        @Override
        public void disconnect() {
            deviceHandle.disconnect();
        }
    }


    public static class GUIAdapter extends IceApplication  implements DeviceDriverProvider.DeviceAdapter, Runnable {

        private DeviceDataMonitor      deviceMonitor;
        private final ProgressBar      progressBar = new ProgressBar();
        private final DeviceView       deviceViewController = new DeviceView();

        private final HeadlessAdapter controller;

        public GUIAdapter(DeviceDriverProvider deviceFactory, AbstractApplicationContext context) throws Exception {
            controller = new HeadlessAdapter(deviceFactory, context, false) {
                protected void update(final String msg, final int pct) {
                    GUIAdapter.this.update(msg, pct);
                }
            };
        }

        @Override
        public void stop()  {

            if(!Platform.isFxApplicationThread())
                throw new IllegalStateException("Sneaky developer! Trying to stop ui outside of FX thread");

            // Required to trigger destruction of animated DevicePanels
            deviceViewController.set(null);

            update("Shut down local monitoring client", 10);
            deviceMonitor.stop();
            update("Shut down local user interface", 20);

            try {
                deFact.destroy();
                isFact.destroy();
                saFact.destroy();
                nFact.destroy();
            } catch (Exception e1) {
                log.error("Failed to stop entity factories", e1);
            }            
            
            controller.stop();
        }

        @Override
        public void init() throws Exception {
            super.init();
            controller.init();
        }

        NumericFxListFactory nFact;
        SampleArrayFxListFactory saFact;
        InfusionStatusFxListFactory isFact;
        DeviceListModelFactory deFact;
        
        
        @Override
        public void start(final Stage primaryStage) throws Exception {

//            if(!Platform.isFxApplicationThread())
//                throw new IllegalStateException("Sneaky developer! Trying to start ui outside of FX thread");

            AbstractDevice device = controller.getDevice();
            DeviceType deviceType = controller.getDeviceType();

            // Use the device subscriber so that we
            // automatically maintain the same partition as the device
            final EventLoop eventLoop = controller.getComponent("eventLoop", EventLoop.class);
            final Subscriber subscriber = controller.getComponent("subscriber", Subscriber.class);
            
            // TODO These beans are required only for the standalone adapter with GUI, perhaps they should get their own spring config though?
            // TODO contentfilter these on the one device?
            nFact = new NumericFxListFactory();
            nFact.setEventLoop(eventLoop);
            nFact.setSubscriber(subscriber);
            nFact.setQosLibrary(QosProfiles.ice_library);
            nFact.setQosProfile(QosProfiles.numeric_data);
            nFact.setTopicName(ice.NumericTopic.VALUE);
            final NumericFxList numericList = nFact.getObject();
            
            saFact = new SampleArrayFxListFactory();
            saFact.setEventLoop(eventLoop);
            saFact.setSubscriber(subscriber);
            saFact.setQosLibrary(QosProfiles.ice_library);
            saFact.setQosProfile(QosProfiles.waveform_data);
            saFact.setTopicName(ice.SampleArrayTopic.VALUE);
            final SampleArrayFxList sampleArrayList = saFact.getObject();
            
            isFact = new InfusionStatusFxListFactory();
            isFact.setEventLoop(eventLoop);
            isFact.setSubscriber(subscriber);
            isFact.setQosLibrary(QosProfiles.ice_library);
            isFact.setQosProfile(QosProfiles.waveform_data);
            isFact.setTopicName(ice.InfusionStatusTopic.VALUE);
            final InfusionStatusFxList infusionStatusList = isFact.getObject();

            org.mdpnp.devices.TimeManager tm=controller.getComponent(org.mdpnp.devices.TimeManager.class);
            deFact = new DeviceListModelFactory(eventLoop, subscriber, tm);
            final DeviceListModel deviceListModel = deFact.getObject();
            
            
            Callback<Class<?>, Object> factory = new Callback<Class<?>, Object>()
            {
                public Object call(Class<?> type) {
                    return deviceViewController;
                }
            };

            FXMLLoader loader = new FXMLLoader(DeviceView.class.getResource("DeviceView.fxml"));
            loader.setControllerFactory(factory);
            Parent node = loader.load();

            
            Platform.runLater( () -> {
                deviceMonitor = new DeviceDataMonitor(device.getDeviceIdentity().unique_device_identifier, deviceListModel, numericList, sampleArrayList, infusionStatusList);
    
                deviceViewController.set(deviceMonitor);
    
    
    
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
    
                Thread deviceRunner = new Thread(AbstractDevice.threadGroup, this);
                deviceRunner.setDaemon(true);
                deviceRunner.start();
    
                stage.show();
            });
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
        public void setPartition(String[] v) {
            controller.setPartition(v);
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

        @Override
        public <T> T getComponent(String name, Class<T> requiredType) throws Exception {
            return controller.getComponent(name, requiredType);
        }

        @Override
        public <T> T getComponent(Class<T> requiredType) throws Exception {
            return controller.getComponent(requiredType);
        }

        @Override
        public boolean connect() {
            return controller.connect();
        }

        @Override
        public void disconnect() {
            controller.disconnect();
        }
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

}
