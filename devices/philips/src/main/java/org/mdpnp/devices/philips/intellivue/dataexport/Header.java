package org.mdpnp.devices.philips.intellivue.dataexport;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;

public class Header implements Parseable, Formatable {

	private int sessionId = 0xE100;
	private int contextId;
	
	@Override
	public void parse(ByteBuffer bb) {
		sessionId = Bits.getUnsignedShort(bb);
		contextId = Bits.getUnsignedShort(bb);
	}
	
	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, sessionId);
		Bits.putUnsignedShort(bb, contextId);
	}
	
}
