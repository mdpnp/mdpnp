package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public class InvokeId implements Value {

	private int invokeId;
	
	@Override
	public void format(ByteBuffer bb) {
	    Bits.putUnsignedShort(bb, invokeId);
	}

	@Override
	public void parse(ByteBuffer bb) {
		invokeId = Bits.getUnsignedShort(bb);
	}

	
	@Override
	public java.lang.String toString() {
		return Integer.toString(invokeId);
	}
}
