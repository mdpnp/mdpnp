package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;

public class ManagedObjectIdentifier implements Parseable, Formatable {
	private OIDType oidType = OIDType.lookup(0);
	private final GlobalHandle globalHandle = new GlobalHandle();
	
	public GlobalHandle getGlobalHandle() {
		return globalHandle;
	}
	
	public OIDType getOidType() {
		return oidType;
	}
	
	
	public void setOidType(ObjectClass type) {
		this.oidType = OIDType.lookup(type.asInt());
	}
	public void setOidType(OIDType type) {
		this.oidType = type;
	}
	
	@Override
	public void parse(ByteBuffer bb) {
		oidType = OIDType.lookup(Bits.getUnsignedShort(bb));
		globalHandle.parse(bb);
	}
	
	@Override
	public void format(ByteBuffer bb) {
		oidType.format(bb);
		globalHandle.format(bb);
	}
	
	@Override
	public java.lang.String toString() {
		java.lang.String ot = ObjectClass.valueOf(oidType.getType()) == null ? oidType.toString() : ObjectClass.valueOf(oidType.getType()).toString();
		return "[oidType="+ot+",globalHandle="+globalHandle+"]";
	}
}
