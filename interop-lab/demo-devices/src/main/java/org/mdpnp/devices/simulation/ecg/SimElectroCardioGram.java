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
package org.mdpnp.devices.simulation.ecg;

import ice.GlobalSimulationObjective;

import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;
import org.mdpnp.devices.simulation.GlobalSimulationObjectiveListener;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

/**
 * @author Jeff Plourde
 * 
 */
public class SimElectroCardioGram extends AbstractSimulatedConnectedDevice {
    private static final Logger log = LoggerFactory.getLogger(SimElectroCardioGram.class);
    protected InstanceHolder<ice.SampleArray> i, ii, iii;

    protected final InstanceHolder<ice.Numeric> respiratoryRate, heartRate;

    // private Number[][][] ecgCache = new Number[3][4][];
    // private int ecgCount;

//    private static final Number[] copy(Number[] source, Number[] target) {
//        if (target == null || target.length < source.length) {
//            target = new Number[source.length];
//        }
//        for (int i = 0; i < source.length; i++) {
//            target[i] = source[i];
//        }
//        return target;
//    }

    private class SimulatedElectroCardioGramExt extends SimulatedElectroCardioGram {

        public SimulatedElectroCardioGramExt(DeviceClock referenceClock) {
            super(referenceClock);
        }

        @Override
        protected void receiveECG(DeviceClock.Reading sampleTime, Number[] iValues, Number[] iiValues, Number[] iiiValues,
                                  double heartRateValue, double respiratoryRateValue, int frequency) {
            // ecgCache[0][ecgCount] = copy(iValues, ecgCache[0][ecgCount]);
            // ecgCache[1][ecgCount] = copy(iiValues, ecgCache[1][ecgCount]);
            // ecgCache[2][ecgCount] = copy(iiiValues, ecgCache[2][ecgCount]);
            //
            // ecgCount++;
            //
            // if(ecgCount==4) {
            // for(int i = 0; i < 4; i++) {
            // sampleArraySample(SimElectroCardioGram.this.i, ecgCache[0][i],
            // (int) msPerSample, null);
            // sampleArraySample(SimElectroCardioGram.this.ii, ecgCache[1][i],
            // (int) msPerSample, null);
            // sampleArraySample(SimElectroCardioGram.this.iii, ecgCache[2][i],
            // (int) msPerSample, null);
            // }
            // ecgCount = 0;
            // }

            try {
                // TODO should get better data that's actually in millivolts
                SimElectroCardioGram.this.i = sampleArraySample(SimElectroCardioGram.this.i, iValues, ice.MDC_ECG_LEAD_I.VALUE, "", 0,
                        rosetta.MDC_DIM_DIMLESS.VALUE, frequency, sampleTime);
                SimElectroCardioGram.this.ii = sampleArraySample(SimElectroCardioGram.this.ii, iiValues, ice.MDC_ECG_LEAD_II.VALUE, "", 0, 
                        rosetta.MDC_DIM_DIMLESS.VALUE, frequency, sampleTime);
                SimElectroCardioGram.this.iii = sampleArraySample(SimElectroCardioGram.this.iii, iiiValues, ice.MDC_ECG_LEAD_III.VALUE, "", 0, 
                        rosetta.MDC_DIM_DIMLESS.VALUE, frequency, sampleTime);

                numericSample(heartRate, (float) heartRateValue, sampleTime);
                numericSample(respiratoryRate, (float) respiratoryRateValue, sampleTime);
            } catch (Throwable t) {
                log.error("Error simulating ECG data", t);
            }
        }
    }

    private final SimulatedElectroCardioGram ecg;

    @Override
    public boolean connect(String str) {
        ecg.connect(executor);
        return super.connect(str);
    }

    @Override
    public void disconnect() {
        ecg.disconnect();
        super.disconnect();
    }

    public SimElectroCardioGram(final Subscriber subscriber, final Publisher publisher, EventLoop eventLoop) {
        super(subscriber, publisher, eventLoop);

        DeviceClock referenceClock = super.getClockProvider();
        ecg = new SimulatedElectroCardioGramExt(referenceClock);

        respiratoryRate = createNumericInstance(rosetta.MDC_TTHOR_RESP_RATE.VALUE, "");
        heartRate = createNumericInstance(rosetta.MDC_ECG_HEART_RATE.VALUE, "");

        deviceIdentity.model = "ECG (Simulated)";
        writeDeviceIdentity();
    }

    @Override
    protected String iconResourceName() {
        return "ecg.png";
    }
    
    @Override
    public void simulatedNumeric(GlobalSimulationObjective obj) {
        Number value = GlobalSimulationObjectiveListener.toIntegerNumber(obj);
        if (rosetta.MDC_TTHOR_RESP_RATE.VALUE.equals(obj.metric_id) ||
            rosetta.MDC_RESP_RATE.VALUE.equals(obj.metric_id)) {
            ecg.setTargetRespiratoryRate(value);
        } else if (rosetta.MDC_ECG_HEART_RATE.VALUE.equals(obj.metric_id) ||
                   rosetta.MDC_PULS_RATE.VALUE.equals(obj.metric_id)) {
            ecg.setTargetHeartRate(value);
        }
    }
}
