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
public class MeasureMode implements Value {
    private int bitfield;

    @Override
    public void format(ByteBuffer bb) {
        Bits.putUnsignedShort(bb, bitfield);
    }

    @Override
    public void parse(ByteBuffer bb) {
        bitfield = Bits.getUnsignedShort(bb);
    }

    private static final int CO2_SIDESTREAM = 0x0400;

    public boolean isCO2SideStream() {
        return 0 != (CO2_SIDESTREAM & bitfield);
    }

    private static final int ECG_PACED = 0x0200;

    public boolean isECGPaced() {
        return 0 != (ECG_PACED & bitfield);
    }

    private static final int ECG_NONPACED = 0x0100;

    public boolean isECGNonPaced() {
        return 0 != (ECG_NONPACED & bitfield);
    }

    private static final int ECG_DIAG = 0x0080;

    public boolean isECGDiag() {
        return 0 != (ECG_DIAG & bitfield);
    }

    private static final int ECG_MONITOR = 0x0040;

    public boolean isECGMonitor() {
        return 0 != (ECG_MONITOR & bitfield);
    }

    private static final int ECG_FILTER = 0x0020;

    public boolean isECGFilter() {
        return 0 != (ECG_FILTER & bitfield);
    }

    private static final int ECG_MODE_EASI = 0x0008;

    public boolean isECGModeEASI() {
        return 0 != (ECG_MODE_EASI & bitfield);
    }

    private static final int ECG_LEAD_PRIMARY = 0x0004;

    public boolean isECGLeadPrimary() {
        return 0 != (ECG_LEAD_PRIMARY & bitfield);
    }

    @Override
    public java.lang.String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        if (isCO2SideStream()) {
            sb.append("CO2_SIDESTREAM ");
        }
        if (isECGPaced()) {
            sb.append("ECG_PACED ");
        }
        if (isECGNonPaced()) {
            sb.append("ECG_NONPACED ");
        }
        if (isECGDiag()) {
            sb.append("ECG_DIAG ");
        }
        if (isECGMonitor()) {
            sb.append("ECG_MONITOR ");
        }
        if (isECGFilter()) {
            sb.append("ECG_FILTER ");
        }
        if (isECGModeEASI()) {
            sb.append("ECG_MODE_EASI ");
        }
        if (isECGLeadPrimary()) {
            sb.append("ECG_LEAD_PRIMARY ");
        }
        return sb.toString();
    }

}
