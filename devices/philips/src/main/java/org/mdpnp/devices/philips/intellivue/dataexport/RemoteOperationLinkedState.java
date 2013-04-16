package org.mdpnp.devices.philips.intellivue.dataexport;

import java.util.Map;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;

public enum RemoteOperationLinkedState implements OrdinalEnum.ShortType {
	First(1),
	NotFirstNotLast(2),
	Last(3);
	
	private final short x;
	
	private RemoteOperationLinkedState(final int x) {
	    this((short)x);
    }
	
	private RemoteOperationLinkedState(final short x) {
	    this.x = x;
    }
	
	private static final Map<Short, RemoteOperationLinkedState> map = OrdinalEnum.buildShort(RemoteOperationLinkedState.class);
	
	public static final RemoteOperationLinkedState valueOf(short x) {
		return map.get(x);
	}
	
	public final short asShort() {
		return x;
	}
}
