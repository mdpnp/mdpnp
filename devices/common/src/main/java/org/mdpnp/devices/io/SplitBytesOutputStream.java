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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Jeff Plourde
 *
 */
public class SplitBytesOutputStream extends FilterOutputStream {

    public interface Splitter {
        int split(byte b, byte[] b1);
    }

    private final Splitter splitter;
    private final byte[] buffer = new byte[8];

    public SplitBytesOutputStream(OutputStream out) {
        this(out, new Splitter() {

            @Override
            public int split(byte b, byte[] b1) {
                switch (0xFF & b) {
                case 0x80:
                    b1[0] = (byte) 0x80;
                    b1[1] = 0x00;
                    return 2;
                case 0x85:
                    b1[0] = (byte) 0x80;
                    b1[1] = (byte) 0x05;
                    return 2;
                default:
                    b1[0] = b;
                    return 1;
                }
            }

        });
    }

    public SplitBytesOutputStream(OutputStream out, Splitter splitter) {
        super(out);
        this.splitter = splitter;
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        int h = 0, i = 0;

        for (i = 0; i < len; i++) {
            int j = splitter.split(b[off + i], buffer);
            if (j > 1) {
                if (i > h) {
                    out.write(b, off + h, i - h);
                }
                out.write(buffer, 0, j);
                h = i + 1;
            }
        }
        if (i > h) {
            out.write(b, off + h, i - h);
        }
    }

    @Override
    public void write(int b) throws IOException {
        int i = splitter.split((byte) b, buffer);
        for (int h = 0; h < i; h++) {
            out.write(0xFF & buffer[h]);
        }
    }

    public void writeProtected(int b) throws IOException {
        out.write(b);
    }

}
