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
package org.mdpnp.devices.simulation.ibp;

import ice.GlobalSimulationObjective;
import ice.NumericSQI;

import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;
import org.mdpnp.devices.simulation.GlobalSimulationObjectiveListener;
import org.mdpnp.devices.simulation.NumberWithJitter;
import org.mdpnp.rtiapi.data.EventLoop;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

/**
 * @author Jeff Plourde
 *
 */
public class SimInvasivePressure extends AbstractSimulatedConnectedDevice {

	private static final double DEFAULT_JITTER_CEILING = 100.0;
	private static final double DEFAULT_JITTER_FLOOR = 90.0;
	private static final double DEFAULT_JITTER_STEP_AMT = 0.25;
	private static final double DEFAULT_JITTER_START = 95.0;
	
    protected final InstanceHolder<ice.Numeric> systolic;
    protected final InstanceHolder<ice.Numeric> diastolic;
    private InstanceHolder<ice.SampleArray> wave;
    private final SimulatedInvasiveBloodPressure pressure;
    
    protected NumericSQI currentPressureSQI = new NumericSQI();
    protected NumericSQI currentSPressureSQI = new NumericSQI();
    protected NumericSQI currentDPressureSQI = new NumericSQI();
    
    private NumberWithJitter<Float> pressureJitter = new NumberWithJitter<Float>(DEFAULT_JITTER_START, DEFAULT_JITTER_STEP_AMT,
			DEFAULT_JITTER_FLOOR, DEFAULT_JITTER_CEILING);
    private NumberWithJitter<Float> pressureSJitter = new NumberWithJitter<Float>(DEFAULT_JITTER_START, DEFAULT_JITTER_STEP_AMT,
			DEFAULT_JITTER_FLOOR, DEFAULT_JITTER_CEILING);
    private NumberWithJitter<Float> pressureDJitter = new NumberWithJitter<Float>(DEFAULT_JITTER_START, DEFAULT_JITTER_STEP_AMT,
			DEFAULT_JITTER_FLOOR, DEFAULT_JITTER_CEILING);

    private class SimulatedInvasiveBloodPressureExt extends SimulatedInvasiveBloodPressure {

        public SimulatedInvasiveBloodPressureExt(DeviceClock referenceClock) {
            super(referenceClock);
        }

        @Override
        protected void receivePressure(DeviceClock.Reading sampleTime, int systolic, int diastolic, Number[] waveValues, int frequency) {
        	currentSPressureSQI.accuracy = pressureSJitter.floatValue();
        	currentSPressureSQI.frequency = 1.03448f;
        	numericSample(SimInvasivePressure.this.systolic, systolic, currentSPressureSQI, sampleTime);
        	currentDPressureSQI.accuracy = pressureDJitter.floatValue();
        	currentDPressureSQI.frequency = 1.03448f;
            numericSample(SimInvasivePressure.this.diastolic, diastolic, currentDPressureSQI, sampleTime);
            currentPressureSQI.accuracy = pressureJitter.floatValue();
            currentPressureSQI.frequency = frequency;
            wave = sampleArraySample(wave, waveValues, currentPressureSQI, rosetta.MDC_PRESS_BLD.VALUE, "", 0, 
                    rosetta.MDC_DIM_DIMLESS.VALUE, frequency, sampleTime);
        }
    }

    @Override
    public boolean connect(String str) {
        pressure.connect(executor);
        return super.connect(str);
    }

    @Override
    public void disconnect() {
        pressure.disconnect();
        super.disconnect();
    }

    public SimInvasivePressure(final Subscriber subscriber, final Publisher publisher, EventLoop eventLoop) {
        super(subscriber, publisher, eventLoop);

        DeviceClock referenceClock = super.getClockProvider();
        pressure = new SimulatedInvasiveBloodPressureExt(referenceClock);

        systolic = createNumericInstance(rosetta.MDC_PRESS_BLD_ART_ABP_SYS.VALUE, "");
        diastolic = createNumericInstance(rosetta.MDC_PRESS_BLD_ART_ABP_DIA.VALUE, "");

        deviceIdentity.model = "Invasive Pressure (Simulated)";
        writeDeviceIdentity();
    }

    @Override
    protected String iconResourceName() {
        return "pressure.png";
    }

    @Override
    public void simulatedNumeric(GlobalSimulationObjective obj) {
        if (obj != null && pressure != null) {
            if (rosetta.MDC_PRESS_BLD_SYS.VALUE.equals(obj.metric_id)) {
                Number value = GlobalSimulationObjectiveListener.toDoubleNumber(obj);
                pressure.setSystolic(value);
            } else if (rosetta.MDC_PRESS_BLD_DIA.VALUE.equals(obj.metric_id)) {
                Number value = GlobalSimulationObjectiveListener.toDoubleNumber(obj);
                pressure.setDiastolic(value);
            }
        }
    }

}
