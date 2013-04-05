package org.mdpnp.devices.philips.intellivue.dataexport;

public enum CommandType {
	EventReport,
	ConfirmedEventReport,
	Get,
	Set,
	ConfirmedSet,
	ConfirmedAction;
	
	public static final CommandType valueOf(int x) {
		switch(x) {
		case 0:
			return CommandType.EventReport;
		case 1:
			return CommandType.ConfirmedEventReport;
		case 3:
			return CommandType.Get;
		case 4:
			return CommandType.Set;
		case 5:
			return CommandType.ConfirmedSet;
		case 7:
			return CommandType.ConfirmedAction;
		default:
			return null;
//			throw new IllegalArgumentException("Unknown CommandType:"+x);
		}
	}
	
	public final int asInt() {
		switch(this) {
		case EventReport:
			return 0;
		case ConfirmedEventReport:
			return 1;
		case Get:
			return 3;
		case Set:
			return 4;
		case ConfirmedSet:
			return 5;
		case ConfirmedAction:
			return 7;
		default:
			throw new IllegalArgumentException("Unknown CommandType:"+this);
		}
	}
}
