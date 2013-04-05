package org.mdpnp.devices.draeger.medibus.types;

import java.util.Map;

import org.mdpnp.devices.EnumHelper;

public enum MeasuredDataCP2 {
	;
	private static final Map<java.lang.Byte, MeasuredDataCP2> fromByte;
	
	private byte b;
	
	static {
		try {
			fromByte = EnumHelper.build(MeasuredDataCP2.class, "measured-data-cp2.map");
		} catch (Exception e) {
			throw new ExceptionInInitializerError(e);
		}
		
	}
	
	public static final MeasuredDataCP2 fromByte(byte b) {
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
