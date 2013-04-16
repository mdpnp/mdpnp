package org.mdpnp.devices.philips.intellivue.association;

import java.util.Map;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;

public enum AssociationMessageType implements OrdinalEnum.ShortType {
	/**
	 * Association Request
	 */
	Connect(0x0D),
	/**
	 * Association Response
	 */
	Accept(0x0E),
	/**
	 * Refused Response
	 */
	Refuse(0x0C),
	/**
	 * Disconnect Request
	 */
	Finish(0x09),
	/**
	 * Disconnect Response
	 */
	Disconnect(0x0A),
	/**
	 * Abort Request
	 */
	Abort(0x19);
	
	private final short x;
	
	// for convenience
    private AssociationMessageType(int x) {
        this( (short) x);
    }
	
	private AssociationMessageType(short x) {
	    this.x = x;
    }
	
	private static final Map<Short, AssociationMessageType> map = OrdinalEnum.buildShort(AssociationMessageType.class);
	
	public final short asShort() {
	    return x;
	}
	
	public static final AssociationMessageType valueOf(short x) {
		return map.get(x);
	}
}
