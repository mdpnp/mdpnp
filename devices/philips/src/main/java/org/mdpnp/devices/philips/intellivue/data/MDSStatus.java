package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;

public enum MDSStatus implements EnumParseable<MDSStatus>, Formatable {
	Disconnected,
	Unassociated,
	Operating;
	
	@Override
	public MDSStatus parse(ByteBuffer bb) {
		return MDSStatus.valueOf(Bits.getUnsignedShort(bb));
	}

	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, asInt());
	}
	
	public static MDSStatus valueOf(int x) {
		switch(x) {
		case 0:
			return Disconnected;
		case 1:
			return Unassociated;
		case 6:
			return Operating;
		default:
			return null;
		}
	}
	
	public int asInt() {
		switch(this) {
		case Disconnected:
			return 0;
		case Unassociated:
			return 1;
		case Operating:
			return 6;
		default:
			throw new IllegalArgumentException("Unknown MDSStatus:"+this);
		}
	}
}
