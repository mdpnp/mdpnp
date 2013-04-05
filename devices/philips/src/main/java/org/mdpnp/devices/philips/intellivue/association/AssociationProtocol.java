package org.mdpnp.devices.philips.intellivue.association;

import java.nio.ByteBuffer;

import org.mdpnp.devices.philips.intellivue.Protocol;

public interface AssociationProtocol extends Protocol {
	AssociationMessage parse(ByteBuffer bb);
	void format(AssociationMessage message, ByteBuffer bb);
}
