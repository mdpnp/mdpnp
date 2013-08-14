/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.simulation.ecg;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;

public class SimElectroCardioGram extends AbstractSimulatedConnectedDevice {

    protected final InstanceHolder<ice.SampleArray> i, ii, iii;
    
    protected final InstanceHolder<ice.Numeric> respiratoryRate, heartRate;
	
	private class MySimulatedElectroCardioGram extends SimulatedElectroCardioGram {
	    @Override
	    protected void receiveECG(Number[] iValues, Number[] iiValues, Number[] iiiValues, int heartRateValue, int respiratoryRateValue,
	            double msPerSample) {
	        sampleArraySample(i, iValues, (int) msPerSample);
            sampleArraySample(ii, iiValues, (int) msPerSample);
            sampleArraySample(iii, iiiValues, (int) msPerSample);
            numericSample(heartRate, heartRateValue);
            numericSample(respiratoryRate, respiratoryRateValue);
	    }
	}
	
	private final MySimulatedElectroCardioGram ecg = new MySimulatedElectroCardioGram();
	
	
	@Override
	public void connect(String str) {
	    ecg.connect(executor);
		super.connect(str);
	}
	
	@Override
	public void disconnect() {
	    ecg.disconnect();
		super.disconnect();
	}
	
	public SimElectroCardioGram(int domainId, EventLoop eventLoop) {
		super(domainId, eventLoop);

		i = createSampleArrayInstance(ice.MDC_ECG_ELEC_POTL_I.VALUE);
		ii = createSampleArrayInstance(ice.MDC_ECG_ELEC_POTL_II.VALUE);
		iii = createSampleArrayInstance(ice.MDC_ECG_ELEC_POTL_III.VALUE);
		respiratoryRate = createNumericInstance(ice.MDC_RESP_RATE.VALUE);
		heartRate = createNumericInstance(ice.MDC_PULS_RATE.VALUE);
		
		deviceIdentity.model = "ECG (Simulated)";
		deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);
	}
	
	@Override
	protected String iconResourceName() {
		return "ecg.png";
	}
}
