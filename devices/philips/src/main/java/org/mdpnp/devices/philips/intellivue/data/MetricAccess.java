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
import org.mdpnp.devices.philips.intellivue.Message;

/**
 * @author Jeff Plourde
 *
 */
public class MetricAccess implements Message {

    private static final int AVAIL_INTERMITTENT = 0x8000;
    private static final int UPD_PERIODIC = 0x4000;
    private static final int UPD_EPISODIC = 0x2000;
    private static final int MSMT_NONCONTINUOUS = 0x1000;
    private static final int ACC_EVREP = 0x0800;
    private static final int ACC_GET = 0x0400;
    private static final int ACC_SCAN = 0x0200;
    private static final int GEN_OPT_SYNC = 0x0080;
    private static final int SC_OPT_NORMAL = 0x0020;
    private static final int SC_OPT_EXTENSIVE = 0x0010;
    private static final int SC_OPT_LONG_PD_AVAIL = 0x0008;
    private static final int SC_OPT_CONFIRM = 0x0004;

    private int value;

    public boolean isAvailIntermittent() {
        return 0 != (AVAIL_INTERMITTENT & value);
    }

    public boolean isUpdPeriodic() {
        return 0 != (UPD_PERIODIC & value);
    }

    public boolean isUpdEpisodic() {
        return 0 != (UPD_EPISODIC & value);
    }

    public boolean isMsmtNoncontinuous() {
        return 0 != (MSMT_NONCONTINUOUS & value);
    }

    public boolean isAccEvRep() {
        return 0 != (ACC_EVREP & value);
    }

    public boolean isAccGet() {
        return 0 != (ACC_GET & value);
    }

    public boolean isAccScan() {
        return 0 != (ACC_SCAN & value);
    }

    public boolean isGenOptSync() {
        return 0 != (GEN_OPT_SYNC & value);
    }

    public boolean isScOptNormal() {
        return 0 != (SC_OPT_NORMAL & value);
    }

    public boolean isScOptExtensive() {
        return 0 != (SC_OPT_EXTENSIVE & value);
    }

    public boolean isScOptLongPdAvail() {
        return 0 != (SC_OPT_LONG_PD_AVAIL & value);
    }

    public boolean isScOptConfirm() {
        return 0 != (SC_OPT_CONFIRM & value);
    }

    @Override
    public java.lang.String toString() {
        StringBuilder sb = new StringBuilder("[");
        if (isAvailIntermittent()) {
            sb.append("AVAIL_INTERMITTENT|");
        }
        if (isUpdPeriodic()) {
            sb.append("UPD_PERIODIC|");
        }
        if (isMsmtNoncontinuous()) {
            sb.append("MSMT_NONCONTINUOUS|");
        }
        if (isAccEvRep()) {
            sb.append("ACC_EVREP|");
        }
        if (isAccGet()) {
            sb.append("ACC_GET|");
        }
        if (isAccScan()) {
            sb.append("ACC_SCAN|");
        }
        if (isGenOptSync()) {
            sb.append("GEN_OPT_SYNC|");
        }
        if (isScOptNormal()) {
            sb.append("SC_OPT_NORMAL|");
        }
        if (isScOptExtensive()) {
            sb.append("SC_OPT_EXTENSIVE|");
        }
        if (isScOptLongPdAvail()) {
            sb.append("SC_OPT_LONG_PD_AVAIL|");
        }
        if (isScOptConfirm()) {
            sb.append("SC_OPT_CONFIRM|");
        }
        if (sb.length() > 1) {
            sb.delete(sb.length() - 1, sb.length());
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public void parse(ByteBuffer bb) {
        value = Bits.getUnsignedShort(bb);
    }

    @Override
    public void format(ByteBuffer bb) {
        Bits.putUnsignedShort(bb, value);
    }
}
