package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;
import java.util.Arrays;

import org.mdpnp.devices.io.util.Bits;

public class SampleArrayObservedValue implements Value {
	private OIDType physioId;
	private final MeasurementState state = new MeasurementState();
	private short[] value = new short[8];
	private int length;

	
	public OIDType getPhysioId() {
		return physioId;
	}
	
	public MeasurementState getState() {
		return state;
	}
	
	public short[] getValue() {
		return value;
	}
	
	
	public void setPhysioId(OIDType physioId) {
		this.physioId = physioId;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	
	public void setValue(short[] value) {
		this.value = value;
	}
	
	@Override
	public java.lang.String toString() {
		java.lang.String pi = ObservedValue.valueOf(physioId.getType())==null?physioId.toString():ObservedValue.valueOf(physioId.getType()).toString();
		return "[physioId="+pi+",state="+state+",length="+length+",value="+Arrays.toString(value)+"]";
	}



	@Override
	public void format(ByteBuffer bb) {
		physioId.format(bb);
		state.format(bb);
		Bits.putUnsignedShort(bb, length);
		for(int i = 0; i < length; i++) {
			Bits.putUnsignedByte(bb, value[i]);
		}
	}



	@Override
	public void parse(ByteBuffer bb) {
		physioId = OIDType.parse(bb);
		state.parse(bb);
		length = Bits.getUnsignedShort(bb);
		if(value.length < length) {
			value = new short[length];
		}
		for(int i = 0; i < length; i++) {
			value[i] = Bits.getUnsignedByte(bb);
		}
	}
}
