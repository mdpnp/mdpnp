package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.philips.intellivue.util.Util;

public class CompoundNumericObservedValue implements Value {

	private final List<NumericObservedValue> list = new ArrayList<NumericObservedValue>();
	
	@Override
	public void format(ByteBuffer bb) {
		Util.PrefixLengthShort.write(bb, list);
	}

	@Override
	public void parse(ByteBuffer bb) {
		Util.PrefixLengthShort.read(bb, list, true, NumericObservedValue.class);
	}
	
	@Override
	public java.lang.String toString() {
		return list.toString();
	}
	
	public List<NumericObservedValue> getList() {
		return list;
	}

}
