package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;

public enum MetricModality implements EnumParseable<MetricModality>, Formatable {
	MANUAL,
	APERIODIC,
	VERIFIED;
	
	@Override
	public MetricModality parse(ByteBuffer bb) {
		return MetricModality.valueOf(Bits.getUnsignedShort(bb));
	}
	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, asInt());
	}
	
	public static final MetricModality valueOf(int x) {
		switch(x) {
		case 0x4000:
			return MANUAL;
		case 0x2000:
			return APERIODIC;
		case 0x1000:
			return VERIFIED;
		default:
			return null;
		}
	}
	
	public int asInt() {
		switch(this) {
		case MANUAL:
			return 0x4000;
		case APERIODIC:
			return 0x2000;
		case VERIFIED:
			return 0x1000;
		default:
			throw new IllegalArgumentException("Unknown MetricModality:"+this);
		}
	}
}
