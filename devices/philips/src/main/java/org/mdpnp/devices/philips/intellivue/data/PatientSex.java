package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;

public enum PatientSex implements EnumParseable<PatientSex>, Formatable {
	SEX_UNKNOWN,
	MALE,
	FEMALE,
	SEX_UNSPECIFIED;
	
	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, asInt());
	}
	
	@Override
	public PatientSex parse(ByteBuffer bb) {
		return PatientSex.valueOf(Bits.getUnsignedShort(bb));
	}
	
	public static PatientSex valueOf(int x) {
		switch(x) {
		case 0:
			return SEX_UNKNOWN;
		case 1:
			return MALE;
		case 2:
			return FEMALE;
		case 9:
			return SEX_UNSPECIFIED;
		default:
			return null;
		}
	}
	
	public int asInt() {
		switch(this) {
		case SEX_UNKNOWN:
			return 0;
		case MALE:
			return 1;
		case FEMALE:
			return 2;
		case SEX_UNSPECIFIED:
			return 9;
		default:
			throw new IllegalArgumentException("Unknown PatientSex:"+this);
		}
	}
}
