package org.mdpnp.devices.philips.intellivue.dataexport.error;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.data.ManagedObjectIdentifier;
import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.util.Util;

public class ErrorDetailGetListImpl implements ErrorDetailGetList {
	private final ManagedObjectIdentifier managedObject = new ManagedObjectIdentifier();
	private final List<GetError> list = new ArrayList<GetError>();
	
	public static class GetErrorImpl implements GetError {
		private ErrorStatus errorStatus;
		private OIDType oid;
		
		@Override
		public void format(ByteBuffer bb) {
			Bits.putUnsignedShort(bb, errorStatus.asInt());
			oid.format(bb);
		}

		@Override
		public void parse(ByteBuffer bb) {
			errorStatus = ErrorStatus.valueOf(Bits.getUnsignedShort(bb));
			oid = OIDType.lookup(Bits.getUnsignedShort(bb));
		}
		public ErrorStatus getErrorStatus() {
			return errorStatus;
		}
		public OIDType getOid() {
			return oid;
		}
		@Override
		public String toString() {
			return "[oid="+oid+",errorStatus="+errorStatus+"]";
		}
	}

	@Override
	public ManagedObjectIdentifier getManagedObject() {
		return managedObject;
	}

	@SuppressWarnings("unused")
    @Override
	public void parse(ByteBuffer bb) {
		managedObject.parse(bb);
		int count = Bits.getUnsignedShort(bb);
		int length = Bits.getUnsignedShort(bb);
		list.clear();
		for(int i = 0; i < count; i++) {
			GetError err = new GetErrorImpl();
			err.parse(bb);
			list.add(err);
		}
	}
	
	@Override
	public void format(ByteBuffer bb) {
		managedObject.format(bb);
		Util.PrefixLengthShort.write(bb, list);
	}
	
	@Override
	public List<GetError> getList() {
		return list;
	}
	
	@Override
	public String toString() {
		return "[object="+managedObject+",list="+list+"]";
	}
}
