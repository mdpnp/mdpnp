package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

public class SystemModel implements Value {
	private final VariableLabel manufacturer = new VariableLabel();
	private final VariableLabel modelNumber = new VariableLabel();
	
	
	public VariableLabel getManufacturer() {
		return manufacturer;
	}
	
	public VariableLabel getModelNumber() {
		return modelNumber;
	}
	
	public void setManufacturer(java.lang.String manufacturer) {
		this.manufacturer.setString(manufacturer);
	}
	
	public void setModelNumber(java.lang.String modelNumber) {
		this.modelNumber.setString(modelNumber);
	}
	
	@Override
	public java.lang.String toString() {
		return "[manufacturer="+manufacturer+",modelNumber="+modelNumber+"]";
	}



	@Override
	public void format(ByteBuffer bb) {
		manufacturer.format(bb);
		modelNumber.format(bb);
	}



	@Override
	public void parse(ByteBuffer bb) {
		manufacturer.parse(bb);
		modelNumber.parse(bb);
	}
}
