package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

public class NumericObservedValue implements Value {

	private OIDType physioId;
	private final MeasurementState msmtState = new MeasurementState();
	private OIDType unitCode;
	private final Float value = new Float();
	
	@Override
	public void format(ByteBuffer bb) {
		physioId.format(bb);
		msmtState.format(bb);
		unitCode.format(bb);
		value.format(bb);
	}

	@Override
	public void parse(ByteBuffer bb) {
		physioId = OIDType.parse(bb);
		msmtState.parse(bb);
		unitCode = OIDType.parse(bb);
		value.parse(bb);
	}
	
	@Override
	public java.lang.String toString() {
		int physioIdType = physioId.getType();
		java.lang.String physioIdStr = ObservedValue.valueOf(physioIdType)==null?physioId.toString():ObservedValue.valueOf(physioIdType).toString();
//		int unitCodeIdType = unitCode.getType();
//		java.lang.String dimension = Dimension.valueOf(unitCodeIdType)==null?unitCode.toString():Dimension.valueOf(unitCodeIdType).toString();
		return "[physioId="+physioIdStr+",msmtState="+msmtState+",unitCode="+unitCode+",value="+value+"]";
	}
	
	public Float getValue() {
		return value;
	}
	
	public MeasurementState getMsmtState() {
		return msmtState;
	}
	
	public OIDType getPhysioId() {
		return physioId;
	}
	public OIDType getUnitCode() {
		return unitCode;
	}
	public void setPhysioId(OIDType physioId) {
		this.physioId = physioId;
	}
	public void setUnitCode(OIDType unitCode) {
		this.unitCode = unitCode;
	}

}
