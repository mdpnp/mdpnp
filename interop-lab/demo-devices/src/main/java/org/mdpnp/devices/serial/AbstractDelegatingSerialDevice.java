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

import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDelegatingSerialDevice<T> extends AbstractSerialDevice {
    public AbstractDelegatingSerialDevice(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);
    }

    private InputStream inputStream;
    private OutputStream outputStream;
    private T delegate;

    protected synchronized void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
        notifyAll();
    }

    protected synchronized void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
        notifyAll();
    }

    private final Logger log = LoggerFactory.getLogger(AbstractDelegatingSerialDevice.class);

    protected abstract T buildDelegate(InputStream in, OutputStream out);

    protected abstract boolean delegateReceive(T delegate) throws IOException;

    @Override
    protected void doInitCommands() throws IOException {
        log.trace("doInitCommands");
    }

    protected synchronized T getDelegate() {
        return getDelegate(true);
    }

    // just a failsafe
    private static final long MAX_GET_DELEGATE_WAIT_TIME = 20000L;

    protected synchronized T getDelegate(boolean build) {
        long giveup = System.currentTimeMillis() + MAX_GET_DELEGATE_WAIT_TIME;

        while (build && null == delegate && (inputStream == null || outputStream == null)) {
            try {
                log.trace("waiting, inputStream=" + inputStream + ", outputStream=" + outputStream);
                long now = System.currentTimeMillis();
                if (now >= giveup) {
                    throw new IllegalStateException("Exceeded maximum time (" + MAX_GET_DELEGATE_WAIT_TIME
                            + "ms awaiting calls to doInitCommands and process inputStream=" + inputStream + " and outputStream=" + outputStream);
                } else {
                    wait(giveup - now);
                }
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
        }
        if (build && null == delegate) {
            delegate = buildDelegate(inputStream, outputStream);
        }
        return delegate;
    }

    @Override
    protected void process(InputStream inputStream, OutputStream outputStream) throws IOException {
        log.trace("process inputStream=" + inputStream);
        // inputStream = new TeeInputStream(inputStream, new
        // FileOutputStream("debug.data"));
        try {
            setInputStream(inputStream);
            setOutputStream(outputStream);
            final T delegate = getDelegate();
            boolean keepGoing = true;
            while (keepGoing) {
                keepGoing = delegateReceive(delegate);
            }
        } finally {
            this.inputStream = null;
            this.outputStream = null;
            this.delegate = null;
            log.trace("process ends");
        }
    }
}
