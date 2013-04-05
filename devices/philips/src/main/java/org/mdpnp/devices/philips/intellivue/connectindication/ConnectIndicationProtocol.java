package org.mdpnp.devices.philips.intellivue.connectindication;

import java.nio.ByteBuffer;

import org.mdpnp.devices.philips.intellivue.Protocol;

public interface ConnectIndicationProtocol extends Protocol {
	public void format(ConnectIndication message, ByteBuffer bb);
	
	@Override
	public ConnectIndication parse(ByteBuffer bb);
}
