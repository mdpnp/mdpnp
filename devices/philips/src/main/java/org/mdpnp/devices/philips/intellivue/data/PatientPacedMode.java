package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;

public enum PatientPacedMode implements EnumParseable<PatientPacedMode>, Formatable {
	PAT_NOT_PACED,
	PAT_PACED_GEN;
	
	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, asInt());
	}
	@Override
	public PatientPacedMode parse(ByteBuffer bb) {
		return PatientPacedMode.valueOf(Bits.getUnsignedShort(bb));
	}
	
	public static PatientPacedMode valueOf(int x) {
		if(0 == x) {
			return PAT_NOT_PACED;
		} else {
			return PAT_PACED_GEN;
		}
	}
	
	public int asInt() {
		switch (this) {
		case PAT_NOT_PACED:
			return 0;
		case PAT_PACED_GEN:
			return 1;
		default:
			throw new IllegalArgumentException("Unknown PatientPacedMode:"+this);
		}
	}
}
