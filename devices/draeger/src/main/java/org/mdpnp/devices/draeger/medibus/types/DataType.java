package org.mdpnp.devices.draeger.medibus.types;

import java.util.Map;

import org.mdpnp.devices.EnumHelper;

/**
 * 
 * @author jplourde
 *
 */
public enum DataType {
	AlarmLimitsCP1,
	AlarmsCP1,
	DeviceSettings,
	TextMessages,
	AlarmLimitsCP2,
	AlarmsCP2;
	
	private static final Map<java.lang.Byte, DataType> fromByte;

	private byte b;
	static {
		try {
			fromByte = EnumHelper.build(DataType.class, "datatype.map");
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
		
		
	}
	
	public static final DataType fromByte(byte b) {
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
}
