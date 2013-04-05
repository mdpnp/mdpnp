package org.mdpnp.devices.philips.intellivue.dataexport.event.impl;

import java.nio.ByteBuffer;

import org.mdpnp.devices.philips.intellivue.data.AttributeValueList;
import org.mdpnp.devices.philips.intellivue.data.ManagedObjectIdentifier;
import org.mdpnp.devices.philips.intellivue.dataexport.event.MdsCreateEvent;

public class MdsCreateEventImpl implements MdsCreateEvent {
	private final ManagedObjectIdentifier managedObject = new ManagedObjectIdentifier();
	private final AttributeValueList attrs = new AttributeValueList();
	
	
	
	@Override
	public AttributeValueList getAttributes() {
		return attrs;
	}
	
	public ManagedObjectIdentifier getManagedObject() {
		return managedObject;
	}
	
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
			attrs.reset();
		}
		attrs.parse(bb);
	}

	@Override
	public void format(ByteBuffer bb) {
		managedObject.format(bb);
		attrs.format(bb);
	}

	@Override
	public String toString() {
		return "[managedObject="+managedObject+",attrs="+attrs+"]";
	}
}
