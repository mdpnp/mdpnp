package org.mdpnp.devices.philips.intellivue.dataexport;

import java.util.Map;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;

public enum ModifyOperator implements OrdinalEnum.IntType {
	Replace(0),
	AddValues(1),
	RemoveValues(2),
	SetToDefault(3);
	
	private final int x;
	
	private ModifyOperator(final int x) {
	    this.x = x;
    }
	
	private static final Map<Integer, ModifyOperator> map = OrdinalEnum.buildInt(ModifyOperator.class);
	
	public static final ModifyOperator valueOf(int x) {
		return map.get(x);
	}
	
	public final int asInt() {
		return x;
	}
}
