package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;
import java.util.Map;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;
import org.mdpnp.devices.io.util.Bits;


public enum ApplicationArea implements EnumMessage<ApplicationArea>, OrdinalEnum.IntType {
	AREA_UNSPEC(0),
	AREA_OPERATING_ROOM(1),
	AREA_INTENSIVE_CARE(2),
	AREA_NEONATAL_INTENSIVE_CARE(3),
	AREA_CARDIOLOGY_CARE(4);
	
	private final int x;
	private final static Map<Integer, ApplicationArea> map = OrdinalEnum.buildInt(ApplicationArea.class);
	
	private ApplicationArea(int x) {
	    this.x = x;
    }
	
	public static ApplicationArea valueOf(int x) {
	    return map.get(x);
	}
	
	public int asInt() {
	    return x;
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
