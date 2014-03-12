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
public class OperatingMode implements Value {
    private int bitfield;

    private static final int OPMODE_UNSPEC = 0x8000;
    private static final int MONITORING = 0x4000;
    private static final int DEMO = 0x2000;
    private static final int SERVICE = 0x1000;
    private static final int OPMODE_STANDBY = 0x0002;
    private static final int CONFIG = 0x0001;

    public boolean isUnspecified() {
        return 0 != (OPMODE_UNSPEC & bitfield);
    }

    public boolean isMonitoring() {
        return 0 != (MONITORING & bitfield);
    }

    public boolean isDemo() {
        return 0 != (DEMO & bitfield);
    }

    public boolean isService() {
        return 0 != (SERVICE & bitfield);
    }

    public boolean isStandby() {
        return 0 != (OPMODE_STANDBY & bitfield);
    }

    public boolean isConfig() {
        return 0 != (CONFIG & bitfield);
    }

    @Override
    public java.lang.String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        if (isUnspecified()) {
            sb.append("OPMODE_UNSPEC ");
        }
        if (isMonitoring()) {
            sb.append("MONITORING ");
        }
        if (isDemo()) {
            sb.append("DEMO ");

        }
        if (isService()) {
            sb.append("SERVICE ");
        }
        if (isStandby()) {
            sb.append("STANDBY ");
        }
        if (isConfig()) {
            sb.append("CONFIG ");
        }
        sb.append("]");

        return sb.toString();
    }

    @Override
    public void format(ByteBuffer bb) {
        Bits.putUnsignedShort(bb, bitfield);
    }

    @Override
    public void parse(ByteBuffer bb) {
        bitfield = Bits.getUnsignedShort(bb);
    }
}
