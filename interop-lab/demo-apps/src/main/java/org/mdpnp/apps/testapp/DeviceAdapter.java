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

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Semaphore;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import ice.ConnectionType;
import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.DeviceDriverProvider;
import org.mdpnp.devices.DeviceDriverProvider.DeviceType;
import org.mdpnp.devices.connected.AbstractConnectedDevice;
import org.mdpnp.devices.serial.SerialProviderFactory;
import org.mdpnp.devices.serial.TCPSerialProvider;
import org.mdpnp.guis.swing.CompositeDevicePanel;
import org.mdpnp.rtiapi.data.DeviceDataMonitor;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;


public abstract class DeviceAdapter {

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
        public int execute(Configuration config) throws Exception
        {
            DeviceDriverProvider ddp = config.getDeviceFactory();
            if(null == ddp) {
                log.error("Unknown device type was specified");
                return -1;
            }

            final AbstractApplicationContext context = config.createContext("DriverContext.xml");

            DeviceAdapter da;
            if(config.isHeadless())
                da = DeviceAdapter.newHeadlessAdapter(ddp, context);
            else
                da = DeviceAdapter.newGUIAdapter(ddp, context);

            da.start(config.getAddress());

            // this will block until killAdapter stops everything.
            context.destroy();
            return 0;
        }
    }

    abstract AbstractDevice init() throws Exception;

    protected void update(String msg, int pct) {
        log.info(pct + "% " + msg);
    }

    protected AbstractDevice device;
    protected String[] initialPartition;

    private final Semaphore stopOk = new Semaphore(0);

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

    public void start(String address) throws Exception {

        init();

        if (null != device && device instanceof AbstractConnectedDevice) {
            if (!((AbstractConnectedDevice) device).connect(address)) {
                stopOk.release();
            }
        }

        // Wait until killAdapter
        stopOk.acquire();
    }

    public void stop()
    {
        stopOk.release();
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

        AbstractDevice init() throws Exception {

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
                    update("Ask the device to disconnect from the ICE", 20);
                    cDevice.disconnect();
                    if (!cDevice.awaitState(ice.ConnectionState.Disconnected, 5000L)) {
                        log.warn("ConnectedDevice ended in State:" + cDevice.getState());
                    }
                    tm = metrics.stop("disconnect", tm);
                }

                tm = metrics.start();
                if (device != null) {
                    update("Shut down the device", 50);
                    device.shutdown();
                    metrics.stop("device.shutdown", tm);
                    device = null;
                }
            } finally {
                super.stop();
            }
        }

    }


    static class GUIAdapter extends HeadlessAdapter {

        private JFrame               frame;
        private DeviceDataMonitor    deviceMonitor;
        private CompositeDevicePanel cdp;

        final JProgressBar      progressBar = new JProgressBar(1, 100);

        public GUIAdapter(DeviceDriverProvider deviceFactory, AbstractApplicationContext context) {
            super(deviceFactory, context, false);
        }

        public JFrame getFrame() {
            return frame;
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

        private void stopComponents() {

            try {
                update("Shut down local monitoring client", 10);
                cdp.stop();
                deviceMonitor.stop();
                update("Shut down local user interface", 20);
                cdp.reset();
            } finally {
                super.stop();
            }
        }

        public void stop() {

            stopComponents();
            frame.setVisible(false);
            frame.dispose();
        }
        AbstractDevice init() throws Exception {

            DeviceType type = deviceFactory.getDeviceType();

            AbstractDevice device = super.init();

            deviceMonitor = new DeviceDataMonitor(device.getDeviceIdentity().unique_device_identifier);

            cdp = new CompositeDevicePanel();
            cdp.setModel(deviceMonitor);

            // Use the device subscriber so that we
            // automatically maintain the same partition as the device
            EventLoop eventLoop = (EventLoop) context.getBean("eventLoop");
            deviceMonitor.start(device.getSubscriber(), eventLoop);

            frame = new DemoFrame("ICE Device Adapter - " + type);
            frame.setIconImage(ImageIO.read(DeviceAdapter.class.getResource("icon.png")));
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {

                    progressBar.setStringPainted(true);
                    update("Shutting down", 1);

                    frame.getContentPane().removeAll();

                    frame.getContentPane().setLayout(new BorderLayout());
                    frame.getContentPane().add(progressBar, BorderLayout.NORTH);
                    frame.validate();
                    frame.repaint();

                    Runnable r = new Runnable() {
                        public void run() {
                            stopComponents();
                        }
                    };
                    new Thread(r, "Device shutdown thread").start();
                    super.windowClosing(e);
                }
            });
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setSize(640, 480);
            frame.setLocationRelativeTo(null);
            frame.getContentPane().setLayout(new BorderLayout());
            JTextArea descriptionText = new JTextArea();
            descriptionText.setEditable(false);
            descriptionText.setLineWrap(true);
            descriptionText.setWrapStyleWord(true);
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

            frame.getContentPane().add(new JScrollPane(descriptionText), BorderLayout.NORTH);
            frame.getContentPane().add(cdp, BorderLayout.CENTER);

            frame.getContentPane().validate();
            frame.setVisible(true);

            return device;
        }

        protected void update(String msg, int pct) {
            log.info(pct + "% " + msg);

            progressBar.setString(msg);
            progressBar.setValue(pct);
        }
    }
}
