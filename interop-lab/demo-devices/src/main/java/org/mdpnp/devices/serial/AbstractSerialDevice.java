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
package org.mdpnp.devices.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.connected.AbstractConnectedDevice;
import org.mdpnp.devices.connected.TimeAwareInputStream;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

public abstract class AbstractSerialDevice extends AbstractConnectedDevice {
    protected abstract void doInitCommands(int idx) throws IOException;

    protected void reportConnected(String transitionNote) {
        reportConnected(0, transitionNote);
    }

    protected void reportConnected(long timeStamp, String transitionNote) {
        reportConnected(0, transitionNote);
    }
    
    protected void reportConnected(int idx, String transitionNote) {
        // Once we transition the watchdog will be watching but we don't want to
        // count elapsed
        // silence from prior to connection
        TimeAwareInputStream tais = this.timeAwareInputStream[idx];
        if (null != tais) {
            tais.promoteLastReadTime();
        }
        // TODO Come back to this for multiple serial ports
        if(idx == 0) {
            synchronized (stateMachine) {
                deviceConnectivity.comPort=portIdentifier[idx];
                if (!ice.ConnectionState.Connected.equals(stateMachine.getState())) {
                    if (!stateMachine.transitionIfLegal(ice.ConnectionState.Connected, transitionNote)) {
                        log.warn("Unable to enter Connected state from " + stateMachine.getState());
                    }
                }
            }
        } else {
            log.trace("connection("+idx+") reported connected but is not the control connection");
        }

    }

    protected abstract void process(int idx, InputStream inputStream, OutputStream outputStream) throws IOException;

    protected SerialSocket[] socket;
    protected TimeAwareInputStream[] timeAwareInputStream;
    protected Throwable[] lastError;

    protected SerialProvider[] serialProvider;

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

    public AbstractSerialDevice(final Subscriber subscriber, final Publisher publisher, final EventLoop eventLoop) {
        this(subscriber, publisher, eventLoop, 1);
    }
    
    public AbstractSerialDevice(final Subscriber subscriber, final Publisher publisher, final EventLoop eventLoop, final int countSerialPorts) {
        super(subscriber, publisher, eventLoop);
        
        this.serialProvider = new SerialProvider[countSerialPorts];
        this.lastError = new Throwable[countSerialPorts];
        this.socket = new SerialSocket[countSerialPorts];
        this.timeAwareInputStream = new TimeAwareInputStream[countSerialPorts];
        this.currentThread = new Thread[countSerialPorts];
        this.portIdentifier = new String[countSerialPorts];
        this.previousAttempt = new long[countSerialPorts];
        this.lastIssueInitCommands = new long[countSerialPorts];
        
        Set<String> serialPorts = new HashSet<String>();
        for(int idx = 0; idx < countSerialPorts; idx++) {
            if (getMaximumQuietTime(idx) <= 0L) {
                throw new RuntimeException("A positive maximumQuietTime("+idx+") is required");
            }
    
            if (getMaximumQuietTime(idx) < 100L || 0L != getMaximumQuietTime(idx) % 100L) {
                log.warn("Watchdog interrupts at 10Hz, consider a different getMaximumQuietTime("+idx+")");
            }
            
            // Hard to imagine this varying by provider but just in case...
            serialPorts.addAll(getSerialProvider(idx).getPortNames());
        }
        
        deviceConnectivity.valid_targets.userData.addAll(serialPorts);
    }

    public void setSerialProvider(int idx, SerialProvider serialProvider) {
        this.serialProvider[idx] = serialProvider;
    }
    
    public SerialProvider getSerialProvider(int idx) {
        if (null == serialProvider[idx]) {
            this.serialProvider[idx] = SerialProviderFactory.getDefaultProvider();
        }
        return serialProvider[idx];
    }

    protected void setLastError(Throwable lastError) {
        setLastError(0, lastError);
    }
    
    protected void setLastError(int idx, Throwable lastError) {
        log.error("setLastError("+idx+")", lastError);
        this.lastError[idx] = lastError;
    }

    public Throwable getLastError() {
        return getLastError(0);
    }
    
    public Throwable getLastError(int idx) {
        return lastError[idx];
    }

    @Override
    public void disconnect() {
        boolean shouldCancel = false;
        boolean shouldClose = false;

        log.trace("disconnect requested");
        synchronized (stateMachine) {
            ice.ConnectionState state = getState();
            if (ice.ConnectionState.Terminal.equals(state)) {
                log.trace("nothing to do getState()=" + state);
            } else if (ice.ConnectionState.Connecting.equals(state)) {
                log.trace("getState()=" + state + " entering Terminal");
                stateMachine.transitionIfLegal(ice.ConnectionState.Terminal, "disconnect requested from Connecting state");
                shouldCancel = true;
            } else if (ice.ConnectionState.Connected.equals(state) || ice.ConnectionState.Negotiating.equals(state)) {
                log.trace("getState()=" + state + " entering Terminal");
                stateMachine.transitionIfLegal(ice.ConnectionState.Terminal, "disconnect requested from Connected or Negotiating states");
                shouldClose = true;
            }
        }
        if (shouldCancel) {
            for(int idx = 0; idx < serialProvider.length; idx++) {
                serialProvider[idx].cancelConnect();
                log.trace("canceled connecting("+idx+")");
            }
        }
        if (shouldClose) {
            log.trace("closing the AbstractSerialDevice");
            close();
        }
    }

    private void close() {
        for(int idx = 0; idx < this.socket.length; idx++) {
            SerialSocket socket = this.socket[idx];
            if (null != socket) {
                close(socket);
            }
        }
    }

    private void close(SerialSocket socket) {
        log.trace("close");

        if (socket != null) {
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

    private final Thread[] currentThread;

    private final String[] portIdentifier;

    private final ThreadGroup threadGroup = new ThreadGroup("AbstractSerialDevice group") {
        public void uncaughtException(Thread t, Throwable e) {
            log.error("Unexpected in thread " + t.getId() + ":" + t.getName(), e);
        };
    };

    
    /**
     * Connect to the specified address.  If the connection is lost attempts will be made
     * to re-establish it until disconnect() is called.
     */
    @Override
    public boolean connect(String address) {
        String[] commaSeparated = address.split(",");
        int countSerialPorts = Math.min(commaSeparated.length, serialProvider.length);
        synchronized(this) {
            ice.ConnectionState state = getState();
            for(int idx = 0; idx < countSerialPorts; idx++) {
                this.portIdentifier[idx] = commaSeparated[idx];
                log.trace("connect("+idx+") requested to " + portIdentifier[idx]);
                
                // TODO Unroll this case; i'm in a hurry at the moment
                if(idx == 0) {
                    if(ice.ConnectionState.Terminal.equals(state)) {
                        log.warn("connect("+address+") called in Terminal state");
                        return false;
                    }
                    if(ice.ConnectionState.Initial.equals(state)) {
                        stateMachine.transitionWhenLegal(ice.ConnectionState.Connecting, "connect requested from Disconnected or Disconnecting states");
                        connect(idx);
                    } else {
                        log.warn("will not connect("+address+") where state="+state);
                    }
                } else {
                    connect(idx);
                }
            }            
        }
        executor.scheduleAtFixedRate(new Watchdog(), 0L, 100L, TimeUnit.MILLISECONDS);

        return true;
    }
    
    protected void connect(int idx) {
        currentThread[idx] = new Thread(threadGroup, new SerialDevice(idx), "AbstractSerialDevice("+idx+") Processing");
        currentThread[idx].setDaemon(true);
        currentThread[idx].start();
    }

    protected final long [] previousAttempt;
    
    private class SerialDevice implements Runnable {
        private final int idx;
        public SerialDevice(final int idx) {
            this.idx = idx;
        }
        
        public void run() {
            log.info(Thread.currentThread().getName() + " (" + Thread.currentThread().getId() + ") begins");
    
            SerialSocket socket = null;
    
            long now = System.currentTimeMillis();
    
            // Staying in the Connecting state while awaiting another time interval
            while (now < (previousAttempt[idx] + getConnectInterval(idx))) {
                if(idx == 0) {
                    setConnectionInfo("Waiting to reconnect... " + ((previousAttempt[idx] + getConnectInterval(idx)) - now) + "ms");
                }
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    log.error("", e);
                }
                now = System.currentTimeMillis();
            }
            if(idx == 0) {
                setConnectionInfo("");
            }
            previousAttempt[idx] = now;
            try {
                log.trace("Invoking SerialProvider("+idx+").connect(" + portIdentifier[idx] + ")");
                socket = getSerialProvider(idx).connect(portIdentifier[idx], 1000L);
    
                if (null == socket) {
                    log.trace("socket is null after connect");
                    return;
                } else {
                    // TODO using connection 0 as the control connection
                    if(idx == 0) {
                        synchronized (stateMachine) {
                            if (ice.ConnectionState.Connecting.equals(stateMachine.getState())) {
                                if (!stateMachine.transitionIfLegal(ice.ConnectionState.Negotiating, "serial port opened")) {
                                    throw new IllegalStateException("Cannot begin negotiating from " + getState());
                                }
                            } else {
                                // Something happened, perhaps the connect request was
                                // cancelled?
                                log.debug("Aborting connection processing because no longer in the Connecting state");
                                return;
                            }
                        }
                    }

    
                }
                AbstractSerialDevice.this.socket[idx] = socket;
    
                process(idx, timeAwareInputStream[idx] = new TimeAwareInputStream(socket.getInputStream()), socket.getOutputStream());
            } catch (IOException e) {
                // Let this thread die, it will be replaced
                log.error("processing thread ends with IOException", e);
            } finally {
                log.info(Thread.currentThread().getName() + " (" + Thread.currentThread().getId() + ")  ends");
                ice.ConnectionState priorState = getState();
                close(socket);
                AbstractSerialDevice.this.socket[idx] = null;
                AbstractSerialDevice.this.timeAwareInputStream[idx] = null;
    
                if(idx == 0) {
                    stateMachine.transitionIfLegal(ice.ConnectionState.Connecting, "serial port reached EOF, reconnecting...");
                    log.trace("process thread died unexpectedly, trying to reconnect");
                    AbstractSerialDevice.this.connect(idx);
                } else {
                    AbstractSerialDevice.this.connect(idx);
                }
            }
    
        }
    }
    protected final long[] lastIssueInitCommands;

    protected void watchdog() {

        synchronized (stateMachine) {
            ice.ConnectionState state = getState();
            if (ice.ConnectionState.Connected.equals(state)) {
                for(int idx = 0; idx < this.timeAwareInputStream.length; idx++) {
                    TimeAwareInputStream tais = AbstractSerialDevice.this.timeAwareInputStream[idx];
                    if (null != tais) {
                        long quietTime = System.currentTimeMillis() - AbstractSerialDevice.this.timeAwareInputStream[idx].getLastReadTime();
                        if (quietTime > getMaximumQuietTime(idx)) {
    
                            log.warn("WATCHDOG("+idx+") - back to Negotiating after " + quietTime + "ms quiet time (exceeds " + getMaximumQuietTime(idx) + ")");
                            if (!stateMachine.transitionIfLegal(ice.ConnectionState.Negotiating, "watchdog("+idx+") "+quietTime + "ms quiet time (exceeds " + getMaximumQuietTime(idx) + ")")) {
                                log.warn("WATCHDOG("+idx+") - unable to move from Connecting to Negotiating state (due to silence on the line)");
                            }
                        }
                        // Rely upon the inheritor to determine when to successfully
                        // move into the Connected state
                    }
                }
            }
        }
        // Separate so we can immediately re-issue connect commands
        synchronized (stateMachine) {
            ice.ConnectionState state = getState();
            if (ice.ConnectionState.Negotiating.equals(state)) {
                for(int idx = 0; idx < AbstractSerialDevice.this.socket.length; idx++) {
                    if (System.currentTimeMillis() >= (lastIssueInitCommands[idx] + getNegotiateInterval(idx))) {
                        log.trace("invoking doInitCommands("+idx+")");
                        lastIssueInitCommands[idx] = System.currentTimeMillis();
                        SerialSocket socket = AbstractSerialDevice.this.socket[idx];
                        if (null != socket) {
                            try {
                                
                                doInitCommands(idx);
                            } catch (IOException e) {
                                setLastError(idx, e);
                            }
                        } else {
                            log.warn("Cannot issue doInitCommands("+idx+") on a null socket");
                        }
                    }
                }
            }
        }
    }

    protected long getMaximumQuietTime(int idx) {
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
     * 
     * @return
     */
    protected long getConnectInterval(int idx) {
        return 20000L;
    }

    /**
     * milliseconds between doInitCommands whilst in the Negotiating state
     * 
     * @return
     */
    protected long getNegotiateInterval(int idx) {
        return 10000L;
    }

    protected String getPortIdentifier() {
        return portIdentifier[0];
    }
}
