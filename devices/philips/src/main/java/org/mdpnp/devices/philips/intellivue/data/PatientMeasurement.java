package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

public class PatientMeasurement implements Value {
	private final Float value = new Float();
	private OIDType unitCode;
	
	@Override
	public void format(ByteBuffer bb) {
		value.format(bb);
		unitCode.format(bb);
	}
	
	public void parse(ByteBuffer bb) {
		value.parse(bb);
		unitCode = OIDType.parse(bb);
	};
	
	@Override
	public java.lang.String toString() {
		return "[value="+value+",unitCode="+unitCode+"]";
	}
}
