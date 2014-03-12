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
package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;

/**
 * @author Jeff Plourde
 *
 */
public class VariableLabel implements Parseable, Formatable {
    private java.lang.String string = "";

    public static final Charset ASCII = Charset.forName("ASCII");

    public static final java.lang.String decode(ByteBuffer bb, int length) {
        CharBuffer cb = CharBuffer.allocate(length);
        while (--length >= 0) {
            byte b = bb.get();
            if (b >= 32) {
                cb.put((char) b);
            }
        }
        cb.flip();
        return cb.toString();

    }

    @Override
    public void parse(ByteBuffer bb) {
        int length = Bits.getUnsignedShort(bb);
        this.string = decode(bb, length);
    }

    @Override
    public void format(ByteBuffer bb) {
        byte[] b = string.getBytes(ASCII);
        Bits.putUnsignedShort(bb, b.length);
        bb.put(b);
    }

    public java.lang.String getString() {
        return string;
    }

    public void setString(java.lang.String string) {
        this.string = string;
    }

    @Override
    public java.lang.String toString() {
        return string;
    }
}
