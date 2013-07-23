/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.masimo.radical;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.serial.AbstractSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;

public class DemoRadical7 extends AbstractSerialDevice {
    private final InstanceHolder<ice.Numeric> pulseUpdate;
    private final InstanceHolder<ice.Numeric> spo2Update;


	private static final String MANUFACTURER_NAME = "Masimo";
	private static final String MODEL_NAME = "Radical-7";
	

	private volatile boolean inited;
	
	private class MyMasimoRadical7 extends MasimoRadical7 {

		public MyMasimoRadical7() throws NoSuchFieldException, SecurityException, IOException {
			super();
		}
		@Override
		public void firePulseOximeter() {
			super.firePulseOximeter();
			numericSample(pulseUpdate, getHeartRate());
			numericSample(spo2Update, getSpO2());
			String guid = getUniqueId();
			if(guid != null && !guid.equals(deviceIdentity.serial_number)) {
				deviceIdentity.serial_number = guid;
				deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);
			}
		}
	}
	
	private final MyMasimoRadical7 fieldDelegate;
	
	@Override
	protected void process(InputStream inputStream) throws IOException {
		fieldDelegate.setInputStream(inputStream);
		fieldDelegate.run();
	}
	@Override
	protected long getMaximumQuietTime() {
		return 3000L;
	}
	@Override
	protected boolean doInitCommands(OutputStream outputStream) throws IOException {
		inited = false;
		long giveup = System.currentTimeMillis() + getMaximumQuietTime();
		synchronized(this) {
			while(!inited) {
				try {
					this.wait(500L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if(System.currentTimeMillis()>giveup) {
					return false;
				}
			}
		}
		return true;
	}
	

	@Override
	public SerialProvider getSerialProvider() {
		SerialProvider serialProvider =  super.getSerialProvider();
		serialProvider.setDefaultSerialSettings(9600, SerialSocket.DataBits.Eight, SerialSocket.Parity.None, SerialSocket.StopBits.One);
		return serialProvider;
	}
	public DemoRadical7(int domainId, EventLoop eventLoop) throws NoSuchFieldException, SecurityException, IOException {
		super(domainId, eventLoop);
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        deviceIdentity.manufacturer = MANUFACTURER_NAME;
        deviceIdentity.model = MODEL_NAME;
        deviceIdentityHandle = deviceIdentityWriter.register_instance(deviceIdentity);
        deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);
        
        deviceConnectivity.universal_device_identifier = deviceIdentity.universal_device_identifier;
        deviceConnectivityHandle = deviceConnectivityWriter.register_instance(deviceConnectivity);
        deviceConnectivityWriter.write(deviceConnectivity, deviceConnectivityHandle);
        
		this.fieldDelegate = new MyMasimoRadical7();

		spo2Update = createNumericInstance(ice.MDC_PULS_OXIM_SAT_O2.VALUE);
		pulseUpdate = createNumericInstance(ice.MDC_PULS_OXIM_PULS_RATE.VALUE);
	}
	

	@Override
	protected String iconResourceName() {
		return "radical7.png";
	}

}
