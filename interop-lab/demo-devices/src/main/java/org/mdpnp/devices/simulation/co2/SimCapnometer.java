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
package org.mdpnp.devices.simulation.co2;

import ice.GlobalSimulationObjective;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;

/**
 * @author Jeff Plourde
 *
 */
public class SimCapnometer extends AbstractSimulatedConnectedDevice {

    protected final InstanceHolder<ice.SampleArray> co2;
    protected final InstanceHolder<ice.Numeric> respiratoryRate, etCO2;

    private class MySimulatedCapnometer extends SimulatedCapnometer {
        @Override
        protected void receiveCO2(Number[] co2Values, int respiratoryRateValue, int etCO2Value, double msPerSample) {
            sampleArraySample(co2, co2Values, (int) msPerSample, null);
            numericSample(respiratoryRate, respiratoryRateValue, null);
            numericSample(etCO2, etCO2Value, null);

        }
    }

    private final MySimulatedCapnometer capnometer = new MySimulatedCapnometer();

    @Override
    public void connect(String str) {
        capnometer.connect(executor);
        super.connect(str);
    }

    @Override
    public void disconnect() {
        capnometer.disconnect();
        super.disconnect();
    }

    public SimCapnometer(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);

        co2 = createSampleArrayInstance(ice.MDC_CAPNOGRAPH.VALUE);
        respiratoryRate = createNumericInstance(rosetta.MDC_RESP_RATE.VALUE);
        etCO2 = createNumericInstance(rosetta.MDC_AWAY_CO2_EXP.VALUE);

        deviceIdentity.model = "Capnometer (Simulated)";
        writeDeviceIdentity();
    }

    @Override
    protected String iconResourceName() {
        return "co2.png";
    }

    @Override
    public void simulatedNumeric(GlobalSimulationObjective obj) {
        if (rosetta.MDC_RESP_RATE.VALUE.equals(obj.metric_id.userData)) {
            capnometer.setRespirationRate((int) obj.value);
        } else if (rosetta.MDC_AWAY_CO2_EXP.VALUE.equals(obj.metric_id.userData)) {
            capnometer.setEndTidalCO2((int) obj.value);
        }
    }
}
