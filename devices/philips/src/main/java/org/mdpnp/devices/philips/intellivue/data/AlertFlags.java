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

/**
 * @author Jeff Plourde
 *
 */
public class AlertFlags implements Formatable, Parseable {
    private int state;

    private static final int BEDSIDE_AUDIBLE = 0x4000;
    private static final int CENTRAL_AUDIBLE = 0x2000;
    private static final int VISUAL_LATCHING = 0x1000;
    private static final int AUDIBLE_LATCHING = 0x0800;
    private static final int SHORT_YELLOW_EXTENSION = 0x0400;
    private static final int DERIVED = 0x0200;

    @Override
    public void format(ByteBuffer bb) {
        Bits.putUnsignedShort(bb, state);
    }

    @Override
    public void parse(ByteBuffer bb) {
        state = Bits.getUnsignedShort(bb);
    }

    public boolean isBedsideAudible() {
        return 0 != (BEDSIDE_AUDIBLE & state);
    }

    public boolean isCentralAudible() {
        return 0 != (CENTRAL_AUDIBLE & state);
    }

    public boolean isVisualLatching() {
        return 0 != (VISUAL_LATCHING & state);
    }

    public boolean isAudibleLatching() {
        return 0 != (AUDIBLE_LATCHING & state);
    }

    public boolean isShortYellowExtension() {
        return 0 != (SHORT_YELLOW_EXTENSION & state);
    }

    public boolean isDerived() {
        return 0 != (DERIVED & state);
    }

    @Override
    public java.lang.String toString() {
        StringBuilder sb = new StringBuilder("[");
        if (isBedsideAudible()) {
            sb.append("BEDSIDE_AUDIBLE ");
        }
        if (isCentralAudible()) {
            sb.append("CENTRAL_AUDIBLE ");
        }
        if (isVisualLatching()) {
            sb.append("VISUAL_LATCHING ");
        }
        if (isAudibleLatching()) {
            sb.append("AUDIBLE_LATCHING ");
        }
        if (isShortYellowExtension()) {
            sb.append("SHORT_YELLOW_EXTENSION ");
        }
        if (isDerived()) {
            sb.append("DERIVED ");
        }
        if (sb.charAt(sb.length() - 1) == ' ') {
            sb.delete(sb.length() - 1, sb.length());
        }
        sb.append("]");
        return sb.toString();
    }
}
