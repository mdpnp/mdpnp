package org.mdpnp.devices.philips.intellivue.data;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;


import java.nio.ByteBuffer;
import java.util.Map;

import org.mdpnp.devices.io.util.Bits;

public enum LineFrequency implements EnumMessage<LineFrequency>, OrdinalEnum.IntType {
	LINE_F_UNSPEC(0),
	LINE_F_50HZ(1),
	LINE_F_60HZ(2);
	@Override
	public LineFrequency parse(ByteBuffer bb) {
		return LineFrequency.valueOf(Bits.getUnsignedShort(bb));
	}

	@Override
	public void format(ByteBuffer bb) {
	    Bits.putUnsignedShort(bb, asInt());
	}
	
	private final int x;
	
	private LineFrequency(int x) {
	    this.x = x;
    }
	
	private static final Map<Integer, LineFrequency> map = OrdinalEnum.buildInt(LineFrequency.class);
	
	public static LineFrequency valueOf(int x) {
	    return map.get(x);
	}
	
	public int asInt() {
		return x;
	}
}
