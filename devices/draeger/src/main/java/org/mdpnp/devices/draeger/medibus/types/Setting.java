package org.mdpnp.devices.draeger.medibus.types;

import java.util.Map;

import org.mdpnp.devices.EnumHelper;
import org.mdpnp.devices.Unit;

public enum Setting {
	Oxygen,
	MaxInpirationFlow,
	InspTidalVolume,
	IPart,
	EPart,
	FrequencyIMV,
	FrequencyIPPV,
	PEEP,
	IntermittentPEEP,
	BIPAPLowPressure,
	BIPAPHighPressure,
	BIPAPLowTime,
	BIPAPHighTime,
	ApneaTime,
	PressureSupportPressure,
	MaxInspirationAirwayPressure,
	TriggerPressure,
	TachyapneaFrequency,
	TachyapneaDuration,
	FlowTrigger,
	ASBRamp,
	InspiratoryTime,
	FreshgasFlow,
	MinimalFrequency,
	InspiratoryPressure,
	Age,
	Weight,
	InspPause_InspTime;
	
	private static final Map<java.lang.Byte, Setting> fromByte;
	
	private byte b;
	private Unit u;
	
	static {
		try {
			fromByte = EnumHelper.build(Setting.class, "setting.map");
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
		
	}
	
	public static final Setting fromByte(byte b) {
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
		return super.toString() + (u!=null?(" (in " + u + ")"):"");
	}
}
