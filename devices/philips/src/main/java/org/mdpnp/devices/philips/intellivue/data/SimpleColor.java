package org.mdpnp.devices.philips.intellivue.data;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;
import java.nio.ByteBuffer;
import java.util.Map;

import org.mdpnp.devices.io.util.Bits;


public enum SimpleColor implements EnumMessage<SimpleColor>, OrdinalEnum.IntType {
	Black(0),
	Red(1),
	Green(2),
	Yellow(3),
	Blue(4),
	Magenta(5),
	Cyan(6),
	White(7),
	Pink(20),
	Orange(35),
	LightGreen(50),
	LightRed(65),;
	
	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, asInt());
	}
	
	@Override
	public SimpleColor parse(ByteBuffer bb) {
		return SimpleColor.valueOf(Bits.getUnsignedShort(bb));
	}
	
	private final int x;
	
	private SimpleColor(final int x) {
	    this.x = x;
    }
	
	private static final Map<Integer, SimpleColor> map = OrdinalEnum.buildInt(SimpleColor.class);
	
	public static final SimpleColor valueOf(int x) {
	    return map.get(x);
	}
	
	public int asInt() {
	    return x;
	}

}
