package org.mdpnp.devices.philips.intellivue.dataexport.error;

import java.nio.ByteBuffer;

import org.mdpnp.devices.philips.intellivue.data.ManagedObjectIdentifier;

public class ErrorDetailNoSuchObjectInstanceImpl implements ErrorDetailNoSuchObjectInstance {
	private final ManagedObjectIdentifier managedObject = new ManagedObjectIdentifier();

	@Override
	public void parse(ByteBuffer bb) {
		managedObject.parse(bb);
	}

	@Override
	public void format(ByteBuffer bb) {
		managedObject.format(bb);
	}

	@Override
	public ManagedObjectIdentifier getManagedObject() {
		return managedObject;
	}
	@Override
	public String toString() {
		return ""+managedObject;
	}
	

}
