package org.mdpnp.devices.philips.intellivue.dataexport.error;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.data.ManagedObjectIdentifier;
import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.dataexport.ModifyOperator;
import org.mdpnp.devices.philips.intellivue.util.Util;

public class ErrorDetailSetListImpl implements ErrorDetailSetList {
	private final ManagedObjectIdentifier managedObject = new ManagedObjectIdentifier();
	private final List<SetError> list = new ArrayList<SetError>();
	
	public static class SetErrorImpl implements SetError {
		private ErrorStatus errorStatus;
		private ModifyOperator modifyOperator;
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
		public ModifyOperator getModifyOperator() {
			return modifyOperator;
		}
		@Override
		public String toString() {
			return "[oid="+oid+",errorStatus="+errorStatus+",modifyOperator=" + modifyOperator+"]";
		}
	}

	@Override
	public ManagedObjectIdentifier getManagedObject() {
		return managedObject;
	}

	@Override
	public void parse(ByteBuffer bb) {
		managedObject.parse(bb);
		Util.PrefixLengthShort.read(bb, list, true, SetErrorImpl.class);
	}
	
	@Override
	public void format(ByteBuffer bb) {
		managedObject.format(bb);
		Util.PrefixLengthShort.write(bb, list);
	}
	
	@Override
	public List<SetError> getList() {
		return list;
	}
	
	@Override
	public String toString() {
		return "[object="+managedObject+",list="+list+"]";
	}
}
