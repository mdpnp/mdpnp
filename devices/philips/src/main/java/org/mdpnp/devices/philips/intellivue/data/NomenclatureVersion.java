package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public class NomenclatureVersion implements Value {

	private long version;
	
	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedInt(bb, version);
	}

	@Override
	public void parse(ByteBuffer bb) {
		version = Bits.getUnsignedInt(bb);
	}
	
	@Override
	public java.lang.String toString() {
		return Long.toString(version);
	}
}
