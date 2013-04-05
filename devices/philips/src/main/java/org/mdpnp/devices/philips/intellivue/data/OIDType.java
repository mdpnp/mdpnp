package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;

public class OIDType implements Formatable {
	private final int type;
	
	private static final Map<Integer, OIDType> values = new HashMap<Integer, OIDType>();
	
	public static OIDType lookup(int type) {
		if(values.containsKey(type)) {
			return values.get(type);
		} else {
			OIDType t = new OIDType(type);
			values.put(type, t);
			return t;
		}
	}
	
	private OIDType(int type) {
		this.type = type;
	}

	@Override
	public java.lang.String toString() {
		ObjectClass oc = ObjectClass.valueOf(type);
		if(oc == null) {
			AttributeId id = AttributeId.valueOf(type);
			if(null == id) {
				ObservedValue ov = ObservedValue.valueOf(type);
				if(null == ov) {
					return Integer.toString(type);
				} else {
					return ov.toString();
				}
			} else {
				return id.toString();
			}
		} else {
			return oc.toString();
		}
		
	}
	
	@Override
	public int hashCode() {
		return type;
	}
	
	public static OIDType parse(ByteBuffer bb) {
		return OIDType.lookup(Bits.getUnsignedShort(bb));
	}
	
	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, type);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof OIDType) {
			return type==((OIDType)obj).type;
		} else {
			return false;
		}
	}
	public int getType() {
		return type;
	}
	public static void main(java.lang.String[] args) {
		System.out.println(OIDType.lookup(0XF23A));
	}
	
}
