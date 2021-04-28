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
import ice.NumericSQI;

import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;
import org.mdpnp.devices.simulation.GlobalSimulationObjectiveListener;
import org.mdpnp.rtiapi.data.EventLoop;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

/**
 * @author Jeff Plourde
 *
 */
public class SimCapnometer extends AbstractSimulatedConnectedDevice {

    private InstanceHolder<ice.SampleArray> co2;
    private final InstanceHolder<ice.Numeric> respiratoryRate, etCO2;
    private final SimulatedCapnometer capnometer;

    private class SimulatedCapnometerExt  extends SimulatedCapnometer {

        public SimulatedCapnometerExt(DeviceClock referenceClock) {
            super(referenceClock);
        }

        @Override
        protected void receiveCO2(DeviceClock.Reading sampleTime, Number[] co2Values, int respiratoryRateValue, int etCO2Value, int frequency) {
            co2 = sampleArraySample(co2, co2Values, getSQI(), rosetta.MDC_AWAY_CO2.VALUE, "", 0, 
                    rosetta.MDC_DIM_MMHG.VALUE, frequency, sampleTime);
            numericSample(respiratoryRate, respiratoryRateValue, getSQI(), sampleTime);
            numericSample(etCO2, etCO2Value, getSQI(), sampleTime);

        }
    }


    @Override
    public boolean connect(String str) {
        capnometer.connect(executor);
        return super.connect(str);
    }

    @Override
    public void disconnect() {
        capnometer.disconnect();
        super.disconnect();
    }

    public SimCapnometer(final Subscriber subscriber, final Publisher publisher, EventLoop eventLoop) {
        super(subscriber, publisher, eventLoop);

        DeviceClock referenceClock = super.getClockProvider();
        capnometer = new SimulatedCapnometerExt(referenceClock);

        respiratoryRate = createNumericInstance(rosetta.MDC_CO2_RESP_RATE.VALUE, "");
        etCO2 = createNumericInstance(rosetta.MDC_AWAY_CO2_ET.VALUE, "");

        deviceIdentity.model = "Capnometer (Simulated)";
        writeDeviceIdentity();
    }

    @Override
    protected String iconResourceName() {
        return "co2.png";
    }

    @Override
    public void simulatedNumeric(GlobalSimulationObjective obj) {
        Number value = GlobalSimulationObjectiveListener.toIntegerNumber(obj);
        if (rosetta.MDC_RESP_RATE.VALUE.equals(obj.metric_id) ||
                rosetta.MDC_CO2_RESP_RATE.VALUE.equals(obj.metric_id)) {
            capnometer.setRespirationRate(value);
        } else if (rosetta.MDC_AWAY_CO2_ET.VALUE.equals(obj.metric_id)) {
            capnometer.setEndTidalCO2(value);
        }
    }
}
