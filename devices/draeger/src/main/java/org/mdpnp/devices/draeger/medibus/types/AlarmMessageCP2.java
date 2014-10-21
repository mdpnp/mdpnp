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
 * Alarm Messages of Code Page 2
 */
public enum AlarmMessageCP2 {
    InspN2OHigh, ExpHalothaneExceedsHighLimit, ExpEnfluraneExceedsHighLimit, ExpIsofluraneExceedsHighLimit, ExpDesfluraneExceedsHighLimit, ExpSevofluraneExceedsHighLimit, InspFlowSensorInoperable, PowerSupplyError, O2CylinderPressureLowWithoutWallSupply, O2CylinderEmptyWithoutWallSupply, O2CylinderNotConnected, N2OCylinderEmpty, N2ODeliveryFailure, O2DeliveryFailure, AIRDeliveryFailure, SetFreshGasFlowNotAttained, InternalExternalSwitchoverValveError, CircleOccluded, BreathingSystemDisconnected, LossOfData, ApneaVentilation, CircleLeakage, VentNotInLockedPosition, SetTidalVolumeNotAttained, SettingCanceled, FreshGasFlowTooHigh, FreshGasFlowActive, OxygenCylinderOpen, N2OCylinderOpen, AirCylinderOpen, N2OCylinderSensorNotConnected, AirCylinderSensorNotConnected, O2CylinderSensorNotConnected, AirCylinderPressureLow, AirFreshGasFlowMeasurementInoperable, O2FreshGasFlowMeasurementInoperable, N2OFreshGasFlowMeasurementInoperable, NoAirSupply, NoN2OSupply;

    private static final Map<java.lang.Byte, AlarmMessageCP2> fromByte;

    private byte b;

    static {
        int lineNumber[] = new int[1];
        try {
            fromByte = EnumHelper.build(AlarmMessageCP2.class, "alarm-message-cp2.map", lineNumber);
        } catch (Exception e) {
            throw new Error("At line number " + lineNumber[0], e);
        }

    }

    public static final AlarmMessageCP2 fromByte(byte b) {
        return fromByte.get(b);

    }

    public static final Object fromByteIf(byte b) {
        if (fromByte.containsKey(b)) {
            return fromByte.get(b);
        } else {
            return b;
        }
    }

    public final java.lang.Byte toByte() {
        return b;
    }

}
