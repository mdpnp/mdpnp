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

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.mdpnp.apps.testapp.Configuration.DeviceType;
import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.EventLoopHandler;
import org.mdpnp.devices.connected.AbstractConnectedDevice;
import org.mdpnp.devices.serial.SerialProviderFactory;
import org.mdpnp.devices.serial.TCPSerialProvider;
import org.mdpnp.guis.swing.CompositeDevicePanel;
import org.mdpnp.rtiapi.data.DeviceDataMonitor;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class DeviceAdapter {

    private JFrame frame;
    private AbstractDevice device;
    private EventLoopHandler handler;
    private String[] initialPartition;
    
    public void setInitialPartition(String[] initialPartition) {
        this.initialPartition = initialPartition;
    }

    public AbstractDevice getDevice() {
        return device;
    }
    
    public JFrame getFrame() {
        return frame;
    }

    long start() {
        return System.currentTimeMillis();
    }

    long stop(String s, long tm) {
        log.trace(s + " took " + (System.currentTimeMillis() - tm) + "ms");
        return start();
    }

    private static final void setString(JProgressBar progressBar, String s, int value) {
        if (progressBar != null) {
            progressBar.setString(s);
            progressBar.setValue(value);
        }
    }

    private void killAdapter() {
        killAdapter(null);
    }

    private synchronized void killAdapter(final JProgressBar progressBar) {
        try {
            long tm = start();

            if (null != device && device instanceof AbstractConnectedDevice) {
                AbstractConnectedDevice cDevice = (AbstractConnectedDevice) device;
                setString(progressBar, "Ask the device to disconnect from the ICE", 20);
                cDevice.disconnect();
                if (!cDevice.awaitState(ice.ConnectionState.Disconnected, 5000L)) {
                    log.warn("ConnectedDevice ended in State:" + cDevice.getState());
                }
                tm = stop("disconnect", tm);
            }

            tm = start();
            if (device != null) {
                setString(progressBar, "Shut down the device", 50);
                device.shutdown();
                stop("device.shutdown", tm);
                device = null;
            }
            tm = start();
            if (handler != null) {
                try {
                    setString(progressBar, "Stop event processing", 95);
                    handler.shutdown();
                    stop("handler.shutdown", tm);
                    handler = null;
                } catch (InterruptedException e) {
                    log.error("Interrupted in handler.shutdown", e);
                }
            }
        } finally {
            synchronized (this) {
                interrupted = true;
                this.notifyAll();
            }
        }
    }

    private static final Logger log = LoggerFactory.getLogger(DeviceAdapter.class);

    public void start(DeviceType type, int domainId, final String address, boolean gui) throws Exception {
        start(type, domainId, address, gui, true, null);
    }

    public void start(DeviceType type, int domainId, final String address, boolean gui, boolean exit, EventLoop eventLoop) throws Exception {
        log.trace("Starting DeviceAdapter with type=" + type);
        if (null != address && address.contains(":")) {
            SerialProviderFactory.setDefaultProvider(new TCPSerialProvider());
            log.info("Using the TCPSerialProvider, be sure you provided a host:port target");
        }
        if (null == eventLoop) {
            eventLoop = new EventLoop();
            handler = new EventLoopHandler(eventLoop);
        } else {
            handler = null;
        }

        device = DeviceFactory.buildDevice(type, domainId, eventLoop);
        
        if(null != initialPartition) {
            device.setPartition(initialPartition);
        }

        if (gui) {
            
            final DeviceDataMonitor deviceMonitor = new DeviceDataMonitor(device.getDeviceIdentity().unique_device_identifier);
            
            final CompositeDevicePanel cdp = new CompositeDevicePanel();
            
            cdp.setModel(deviceMonitor);
            
            // Use the device subscriber so that we
            // automatically maintain the same partition as the device
            deviceMonitor.start(device.getSubscriber(), eventLoop);

            frame = new DemoFrame("ICE Device Adapter - " + type);
            frame.setIconImage(ImageIO.read(DeviceAdapter.class.getResource("icon.png")));
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    // On the AWT EventQueue
                    final JProgressBar progressBar = new JProgressBar();
                    // progressBar.setMaximumSize(new Dimension(300, 100));
                    // progressBar.setPreferredSize(new Dimension(300, 100));
                    progressBar.setMinimum(1);
                    progressBar.setMaximum(100);

                    progressBar.setStringPainted(true);
                    setString(progressBar, "Shutting down", 1);

                    frame.getContentPane().removeAll();

                    frame.getContentPane().setLayout(new BorderLayout());
                    frame.getContentPane().add(progressBar, BorderLayout.NORTH);
                    frame.validate();
                    frame.repaint();

                    Runnable r = new Runnable() {
                        public void run() {
                            try {
                                setString(progressBar, "Shut down local monitoring client", 10);
                                cdp.stop();
                                deviceMonitor.stop();
                                setString(progressBar, "Shut down local user interface", 20);
                                cdp.reset();
                            } finally {
                                killAdapter(progressBar);
                            }
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
        } else {
            Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                public void run() {
                    log.info("Calling killAdapter from shutdown hook");
                    killAdapter();
                }
            }));
        }

        if (null != device && device instanceof AbstractConnectedDevice) {
            if(!((AbstractConnectedDevice) device).connect(address)) {
                synchronized(this) {
                    interrupted = true;
                    this.notifyAll();
                }
            }
        }

        // Wait until killAdapter, then report on any threads that didn't come
        // down successfully
        synchronized (this) {
            while (!interrupted) {
                wait();
            }
        }

        if (gui) {
            frame.setVisible(false);
            frame.dispose();
        }

        if (exit) {

            int n = Thread.activeCount() + 10;
            Thread[] threads = new Thread[n];
            n = Thread.enumerate(threads);
            for (int i = 0; i < n; i++) {
                if (threads[i].isAlive() && !threads[i].isDaemon() && !Thread.currentThread().equals(threads[i])) {
                    log.warn("Non-Daemon thread would block exit: " + threads[i].getName());
                }
            }

            System.exit(0);
        }
    }

    private boolean interrupted = false;
}
