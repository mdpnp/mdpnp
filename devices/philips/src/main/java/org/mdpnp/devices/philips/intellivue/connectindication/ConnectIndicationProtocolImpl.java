package org.mdpnp.devices.philips.intellivue.connectindication;

import java.nio.ByteBuffer;

import org.mdpnp.devices.philips.intellivue.Message;

public class ConnectIndicationProtocolImpl implements ConnectIndicationProtocol {
	private final ConnectIndication ci = new ConnectIndicationImpl();
	
	@Override
	public ConnectIndication parse(ByteBuffer bb) {
		ci.parse(bb);
		return ci;
	}

	@Override
	public void format(Message message, ByteBuffer bb) {
		if(message instanceof ConnectIndication) {
			format((ConnectIndication)message, bb);
		}
	}
	
	@Override
	public void format(ConnectIndication message, ByteBuffer bb) {
		message.format(bb);
	}

}
