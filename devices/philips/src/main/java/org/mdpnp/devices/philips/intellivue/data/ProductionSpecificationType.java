package org.mdpnp.devices.philips.intellivue.data;

import java.util.Map;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;

public enum ProductionSpecificationType implements OrdinalEnum.IntType {
	UNSPECIFIED(0),
	SERIAL_NUMBER(1),
	PART_NUMBER(2),
	HW_REVISION(3),
	SW_REVISION(4),
	FW_REVISION(5),
	PROTOCOL_REVISION(6),
	;
	
	private final int x;
	
	private ProductionSpecificationType(final int x) {
	    this.x = x;
    }
	
	private static final Map<Integer, ProductionSpecificationType> map = OrdinalEnum.buildInt(ProductionSpecificationType.class);
	
	public static ProductionSpecificationType valueOf(int x) {
		return map.get(x);
	}
	
	public int asInt() {
		return x;
	}
}
