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
import javax.swing.SwingUtilities;

import org.mdpnp.apps.testapp.Configuration.DeviceType;
import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.EventLoopHandler;
import org.mdpnp.devices.connected.GetConnected;
import org.mdpnp.devices.connected.GetConnectedToFixedAddress;
import org.mdpnp.devices.serial.SerialProviderFactory;
import org.mdpnp.devices.serial.TCPSerialProvider;
import org.mdpnp.guis.swing.CompositeDevicePanel;
import org.mdpnp.guis.swing.DeviceMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeviceAdapter {

    private JFrame frame;
    private GetConnected getConnected;
    private AbstractDevice device;
    private EventLoopHandler handler;

    public JFrame getFrame() {
        return frame;
    }

    public GetConnected getGetConnected() {
        return getConnected;
    }


    long start() {
        return System.currentTimeMillis();
    }

    long stop(String s, long tm) {
        log.trace(s + " took " + (System.currentTimeMillis() - tm) + "ms");
        return start();
    }
    private static final void setString(JProgressBar progressBar, String s, int value) {
        if(progressBar != null) {
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
            if (getConnected != null) {
                setString(progressBar, "Ask the device to disconnect from the ICE", 30);
                getConnected.disconnect();
                tm = stop("getConnected.disconnect", tm);
                setString(progressBar, "Shut down the connection request client", 55);
                getConnected.shutdown();
                stop("getConnected.shutdown", tm);
                getConnected = null;
            }
            tm = start();
            if (device != null) {
                setString(progressBar, "Shut down the device", 80);
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
        log.trace("Starting DeviceAdapter with type="+type);
        if (null != address && address.contains(":")) {
            SerialProviderFactory.setDefaultProvider(new TCPSerialProvider());
            log.info("Using the TCPSerialProvider, be sure you provided a host:port target");
        }
        if(null == eventLoop) {
            eventLoop = new EventLoop();
            handler = new EventLoopHandler(eventLoop);
        } else {
            handler = null;
        }

        device = DeviceFactory.buildDevice(type, domainId, eventLoop);

        if (gui) {
            final CompositeDevicePanel cdp = new CompositeDevicePanel();
            final DeviceMonitor deviceMonitor = new DeviceMonitor(device.getDeviceIdentity().universal_device_identifier);
            deviceMonitor.addListener(cdp);
            deviceMonitor.start(device.getParticipant(), eventLoop);

            frame = new DemoFrame("ICE Device Adapter - " + type);
            frame.setIconImage(ImageIO.read(DeviceAdapter.class.getResource("icon.png")));
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    // On the AWT EventQueue
                    final JProgressBar progressBar = new JProgressBar();
//                    progressBar.setMaximumSize(new Dimension(300, 100));
//                    progressBar.setPreferredSize(new Dimension(300, 100));
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
                    killAdapter();
                }
            }));
        }

        if (null == address) {
            getConnected = new GetConnected(frame, domainId, device.getDeviceIdentity().universal_device_identifier,
                    eventLoop);
        } else {
            getConnected = new GetConnectedToFixedAddress(frame, domainId,
                    device.getDeviceIdentity().universal_device_identifier, address, eventLoop);
        }

        getConnected.connect();


        // Wait until killAdapter, then report on any threads that didn't come down successfully
        synchronized (this) {
            while (!interrupted) {
                wait();
            }
        }


        if(gui) {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    awtThread = Thread.currentThread();
                }
            });
            frame.setVisible(false);
            frame.dispose();
//            awtThread.join(5000L);
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
    private Thread awtThread;
    private boolean interrupted = false;
}
