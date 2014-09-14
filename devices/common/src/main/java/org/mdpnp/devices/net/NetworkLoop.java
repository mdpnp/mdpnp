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
package org.mdpnp.devices.net;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class NetworkLoop implements Runnable {
    public enum LoopState {
        /**
         * No thread has invoked runLoop
         */
        New,
        /**
         * Running state, processing continues
         */
        Resumed,
        /**
         * The interrupt has concluded, processing may continue
         */
        Resuming,
        /**
         * Processing has been successfully interrupted
         */
        Paused,
        /**
         * Interrupt of processing is requested
         */
        Pausing,
        /**
         * Termination of runLoop has been requested
         */
        Terminating,
        /**
         * runLoop has terminated
         */
        Terminated
    }

    private final Selector select;
    private final TaskQueue taskQueue = new TaskQueue();
    private LoopState loopState = LoopState.New;

    private Thread myThread;

    private static final Logger log = LoggerFactory.getLogger(NetworkLoop.class);

    public NetworkLoop() throws IOException {
        select = Selector.open();
    }

    public NetworkLoop(Selector select) {
        this.select = select;
    }

    private synchronized void pause(String action) {
        long start = System.currentTimeMillis();
        if (!Thread.currentThread().equals(myThread)) {
            while (!LoopState.Paused.equals(loopState)) {
                long now = System.currentTimeMillis();
                switch (loopState) {
                case New:
                    if (now >= (start + 5000L)) {
                        throw new IllegalStateException("RunLoop not started after five seconds, unable to register connection");
                    }
                    break;
                case Terminating:
                case Terminated:
                    throw new IllegalStateException("Cannot " + action + "; runLoop is " + loopState);
                case Resuming:
                case Pausing:
                case Paused:
                    break;
                case Resumed:
                    loopState = LoopState.Pausing;
                    select.wakeup();
                    break;
                }

                try {
                    this.wait(250L);
                } catch (InterruptedException e) {
                    log.error("Interrupted", e);
                }
            }
        }
    }

    private synchronized void resume() {
        loopState = LoopState.Resuming;
        this.notifyAll();
    }

    public SelectionKey register(NetworkConnection conn, SelectableChannel channel) throws ClosedChannelException {
        SelectionKey key = null;
        synchronized (this) {
            if (LoopState.New.equals(loopState)) {
                key = channel.register(select, SelectionKey.OP_READ, conn);
            }
        }
        if (null != key) {
            conn.registered(this, key);
            return key;
        } else {
            pause("register a new connection");
            key = channel.register(select, SelectionKey.OP_READ, conn);
            conn.registered(this, key);
            resume();
            return key;
        }
    }

    public void unregister(SelectionKey key, NetworkConnection conn) {
        boolean canceled = false;
        synchronized (this) {
            if (LoopState.New.equals(loopState)) {
                key.cancel();
                canceled = true;
            }
        }
        if (canceled) {
            conn.unregistered(this, key);
        } else {
            pause("unregister a connection");
            key.cancel();
            conn.unregistered(this, key);
            resume();
        }
    }

    private int select(long time) throws IOException {
        if (time < 0L) {
            return select.selectNow();
        } else if (time == 0L) {
            return select.select();
        } else {
            return select.select(time);
        }
    }

    public void runLoop() {
        synchronized (this) {
            if (!LoopState.New.equals(loopState)) {
                throw new IllegalStateException("runLoop has already been called, loopState=" + loopState);
            } else if (null != myThread) {
                throw new IllegalStateException("Do not invoke the runLoop from multiple threads");
            } else {
                myThread = Thread.currentThread();
                loopState = LoopState.Resumed;
                this.notifyAll();
            }
        }

        try {
            while (true) {
                synchronized (this) {
                    while (!LoopState.Resumed.equals(loopState)) {
                        switch (loopState) {
                        case New:
                        case Terminated:
                            throw new IllegalStateException();
                        case Terminating:
                            log.info("runLoop in Terminating state; runLoop will end");
                            return;
                        case Pausing:
                            loopState = LoopState.Paused;
                            this.notifyAll();
                            break;
                        case Resuming:
                            loopState = LoopState.Resumed;
                            this.notifyAll();
                            continue;
                        case Paused:
                        case Resumed:
                        }
                        try {
                            this.wait();
                        } catch (InterruptedException e) {
                            log.error("Interrupted", e);
                        }
                    }
                }

                try {
                    select(taskQueue.doExpiredTasks(System.currentTimeMillis()));
                } catch (IOException e) {
                    log.error("in select", e);
                    break;
                }

                java.util.Set<SelectionKey> keys = select.selectedKeys();
                for (SelectionKey sk : keys) {
                    if (sk.isReadable()) {
                        NetworkConnection nc = (NetworkConnection) sk.attachment();
                        try {
                            nc.read(sk);
                        } catch (IOException e) {
                            log.error("in NetworkConnection.read, canceling the SelectionKey", e);
                            sk.cancel();
                            continue;
                        } catch (Throwable t) {
                            log.error("in NetworkConnection.read, canceling the SelectionKey", t);
                            sk.cancel();
                            continue;
                        }
                    }

                    if (sk.isValid() && sk.isWritable()) {
                        NetworkConnection nc = (NetworkConnection) sk.attachment();
                        try {
                            nc.write(sk);
                        } catch (IOException e) {
                            log.error("NetworkConnection.write, canceling SelectionKey", e);
                            sk.cancel();
                            continue;
                        } catch(Throwable t) {
                            log.error("in NetworkConnection.write, canceling the SelectionKey", t);
                            sk.cancel();
                            continue;
                        }
                    }
                    // if(sk.isAcceptable()) {
                    // NetworkConnection nc = (NetworkConnection)
                    // sk.attachment();
                    // try {
                    // nc.accept(sk);
                    // } catch (IOException e) {
                    // e.printStackTrace();
                    // sk.cancel();
                    // }
                    // }
                }
            }
        } catch(Throwable t) {
            log.error("NetworkLoop.runLoop exiting on uncaught Throwable; this should not happen", t);
        } finally {
            synchronized (this) {
                myThread = null;
                loopState = LoopState.Terminated;
                this.notifyAll();
            }
        }
    }

    public synchronized void cancelThread() {
        while (!LoopState.Resumed.equals(loopState)) {
            switch (loopState) {
            case New:
                throw new IllegalStateException("runLoop has not been started");
            case Paused:
            case Pausing:
            case Resuming:
            case Resumed:
                break;
            case Terminating:
            case Terminated:
                return;
            }

            try {
                this.wait();
            } catch (InterruptedException e) {
                log.error("Interrupted", e);
            }
        }
        this.loopState = LoopState.Terminating;
        select.wakeup();
    }

    public void cancelThreadAndWait() {
        cancelThread();
        synchronized (this) {
            while (null != myThread) {
                try {
                    this.wait();
                } catch (InterruptedException e) {

                }
            }
        }
    }

    public void add(TaskQueue.Task<?> task) {
        taskQueue.add(task);
        select.wakeup();
    }

    public void clearTasks() {
        taskQueue.clear();
        select.wakeup();
    }

    public void wakeup() {
        select.wakeup();
    }

    @Override
    public void run() {
        runLoop();
    }
}
