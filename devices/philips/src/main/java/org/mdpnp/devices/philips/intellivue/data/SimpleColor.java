package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;


public enum SimpleColor implements EnumParseable<SimpleColor>, Formatable {
	Black,
	Red,
	Green,
	Yellow,
	Blue,
	Magenta,
	Cyan,
	White,
	Pink,
	Orange,
	LightGreen,
	LightRed,;
	
	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, asInt());
	}
	
	@Override
	public SimpleColor parse(ByteBuffer bb) {
		return SimpleColor.valueOf(Bits.getUnsignedShort(bb));
	}
	public static final SimpleColor valueOf(int x) {
		switch(x) {
		case 0:
			return Black;
		case 1:
			return Red;
		case 2:
			return Green;
		case 3:
			return Yellow;
		case 4:
			return Blue;
		case 5:
			return Magenta;
		case 6:
			return Cyan;
		case 7:
			return White;
		case 20:
			return Pink;
		case 35:
			return Orange;
		case 50:
			return LightGreen;
		case 65:
			return LightRed;
		default:
			return null;
		}
	}
	
	public int asInt() {
		switch(this) {
		case Black:
			return 0;
		case Red:
			return 1;
		case Green:
			return 2;
		case Yellow:
			return 3;
		case Blue:
			return 4;
		case Magenta:
			return 5;
		case Cyan:
			return 6;
		case White:
			return 7;
		case Pink:
			return 20;
		case Orange:
			return 35;
		case LightGreen:
			return 50;
		case LightRed:
			return 65;
		default:
			throw new IllegalArgumentException("Unknown SimpleColor:"+this);
		}
	}

}
