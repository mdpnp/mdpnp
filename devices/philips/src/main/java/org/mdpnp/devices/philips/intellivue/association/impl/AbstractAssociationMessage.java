package org.mdpnp.devices.philips.intellivue.association.impl;

import java.nio.ByteBuffer;

import org.mdpnp.devices.philips.intellivue.association.AssociationMessage;

public abstract class AbstractAssociationMessage implements AssociationMessage { 
	@Override
	public boolean advancePastPresentationHeader(ByteBuffer bb) {
		int pLength = getPresentationHeader().length;
		if(bb.remaining()>=pLength) {
			bb.position(bb.position() + pLength);
			return true;
		} else {
			return false;
		}
		
	}
	
	@Override
	public void format(ByteBuffer bb) {
		
	}
	@Override
	public void parse(ByteBuffer bb) {
		
	}
	
	@Override
	public String toString() {
		return getType().toString();
	}
}
