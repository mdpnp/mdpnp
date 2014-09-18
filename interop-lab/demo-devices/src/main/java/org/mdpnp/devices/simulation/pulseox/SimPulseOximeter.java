/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.devices.simulation.pulseox;

import ice.GlobalSimulationObjective;

import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;
import org.mdpnp.rtiapi.data.EventLoop;

import com.rti.dds.infrastructure.Time_t;

/**
 * @author Jeff Plourde
 *
 */
public class SimPulseOximeter extends AbstractSimulatedConnectedDevice {

    protected final InstanceHolder<ice.Numeric> pulse;
    protected final InstanceHolder<ice.Numeric> SpO2;
    protected InstanceHolder<ice.SampleArray> pleth;

    private final Time_t sampleTime = new Time_t(0, 0);

    private class MySimulatedPulseOximeter extends SimulatedPulseOximeter {
        @Override
        protected void receivePulseOx(long timestamp, int heartRate, int SpO2, Number[] plethValues, int frequency) {
            sampleTime.sec = (int) (timestamp / 1000L);
            sampleTime.nanosec = (int) (timestamp % 1000L * 1000000L);
            numericSample(pulse, heartRate, sampleTime);
            numericSample(SimPulseOximeter.this.SpO2, SpO2, sampleTime);
            pleth = sampleArraySample(pleth, plethValues, rosetta.MDC_PULS_OXIM_PLETH.VALUE, 0, 
                    rosetta.MDC_DIM_DIMLESS.VALUE, frequency, sampleTime);
        }
    }

    private final MySimulatedPulseOximeter pulseox = new MySimulatedPulseOximeter();

    @Override
    public boolean connect(String str) {
        pulseox.connect(executor);
        return super.connect(str);
    }

    @Override
    public void disconnect() {
        pulseox.disconnect();
        super.disconnect();
    }

    public SimPulseOximeter(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);

        pulse = createNumericInstance(rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE);
        SpO2 = createNumericInstance(rosetta.MDC_PULS_OXIM_SAT_O2.VALUE);

        deviceIdentity.model = "Pulse Ox (Simulated)";
        writeDeviceIdentity();
    }

    @Override
    protected String iconResourceName() {
        return "pulseox.png";
    }

    @Override
    public void simulatedNumeric(GlobalSimulationObjective obj) {
        // Currently the super ctor registers for this callback; so pulseox might not yet be initialized
        if (obj != null && pulseox != null) {
            if (rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE.equals(obj.metric_id)) {
                pulseox.setTargetHeartRate((double) obj.value);
            } else if (rosetta.MDC_PULS_OXIM_SAT_O2.VALUE.equals(obj.metric_id)) {
                pulseox.setTargetSpO2((double) obj.value);
            }
        }
    }
    
    @Override
    protected boolean sampleArraySpecifySourceTimestamp() {
        return true;
    }
    

}
