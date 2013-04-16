package org.mdpnp.devices.philips.intellivue.data;
import org.mdpnp.devices.philips.intellivue.OrdinalEnum;
import java.nio.ByteBuffer;
import java.util.Map;

import org.mdpnp.devices.io.util.Bits;

public enum PatientType implements EnumMessage<PatientType>, OrdinalEnum.IntType {
	PAT_TYPE_UNSPECIFIED(0),
	ADULT(1),
	PEDIATRIC(2),
	NEONATAL(3);
	
	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, asInt());
	}
	
	public PatientType parse(ByteBuffer bb) {
		return PatientType.valueOf(Bits.getUnsignedShort(bb));
	};
	
	
	private final int x;
	
	private PatientType(final int x) {
	    this.x = x;
    }
	
	private static final Map<Integer, PatientType> map = OrdinalEnum.buildInt(PatientType.class);
	
	public static PatientType valueOf(int x) {
		return map.get(x);
	}
	
	public final int asInt() {
		return x;
	}
}
