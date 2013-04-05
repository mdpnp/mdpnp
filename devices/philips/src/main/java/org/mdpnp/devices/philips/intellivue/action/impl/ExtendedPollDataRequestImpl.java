package org.mdpnp.devices.philips.intellivue.action.impl;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.action.ExtendedPollDataRequest;
import org.mdpnp.devices.philips.intellivue.data.AttributeValueList;
import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.data.Type;
import org.mdpnp.devices.philips.intellivue.dataexport.command.ActionResult;

public class ExtendedPollDataRequestImpl implements ExtendedPollDataRequest {
	private int pollNumber;
	private final Type polledObjectType = new Type();
	private OIDType polledAttributeGroup = OIDType.lookup(0);
	private final AttributeValueList pollExtAttr = new AttributeValueList();
	
	private ActionResult action;
	
	@Override
	public ActionResult getAction() {
		return this.action;
	}
	
	@Override
	public void setAction(ActionResult action) {
		this.action = action;
	}
	
	@Override
	public void parse(ByteBuffer bb) {
		pollNumber = Bits.getUnsignedShort(bb);
		polledObjectType.parse(bb);
		polledAttributeGroup = OIDType.parse(bb);
		pollExtAttr.parse(bb);
	}
	
	@Override
	public void format(ByteBuffer bb) {
	    Bits.putUnsignedShort(bb, pollNumber);
		polledObjectType.format(bb);
		polledAttributeGroup.format(bb);
		pollExtAttr.format(bb);
		
	}
	
	@Override
	public void parseMore(ByteBuffer bb) {
		parse(bb);
	}
	
	@Override
	public String toString() {
		return "[pollNumber="+pollNumber+",polledObjectType="+polledObjectType+",oidType="+polledAttributeGroup+",pollExtAttr="+pollExtAttr+"]";
	}
	
	@Override
	public OIDType getPolledAttributeGroup() {
		return polledAttributeGroup;
	}
	
	@Override
	public Type getPolledObjectType() {
		return polledObjectType;
	}
	@Override
	public int getPollNumber() {
		return pollNumber;
	}
	@Override
	public void setPolledAttributeGroup(OIDType lookup) {
		this.polledAttributeGroup = lookup;
	}
	@Override
	public void setPollNumber(int pollNumber) {
		this.pollNumber = pollNumber;
	}

	@Override
	public AttributeValueList getPollExtra() {
		return pollExtAttr;
	}
}
