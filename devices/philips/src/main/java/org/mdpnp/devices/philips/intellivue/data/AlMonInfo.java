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

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;

public abstract class AlMonInfo implements Parseable, Formatable {
    private int al_inst_no;
    private final TextId al_text = new TextId();
    private int priority;
    private final AlertFlags flags = new AlertFlags();

    @Override
    public void format(ByteBuffer bb) {
        Bits.putUnsignedShort(bb, al_inst_no);
        al_text.format(bb);
        Bits.putUnsignedShort(bb, priority);
        flags.format(bb);
    }

    @Override
    public void parse(ByteBuffer bb) {
        al_inst_no = Bits.getUnsignedShort(bb);
        al_text.parse(bb);
        Bits.getUnsignedShort(bb);
        flags.parse(bb);
    }

    public int getAlInstNo() {
        return al_inst_no;
    }

    public TextId getAlText() {
        return al_text;
    }

    public int getPriority() {
        return priority;
    }

    public AlertFlags getFlags() {
        return flags;
    }

    @Override
    public java.lang.String toString() {
        return "[al_inst_no=" + al_inst_no + ",al_text=" + al_text + ",priority=" + priority + ",flags=" + flags + "]";
    }
}
