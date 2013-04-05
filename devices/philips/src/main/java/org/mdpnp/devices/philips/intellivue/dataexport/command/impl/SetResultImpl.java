package org.mdpnp.devices.philips.intellivue.dataexport.command.impl;

import java.nio.ByteBuffer;

import org.mdpnp.devices.philips.intellivue.data.AttributeValueList;
import org.mdpnp.devices.philips.intellivue.data.ManagedObjectIdentifier;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportMessage;
import org.mdpnp.devices.philips.intellivue.dataexport.command.SetResult;

public class SetResultImpl implements SetResult {

	private final ManagedObjectIdentifier managedObject = new ManagedObjectIdentifier();
	private final AttributeValueList avl = new AttributeValueList();
	
	private DataExportMessage message;
	
	@Override
	public void parseMore(ByteBuffer bb) {
		managedObject.parse(bb);
		avl.parseMore(bb);
	}

	@Override
	public void setMessage(DataExportMessage message) {
		this.message = message;
	}

	@Override
	public DataExportMessage getMessage() {
		return message;
	}

	@Override
	public void parse(ByteBuffer bb) {
		managedObject.parse(bb);
		avl.parse(bb);
	}

	@Override
	public void format(ByteBuffer bb) {
		managedObject.format(bb);
		avl.format(bb);
	}

	@Override
	public ManagedObjectIdentifier getManagedObject() {
		return managedObject;
	}

	@Override
	public AttributeValueList getAttributes() {
		return avl;
	}
	@Override
	public String toString() {
		return "[managedObject="+managedObject+",attrs="+avl+"]";
	}
}
