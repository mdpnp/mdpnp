package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public class ScaleAndRangeSpecification implements Value {

	private final Float lowerAbsoluteValue = new Float();
	private final Float upperAbsoluteValue = new Float();
	private int lowerScaledValue, upperScaledValue;
	
	@Override
	public void format(ByteBuffer bb) {
		lowerAbsoluteValue.format(bb);
		upperAbsoluteValue.format(bb);
		Bits.putUnsignedShort(bb, lowerScaledValue);
		Bits.putUnsignedShort(bb, upperScaledValue);
	}

	@Override
	public void parse(ByteBuffer bb) {
		lowerAbsoluteValue.parse(bb);
		upperAbsoluteValue.parse(bb);
		Bits.putUnsignedShort(bb, lowerScaledValue);
		Bits.putUnsignedShort(bb, upperScaledValue);
	}

	@Override
	public java.lang.String toString() {
		return "[lowerAbsoluteValue="+lowerAbsoluteValue+",upperAbsoluteValue="+upperAbsoluteValue+",lowerScaledValue="+lowerScaledValue+",upperScaledValue="+upperScaledValue+"]";
	}
}
