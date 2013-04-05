package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.io.util.Bits;

public class ProductionSpecification implements Value {

	public static class Entry implements Value {
		private ProductionSpecificationType specType;
		private ComponentId componentId;
		private final VariableLabel prodSpec = new VariableLabel();
		
		@Override
		public void parse(ByteBuffer bb) {
			specType = ProductionSpecificationType.valueOf(Bits.getUnsignedShort(bb));
			componentId = ComponentId.valueOf(Bits.getUnsignedShort(bb));
			prodSpec.parse(bb);
		}
		
		@Override
		public void format(ByteBuffer bb) {
			Bits.putUnsignedShort(bb, specType.asInt());
			Bits.putUnsignedShort(bb, componentId.asInt());
			prodSpec.format(bb);
		}
		
		@Override
		public java.lang.String toString() {
			return "[specType="+specType+",componentId="+componentId+",prodSpec="+prodSpec+"]";
		}
		
		public ComponentId getComponentId() {
			return componentId;
		}
		public VariableLabel getProdSpec() {
			return prodSpec;
		}
		public ProductionSpecificationType getSpecType() {
			return specType;
		}
		public void setComponentId(ComponentId componentId) {
			this.componentId = componentId;
		}
		public void setSpecType(ProductionSpecificationType specType) {
			this.specType = specType;
		}
	}
	
	public VariableLabel getByComponentId(ProductionSpecificationType type, ComponentId componentId) {
		for(Entry e : list) {
			if(e.specType.equals(type) && e.componentId.equals(componentId)) {
				return e.prodSpec;
			}
		}
		return null;
	}
	
	private final List<Entry> list = new ArrayList<Entry>();
	
	public List<Entry> getList() {
		return list;
	}
	
	@Override
	public void parse(ByteBuffer bb) {
		int count = Bits.getUnsignedShort(bb);
		@SuppressWarnings("unused")
		int length = Bits.getUnsignedShort(bb);
		list.clear();
		for(int i = 0; i < count; i++) {
			Entry e = new Entry();
			e.parse(bb);
			list.add(e);
		}
	}

	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, list.size());
		bb.mark();
		Bits.putUnsignedShort(bb, 0);
		int pos = bb.position();
		for(Entry e : list) {
			e.format(bb);
		}
		int length = bb.position() - pos;
		bb.reset();
		Bits.putUnsignedShort(bb, length);
		bb.position(bb.position()+length);
		
	}
	
	@Override
	public java.lang.String toString() {
		return list.toString();
	}

}
