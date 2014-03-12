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
import java.util.Map;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.OrdinalEnum;

/**
 * @author Jeff Plourde
 *
 */
public enum SimpleColor implements EnumMessage<SimpleColor>, OrdinalEnum.IntType {
    Black(0), Red(1), Green(2), Yellow(3), Blue(4), Magenta(5), Cyan(6), White(7), Pink(20), Orange(35), LightGreen(50), LightRed(65), ;

    @Override
    public void format(ByteBuffer bb) {
        Bits.putUnsignedShort(bb, asInt());
    }

    @Override
    public SimpleColor parse(ByteBuffer bb) {
        return SimpleColor.valueOf(Bits.getUnsignedShort(bb));
    }

    private final int x;

    private SimpleColor(final int x) {
        this.x = x;
    }

    private static final Map<Integer, SimpleColor> map = OrdinalEnum.buildInt(SimpleColor.class);

    public static final SimpleColor valueOf(int x) {
        return map.get(x);
    }

    public int asInt() {
        return x;
    }

}
