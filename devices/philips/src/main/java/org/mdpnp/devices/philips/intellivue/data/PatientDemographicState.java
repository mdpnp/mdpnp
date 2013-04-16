package org.mdpnp.devices.philips.intellivue.data;
import org.mdpnp.devices.philips.intellivue.OrdinalEnum;
import java.nio.ByteBuffer;
import java.util.Map;

import org.mdpnp.devices.io.util.Bits;

public enum PatientDemographicState implements EnumMessage<PatientDemographicState>, OrdinalEnum.IntType {
	EMPTY(0),
	PRE_ADMITTED(1),
	ADMITTED(2),
	DISCHARGED(8);
	
	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, asInt());
	}
	@Override
	public PatientDemographicState parse(ByteBuffer bb) {
		return PatientDemographicState.valueOf(Bits.getUnsignedShort(bb));
	}
	
	private final int x;
	private PatientDemographicState(final int x) {
	    this.x = x;
    }
	
	private static final Map<Integer, PatientDemographicState> map = OrdinalEnum.buildInt(PatientDemographicState.class);
	
	public static PatientDemographicState valueOf(int x) {
	    return map.get(x);
	}
	
	public final int asInt() {
	    return x;
	}
}
