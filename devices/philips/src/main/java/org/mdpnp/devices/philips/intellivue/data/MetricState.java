package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public class MetricState implements Value {
	private int bitfield;
	
	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, bitfield);
	}
	
	@Override
	public void parse(ByteBuffer bb) {
		bitfield = Bits.getUnsignedShort(bb);
	}
	
	@Override
	public java.lang.String toString() {
		return isOff()?"OFF":"ON";
	}
	
	private static final int METRIC_OFF = 0x8000;
	
	public boolean isOff() {
		return 0 != (METRIC_OFF & bitfield);
	}

}
