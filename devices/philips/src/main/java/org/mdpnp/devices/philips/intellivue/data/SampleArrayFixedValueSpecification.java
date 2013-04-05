package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.io.util.Bits;

public class SampleArrayFixedValueSpecification implements Value {
	public static class Entry implements Value {
		private SampleArrayFixedValId valId;
		private int fixedValue;
		@Override
		public void format(ByteBuffer bb) {
			Bits.putUnsignedShort(bb,valId.asInt());
			Bits.putUnsignedShort(bb, fixedValue);
		}
		@Override
		public void parse(ByteBuffer bb) {
			valId = SampleArrayFixedValId.valueOf(Bits.getUnsignedShort(bb));
			fixedValue = Bits.getUnsignedShort(bb);
		}
		@Override
		public java.lang.String toString() {
			return "[valId="+valId+",fixedValue="+fixedValue+"]";
		}
	}
	
	private final List<Entry> list = new ArrayList<Entry>();

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
	public java.lang.String toString() {
		return list.toString();
	}
}
