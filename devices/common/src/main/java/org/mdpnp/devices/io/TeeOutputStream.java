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
package org.mdpnp.devices.io;

import java.io.IOException;
import java.io.OutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class TeeOutputStream extends java.io.FilterOutputStream {

    private final OutputStream tee;

    public TeeOutputStream(OutputStream out, OutputStream tee) {
        super(out);
        this.tee = tee;
    }

    private static final Logger log = LoggerFactory.getLogger(TeeOutputStream.class);

    @Override
    public void write(byte[] b) throws IOException {
        try {
            tee.write(b);
        } catch (Throwable t) {
            log.warn("error writing to tee", t);
        }
        out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        try {
            tee.write(b, off, len);
        } catch (Throwable t) {
            log.warn("error writing to tee", t);
        }
        out.write(b, off, len);
    }

    @Override
    public void write(int b) throws IOException {
        try {
            tee.write(b);
        } catch (Throwable t) {
            log.warn("error writing to tee", t);
        }
        out.write(b);
    }

}
