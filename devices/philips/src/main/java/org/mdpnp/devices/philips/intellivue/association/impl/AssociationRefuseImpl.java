package org.mdpnp.devices.philips.intellivue.association.impl;

import org.mdpnp.devices.philips.intellivue.association.AssociationMessageType;
import org.mdpnp.devices.philips.intellivue.association.AssociationRefuse;

public class AssociationRefuseImpl extends AbstractAssociationMessage implements AssociationRefuse {

	@Override
	public AssociationMessageType getType() {
		return AssociationMessageType.Refuse;
	}

	private static final byte[] PRESENTATION_HEADER = new byte[0];
	private static final byte[] PRESENTATION_TRAILER = PRESENTATION_HEADER;
	
	@Override
	public byte[] getPresentationHeader() {
		return PRESENTATION_HEADER;
	}

	
	@Override
	public byte[] getPresentationTrailer() {
		return PRESENTATION_TRAILER;
	}

}
