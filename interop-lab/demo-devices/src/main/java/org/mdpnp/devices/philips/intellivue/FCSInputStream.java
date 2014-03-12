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
package org.mdpnp.devices.philips.intellivue;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jeff Plourde
 *
 */
public class FCSInputStream extends java.io.FilterInputStream {

    protected FCSInputStream(InputStream in) {
        super(in);
    }

    private int fcs = FCSOutputStream.INITIAL_FCS_VALUE;

    public void resetFCS() {
        fcs = FCSOutputStream.INITIAL_FCS_VALUE;
    }

    public int currentFCS() {
        return fcs;
    }

    @Override
    public int read() throws IOException {
        int b = in.read();
        if (b >= 0) {
            fcs = FCSOutputStream.pppfcs(fcs, (byte) b);
        }
        return b;
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    };

    public int read(byte[] b, int off, int len) throws IOException {
        int n = in.read(b, off, len);
        if (n >= 0) {
            fcs = FCSOutputStream.pppfcs(fcs, b, off, n);
        }
        return n;
    };
}
