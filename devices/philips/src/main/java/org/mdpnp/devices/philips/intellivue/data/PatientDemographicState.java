package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public enum PatientDemographicState implements EnumMessage<PatientDemographicState> {
	EMPTY,
	PRE_ADMITTED,
	ADMITTED,
	DISCHARGED;
	
	@Override
	public void format(ByteBuffer bb) {
		Bits.putUnsignedShort(bb, asInt());
	}
	@Override
	public PatientDemographicState parse(ByteBuffer bb) {
		return PatientDemographicState.valueOf(Bits.getUnsignedShort(bb));
	}
	
	public static PatientDemographicState valueOf(int x) {
		switch(x) {
		case 0:
			return EMPTY;
		case 1:
			return PRE_ADMITTED;
		case 2:
			return ADMITTED;
		case 8:
			return DISCHARGED;
		default:
			return null;
		}
	}
	
	public final int asInt() {
		switch(this) {
		case EMPTY:
			return 0;
		case PRE_ADMITTED:
			return 1;
		case ADMITTED:
			return 2;
		case DISCHARGED:
			return 8;
		default:
			throw new IllegalArgumentException("Unknown PatientDemographicState:"+this);
				
		}
	}
}
