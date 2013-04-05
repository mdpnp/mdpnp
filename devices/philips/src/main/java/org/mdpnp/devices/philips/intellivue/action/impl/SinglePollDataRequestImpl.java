package org.mdpnp.devices.philips.intellivue.action.impl;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.action.SinglePollDataRequest;
import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.data.Type;
import org.mdpnp.devices.philips.intellivue.dataexport.command.ActionResult;

public class SinglePollDataRequestImpl implements SinglePollDataRequest {
	private int pollNumber;
	private final Type polledObjectType = new Type();
	private OIDType polledAttributeGroup;
	
	private ActionResult action;
	
	@Override
	public ActionResult getAction() {
		return action;
	}
	
	@Override
	public void setAction(ActionResult action) {
		this.action = action;
	}
	
	@Override
	public void format(ByteBuffer bb) {
	    Bits.putUnsignedShort(bb, pollNumber);
		polledObjectType.format(bb);
		polledAttributeGroup.format(bb);
	}
	
	@Override
	public void parse(ByteBuffer bb) {
		pollNumber = Bits.getUnsignedShort(bb);
		polledObjectType.parse(bb);
		polledAttributeGroup = OIDType.parse(bb);
	}
	
	@Override
	public void parseMore(ByteBuffer bb) {
		parse(bb);
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
	public void setPolledAttributeGroup(OIDType type) {
		this.polledAttributeGroup = type;
	}
	

	@Override
	public void setPollNumber(int x) {
		this.pollNumber = x;
	}
	@Override
	public String toString() {
		return "[pollNumber="+pollNumber+",polledObjType="+polledObjectType+",polledAttrGroup="+polledAttributeGroup+"]";
	}
}
