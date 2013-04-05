package org.mdpnp.devices.philips.intellivue.association;

import java.nio.ByteBuffer;

import org.mdpnp.devices.philips.intellivue.Message;

public interface AssociationMessage extends Message {
	AssociationMessageType getType();
	byte[] getPresentationHeader();
	byte[] getPresentationTrailer();
	boolean advancePastPresentationHeader(ByteBuffer bb);
}
