package org.mdpnp.devices.draeger.medibus.types;

import java.util.Map;

import org.mdpnp.devices.EnumHelper;
import org.mdpnp.devices.Unit;

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
	OcclusionPressure,
	MeanBreathingPressure,
	PlateauPressure,
	PEEPBreathingPressure,
	IntrinsicPEEPBreathingPressure,
	PeakBreathingPressure,
	TrappedVolume,
	TidalVolume,
	SpontaneousRespiratoryRate,
	SpontaneousMinuteVolume,
	RespiratoryMinuteVolume,
	AirwayTemperature,
	RespiratoryRate,
	InspiredOxygen,
	CarbonDioxideProduction,
	DeadSpace,
	RelativeDeadSpace,
	EndTidalCO2Percent,
	ComplianceFrac,
	ResistanceFrac,
	TidalVolumeFrac,
	SpontaneousMinuteVolumeFrac,
	RespiratoryMinuteVolumeFrac,
	InspiratorySpontaneousSupportVolume,
	RapidShallowBreathingIndex,
	PulseRate,
	InspHalothanekPa,
	ExpHalothanekPa,
	InspEnfluranekPa,
	ExpEnfluranekPa,
	InspIsofluranekPa,
	ExpIsofluranekPa,
	InspDesfluranekPa,
	ExpDesfluranekPa,
	InspSevofluranekPa,
	ExpSevofluranekPa,
	InspAgentkPa,
	ExpAgentkPa,
	InspAgent2kPa,
	ExpAgent2kPa,
	InspMAC,
	ExpMAC,
	InspDesfluranePct,
	ExpDesfluranePct,
	InspSevofluranePct,
	ExpSevofluranePct,
	InspAgentPct,
	ExpAgentPct,
	InspAgent2Pct,
	ExpAgent2Pct,
	InspHalothanePct,
	ExpHalothanePct,
	InspEnfluranePct,
	ExpEnfluranePct,
	InspIsofluranePct,
	ExpIsofluranePct,
	InspN2OPct,
	ExpN2OPct,
	BreathingPressure,
	AmbientPressure,
	RespiratoryRatePressure,
	ApneaDuration,
	RespiratoryRateVolumePerFlow,
	RespiratoryRateDerived,
	RespiratoryRateCO2,
	InspCO2Pct,
	EndTidalCO2kPa,
	InspCO2mmHg,
	EndTidalCO2mmHg,
	InspCO2kPa,
	DeltaO2,
	ExpO2,
	InspO2,
	O2Uptake,
	OxygenSaturation,
	PulseRateDerived,
	PulseRateOximeter,
	Leakage;
	
	
	
	
	
	private static final Map<java.lang.Byte, MeasuredDataCP1> fromByte;
	
	private byte b;
	private Unit u;
	
	static {
		try {
			fromByte = EnumHelper.build(MeasuredDataCP1.class, "measured-data-cp1.map");
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
		
	}
	
	public static final MeasuredDataCP1 fromByte(byte b) {
		return fromByte.get(b);
	
	}
	
	public final java.lang.Byte toByte() {
		return b;
	}
	public static final Object fromByteIf(byte b) {
		if(fromByte.containsKey(b)) {
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
		return super.toString() + (u!=null?(" (in "+u+")"):"");
	}
}
