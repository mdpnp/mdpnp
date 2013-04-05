package org.mdpnp.devices.draeger.medibus.types;

import java.util.Map;

import org.mdpnp.devices.EnumHelper;
import org.mdpnp.devices.Unit;

public enum RealtimeData {
	AirwayPressure,
	FlowInspExp,
	RespiratoryVolumeSinceInspBegin,
	ExpiratoryVolume,
	ExpiratoryCO2mmHg,
	ExpiratoryCO2kPa,
	ExpiratoryCO2Percent,
	Pleth,
	InspiratoryFlow,
	ExpiratoryFlow,
	O2InspExp,
	AgentInspExpPercent,
	HalothaneInspExpPercent,
	EnfluraneInspExpPercent,
	IsofluraneInspExpPercent,
	DesfluraneInspExpPercent,
	SevofluraneInspExpPercent,
	AgentInspExpkPa,
	HalothaneInspExpkPa,
	EnfluraneInspExpkPa,
	IsofluraneInspExpkPa,
	DesfluraneInspExpkPa,
	SevofluraneInspExpkPa;
	
	
	private static final Map<java.lang.Byte, RealtimeData> fromByte;
	
	private byte b;
	private Unit u;
	
	static {
		try {
			fromByte = EnumHelper.build(RealtimeData.class, "realtime-data.map");
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
		
	}
	
	public static final RealtimeData fromByte(byte b) {
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
		return super.toString()+(u!=null?(" (in " +u+")"):null);
	}
}
