package org.mdpnp.devices.philips.intellivue.dataexport.error;

public enum ErrorStatus {
	AccessDenied,
	NoSuchAttribute,
	InvalidAttributeValue,
	InvalidOperation,
	InvalidOperator;
	
	public static final ErrorStatus valueOf(int x) {
		switch(x) {
		case 2:
			return AccessDenied;
		case 5:
			return NoSuchAttribute;
		case 6:
			return InvalidAttributeValue;
		case 24:
			return InvalidOperation;
		case 25:
			return InvalidOperator;
		default:
			throw new IllegalArgumentException("Unknown error status:"+x);
		}
	}
	
	public final int asInt() {
		switch(this) {
		case AccessDenied:
			return 2;
		case NoSuchAttribute:
			return 5;
		case InvalidAttributeValue:
			return 6;
		case InvalidOperation:
			return 24;
		case InvalidOperator:
			return 25;
		default:
			throw new IllegalArgumentException("Unknown error status:"+this);
		}
	}
}
