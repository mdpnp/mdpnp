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

/**
 * @author Jeff Plourde
 *
 */
public class SampleArraySpecification implements Value {
    private int arraySize = 8;
    private short sampleSize, significantBits;
    private int flags;

    public SampleArraySpecification clone() {
        SampleArraySpecification dup = new SampleArraySpecification();
        dup.arraySize = this.arraySize;
        dup.sampleSize = this.sampleSize;
        dup.significantBits = this.significantBits;
        dup.flags = this.flags;
        return dup;
    }
    
    @Override
    public void format(ByteBuffer bb) {
        Bits.putUnsignedShort(bb, arraySize);
        Bits.putUnsignedByte(bb, sampleSize);
        Bits.putUnsignedByte(bb, significantBits);
        Bits.putUnsignedShort(bb, flags);
    }

    @Override
    public void parse(ByteBuffer bb) {
        arraySize = Bits.getUnsignedShort(bb);
        sampleSize = Bits.getUnsignedByte(bb);
        significantBits = Bits.getUnsignedByte(bb);
        flags = Bits.getUnsignedShort(bb);
    }

    private static final int SMOOTH_CURVE = 0x8000;

    public boolean isSmoothCurve() {
        return 0 != (SMOOTH_CURVE & flags);
    }

    private static final int DELAYED_CURVE = 0x4000;

    public boolean isDelayedCurve() {
        return 0 != (DELAYED_CURVE & flags);
    }

    private static final int STATIC_SCALE = 0x2000;

    public boolean isStaticScale() {
        return 0 != (STATIC_SCALE & flags);
    }

    private static final int SA_EXT_VAL_RANGE = 0x1000;

    public boolean isExtendedValueRange() {
        return 0 != (SA_EXT_VAL_RANGE & flags);
    }

    @Override
    public java.lang.String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[arraySize=").append(arraySize);
        sb.append(",sampleSize=").append(sampleSize);
        sb.append(",significantBits=").append(significantBits);
        sb.append(",flags=");
        if (isSmoothCurve()) {
            sb.append("SMOOTH_CURVE ");
        }
        if (isDelayedCurve()) {
            sb.append("DELAYED_CURVE ");
        }
        if (isStaticScale()) {
            sb.append("STATIC SCALE ");
        }
        if (isExtendedValueRange()) {
            sb.append("SA_EXT_VAL_RANGE ");
        }

        sb.append("]");
        return sb.toString();
    }

    public int getArraySize() {
        return arraySize;
    }

    public void setArraySize(int arraySize) {
        this.arraySize = arraySize;
    }

    public void setSampleSize(short sampleSize) {
        this.sampleSize = sampleSize;
    }

    public void setSignificantBits(short significantBits) {
        this.significantBits = significantBits;
    }

    public short getSampleSize() {
        return sampleSize;
    }

    public short getSignificantBits() {
        return significantBits;
    }
}
