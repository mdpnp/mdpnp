package org.mdpnp.devices.philips.intellivue.dataexport.error;

public enum RemoteError {
	NoSuchObjectClass,
	NoSuchObjectInstance,
	AccessDenied,
	GetListError,
	SetListError,
	NoSuchAction,
	ProcessingFailure,
	InvalidArgumentValue,
	InvalidScope,
	InvalidObjectInstance;
	
	public static final RemoteError valueOf(int x) {
		switch(x) {
		case 0:
			return NoSuchObjectClass;
		case 1:
			return NoSuchObjectInstance;
		case 2:
			return AccessDenied;
		case 7:
			return GetListError;
		case 8:
			return SetListError;
		case 9:
			return NoSuchAction;
		case 10:
			return ProcessingFailure;
		case 15:
			return InvalidArgumentValue;
		case 16:
			return InvalidScope;
		case 17:
			return InvalidObjectInstance;
		default:
			throw new IllegalArgumentException("Unknown error value:"+x);
		}
	}
	public final int asInt() {
		switch(this) {
		case NoSuchObjectClass:
			return 0;
		case NoSuchObjectInstance:
			return 1;
		case AccessDenied:
			return 2;
		case GetListError:
			return 7;
		case SetListError:
			return 8;
		case NoSuchAction:
			return 9;
		case ProcessingFailure:
			return 10;
		case InvalidArgumentValue:
			return 15;
		case InvalidScope:
			return 16;
		case InvalidObjectInstance:
			return 17;
		default:
			throw new IllegalArgumentException("Unknown error value:"+this);
		}
	}

}
