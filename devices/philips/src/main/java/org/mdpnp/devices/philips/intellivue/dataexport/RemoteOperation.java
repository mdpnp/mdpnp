package org.mdpnp.devices.philips.intellivue.dataexport;

public enum RemoteOperation {
	Invoke,
	Result,
	Error,
	LinkedResult;
	
	public static final RemoteOperation valueOf(int x) {
		switch(x) {
		case 1:
			return RemoteOperation.Invoke;
		case 2:
			return RemoteOperation.Result;
		case 3:
			return RemoteOperation.Error;
		case 5:
			return RemoteOperation.LinkedResult;
		default:
			throw new IllegalArgumentException("Unknown RemoteOperation:"+x);
		}
	}
	
	public final int asInt() {
		switch(this) {
		case Invoke:
			return 1;
		case Result:
			return 2;
		case Error:
			return 3;
		case LinkedResult:
			return 5;
		default:
			throw new IllegalArgumentException("Unknown RemoteOperation:"+this);
		}
	}
}
