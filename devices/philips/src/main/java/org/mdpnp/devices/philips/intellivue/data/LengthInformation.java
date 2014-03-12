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

import org.mdpnp.devices.io.util.HexUtil;
import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;

/**
 * @author Jeff Plourde
 *
 */
public class LengthInformation implements Parseable, Formatable {
    private int length;

    private final byte[] prefix;

    public LengthInformation() {
        this(new byte[0]);
    }

    public LengthInformation(byte[] prefix) {
        this.prefix = prefix;
    }

    @Override
    public void parse(ByteBuffer bb) {
        if (HexUtil.startsWith(bb, prefix)) {
            bb.position(bb.position() + (null == prefix ? 0 : prefix.length));
        } else {
            length = 0;
            // return;
        }

        short first = (short) (0x00FF & bb.get());
        if (0xFF == first) {
            length = 0xFFFF & bb.getShort();
        } else {
            length = first;
        }
    }

    public int getLength() {
        return length;
    }

    @Override
    public void format(ByteBuffer bb) {
        if (length > 0) {
            bb.put(prefix);
            if (length < 255) {
                bb.put((byte) (0xFF & length));
            } else {
                bb.put((byte) 0xFF);
                bb.putShort((short) (0xFFFF & length));
            }
        }
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getByteCount() {
        if (length > 0) {
            if (length < 255) {
                return 1 + prefix.length;
            } else {
                return 3 + prefix.length;
            }
        } else {
            return 0;
        }
    }

    @Override
    public java.lang.String toString() {
        return Integer.toString(length);
    }

}
