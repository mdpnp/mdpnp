package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.io.util.Bits;

public class VisualGrid implements Value {
	public static class Entry implements Value {
		private final Float absoluteValue = new Float();
		private int scaledValue, level;
		@Override
		public void format(ByteBuffer bb) {
			absoluteValue.format(bb);
			Bits.putUnsignedShort(bb, scaledValue);
			Bits.putUnsignedShort(bb, level);
		}
		@Override
		public void parse(ByteBuffer bb) {
			absoluteValue.parse(bb);
			scaledValue = Bits.getUnsignedShort(bb);
			level = Bits.getUnsignedShort(bb);
		}
		@Override
		public java.lang.String toString() {
			return "[absoluteValue="+absoluteValue+",scaledValue="+scaledValue+",level="+level+"]";
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
		bb.position(bb.position() + length);
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
