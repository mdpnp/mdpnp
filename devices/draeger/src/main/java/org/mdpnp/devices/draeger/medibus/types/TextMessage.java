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
package org.mdpnp.devices.draeger.medibus.types;

import java.util.Map;

import org.mdpnp.devices.EnumHelper;

/**
 * @author Jeff Plourde
 *
 */
public enum TextMessage {
    VentModeIPPV, VentModeIPPVAssist, VentModeCPPV, VentModeCPPVAssist, VentModeSIMV, VentModeSIMVASB, SB, ASB, CPAP, CPAP_ASB, MMV, MMV_ASB, BIPAP, SYNCHRON_MASTER, SYNCHRON_SLAVE, APNEA_VENTILATION, DS, BIPAP_SMV, BIPAP_SMV_ASB, BIPAP_APRV, Adults, Neonates, CO2InmmHg, CO2InkPa, CO2InPercent, VentStandby, AnesGasHalothane, AnesGasEnflurane, AnesGasIsoflurane, AnesGasDesflurane, AnesGasSevoflurane, NoAnesGas, VentModeManualSpont, SelectedLanguage, VentModePCV, VentModeFreshGasExt, CarrierGasAir, CarrierGasN2O, AnesGas2Halothane, AnesGas2Enflurane, AnesGas2Isoflurane, AnesGas2Desflurane, AnesGas2Sevoflurane, NoAnesGas2, PerformingLeakageTest, DeviceInStandby, AgentUnitkPa, AgentUnitPct, HLMModeActive, VolumeMode, PressureMode, PressureSupportMode, PressureSupportAdded, SyncIntermittentVent;

    private static final Map<java.lang.Byte, TextMessage> fromByte;

    private byte b;

    static {
        int lineNumber[] = new int[1];
        try {
            fromByte = EnumHelper.build(TextMessage.class, "text-message.map", lineNumber);
        } catch (Exception e) {
            throw new Error("At line number " + lineNumber[0], e);
        }

    }

    public static final TextMessage fromByte(byte b) {
        return fromByte.get(b);

    }

    public final java.lang.Byte toByte() {
        return b;
    }

    public static final Object fromByteIf(byte b) {
        if (fromByte.containsKey(b)) {
            return fromByte.get(b);
        } else {
            return b;
        }
    }
}
