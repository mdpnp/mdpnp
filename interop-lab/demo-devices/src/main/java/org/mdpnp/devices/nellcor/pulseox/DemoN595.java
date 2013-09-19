/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.nellcor.pulseox;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.serial.AbstractSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;

public class DemoN595 extends AbstractSerialDevice {
    private InstanceHolder<ice.Numeric> pulseUpdate;
    private InstanceHolder<ice.Numeric> spo2Update;
    
//	private final MutableNumericUpdate pulseUpperUpdate = new MutableNumericUpdateImpl(PulseOximeter.PULSE_UPPER);
//	private final MutableNumericUpdate pulseLowerUpdate = new MutableNumericUpdateImpl(PulseOximeter.PULSE_LOWER);
//	private final MutableNumericUpdate spo2UpperUpdate = new MutableNumericUpdateImpl(PulseOximeter.SPO2_UPPER);
//	private final MutableNumericUpdate spo2LowerUpdate = new MutableNumericUpdateImpl(PulseOximeter.SPO2_LOWER);
	
	private class MyNellcorN595 extends NellcorN595 {
		public MyNellcorN595() throws NoSuchFieldException, SecurityException, IOException {
			super();
		}
		
		@Override
		public void firePulseOximeter() {
		    reportConnected();
		    pulseUpdate = numericSample(pulseUpdate, getHeartRate(), ice.Physio._MDC_PULS_OXIM_PULS_RATE);
		    spo2Update = numericSample(spo2Update, getSpO2(), ice.Physio._MDC_PULS_OXIM_SAT_O2);
		}
		@Override
		public void fireAlarmPulseOximeter() {
//			pulseLowerUpdate.set(getPRLower(), getTimestamp());
//			pulseUpperUpdate.set(getPRUpper(), getTimestamp());
//			spo2LowerUpdate.set(getSpO2Lower(), getTimestamp());
//			spo2UpperUpdate.set(getSpO2Upper(), getTimestamp());
//			gateway.update(pulseLowerUpdate, pulseUpperUpdate, spo2LowerUpdate, spo2UpperUpdate);
		}
		@Override
		public void fireDevice() {
		    deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);
		    
		}
		@Override
		protected void setName(String name) {
		    deviceIdentity.model = name;
		}
		@Override
		protected void setGuid(String guid) {
		    deviceIdentity.serial_number = guid;
		}
	}

	private final MyNellcorN595 fieldDelegate;
	private OutputStream outputStream;
	@Override
	protected void process(InputStream inputStream, OutputStream outputStream) throws IOException {
	    this.outputStream = outputStream;
		fieldDelegate.setInputStream(inputStream);
		fieldDelegate.run();
	}
	
	private static final byte[] dumpInstrumentInfo = new byte[] {0x30, 0x0D, 0x0A, 0x03, 0x03, 0x31, 0x0D, 0x0A, 0x30, 0x0D, 0x0A};
	
	@Override
	protected void doInitCommands() throws IOException {
//		outputStream.write(dumpInstrumentInfo);
//		outputStream.flush();
	}
	
	
	
	public DemoN595(int domainId, EventLoop eventLoop) throws NoSuchFieldException, SecurityException, IOException {
		super(domainId, eventLoop);
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        deviceIdentity.manufacturer = NellcorN595.MANUFACTURER_NAME;
        deviceIdentity.model = NellcorN595.MODEL_NAME;
        deviceIdentityHandle = deviceIdentityWriter.register_instance(deviceIdentity);
        deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);
        
        deviceConnectivity.universal_device_identifier = deviceIdentity.universal_device_identifier;
        deviceConnectivityHandle = deviceConnectivityWriter.register_instance(deviceConnectivity);
        deviceConnectivityWriter.write(deviceConnectivity, deviceConnectivityHandle);
		
        spo2Update = createNumericInstance(ice.Physio.MDC_PULS_OXIM_SAT_O2.value());
        pulseUpdate = createNumericInstance(ice.Physio.MDC_PULS_OXIM_PULS_RATE.value());
        
		this.fieldDelegate = new MyNellcorN595();
	}

	@Override
	protected long getMaximumQuietTime() {
		return 2200L;
	}
	
	@Override
	protected long getConnectInterval() {
	    return 10000L;
	}
	
	@Override
	public SerialProvider getSerialProvider() {
		SerialProvider serialProvider =  super.getSerialProvider();
		serialProvider.setDefaultSerialSettings(9600, DataBits.Eight, Parity.None, StopBits.One);
		return serialProvider;
	}
	@Override
	protected String iconResourceName() {
		return "n595.png";
	}
}
