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
import java.lang.reflect.Array;

import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

public abstract class AbstractDelegatingSerialDevice<T> extends AbstractSerialDevice {
    public AbstractDelegatingSerialDevice(final Subscriber subscriber, final Publisher publisher, EventLoop eventLoop, Class<T> clazz) {
        this(subscriber, publisher, eventLoop, 1, clazz);
    }
    
    @SuppressWarnings("unchecked")
    public AbstractDelegatingSerialDevice(final Subscriber subscriber, final Publisher publisher, EventLoop eventLoop, final int countSerialPorts, Class<T> clazz) {
        super(subscriber, publisher, eventLoop, countSerialPorts);
        inputStream = new InputStream[countSerialPorts];
        outputStream = new OutputStream[countSerialPorts];
        delegate = (T[]) Array.newInstance(clazz, countSerialPorts);
    }

    private final InputStream[] inputStream;
    private final OutputStream[] outputStream;
    private final T[] delegate;

    protected synchronized void setOutputStream(int idx, OutputStream outputStream) {
        this.outputStream[idx] = outputStream;
        notifyAll();
    }

    protected synchronized void setInputStream(int idx, InputStream inputStream) {
        this.inputStream[idx] = inputStream;
        notifyAll();
    }

    private final Logger log = LoggerFactory.getLogger(AbstractDelegatingSerialDevice.class);

    protected abstract T buildDelegate(int idx, InputStream in, OutputStream out);

    protected abstract boolean delegateReceive(int idx, T delegate) throws IOException;

    @Override
    protected void doInitCommands(int idx) throws IOException {
        log.trace("doInitCommands("+idx+")");
    }

    protected synchronized T getDelegate() {
        return getDelegate(0, true);
    }
    
    protected synchronized T getDelegate(int idx) {
        return getDelegate(idx, true);
    }
    
    protected synchronized T getDelegate(boolean b) {
        return getDelegate(0, b);
    }

    // just a failsafe
    private static final long MAX_GET_DELEGATE_WAIT_TIME = 20000L;

    protected synchronized T getDelegate(final int idx, final boolean build) {
        long giveup = System.currentTimeMillis() + MAX_GET_DELEGATE_WAIT_TIME;

        while (build && null == delegate[idx] && (inputStream[idx] == null || outputStream[idx] == null)) {
            try {
                log.trace("waiting, inputStream("+idx+")=" + inputStream[idx] + ", outputStream("+idx+")=" + outputStream[idx]);
                long now = System.currentTimeMillis();
                if (now >= giveup) {
                    throw new IllegalStateException("Exceeded maximum time (" + MAX_GET_DELEGATE_WAIT_TIME
                            + "ms awaiting calls to doInitCommands and process inputStream=" + inputStream[idx] + " and outputStream=" + outputStream[idx]);
                } else {
                    wait(giveup - now);
                }
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
        if (build && null == delegate[idx]) {
            delegate[idx] = buildDelegate(idx, inputStream[idx], outputStream[idx]);
        }
        return delegate[idx];
    }

    @Override
    protected void process(final int idx, final InputStream inputStream, final OutputStream outputStream) throws IOException {
        log.trace("process("+idx+") inputStream=" + inputStream);
        // inputStream = new TeeInputStream(inputStream, new
        // FileOutputStream("debug.data"));
        try {
            setInputStream(idx, inputStream);
            setOutputStream(idx, outputStream);
            final T delegate = getDelegate(idx);
            boolean keepGoing = true;
            while (keepGoing) {
                keepGoing = delegateReceive(idx, delegate);
            }
        } finally {
            this.inputStream[idx] = null;
            this.outputStream[idx] = null;
            this.delegate[idx] = null;
            log.trace("process("+idx+") ends");
        }
    }
}
