package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;

public enum PatientType implements EnumParseable<PatientType>, Formatable {
	PAT_TYPE_UNSPECIFIED,
	ADULT,
	PEDIATRIC,
	NEONATAL;
	
	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, asInt());
	}
	
	public PatientType parse(ByteBuffer bb) {
		return PatientType.valueOf(Bits.getUnsignedShort(bb));
	};
	
	public static PatientType valueOf(int x) {
		switch(x) {
		case 0:
			return PAT_TYPE_UNSPECIFIED;
		case 1:
			return ADULT;
		case 2:
			return PEDIATRIC;
		case 3:
			return NEONATAL;
		default:
			return null;
		}
	}
	
	public final int asInt() {
		switch(this) {
		case PAT_TYPE_UNSPECIFIED:
			return 0;
		case ADULT:
			return 1;
		case PEDIATRIC:
			return 2;
		case NEONATAL:
			return 3;
		default:
			throw new IllegalArgumentException("Unknown PatientType:"+this);
		}
	}
}
