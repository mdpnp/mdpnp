package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;


public enum ApplicationArea implements EnumMessage<ApplicationArea> {
	AREA_UNSPEC,
	AREA_OPERATING_ROOM,
	AREA_INTENSIVE_CARE,
	AREA_NEONATAL_INTENSIVE_CARE,
	AREA_CARDIOLOGY_CARE;
	
	public static ApplicationArea valueOf(int x) {
		switch(x) {
		case 0:
			return AREA_UNSPEC;
		case 1:
			return AREA_OPERATING_ROOM;
		case 2:
			return AREA_INTENSIVE_CARE;
		case 3:
			return AREA_NEONATAL_INTENSIVE_CARE;
		case 4:
			return AREA_CARDIOLOGY_CARE;
		default:
			return null;
		}
	}
	
	public int asInt() {
		switch(this) {
		case AREA_UNSPEC:
			return 0;
		case AREA_OPERATING_ROOM:
			return 1;
		case AREA_INTENSIVE_CARE:
			return 2;
		case AREA_NEONATAL_INTENSIVE_CARE:
			return 3;
		case AREA_CARDIOLOGY_CARE:
			return 4;
		default:
			throw new IllegalArgumentException("Unknown ApplicationArea:"+this);
		}
	}

	@Override
	public ApplicationArea parse(ByteBuffer bb) {
		return ApplicationArea.valueOf(Bits.getUnsignedShort(bb));
	}

	@Override
	public void format(ByteBuffer bb) {
	    Bits.putUnsignedShort(bb, asInt());
	}
}
