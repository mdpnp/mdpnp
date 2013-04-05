package org.mdpnp.devices.philips.intellivue.dataexport;

public enum ModifyOperator {
	Replace,
	AddValues,
	RemoveValues,
	SetToDefault;
	
	public static final ModifyOperator valueOf(int x) {
		switch(x) {
		case 0:
			return Replace;
		case 1:
			return AddValues;
		case 2:
			return RemoveValues;
		case 3:
			return SetToDefault;
		default:
			throw new IllegalArgumentException("Unknown ModifyOperator:"+x);
		}
	}
	
	public final int asInt() {
		switch(this) {
		case Replace:
			return 0;
		case AddValues:
			return 1;
		case RemoveValues:
			return 2;
		case SetToDefault:
			return 3;
		default:
			throw new IllegalArgumentException("Unknown ModifyOperator:"+this);
		}
	}
}
