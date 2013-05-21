package org.mdpnp.devices;

/**
 * Some useful bytes named from the ASCII character set. 
 * @author jplourde
 *
 */
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
	
	/**
	 * ASCII Negative Acknowledgment
	 */
	public static final byte NAK = 0x15;
	
	/**
	 * ASCII End of Text
	 */
	public static final byte ETX = 0x03;
}
