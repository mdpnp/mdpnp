package org.mdpnp.devices.philips.intellivue.attribute;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.data.AttributeId;
import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.data.Value;
import org.mdpnp.devices.philips.intellivue.util.Util;

public class AttributeImpl<T extends Value> implements Attribute<T> {

	private T value;
	
	protected OIDType oid;
	
	@Override
	public OIDType getOid() {
		return oid;
	}
	
	public AttributeImpl(OIDType oid, T value) {
		this.oid = oid;
		this.value = value;
	}
	
	@SuppressWarnings("unused")
    @Override
	public void parse(ByteBuffer bb) {
		oid = OIDType.lookup(Bits.getUnsignedShort(bb));
		int length = Bits.getUnsignedShort(bb);
		value.parse(bb);
	}

	@Override
	public void format(ByteBuffer bb) {
//		value.format(bb);
		oid.format(bb);
		Util.PrefixLengthShort.write(bb, value);
	}

	@Override
	public T getValue() {
		return value;
	}
	

	private static final Object x(int x) {
		AttributeId id = AttributeId.valueOf(x);
		if(id != null) {
			return id;
		} else {
			return x;
		}
	}
	
	@Override
	public String toString() {
		
		return "[attrId="+x(oid.getType())+",value="+(null==value?"null":value.toString())+"]";
	}


}
