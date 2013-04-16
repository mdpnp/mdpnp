package org.mdpnp.devices.philips.intellivue.data;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;
import java.nio.ByteBuffer;
import java.util.Map;

import org.mdpnp.devices.io.util.Bits;

public enum MDSStatus implements EnumMessage<MDSStatus>, OrdinalEnum.IntType {
	Disconnected(0),
	Unassociated(1),
	Operating(6);
	
	@Override
	public MDSStatus parse(ByteBuffer bb) {
		return MDSStatus.valueOf(Bits.getUnsignedShort(bb));
	}

	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, asInt());
	}
	
	private final int x;
	
	private MDSStatus(int x) {
	    this.x = x;
    }
	
	private static final Map<Integer, MDSStatus> map = OrdinalEnum.buildInt(MDSStatus.class);
	
	public static MDSStatus valueOf(int x) {
		return map.get(x);
	}
	
	public int asInt() {
	    return x;
	}
}
