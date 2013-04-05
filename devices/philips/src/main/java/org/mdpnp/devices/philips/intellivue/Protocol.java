package org.mdpnp.devices.philips.intellivue;

import java.nio.ByteBuffer;

public interface Protocol {
	Message parse(ByteBuffer bb);
	void format(Message message, ByteBuffer bb);
}
