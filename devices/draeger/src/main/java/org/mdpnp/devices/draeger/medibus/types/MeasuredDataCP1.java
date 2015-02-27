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
import org.mdpnp.devices.Unit;

/**
 * @author Jeff Plourde
 * 
 */
public enum MeasuredDataCP1 {
    /**
     * Compliance L/bar
     */
    Compliance,
    /**
     * Resistance mbar/L/s
     */
    Resistance,
    /**
     * MinimalAirwayPressure mbar
     */
    MinimalAirwayPressure,
    /**
     * Occlusion Pressure mbar
     */
    OcclusionPressure, MeanBreathingPressure, PlateauPressure, PEEPBreathingPressure, IntrinsicPEEPBreathingPressure, PeakBreathingPressure, TrappedVolume, TidalVolume, SpontaneousRespiratoryRate, SpontaneousMinuteVolume, RespiratoryMinuteVolume, AirwayTemperature, RespiratoryRate, InspiredOxygen, CarbonDioxideProduction, DeadSpace, RelativeDeadSpace, EndTidalCO2Percent, ComplianceFrac, ResistanceFrac, TidalVolumeFrac, SpontaneousMinuteVolumeFrac, RespiratoryMinuteVolumeFrac, InspiratorySpontaneousSupportVolume, RapidShallowBreathingIndex, PulseRate, InspHalothanekPa, ExpHalothanekPa, InspEnfluranekPa, ExpEnfluranekPa, InspIsofluranekPa, ExpIsofluranekPa, InspDesfluranekPa, ExpDesfluranekPa, InspSevofluranekPa, ExpSevofluranekPa, InspAgentkPa, ExpAgentkPa, InspAgent2kPa, ExpAgent2kPa, InspMAC, ExpMAC, InspDesfluranePct, ExpDesfluranePct, InspSevofluranePct, ExpSevofluranePct, InspAgentPct, ExpAgentPct, InspAgent2Pct, ExpAgent2Pct, InspHalothanePct, ExpHalothanePct, InspEnfluranePct, ExpEnfluranePct, InspIsofluranePct, ExpIsofluranePct, InspN2OPct, ExpN2OPct, BreathingPressure, AmbientPressure, RespiratoryRatePressure, ApneaDuration, RespiratoryRateVolumePerFlow, RespiratoryRateDerived, RespiratoryRateCO2, InspCO2Pct, EndTidalCO2kPa, InspCO2mmHg, EndTidalCO2mmHg, InspCO2kPa, DeltaO2, ExpO2, InspO2, O2Uptake, OxygenSaturation, PulseRateDerived, PulseRateOximeter, Leakage, RRmand, VTmand, VTspon, rSquared, VTemand, VTespon, InspmandatoryTidalVolumeVTimand, LeakageRelPctleak, SpontaneousFractionMinVoPctMVsponMVtotal, ItoE_Ipart, ItoE_Epart,N2OFlow,AirFlow,O2Flow;
   

    private static final Map<java.lang.Byte, MeasuredDataCP1> fromByte;

    private byte b;
    private Unit u;

    static {
        int lineNumber[] = new int[1];
        try {
            fromByte = EnumHelper.build(MeasuredDataCP1.class, "measured-data-cp1.map", lineNumber);
        } catch (Exception e) {
            throw new Error("At line number " + lineNumber[0], e);
        }

    }

    public static final MeasuredDataCP1 fromByte(byte b) {
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

    public Unit getUnit() {
        return u;
    }

    @Override
    public String toString() {
        return super.toString() + (u != null ? (" (in " + u + ")") : "");
    }
}
