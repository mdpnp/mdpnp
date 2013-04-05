package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public class SampleArrayCalibrationSpecification implements Value {
	private final Float lowerAbsoluteValue = new Float();
	private final Float upperAbsoluteValue = new Float();
	private int lowerScaledValue, upperScaledValue, increment, calType;
	
	@Override
	public void format(ByteBuffer bb) {
		lowerAbsoluteValue.format(bb);
		upperAbsoluteValue.format(bb);
		Bits.putUnsignedShort(bb, lowerScaledValue);
		Bits.putUnsignedShort(bb, upperScaledValue);
		Bits.putUnsignedShort(bb, increment);
		Bits.putUnsignedShort(bb, calType);
	}
	
	@Override
	public void parse(ByteBuffer bb) {
		lowerAbsoluteValue.parse(bb);
		upperAbsoluteValue.parse(bb);
		lowerScaledValue = Bits.getUnsignedShort(bb);
		upperScaledValue = Bits.getUnsignedShort(bb);
		increment = Bits.getUnsignedShort(bb);
		calType = Bits.getUnsignedShort(bb);
	}

	
	@Override
	public java.lang.String toString() {
		return "[lowerAbsoluteValue="+lowerAbsoluteValue+",upperAbsoluteValue="+upperAbsoluteValue+",lowerScaledValue="+lowerScaledValue+",upperScaledValue="+upperScaledValue+",increment="+increment+",calType="+calType+"]";
	}
}
