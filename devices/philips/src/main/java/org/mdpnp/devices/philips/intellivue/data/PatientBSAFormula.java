package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public enum PatientBSAFormula implements EnumMessage<PatientBSAFormula> {
	BSA_FORMULA_UNSPEC,
	BSA_FORMULA_BOYD,
	BSA_FORMULA_DUBOIS;
	
	public void format(java.nio.ByteBuffer bb) {
		Bits.putUnsignedShort(bb, asInt());
	};
	
	@Override
	public PatientBSAFormula parse(ByteBuffer bb) {
		return PatientBSAFormula.valueOf(Bits.getUnsignedShort(bb));
	}
	
	public static PatientBSAFormula valueOf(int x) {
		switch(x) {
		case 0:
			return BSA_FORMULA_UNSPEC;
		case 1:
			return BSA_FORMULA_BOYD;
		case 2:
			return BSA_FORMULA_DUBOIS;
		default:
			return null;
		}
	}
	
	public int asInt() {
		switch(this) {
		case BSA_FORMULA_UNSPEC:
			return 0;
		case BSA_FORMULA_BOYD:
			return 1;
		case BSA_FORMULA_DUBOIS:
			return 2;
		default:
			throw new IllegalArgumentException("Unknown PatientBSAFormula:"+this);
		}
	}
}
