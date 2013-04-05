package org.mdpnp.devices.philips.intellivue.action.impl;

import java.nio.ByteBuffer;

import org.mdpnp.devices.philips.intellivue.action.ObservationPoll;
import org.mdpnp.devices.philips.intellivue.data.AttributeValueList;
import org.mdpnp.devices.philips.intellivue.data.Handle;

public class ObservationPollImpl implements ObservationPoll {
	private final Handle handle = new Handle();
	private final AttributeValueList attrList = new AttributeValueList();
	@Override
	public void parse(ByteBuffer bb) {
		handle.parse(bb);
		attrList.reset();
		attrList.parse(bb);
	}
	@Override
	public void format(ByteBuffer bb) {
		handle.format(bb);
		attrList.format(bb);
	}
	@Override
	public Handle getHandle() {
		return handle;
	}
	@Override
	public AttributeValueList getAttributes() {
		return attrList;
	}
	@Override
	public String toString() {
		return "[handle="+handle+",attrList=["+attrList+"]]";
	}
	
}