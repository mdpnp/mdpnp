package org.mdpnp.devices.draeger.medibus;

public class ASCIIByte {
	/**
	 * suspend data transmission
	 */
	public static final byte DC1 = 0x11;
	
	/**
	 * resume data transmission
	 */
	public static final byte DC3 = 0x13;
	
	/**
	 * abort data transmission
	 */
	public static final byte CAN = 0x18;
	
	/**
	 * ASCII Escape
	 */
	public static final byte ESC = 0x1B;
	
	/**
	 * ASCII Carriage Return
	 */
	public static final byte CR = 0x0D;
	
	/** 
	 * ASCII Start of Header
	 */
	public static final byte SOH = 0x01;
	
	public static final byte NAK = 0x15;
	
	public static final byte ETX = 0x03;
}
