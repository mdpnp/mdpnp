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
package org.mdpnp.devices.simulation.multi;

import ice.GlobalSimulationObjective;

import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;
import org.mdpnp.devices.simulation.GlobalSimulationObjectiveListener;
import org.mdpnp.devices.simulation.co2.SimulatedCapnometer;
import org.mdpnp.devices.simulation.ecg.SimulatedElectroCardioGram;
import org.mdpnp.devices.simulation.pulseox.SimulatedPulseOximeter;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

/**
 * @author Jeff Plourde
 *
 */
public class SimMultiparameter extends AbstractSimulatedConnectedDevice {
    
    private static final Logger log = LoggerFactory.getLogger(SimMultiparameter.class);

    protected final InstanceHolder<ice.Numeric> pulse, SpO2, respiratoryRate, etCO2, ecgRespiratoryRate, heartRate;
    protected InstanceHolder<ice.SampleArray> pleth, co2, i, ii, iii;

    private class SimulatedPulseOximeterExt extends SimulatedPulseOximeter {

        public SimulatedPulseOximeterExt(DeviceClock referenceClock) {
            super(referenceClock);
        }

        @Override
        protected void receivePulseOx(DeviceClock.Reading sampleTime, int heartRate, int SpO2, Number[] plethValues, int frequency) {

            numericSample(pulse, heartRate, sampleTime);
            numericSample(SimMultiparameter.this.SpO2, SpO2, sampleTime);
            pleth = sampleArraySample(pleth, plethValues, rosetta.MDC_PULS_OXIM_PLETH.VALUE, "", 0, 
                    rosetta.MDC_DIM_DIMLESS.VALUE, frequency, sampleTime);
        }
    }
    
    private class SimulatedCapnometerExt extends SimulatedCapnometer {

        public SimulatedCapnometerExt(DeviceClock referenceClock) {
            super(referenceClock);
        }

        @Override
        protected void receiveCO2(DeviceClock.Reading sampleTime, Number[] co2Values, int respiratoryRateValue, int etCO2Value, int frequency) {

            co2 = sampleArraySample(co2, co2Values, rosetta.MDC_AWAY_CO2.VALUE, "", 0, 
                    rosetta.MDC_DIM_MMHG.VALUE, frequency, sampleTime);
            numericSample(respiratoryRate, respiratoryRateValue, sampleTime);
            numericSample(etCO2, etCO2Value, sampleTime);

        }
    }
    
    private class SimulatedElectroCardioGramExt extends SimulatedElectroCardioGram {

        public SimulatedElectroCardioGramExt(DeviceClock referenceClock) {
            super(referenceClock);
        }

        @Override
        protected void receiveECG(DeviceClock.Reading sampleTime, Number[] iValues, Number[] iiValues, Number[] iiiValues,
                                  double heartRateValue, double respiratoryRateValue, int frequency) {

            try {
                // TODO get better numbers in actual millivolts
                i = sampleArraySample(i, iValues, ice.MDC_ECG_LEAD_I.VALUE, "", 0, 
                        rosetta.MDC_DIM_DIMLESS.VALUE, frequency, sampleTime);
                ii = sampleArraySample(ii, iiValues, ice.MDC_ECG_LEAD_II.VALUE, "", 0, 
                        rosetta.MDC_DIM_DIMLESS.VALUE, frequency, sampleTime);
                iii = sampleArraySample(iii, iiiValues, ice.MDC_ECG_LEAD_III.VALUE, "", 0, 
                        rosetta.MDC_DIM_DIMLESS.VALUE, frequency, sampleTime);

                numericSample(heartRate, (float) heartRateValue, sampleTime);
                numericSample(ecgRespiratoryRate, (float) respiratoryRateValue, sampleTime);
            } catch (Throwable t) {
                log.error("Error simulating ECG data", t);
            }
        }
    }

    private final SimulatedElectroCardioGram ecg;
    private final SimulatedPulseOximeter pulseox;
    private final SimulatedCapnometer capnometer;

    @Override
    public boolean connect(String str) {
        pulseox.connect(executor);
        capnometer.connect(executor);
        ecg.connect(executor);
        return super.connect(str);
    }

    @Override
    public void disconnect() {
        pulseox.disconnect();
        capnometer.disconnect();
        ecg.disconnect();
        super.disconnect();
    }

    public SimMultiparameter(final Subscriber subscriber, final Publisher publisher, EventLoop eventLoop) {
        super(subscriber, publisher, eventLoop);

        DeviceClock referenceClock = getClockProvider();

        ecg = new SimulatedElectroCardioGramExt(referenceClock);
        pulseox = new SimulatedPulseOximeterExt(referenceClock);
        capnometer = new SimulatedCapnometerExt(referenceClock);

        pulse = createNumericInstance(rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, "");
        SpO2 = createNumericInstance(rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, "");
        
        respiratoryRate = createNumericInstance(rosetta.MDC_CO2_RESP_RATE.VALUE, "");
        etCO2 = createNumericInstance(rosetta.MDC_AWAY_CO2_ET.VALUE, "");

        ecgRespiratoryRate = createNumericInstance(rosetta.MDC_TTHOR_RESP_RATE.VALUE, "");
        heartRate = createNumericInstance(rosetta.MDC_ECG_HEART_RATE.VALUE, "");

        deviceIdentity.model = "Multiparameter (Simulated)";
        writeDeviceIdentity();
    }

    @Override
    protected String iconResourceName() {
        return "multi.png";
    }
    
    

    @Override
    public void simulatedNumeric(GlobalSimulationObjective obj) {
        // Currently the super ctor registers for this callback; so pulseox might not yet be initialized
        if (obj != null && pulseox != null) {
            if (rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE.equals(obj.metric_id)) {
                Number value = GlobalSimulationObjectiveListener.toDoubleNumber(obj);
                pulseox.setTargetHeartRate(value);
            } else if (rosetta.MDC_PULS_OXIM_SAT_O2.VALUE.equals(obj.metric_id)) {
                Number value = GlobalSimulationObjectiveListener.toDoubleNumber(obj);
                pulseox.setTargetSpO2(value);
            } else if (rosetta.MDC_CO2_RESP_RATE.VALUE.equals(obj.metric_id)) {
                Number value = GlobalSimulationObjectiveListener.toDoubleNumber(obj);
                capnometer.setRespirationRate(value);
                ecg.setTargetRespiratoryRate(value);
            } else if (rosetta.MDC_AWAY_CO2_ET.VALUE.equals(obj.metric_id)) {
                Number value = GlobalSimulationObjectiveListener.toIntegerNumber(obj);
                capnometer.setEndTidalCO2(value);
            } else if (rosetta.MDC_ECG_HEART_RATE.VALUE.equals(obj.metric_id)) {
                Number value = GlobalSimulationObjectiveListener.toDoubleNumber(obj);
                ecg.setTargetHeartRate(value);
            }
        }
    }

}
