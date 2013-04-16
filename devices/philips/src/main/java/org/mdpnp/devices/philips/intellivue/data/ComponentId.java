package org.mdpnp.devices.philips.intellivue.data;

import java.util.Map;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;

public enum ComponentId implements OrdinalEnum.IntType {
	ID_COMP_PRODUCT(0x0008),
	ID_COMP_CONFIG(0x0010),
	ID_COMP_BOOT(0x0018),
	ID_COMP_MAIN_BD(0x0050),
	ID_COMP_APPL_SW(0x0058);
	
	private final int x;
	
	private ComponentId(int x) {
	    this.x = x;
    }
	
	private static final Map<Integer, ComponentId> map = OrdinalEnum.buildInt(ComponentId.class);
	
	public static ComponentId valueOf(int x) {
		return map.get(x);
	}
	
	public int asInt() {
	    return x;
	}
}
