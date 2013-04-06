package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public enum LineFrequency implements EnumMessage<LineFrequency> {
	LINE_F_UNSPEC,
	LINE_F_50HZ,
	LINE_F_60HZ;
	@Override
	public LineFrequency parse(ByteBuffer bb) {
		return LineFrequency.valueOf(Bits.getUnsignedShort(bb));
	}

	@Override
	public void format(ByteBuffer bb) {
	    Bits.putUnsignedShort(bb, asInt());
	}
	
	public static LineFrequency valueOf(int x) {
		switch(x) {
		case 0:
			return LINE_F_UNSPEC;
		case 1:
			return LINE_F_50HZ;
		case 2:
			return LINE_F_60HZ;
		default:
			return null;
		}
		
	}
	
	public int asInt() {
		switch(this) {
		case LINE_F_UNSPEC:
			return 0;
		case LINE_F_50HZ:
			return 1;
		case LINE_F_60HZ:
			return 2;
		default:
			throw new IllegalArgumentException("Unknown LineFrequency:"+this);
		}
	}
}
