/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.simulation.pulseox;

import ice.MDC_PULS_OXIM_PLETH;
import ice.MDC_PULS_OXIM_PULS_RATE;
import ice.MDC_PULS_OXIM_SAT_O2;
import ice.Numeric;
import ice.SampleArray;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;

public class SimPulseOximeter extends AbstractSimulatedConnectedDevice {
    
    protected final InstanceHolder<Numeric> pulse;
    protected final InstanceHolder<Numeric> SpO2;
    protected final InstanceHolder<SampleArray> pleth;
	
	private class MySimulatedPulseOximeter extends SimulatedPulseOximeter {
	    @Override
	    protected void receivePulseOx(long timestamp, int heartRate, int SpO2, Number[] plethValues, double msPerSample) {
	        numericSample(pulse, heartRate);
	        numericSample(SimPulseOximeter.this.SpO2, SpO2);
	        sampleArraySample(pleth, plethValues, (int) msPerSample);
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
	
	public SimPulseOximeter(int domainId, EventLoop eventLoop) {
		super(domainId, eventLoop);
		
		pulse = createNumericInstance(MDC_PULS_OXIM_PULS_RATE.VALUE);
		SpO2 = createNumericInstance(MDC_PULS_OXIM_SAT_O2.VALUE);
		pleth = createSampleArrayInstance(MDC_PULS_OXIM_PLETH.VALUE);
		
		deviceIdentity.model = "Pulse Ox (Simulated)";
		deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);
	}
	
	@Override
	protected String iconResourceName() {
		return "pulseox.png";
	}
}
