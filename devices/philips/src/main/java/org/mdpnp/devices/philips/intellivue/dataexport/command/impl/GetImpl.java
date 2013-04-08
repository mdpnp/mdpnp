package org.mdpnp.devices.philips.intellivue.dataexport.command.impl;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.data.ManagedObjectIdentifier;
import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportMessage;
import org.mdpnp.devices.philips.intellivue.dataexport.command.Get;

public class GetImpl implements Get {
	private final ManagedObjectIdentifier managedObject = new ManagedObjectIdentifier();
	private long scope;
	private final List<OIDType> list  = new ArrayList<OIDType>();
	
	private DataExportMessage message;
	
	@Override
	public DataExportMessage getMessage() {
		return message;
	}
	@Override
	public void setMessage(DataExportMessage message) {
		this.message = message;
	}
	
	@Override
	public void parse(ByteBuffer bb) {
		parse(bb, true);
	}
	@Override
	public void parseMore(ByteBuffer bb) {
		parse(bb, false);
	}
	
	@SuppressWarnings("unused")
    private void parse(ByteBuffer bb, boolean clear) {
		managedObject.parse(bb);
		scope = Bits.getUnsignedInt(bb);
		int count = Bits.getUnsignedShort(bb);
		int length = Bits.getUnsignedShort(bb);
		list.clear();
		for(int i = 0; i < count; i++) {
			list.add(OIDType.parse(bb));
		}
	}

	@Override
	public void format(ByteBuffer bb) {
		managedObject.format(bb);
		Bits.putUnsignedInt(bb, scope);
		Bits.putUnsignedShort(bb, list.size());
		bb.mark();
		Bits.putUnsignedShort(bb, 0);
		int pos = bb.position();
		for(OIDType t : list) {
			t.format(bb);
		}
		int length = bb.position() - pos;
		bb.reset();		
		Bits.putUnsignedShort(bb, length);
		bb.position(bb.position()+length);
	}

	@Override
	public List<OIDType> getAttributeId() {
		return list;
	}
	
	@Override
	public ManagedObjectIdentifier getManagedObject() {
		return managedObject;
	}
	
	@Override
	public String toString() {
		return "[managedObject="+managedObject+",scope="+scope+",list="+list+"]";
	}

}
