package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;

public class ASNLength implements Parseable, Formatable {

	private long length;
	
	@Override
	public void format(ByteBuffer bb) {
		if(length < 128) {
		    Bits.putUnsignedByte(bb, (short)length);
		} else {
			int bits = Long.SIZE - Long.numberOfLeadingZeros(length);
			int bytes = bits / Byte.SIZE + 1; 
			bb.put((byte)(0x80 | bytes));
			int shift = Byte.SIZE * (bytes-1);
			for(; shift>=0; shift-=Byte.SIZE) {
				bb.put( (byte)((length >> shift) & 0xFF));
			}
		}
	}

	@Override
	public void parse(ByteBuffer bb) {
		short start = Bits.getUnsignedByte(bb);
		if(start < 128) {
			length = start;
		} else {
			int bytes = 0x7F & start;
			int shift = Byte.SIZE * (bytes-1);

			length = 0;
			for(; shift >= 0; shift-=Byte.SIZE) {
				length |= (bb.get() << shift);
			}
		}
	}
	
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
	
	@Override
	public java.lang.String toString() {
		return Long.toString(length);
	}
}
