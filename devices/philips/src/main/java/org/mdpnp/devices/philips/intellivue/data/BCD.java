package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public class BCD {
	public static final short get(byte b) {
		int x = (0xFF & b);
		int digit1 = (0xF0 & x)>>4;
		int digit2 = (0x0F & x);
		
		if(digit1 > 9 || digit2 > 9) {
			return (short)(0xFF & b);
		} else {
			return (short)(digit1 * 10 + digit2);
		}
		
	}
	public static final short get(ByteBuffer bb) {
		return get(bb.get());
	}
	public static final void put(ByteBuffer bb, short s) {
	    Bits.putUnsignedByte(bb, (short)((0xF0 & ((s / 10) << 4)) + s%10));
	}

}
