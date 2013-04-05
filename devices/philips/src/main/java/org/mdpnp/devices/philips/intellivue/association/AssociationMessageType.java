package org.mdpnp.devices.philips.intellivue.association;

public enum AssociationMessageType {
	/**
	 * Association Request
	 */
	Connect,
	/**
	 * Association Response
	 */
	Accept,
	/**
	 * Refused Response
	 */
	Refuse,
	/**
	 * Disconnect Request
	 */
	Finish,
	/**
	 * Disconnect Response
	 */
	Disconnect,
	/**
	 * Abort Request
	 */
	Abort;
	
	public final short asShort() {
		switch(this) {
		case Connect:
			return 0x0D;
		case Accept:
			return 0x0E;
		case Refuse:
			return 0x0C;
		case Finish:
			return 0x09;
		case Disconnect:
			return 0x0A;
		case Abort:
			return 0x19;
		default:
			throw new IllegalArgumentException("Unknown SessionHeaderType:"+this);
		}
	}
	
	public static final AssociationMessageType valueOf(short x) {
		switch(x) {
		case 0x0D:
			return Connect;
		case 0x0E:
			return Accept;
		case 0x0C:
			return Refuse;
		case 0x09:
			return Finish;
		case 0x0A:
			return Disconnect;
		case 0x19:
			return Abort;
		default:
			throw new IllegalArgumentException("Unknown SessionHeaderType:"+x);
		
		}
	}
}
