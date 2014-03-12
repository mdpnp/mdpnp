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

import java.util.Map;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;

/**
 * @author Jeff Plourde
 *
 */
public enum Dimension implements OrdinalEnum.IntType {
    NOM_DIM_DIMLESS(0x0200), NOM_DIM_PERCENT(0x0220), NOM_DIM_MILLI_M(0x0512), NOM_DIM_MILLI_L_PER_M_SQ(0x0592), NOM_DIM_MILLI_L(0x0652), NOM_DIM_X_G_PER_L(
            0x0800), NOM_DIM_MILLI_G_PER_L(0x0812), NOM_DIM_NANO_G_PER_L(0x0814), NOM_DIM_X_G_PER_DL(0x0840), NOM_DIM_MILLI_G_PER_DL(0x0852), NOM_DIM_PICO_G_PER_ML(
            0x0875), NOM_DIM_SEC(0x0880), NOM_DIM_MILLI_SEC(0x0892), NOM_DIM_HZ(0x09C0), NOM_DIM_BEAT_PER_MIN(0x0AA0), NOM_DIM_RESP_PER_MIN(0x0AE0), NOM_DIM_X_L_PER_MIN_PER_M_SQ(
            0x0B20), NOM_DIM_X_L_PER_MIN(0x0C00), NOM_DIM_MILLI_L_PER_KG(0x0C72), NOM_DIM_KILO_PASCAL(0x0F03), NOM_DIM_MMHG(0x0F20), NOM_DIM_MILLI_BAR(
            0x0F72), NOM_DIM_NANO_WATT(0x0FD4), NOM_DIM_X_DYNE_PER_SEC_PER_CM5(0x1020), NOM_DIM_MILLI_VOLT(0x10B2), NOM_DIM_MICRO_VOLT(0x10B3), NOM_DIM_X_OHM(
            0x10C0), NOM_DIM_FAHR(0x1140), NOM_DIM_MILLI_MOLE_PER_L(1272), NOM_DIM_DEGC(0x17A0), NOM_DIM_DECIBEL(0x1920), ;

    private final int x;

    private Dimension(int x) {
        this.x = x;
    }

    private static final Map<Integer, Dimension> map = OrdinalEnum.buildInt(Dimension.class);

    public static final Dimension valueOf(int x) {
        return map.get(x);
    }

    public final int asInt() {
        return x;
    }
}
