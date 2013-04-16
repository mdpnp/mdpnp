package org.mdpnp.devices.philips.intellivue.dataexport;

import java.util.Map;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;

public enum CommandType implements OrdinalEnum.IntType {
	EventReport(0),
	ConfirmedEventReport(1),
	Get(3),
	Set(4),
	ConfirmedSet(5),
	ConfirmedAction(7);
	
	private final int x;
	
	private CommandType(final int x) {
	    this.x = x;
    }
	
	private static final Map<Integer, CommandType> map = OrdinalEnum.buildInt(CommandType.class);
	
	public static final CommandType valueOf(int x) {
	    return map.get(x);
	}
	
	public final int asInt() {
	    return x;
	}
}
