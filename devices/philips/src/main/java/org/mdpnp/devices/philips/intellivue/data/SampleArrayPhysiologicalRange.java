package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public class SampleArrayPhysiologicalRange implements Value {

	private int lowerScaledValue, upperScaledValue;
	
	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, lowerScaledValue);
		Bits.putUnsignedShort(bb, upperScaledValue);
	}

	@Override
	public void parse(ByteBuffer bb) {
		lowerScaledValue = Bits.getUnsignedShort(bb);
		upperScaledValue = Bits.getUnsignedShort(bb);
	}

	@Override
	public java.lang.String toString() {
		return "[lowerScaledValue="+lowerScaledValue+",upperScaledValue="+upperScaledValue+"]";
	}
}
