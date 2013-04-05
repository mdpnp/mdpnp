package org.mdpnp.devices.philips.intellivue.data;

public enum StringFormat {
	Unicode;
	
	public static StringFormat valueOf(int x) {
		switch(x) {
		case 11:
			return Unicode;
		default:
			throw new IllegalArgumentException("Unknown StringFormat:"+x);
		}
	}
	
	public int asShort() {
		switch(this) {
		case Unicode:
			return 11;
		default:
			throw new IllegalArgumentException("Unknown StringFormat:"+this);
		}
	}
}
