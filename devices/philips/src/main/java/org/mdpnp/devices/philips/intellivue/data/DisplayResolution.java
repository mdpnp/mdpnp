package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public class DisplayResolution implements Value {
	private short prePoint, postPoint;
	
	@Override
	public void format(ByteBuffer bb) {
	    Bits.putUnsignedByte(bb, prePoint);
	    Bits.putUnsignedByte(bb, postPoint);
	}
	
	@Override
	public void parse(ByteBuffer bb) {
		prePoint = Bits.getUnsignedByte(bb);
		postPoint = Bits.getUnsignedByte(bb);
		
	};

	@Override
	public java.lang.String toString() {
		return "[prePoint="+prePoint+",postPoint="+postPoint+"]";
	}
	
	public short getPostPoint() {
		return postPoint;
	}
	public short getPrePoint() {
		return prePoint;
	}
}

