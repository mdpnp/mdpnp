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

import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;
import org.mdpnp.devices.simulation.GlobalSimulationObjectiveListener;
import org.mdpnp.rtiapi.data.EventLoop;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

import ice.GlobalSimulationObjective;
import ice.NumericSQI;

/**
 * @author Jeff Plourde
 * 
 * This pulse oximeter is "fully fixed" - the averaging time cannot be set either
 * by the operator, or through software
 *
 */
public class EightSecFixedAvgSimPulseOximeter extends AbstractSimulatedConnectedDevice {
	
	//Need to do something in here about setting an alarm limit...
	//AbstractSimulatedConnectedDevice.setAlarmLimit will be a starting point.
	
    private final InstanceHolder<ice.Numeric> pulse;
    private final InstanceHolder<ice.Numeric> SpO2;
    private InstanceHolder<ice.SampleArray> pleth;
    private final SimulatedPulseOximeter pulseox;
    
    private final InstanceHolder<ice.Numeric> canGetAveragingTime;
    private final InstanceHolder<ice.Numeric> operCanSetAveragingTime;
    private final InstanceHolder<ice.Numeric> softCanSetAveragingTime;
    private final InstanceHolder<ice.Numeric> averagingTime;


    private class SimulatedPulseOximeterExt extends SimulatedPulseOximeter {

        public SimulatedPulseOximeterExt(DeviceClock referenceClock) {
            super(referenceClock);
        }

        @Override
        protected void receivePulseOx(DeviceClock.Reading sampleTime, int heartRate, int SpO2, Number[] plethValues, int frequency) {
            numericSample(pulse, heartRate, sampleTime);
            numericSample(EightSecFixedAvgSimPulseOximeter.this.SpO2, SpO2, sampleTime);
            numericSample(operCanSetAveragingTime, 0, sampleTime);
            numericSample(softCanSetAveragingTime, 0, sampleTime);
            //Indicate that we cannot supply the averaging time.
            //System.err.println("Doing 0 for canGetAveragingTime");
            numericSample(canGetAveragingTime, 1, sampleTime);
            //Still need to decide how to indicate an unknown or invalid value
            //for cases where it should be gettable.  Return -1 in this case,
            //remembering that when returning 0 for "canGet", this should not
            //be used anyway.
            //System.err.println("Doing 0 for averagingTime");
            numericSample(averagingTime, 8, sampleTime);
            pleth = sampleArraySample(pleth, plethValues, new NumericSQI(), rosetta.MDC_PULS_OXIM_PLETH.VALUE, "", 0, 
                    rosetta.MDC_DIM_DIMLESS.VALUE, frequency, sampleTime);
        }
    }

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

    public EightSecFixedAvgSimPulseOximeter(final Subscriber subscriber, final Publisher publisher, EventLoop eventLoop) {
        super(subscriber, publisher, eventLoop);

        DeviceClock referenceClock = super.getClockProvider();
        pulseox = new SimulatedPulseOximeterExt(referenceClock);

        pulse = createNumericInstance(rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, "");
        SpO2 = createNumericInstance(rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, "");
        canGetAveragingTime = createNumericInstance(ice.SP02_SOFT_CAN_GET_AVERAGING_RATE.VALUE, "");
        operCanSetAveragingTime = createNumericInstance(ice.SP02_OPER_CAN_SET_AVERAGING_RATE.VALUE, "");
        softCanSetAveragingTime = createNumericInstance(ice.SP02_SOFT_CAN_SET_AVERAGING_RATE.VALUE, "");
        averagingTime = createNumericInstance(ice.SP02_AVERAGING_RATE.VALUE, "");

        deviceIdentity.model = "8s Fully Fixed Average Pulse Ox (Simulated)";
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
            Number value = GlobalSimulationObjectiveListener.toIntegerNumber(obj);
            if (rosetta.MDC_PULS_RATE.VALUE.equals(obj.metric_id) ||
                rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE.equals(obj.metric_id)) {
                pulseox.setTargetHeartRate(value);
            } else if (rosetta.MDC_PULS_OXIM_SAT_O2.VALUE.equals(obj.metric_id)) {
                pulseox.setTargetSpO2(value);
            }
        }
    }

}
