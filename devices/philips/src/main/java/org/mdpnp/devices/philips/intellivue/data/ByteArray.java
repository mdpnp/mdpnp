package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

public class ByteArray implements Value {

	private final byte[] array;
	
	public ByteArray(byte[] array) {
		this.array = array;
	}
	
	public byte[] getArray() {
		return array;
	}
	
	@Override
	public void format(ByteBuffer bb) {
		bb.put(array);
	}

	@Override
	public void parse(ByteBuffer bb) {
		bb.get(array);
	}

}
