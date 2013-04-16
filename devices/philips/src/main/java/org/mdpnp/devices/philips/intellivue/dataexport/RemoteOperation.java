package org.mdpnp.devices.philips.intellivue.dataexport;

import java.util.Map;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;

public enum RemoteOperation implements OrdinalEnum.IntType {
	Invoke(1),
	Result(2),
	Error(3),
	LinkedResult(5);
	
	private final int x;
	
	private RemoteOperation(final int x) {
	    this.x = x;
    }
	
	private static final Map<Integer, RemoteOperation> map = OrdinalEnum.buildInt(RemoteOperation.class);
	
	public static final RemoteOperation valueOf(int x) {
		return map.get(x);
	}
	
	public final int asInt() {
	    return x;
	}
}
