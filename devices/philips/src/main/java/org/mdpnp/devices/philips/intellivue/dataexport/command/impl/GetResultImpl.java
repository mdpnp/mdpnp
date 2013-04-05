package org.mdpnp.devices.philips.intellivue.dataexport.command.impl;

import java.nio.ByteBuffer;

import org.mdpnp.devices.philips.intellivue.data.AttributeValueList;
import org.mdpnp.devices.philips.intellivue.data.ManagedObjectIdentifier;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportMessage;
import org.mdpnp.devices.philips.intellivue.dataexport.command.GetResult;

public class GetResultImpl implements GetResult {

	private final ManagedObjectIdentifier managedObject = new ManagedObjectIdentifier();
	private final AttributeValueList attr = new AttributeValueList();
	
	private DataExportMessage message;
	
	@Override
	public void parse(ByteBuffer bb) {
		parse(bb, true);
	}
	@Override
	public void parseMore(ByteBuffer bb) {
		parse(bb, false);
	}
	
	private void parse(ByteBuffer bb, boolean clear) {
		managedObject.parse(bb);
		if(clear) {
			attr.reset();
		}
		attr.parse(bb);
	}

	@Override
	public void format(ByteBuffer bb) {
		managedObject.format(bb);
		attr.format(bb);
	}

	@Override
	public ManagedObjectIdentifier getManagedObject() {
		return managedObject;
	}

	@Override
	public AttributeValueList getAttributeList() {
		return attr;
	}
	
	@Override
	public String toString() {
		return "[managedObject="+managedObject+",attrs="+attr+"]";
	}
	@Override
	public void setMessage(DataExportMessage message) {
		this.message = message;
	}
	@Override
	public DataExportMessage getMessage() {
		return message;
	}

}
