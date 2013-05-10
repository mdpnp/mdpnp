package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

public class Altitude implements Value {

	private short altitude;
	
	@Override
	public void parse(ByteBuffer bb) {
		altitude = bb.getShort();
	}

	@Override
	public void format(ByteBuffer bb) {
		bb.putShort(altitude);
	}
	
	@Override
	public java.lang.String toString() {
	    return Short.toString(altitude);
	}
}
