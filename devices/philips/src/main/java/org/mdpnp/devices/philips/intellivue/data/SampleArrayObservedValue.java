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
import java.util.Arrays;

import org.mdpnp.devices.io.util.Bits;

/**
 * @author Jeff Plourde
 *
 */
public class SampleArrayObservedValue implements Value {
    private OIDType physioId;
    private final MeasurementState state = new MeasurementState();
    private short[] value = new short[8];
    private int length;

    public OIDType getPhysioId() {
        return physioId;
    }

    public MeasurementState getState() {
        return state;
    }

    public short[] getValue() {
        return value;
    }

    public void setPhysioId(OIDType physioId) {
        this.physioId = physioId;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setValue(short[] value) {
        this.value = value;
    }

    @Override
    public java.lang.String toString() {
        java.lang.String pi = ObservedValue.valueOf(physioId.getType()) == null ? physioId.toString() : ObservedValue.valueOf(physioId.getType())
                .toString();
        return "[physioId=" + pi + ",state=" + state + ",length=" + length + ",value=" + Arrays.toString(value) + "]";
    }

    @Override
    public void format(ByteBuffer bb) {
        physioId.format(bb);
        state.format(bb);
        Bits.putUnsignedShort(bb, length);
        for (int i = 0; i < length; i++) {
            Bits.putUnsignedByte(bb, value[i]);
        }
    }

    @Override
    public void parse(ByteBuffer bb) {
        physioId = OIDType.parse(bb);
        state.parse(bb);
        length = Bits.getUnsignedShort(bb);
        if (value.length < length) {
            value = new short[length];
        }
        for (int i = 0; i < length; i++) {
            value[i] = Bits.getUnsignedByte(bb);
        }
    }
}
