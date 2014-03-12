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
package org.mdpnp.devices.io.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Jeff Plourde
 *
 */
public class IOUtil {
    private IOUtil() {

    }

    public static final int readFully(InputStream is, byte[] bytes) throws IOException {
        return readFully(is, bytes, 0);
    }

    public static final int readFully(InputStream is, byte[] bytes, int len) throws IOException {
        return readFully(is, bytes, len, bytes.length);
    }

    public static final int readFully(InputStream is, byte[] bytes, int len, int totalRequired) throws IOException {
        while (len < totalRequired) {
            int b = is.read(bytes, len, totalRequired - len);
            if (b < 0) {
                throw new EOFException("Reached EOF unexpectedly");
            } else {
                len += b;
            }
        }
        return len;
    }
}
