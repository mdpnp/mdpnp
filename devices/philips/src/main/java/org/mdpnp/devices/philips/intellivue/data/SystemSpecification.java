package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.philips.intellivue.attribute.Attribute;
import org.mdpnp.devices.philips.intellivue.attribute.AttributeFactory;

public class SystemSpecification implements Value {
	private final AttributeValueList attrs = new AttributeValueList();
	
	private final Attribute<MdibObjectSupport> objectSupport = AttributeFactory.getMdibObjectSupport();
	
	@Override
	public void format(ByteBuffer bb) {
		attrs.add(objectSupport);
		attrs.format(bb);
	}
	
	public MdibObjectSupport getMdibObjectSupport() {
		return objectSupport.getValue();
	}
	
	public AttributeValueList getAttributes() {
		return attrs;
	}
	
	@Override
	public void parse(ByteBuffer bb) {
		attrs.reset();
		attrs.parse(bb);
		attrs.get(objectSupport);
		
	}
	
	@Override
	public java.lang.String toString() {
		
		if(attrs.getList().isEmpty()) {
			return objectSupport.toString();
		} else {
			StringBuilder sb = new StringBuilder();
			sb.append("[attrs=");
			sb.append(attrs);
			sb.append(",objectSupport=");
			sb.append(objectSupport);
			sb.append("]");
			return sb.toString();
		}
	}
}
