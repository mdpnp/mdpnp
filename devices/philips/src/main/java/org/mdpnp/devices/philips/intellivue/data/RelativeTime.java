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
public class RelativeTime implements Value {
    private long relativeTime;
    private static final long RESOLUTION_MICROSECONDS = 125L;

    public RelativeTime() {
        this(60000L);
    }

    public RelativeTime(long milliseconds) {
        fromMilliseconds(milliseconds);
    }

    @Override
    public void parse(ByteBuffer bb) {
        relativeTime = Bits.getUnsignedInt(bb);
    }

    public long getRelativeTime() {
        return relativeTime;
    }

    public void setRelativeTime(long relativeTime) {
        this.relativeTime = relativeTime;
    }

    @Override
    public java.lang.String toString() {
        return Long.toString(relativeTime) + " (" + Long.toString(toMilliseconds()) + "ms)";
    }

    @Override
    public void format(ByteBuffer bb) {
        Bits.putUnsignedInt(bb, relativeTime);
    }

    public long toMicroseconds() {
        return relativeTime * RESOLUTION_MICROSECONDS;
    }

    public void fromMicroseconds(long microseconds) {
        this.relativeTime = microseconds / RESOLUTION_MICROSECONDS;
    }

    public long toSeconds() {
        return toMicroseconds() / 1000000L;
    }
    
    public long toMilliseconds() {
        return toMicroseconds() / 1000L;
    }

    public void fromMilliseconds(long milliseconds) {
        fromMicroseconds(1000L * milliseconds);
    }

}
