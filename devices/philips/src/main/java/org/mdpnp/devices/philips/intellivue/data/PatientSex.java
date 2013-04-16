package org.mdpnp.devices.philips.intellivue.data;
import org.mdpnp.devices.philips.intellivue.OrdinalEnum;
import java.nio.ByteBuffer;
import java.util.Map;

import org.mdpnp.devices.io.util.Bits;

public enum PatientSex implements EnumMessage<PatientSex>, OrdinalEnum.IntType {
	SEX_UNKNOWN(0),
	MALE(1),
	FEMALE(2),
	SEX_UNSPECIFIED(9);
	
	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, asInt());
	}
	
	@Override
	public PatientSex parse(ByteBuffer bb) {
		return PatientSex.valueOf(Bits.getUnsignedShort(bb));
	}
	
	private final int x;
	
	private PatientSex(final int x) {
	    this.x = x;
    }
	
	private static final Map<Integer, PatientSex> map = OrdinalEnum.buildInt(PatientSex.class);
	
	public static PatientSex valueOf(int x) {
		return map.get(x);
	}
	
	public int asInt() {
		return x;
	}
}
