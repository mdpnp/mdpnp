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

import org.mdpnp.data.numeric.MutableNumericUpdate;
import org.mdpnp.data.numeric.MutableNumericUpdateImpl;
import org.mdpnp.devices.serial.AbstractSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket;
import org.mdpnp.messaging.Gateway;
import org.mdpnp.nomenclature.PulseOximeter;

public class DemoRadical7 extends AbstractSerialDevice {
	private final MutableNumericUpdate pulseUpdate = new MutableNumericUpdateImpl(PulseOximeter.PULSE);
	private final MutableNumericUpdate spo2Update = new MutableNumericUpdateImpl(PulseOximeter.SPO2);

	private static final String NAME = "Masimo Radical-7";
	

	private volatile boolean inited;
	
	private class MyMasimoRadical7 extends MasimoRadical7 {

		public MyMasimoRadical7() throws NoSuchFieldException, SecurityException, IOException {
			super();
		}
		@Override
		public void firePulseOximeter() {
			super.firePulseOximeter();
			pulseUpdate.set(getHeartRate(), getTimestamp());
			spo2Update.set(getSpO2(), getTimestamp());
			String guid = getUniqueId();
			if(guid != null && !guid.equals(guidUpdate.getValue())) {
				guidUpdate.setValue(guid);
				gateway.update(guidUpdate);
			}
			gateway.update(pulseUpdate, spo2Update);
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
	public DemoRadical7(Gateway gateway) throws NoSuchFieldException, SecurityException, IOException {
		super(gateway);
		this.fieldDelegate = new MyMasimoRadical7();
		nameUpdate.setValue(NAME);
		guidUpdate.setValue(null);
		add(spo2Update);
		add(pulseUpdate);
	}
	

	@Override
	protected String iconResourceName() {
		return "radical7.png";
	}

}
