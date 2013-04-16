package org.mdpnp.devices.philips.intellivue.data;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;
import java.nio.ByteBuffer;
import java.util.Map;

import org.mdpnp.devices.io.util.Bits;

public enum PatientPacedMode implements EnumMessage<PatientPacedMode>, OrdinalEnum.IntType {
	PAT_NOT_PACED(0),
	PAT_PACED_GEN(1);
	
	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, asInt());
	}
	@Override
	public PatientPacedMode parse(ByteBuffer bb) {
		return PatientPacedMode.valueOf(Bits.getUnsignedShort(bb));
	}
	private final int x;
	
	private PatientPacedMode(final int x) {
	    this.x = x;
    }
	
	private static final Map<Integer, PatientPacedMode> map = OrdinalEnum.buildInt(PatientPacedMode.class);
	public static PatientPacedMode valueOf(int x) {
		return map.get(x);
	}
	
	public int asInt() {
		return x;
	}
}
