package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;

public class PrivateOID implements Parseable, Formatable {
	private int oid;
	
	
	@Override
	public java.lang.String toString() {
		return Integer.toString(oid);
	}


	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, oid);
	}


	@Override
	public void parse(ByteBuffer bb) {
		oid = Bits.getUnsignedShort(bb);
	}
	
	public int getOid() {
		return oid;
	}
}
