package org.mdpnp.devices.philips.intellivue.dataexport;

public enum RemoteOperationLinkedState {
	First,
	NotFirstNotLast,
	Last;
	
	public static final RemoteOperationLinkedState valueOf(short x) {
		switch(x) {
		case 1:
			return First;
		case 2:
			return NotFirstNotLast;
		case 3:
			return Last;
		default:
			throw new IllegalArgumentException("Unknown RemoteOperationLinkedState:"+x);
		}
	}
	
	public final short asShort() {
		switch(this) {
		case First:
			return 1;
		case NotFirstNotLast:
			return 2;
		case Last:
			return 3;
		default:
			throw new IllegalArgumentException("Unknown RemoteOperationLinkedState:"+this);
			
		}
	}
}
