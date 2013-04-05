package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public class Handle implements Value {

	private int handle;
	
	@Override
	public void parse(ByteBuffer bb) {
		this.handle = Bits.getUnsignedShort(bb);
	}
	@Override
	public void format(ByteBuffer bb) {
	    Bits.putUnsignedShort(bb, handle);
	}
	
	
	public int getHandle() {
		return handle;
	}
	public void setHandle(int handle) {
		this.handle = handle;
	}
	
	@Override
	public java.lang.String toString() {
		return Integer.toString(handle);
	}
}
