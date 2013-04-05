package org.mdpnp.devices.philips.intellivue.association.impl;

import org.mdpnp.devices.philips.intellivue.association.AssociationFinish;
import org.mdpnp.devices.philips.intellivue.association.AssociationMessageType;

public class AssociationFinishImpl extends AbstractAssociationMessage implements AssociationFinish {

	@Override
	public AssociationMessageType getType() {
		return AssociationMessageType.Finish;
	}
	
	private static final byte[] PRESENTATION_HEADER = new byte[] {
		0x61, (byte) 0x80, 0x30, (byte) 0x80, 0x02, 0x01, 0x01, (byte) 0xA0, (byte) 0x80, 0x62, (byte) 0x80, (byte) 0x80, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00
	};
	private static final byte[] PRESENTATION_TRAILER = new byte[] {
		0x00, 0x00, 0x00, 0x00
	};

	@Override
	public byte[] getPresentationHeader() {
		return PRESENTATION_HEADER;
	}
	@Override
	public byte[] getPresentationTrailer() {
		return PRESENTATION_TRAILER;
	}
}
