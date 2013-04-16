package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;
import java.util.Map;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;

import org.mdpnp.devices.io.util.Bits;

// ought this be a bitfield and not an enum?
public enum MetricModality implements EnumMessage<MetricModality>, OrdinalEnum.IntType {
	MANUAL(0x4000),
	APERIODIC(0x2000),
	VERIFIED(0x1000);
	
	@Override
	public MetricModality parse(ByteBuffer bb) {
		return MetricModality.valueOf(Bits.getUnsignedShort(bb));
	}
	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, asInt());
	}
	
	private final int x;
	
	private MetricModality(int x) {
	    this.x = x;
	}
	
	private static final Map<Integer, MetricModality> map = OrdinalEnum.buildInt(MetricModality.class);
	
	public static final MetricModality valueOf(int x) {
	    return map.get(x);
	}
	
	public int asInt() {
	    return x;
	}
}
