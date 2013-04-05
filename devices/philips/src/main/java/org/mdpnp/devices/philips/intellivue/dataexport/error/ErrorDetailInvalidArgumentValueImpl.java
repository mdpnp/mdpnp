package org.mdpnp.devices.philips.intellivue.dataexport.error;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.data.ManagedObjectIdentifier;
import org.mdpnp.devices.philips.intellivue.data.OIDType;

public class ErrorDetailInvalidArgumentValueImpl implements ErrorDetailInvalidArgumentValue {
	private final ManagedObjectIdentifier managedObject = new ManagedObjectIdentifier();
	private OIDType actionType;
	private int length;
	
	
	@Override
	public void parse(ByteBuffer bb) {
		managedObject.parse(bb);
		actionType = OIDType.parse(bb);
		length = Bits.getUnsignedShort(bb);
	}

	@Override
	public void format(ByteBuffer bb) {
		managedObject.format(bb);
		actionType.format(bb);
		Bits.putUnsignedShort(bb, length);
	}

	@Override
	public ManagedObjectIdentifier getManagedObject() {
		return managedObject;
	}

	@Override
	public OIDType getActionType() {
		return actionType;
	}

	@Override
	public int getLength() {
		return length;
	}
	
	@Override
	public String toString() {
		return "[managedObject="+managedObject+",actionType="+actionType+",length="+length+"]";
	}

}
