/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.simulation.pulseox;

import java.util.Date;

import org.mdpnp.data.numeric.MutableNumericUpdate;
import org.mdpnp.data.numeric.MutableNumericUpdateImpl;
import org.mdpnp.data.waveform.MutableWaveformUpdate;
import org.mdpnp.data.waveform.MutableWaveformUpdateImpl;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;
import org.mdpnp.messaging.Gateway;
import org.mdpnp.nomenclature.PulseOximeter;

public class SimPulseOximeter extends AbstractSimulatedConnectedDevice {
	protected final MutableNumericUpdate pulseUpdate = new MutableNumericUpdateImpl(PulseOximeter.PULSE);
	protected final MutableNumericUpdate spo2Update = new MutableNumericUpdateImpl(PulseOximeter.SPO2);
	protected final MutableWaveformUpdate plethUpdate = new MutableWaveformUpdateImpl(PulseOximeter.PLETH);
	
	private class MySimulatedPulseOximeter extends SimulatedPulseOximeter {
	    @Override
	    protected void receivePulseOx(long timestamp, int heartRate, int SpO2, Number[] plethValues, double msPerSample) {
	        myDate.setTime(timestamp);
            pulseUpdate.set(heartRate, myDate);
            spo2Update.set(SpO2, myDate);
            plethUpdate.setValues(plethValues);
            plethUpdate.setMillisecondsPerSample(msPerSample);
            gateway.update(SimPulseOximeter.this, spo2Update, pulseUpdate, plethUpdate);
	    }
	}
	
	private final MySimulatedPulseOximeter pulseox = new MySimulatedPulseOximeter();
	
	
	@Override
	public void connect(String str) {
	    pulseox.connect(executor);
		super.connect(str);
	}
	
	@Override
	public void disconnect() {
	    pulseox.disconnect();
		super.disconnect();
	}
	
	public SimPulseOximeter(Gateway gateway) {
		super(gateway);
		add(plethUpdate);
		add(spo2Update);
		add(pulseUpdate);
		nameUpdate.setValue("Pulse Ox (Simulated)");

	}
	
	@Override
	protected String iconResourceName() {
		return "pulseox.png";
	}
		
	protected final Date myDate = new Date();
	
}
