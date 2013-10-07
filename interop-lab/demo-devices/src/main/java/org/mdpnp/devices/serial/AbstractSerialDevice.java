/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.connected.AbstractConnectedDevice;
import org.mdpnp.devices.connected.TimeAwareInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractSerialDevice extends AbstractConnectedDevice implements Runnable {
    protected abstract void doInitCommands() throws IOException;
    protected void reportConnected() {
        // Once we transition the watchdog will be watching but we don't want to count elapsed
        // silence from prior to connection
        TimeAwareInputStream tais = this.timeAwareInputStream;
        if(null != tais) {
            tais.promoteLastReadTime();
        }
        synchronized(stateMachine) {
            if(!ice.ConnectionState.Connected.equals(stateMachine.getState())) {
                if(!stateMachine.transitionIfLegal(ice.ConnectionState.Connected)) {
                    log.warn("Unable to enter Connected state from " + stateMachine.getState());
                }
            }
        }

    }
    protected abstract void process(InputStream inputStream, OutputStream outputStream) throws IOException;

    protected SerialSocket socket;
    protected TimeAwareInputStream timeAwareInputStream;
    protected Throwable lastError;

    protected SerialProvider serialProvider;

    private static final Logger log = LoggerFactory.getLogger(AbstractSerialDevice.class);

    private class Watchdog implements Runnable {

        @Override
        public void run() {
            try {
                watchdog();
            } catch (Throwable t) {
                log.warn("Something wicked happened in the watchdog thread", t);
            }
        }

    }

    public AbstractSerialDevice(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);

        if(getMaximumQuietTime() <= 0L) {
            throw new RuntimeException("A positive maximumQuietTime is required");
        }

        if(getMaximumQuietTime() < 100L || 0L != getMaximumQuietTime() % 100L) {
            log.warn("Watchdog interrupts at 10Hz, consider a different getMaximumQuietTime()");
        }

        executor.scheduleAtFixedRate(new Watchdog(), 0L, 100L, TimeUnit.MILLISECONDS);

        deviceConnectivity.valid_targets.addAll(getSerialProvider().getPortNames());
    }

    public AbstractSerialDevice(int domainId, EventLoop eventLoop, SerialSocket sock) {
        super(domainId, eventLoop);

        if(getMaximumQuietTime() <= 0L) {
            throw new RuntimeException("A positive maximumQuietTime is required");
        }

        if(getMaximumQuietTime() < 100L || 0L != getMaximumQuietTime() % 100L) {
            log.warn("Watchdog interrupts at 10Hz, consider a different getMaximumQuietTime()");
        }

        executor.scheduleAtFixedRate(new Watchdog(), 0L, 100L, TimeUnit.MILLISECONDS);
        if(null != sock) {
            this.portIdentifier = sock.getPortIdentifier();
        }
    }

    public void setSerialProvider(SerialProvider serialProvider) {
        this.serialProvider = serialProvider;
    }

    public SerialProvider getSerialProvider() {
        if(null == serialProvider) {
            this.serialProvider = SerialProviderFactory.getDefaultProvider();
        }
        return serialProvider;
    }

    protected void setLastError(Throwable lastError) {
        log.error("setLastError", lastError);
        this.lastError = lastError;
    }

    public Throwable getLastError() {
        return lastError;
    }

    @Override
    public void disconnect() {
        boolean shouldCancel = false;
        boolean shouldClose = false;

        log.trace("disconnect requested");
        synchronized(stateMachine) {
            ice.ConnectionState state = getState();
            if(ice.ConnectionState.Disconnected.equals(state) ||
               ice.ConnectionState.Disconnecting.equals(state)) {
                log.trace("nothing to do getState()="+state);
            } else if(ice.ConnectionState.Connecting.equals(state)) {
                log.trace("getState()="+state+" entering Disconnecting");
                stateMachine.transitionIfLegal(ice.ConnectionState.Disconnecting);
                shouldCancel = true;
            } else if(ice.ConnectionState.Connected.equals(state) ||
                      ice.ConnectionState.Negotiating.equals(state)) {
                log.trace("getState()="+state+" entering Disconnecting");
                stateMachine.transitionIfLegal(ice.ConnectionState.Disconnecting);
                shouldClose = true;
            }
        }
        if(shouldCancel) {
            serialProvider.cancelConnect();
            log.trace("canceled connecting");
        }
        if(shouldClose) {
            log.trace("closing the AbstractSerialDevice");
            close();
        }
    }

    private void close() {
        SerialSocket socket = this.socket;
        if(null != socket) {
            close(socket);
        }
    }

    private void close(SerialSocket socket) {
        log.trace("close");

        if(socket != null) {
            try {
                log.trace("attempting to close socket");
                socket.close();
                log.trace("close - socket closed without error");
            } catch (IOException e) {
                setLastError(e);
            }
        } else {
            log.debug("close - socket was already null");
        }
    }
    private Thread currentThread;

    private String portIdentifier;

    private final ThreadGroup threadGroup = new ThreadGroup("AbstractSerialDevice group") {
        public void uncaughtException(Thread t, Throwable e) {
            log.error("Unexpected in thread " + t.getId() + ":"+ t.getName(), e);
        };
    };

    @Override
    public void connect(String portIdentifier) {
        log.trace("connect requested to " + portIdentifier);
        synchronized(this) {
            this.portIdentifier = portIdentifier;
            ice.ConnectionState state = getState();
            if(ice.ConnectionState.Connected.equals(state) ||
               ice.ConnectionState.Negotiating.equals(state) ||
               ice.ConnectionState.Connecting.equals(state)) {
            } else if(ice.ConnectionState.Disconnected.equals(state) ||
                      ice.ConnectionState.Disconnecting.equals(state)) {
                stateMachine.transitionWhenLegal(ice.ConnectionState.Connecting);

                currentThread = new Thread(threadGroup, this, "AbstractSerialDevice Processing");
                currentThread.setDaemon(true);
                currentThread.start();
            }

        }
    }

    private long previousAttempt = 0L;

    public void run() {
        log.info(Thread.currentThread().getName() + " (" + Thread.currentThread().getId() + ") begins");

        SerialSocket socket = null;

        long now = System.currentTimeMillis();

        // Staying in the Connecting state while awaiting another time interval
        while(now < (previousAttempt+getConnectInterval())) {
            setConnectionInfo("Waiting to reconnect... " + ((previousAttempt+getConnectInterval()) - now) + "ms");
            try {
                Thread.sleep(100L);
            } catch (InterruptedException e) {
                log.error("", e);
            }
            now = System.currentTimeMillis();
        }
        setConnectionInfo("");
        previousAttempt = now;
        try {
            log.trace("Invoking SerialProvider.connect("+portIdentifier+")");
            socket = getSerialProvider().connect(portIdentifier, Long.MAX_VALUE);

            if(null == socket) {
                log.trace("socket is null after connect");
                return;
            } else {
                synchronized(stateMachine) {
                    if(ice.ConnectionState.Connecting.equals(stateMachine.getState())) {
                        if(!stateMachine.transitionIfLegal(ice.ConnectionState.Negotiating)) {
                            throw new IllegalStateException("Cannot begin negotiating from " + getState());
                        }
                    } else {
                        // Something happened, perhaps the connect request was cancelled?
                        log.debug("Aborting connection processing because no longer in the Connecting state");
                        return;
                    }
                }

            }
            this.socket = socket;

            process(timeAwareInputStream = new TimeAwareInputStream(socket.getInputStream()), socket.getOutputStream());
        } catch (IOException e) {
            // Let this thread die, it will be replaced
            log.error("processing thread ends with IOException", e);
        } finally {
            log.info(Thread.currentThread().getName() + " (" + Thread.currentThread().getId() + ")  ends");
            ice.ConnectionState priorState = getState();
            close(socket);
            this.socket = null;
            this.timeAwareInputStream = null;

            stateMachine.transitionIfLegal(ice.ConnectionState.Disconnected);
            if(ice.ConnectionState.Connecting.equals(priorState) ||
               ice.ConnectionState.Connected.equals(priorState) ||
               ice.ConnectionState.Negotiating.equals(priorState)) {
                log.trace("process thread died unexpectedly, trying to reconnect");
                connect(portIdentifier);
            }
        }


    }
    protected long lastIssueInitCommands;

    protected void watchdog() {

        synchronized(stateMachine) {
            ice.ConnectionState state = getState();
            if(ice.ConnectionState.Connected.equals(state)) {
                TimeAwareInputStream tais = this.timeAwareInputStream;
                if(null != tais) {
                    long quietTime = System.currentTimeMillis() - timeAwareInputStream.getLastReadTime();
                    if(quietTime > getMaximumQuietTime()) {


                        log.warn("WATCHDOG - back to Negotiating after " + quietTime + "ms quiet time (exceeds " + getMaximumQuietTime()+")");
                        if(!stateMachine.transitionIfLegal(ice.ConnectionState.Negotiating)) {
                            log.warn("WATCHDOG - unable to move from Connecting to Negotiating state (due to silence on the line)");
                        }
                    }
                    // Rely upon the inheritor to determine when to successfully move into the Connected state
                }
            }
        }
        // Separate so we can immediately re-issue connect commands
        synchronized(stateMachine) {
            ice.ConnectionState state = getState();
            if(ice.ConnectionState.Negotiating.equals(state)) {
                if(System.currentTimeMillis() >= (lastIssueInitCommands+getNegotiateInterval())) {
                    log.trace("invoking doInitCommands");
                    SerialSocket socket = this.socket;
                    if(null != socket) {
                        try {
                            doInitCommands();
                            lastIssueInitCommands = System.currentTimeMillis();
                        } catch (IOException e) {
                            setLastError(e);
                        }
                    }
                }
            }
        }
    }
    protected long getMaximumQuietTime() {
        return -1L;
    }



    @Override
    protected ice.ConnectionType getConnectionType() {
        return ice.ConnectionType.Serial;
    }

    @Override
    public void shutdown() {
        close();
        super.shutdown();
    }

    /**
     * milliseconds to wait between connect attempts
     * @return
     */
    protected long getConnectInterval() {
        return 20000L;
    }

    /**
     * milliseconds between doInitCommands whilst in the Negotiating state
     * @return
     */
    protected long getNegotiateInterval() {
        return 10000L;
    }
}
