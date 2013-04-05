package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;

public class MetricStructure implements Formatable, Parseable {
	private short structure, maxNumberOfComponents;

	@Override
	public void parse(ByteBuffer bb) {
		structure = Bits.getUnsignedByte(bb);
		maxNumberOfComponents = Bits.getUnsignedByte(bb);
	}

	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedByte(bb, structure);
		Bits.putUnsignedByte(bb, maxNumberOfComponents);
	}
	
	@Override
	public java.lang.String toString() {
		return "[structure="+structure+",maxNumberOfComponents="+maxNumberOfComponents+"]";
	}
	
	
}
