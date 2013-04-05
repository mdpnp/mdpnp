package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.io.util.Bits;

public class SampleArrayCompoundObservedValue implements Value {

	private final List<SampleArrayObservedValue> list = new ArrayList<SampleArrayObservedValue>();
	
	@Override
	public void parse(ByteBuffer bb) {
		int count = Bits.getUnsignedShort(bb);
		@SuppressWarnings("unused")
		int length = Bits.getUnsignedShort(bb);
		list.clear();
		for(int i = 0; i < count; i++) {
			SampleArrayObservedValue ov = new SampleArrayObservedValue();
			ov.parse(bb);
			list.add(ov);
		}
		
	}

	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, list.size());
		bb.mark();
		Bits.putUnsignedShort(bb, 0);
		int pos = bb.position();
		for(SampleArrayObservedValue ov : list) {
			ov.format(bb);
		}
		int length = bb.position() - pos;
		bb.reset();
		Bits.putUnsignedShort(bb, length);
		bb.position(bb.position() + length);
	}
	
	
	@Override
	public java.lang.String toString() {
		return list.toString();
	}
	
	public List<SampleArrayObservedValue> getList() {
		return list;
	}
}
