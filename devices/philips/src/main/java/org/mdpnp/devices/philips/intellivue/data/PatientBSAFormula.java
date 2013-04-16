package org.mdpnp.devices.philips.intellivue.data;

import org.mdpnp.devices.philips.intellivue.OrdinalEnum;
import java.nio.ByteBuffer;
import java.util.Map;

import org.mdpnp.devices.io.util.Bits;

public enum PatientBSAFormula implements EnumMessage<PatientBSAFormula>, OrdinalEnum.IntType {
	BSA_FORMULA_UNSPEC(0),
	BSA_FORMULA_BOYD(1),
	BSA_FORMULA_DUBOIS(2);
	
	public void format(java.nio.ByteBuffer bb) {
		Bits.putUnsignedShort(bb, asInt());
	};
	
	@Override
	public PatientBSAFormula parse(ByteBuffer bb) {
		return PatientBSAFormula.valueOf(Bits.getUnsignedShort(bb));
	}
	
	private final int x;
	
	private PatientBSAFormula(final int x) {
	    this.x = x;
    }
	
	private static final Map<Integer, PatientBSAFormula> map = OrdinalEnum.buildInt(PatientBSAFormula.class);
	
	public static PatientBSAFormula valueOf(int x) {
		return map.get(x);
	}
	
	public int asInt() {
		return x;
	}
}
