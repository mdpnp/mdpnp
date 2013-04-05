package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public class GlobalHandle extends Handle {
	private int mdsContext;
	
	@Override
	public void parse(ByteBuffer bb) {
		this.mdsContext = Bits.getUnsignedShort(bb);
		super.parse(bb);
	}
	
	@Override
	public void format(ByteBuffer bb) {
	    Bits.putUnsignedShort(bb, mdsContext);
		super.format(bb);
	}
	
	
	
	public int getMdsContext() {
		return mdsContext;
	}
	
	public void setMdsContext(int mdsContext) {
		this.mdsContext = mdsContext;
	}
	
	@Override
	public java.lang.String toString() {
		return "[mdsContext="+mdsContext+",handle="+getHandle()+"]";
	}
}
