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
import ice.NumericSQI;

import java.util.HashMap;
import java.util.Map;

import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.DeviceClock.Reading;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;
import org.mdpnp.devices.simulation.GlobalSimulationObjectiveListener;
import org.mdpnp.devices.simulation.NumberWithJitter;
import org.mdpnp.devices.simulation.co2.SimulatedCapnometer;
import org.mdpnp.devices.simulation.ecg.SimulatedElectroCardioGram;
import org.mdpnp.devices.simulation.ibp.SimulatedInvasiveBloodPressure;
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
    private static final float FREQUENCY_HZ = 1.03448f;
	private static final double DEFAULT_JITTER_CEILING = 100.0;
	private static final double DEFAULT_JITTER_FLOOR = 90.0;
	private static final double DEFAULT_JITTER_STEP_AMT = 0.25;
	private static final double DEFAULT_JITTER_START = 95.0;
	private static final String ECG_RESP_RATE = "ecgRespRate";
	private static final String HEART_RATE = "heartRate";
	private static final String CO22 = "co2";
	private static final String RESP_RATE = "respRate";
	private static final String PRESSURE2 = "pressure";
	private static final String PULSE_OX_HR = "pulseOxHR";
	private static final String PULSE_OX = "pulseOx";
	private static final String PLETH= "pleth";
	private static final String ECG= "ecg";
	private static final String PRESSURESA= "pressuresa";
	private static final String CO2SA= "co2sa";
	
	private static final Logger log = LoggerFactory.getLogger(SimMultiparameter.class);

    protected final InstanceHolder<ice.Numeric> pulse, SpO2, respiratoryRate, etCO2, ecgRespiratoryRate, heartRate, systolic, diastolic;
    protected InstanceHolder<ice.SampleArray> pleth, co2, i, ii, iii, pressure;
    
	protected NumericSQI currentPulseOxSQI = new NumericSQI(), currentPulseOxHRSQI = new NumericSQI(),
			currentPressureSQI = new NumericSQI(), currentRespRateSQI = new NumericSQI(),
			currentCO2SQI = new NumericSQI(), currentHeartRateSQI = new NumericSQI(),
			currentECGRespRateSQI = new NumericSQI(), currentPlethSQI = new NumericSQI(),
			currentECGSQI = new NumericSQI(), currentArrayPressureSQI = new NumericSQI(), 
			currentArrayCO2SQI = new NumericSQI();
    
	private Map<String, NumberWithJitter<Float>> jitterAccuracyMap = new HashMap<String, NumberWithJitter<Float>>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = -3618879384119365537L;

		{
			put(PULSE_OX, new NumberWithJitter<Float>(DEFAULT_JITTER_START, DEFAULT_JITTER_STEP_AMT,
					DEFAULT_JITTER_FLOOR, DEFAULT_JITTER_CEILING));
			put(PULSE_OX_HR, new NumberWithJitter<Float>(DEFAULT_JITTER_START, DEFAULT_JITTER_STEP_AMT,
					DEFAULT_JITTER_FLOOR, DEFAULT_JITTER_CEILING));
			put(PRESSURE2, new NumberWithJitter<Float>(DEFAULT_JITTER_START, DEFAULT_JITTER_STEP_AMT,
					DEFAULT_JITTER_FLOOR, DEFAULT_JITTER_CEILING));
			put(RESP_RATE, new NumberWithJitter<Float>(DEFAULT_JITTER_START, DEFAULT_JITTER_STEP_AMT,
					DEFAULT_JITTER_FLOOR, DEFAULT_JITTER_CEILING));
			put(CO22, new NumberWithJitter<Float>(DEFAULT_JITTER_START, DEFAULT_JITTER_STEP_AMT, DEFAULT_JITTER_FLOOR,
					DEFAULT_JITTER_CEILING));
			put(HEART_RATE, new NumberWithJitter<Float>(DEFAULT_JITTER_START, DEFAULT_JITTER_STEP_AMT,
					DEFAULT_JITTER_FLOOR, DEFAULT_JITTER_CEILING));
			put(ECG_RESP_RATE, new NumberWithJitter<Float>(DEFAULT_JITTER_START, DEFAULT_JITTER_STEP_AMT,
					DEFAULT_JITTER_FLOOR, DEFAULT_JITTER_CEILING));
			put(PLETH, new NumberWithJitter<Float>(DEFAULT_JITTER_START, DEFAULT_JITTER_STEP_AMT,
					DEFAULT_JITTER_FLOOR, DEFAULT_JITTER_CEILING));
			put(ECG, new NumberWithJitter<Float>(DEFAULT_JITTER_START, DEFAULT_JITTER_STEP_AMT,
					DEFAULT_JITTER_FLOOR, DEFAULT_JITTER_CEILING));
			put(PRESSURESA, new NumberWithJitter<Float>(DEFAULT_JITTER_START, DEFAULT_JITTER_STEP_AMT,
					DEFAULT_JITTER_FLOOR, DEFAULT_JITTER_CEILING));
			put(CO2SA, new NumberWithJitter<Float>(DEFAULT_JITTER_START, DEFAULT_JITTER_STEP_AMT,
					DEFAULT_JITTER_FLOOR, DEFAULT_JITTER_CEILING));
		}
	};

    private class SimulatedPulseOximeterExt extends SimulatedPulseOximeter {

        public SimulatedPulseOximeterExt(DeviceClock referenceClock) {
            super(referenceClock);
        }

        @Override
        protected void receivePulseOx(DeviceClock.Reading sampleTime, int heartRate, int SpO2, Number[] plethValues, int frequency) {
        	currentPulseOxHRSQI.accuracy = jitterAccuracyMap.get(PULSE_OX_HR).floatValue();
        	currentPulseOxHRSQI.frequency = FREQUENCY_HZ;
            numericSample(pulse, heartRate, currentPulseOxHRSQI, sampleTime);
            currentPulseOxSQI.accuracy = jitterAccuracyMap.get(PULSE_OX).floatValue();
            currentPulseOxSQI.frequency = FREQUENCY_HZ;
            numericSample(SimMultiparameter.this.SpO2, SpO2, currentPulseOxSQI, sampleTime);
            currentPlethSQI.accuracy = jitterAccuracyMap.get(PLETH).floatValue();
            currentPlethSQI.frequency = frequency;
            pleth = sampleArraySample(pleth, plethValues, currentPlethSQI, rosetta.MDC_PULS_OXIM_PLETH.VALUE, "", 0, 
                    rosetta.MDC_DIM_DIMLESS.VALUE, frequency, sampleTime);
        }
    }
    
    private class SimulatedInvasiveBloodPressureExt extends SimulatedInvasiveBloodPressure {
        public SimulatedInvasiveBloodPressureExt(DeviceClock referenceClock) {  
            super(referenceClock);
        }
        
        @Override
        protected void receivePressure(Reading sampleTime, int systolic, int diastolic, Number[] waveValues, int frequency) {
        	currentPressureSQI.accuracy = jitterAccuracyMap.get(PRESSURE2).floatValue();
        	currentPressureSQI.frequency = FREQUENCY_HZ;
            numericSample(SimMultiparameter.this.systolic, systolic, currentPressureSQI, sampleTime);
            numericSample(SimMultiparameter.this.diastolic, diastolic, currentPressureSQI, sampleTime);
            currentArrayPressureSQI.accuracy = jitterAccuracyMap.get(PRESSURESA).floatValue();
            currentArrayPressureSQI.frequency = frequency;
            pressure = sampleArraySample(pressure, waveValues, currentArrayPressureSQI, rosetta.MDC_PRESS_BLD_ART_ABP.VALUE, "", 0,
                    rosetta.MDC_DIM_DIMLESS.VALUE, frequency, sampleTime);
        }
        
    }
    
    private class SimulatedCapnometerExt extends SimulatedCapnometer {

        public SimulatedCapnometerExt(DeviceClock referenceClock) {
            super(referenceClock);
        }

        @Override
        protected void receiveCO2(DeviceClock.Reading sampleTime, Number[] co2Values, int respiratoryRateValue, int etCO2Value, int frequency) {
        	currentArrayCO2SQI.accuracy = jitterAccuracyMap.get(CO2SA).floatValue();
        	currentArrayCO2SQI.frequency = frequency;
            co2 = sampleArraySample(co2, co2Values, currentArrayCO2SQI, rosetta.MDC_AWAY_CO2.VALUE, "", 0, 
                    rosetta.MDC_DIM_MMHG.VALUE, frequency, sampleTime);
            currentRespRateSQI.accuracy = jitterAccuracyMap.get(RESP_RATE).floatValue();
            currentCO2SQI.accuracy = jitterAccuracyMap.get(CO22).floatValue();
            currentRespRateSQI.frequency = FREQUENCY_HZ;
            currentCO2SQI.frequency = FREQUENCY_HZ;
            numericSample(respiratoryRate, respiratoryRateValue, currentRespRateSQI, sampleTime);
            numericSample(etCO2, etCO2Value, currentCO2SQI, sampleTime);
        }
    }
    
    private class SimulatedElectroCardioGramExt extends SimulatedElectroCardioGram {

        public SimulatedElectroCardioGramExt(DeviceClock referenceClock) {
            super(referenceClock);
        }

        @Override
        protected void receiveECG(DeviceClock.Reading sampleTime, Number[] iValues, Number[] iiValues, Number[] iiiValues,
                                  int heartRateValue, int respiratoryRateValue, int frequency) {

            try {
                // TODO get better numbers in actual millivolts
            	currentECGSQI.accuracy = jitterAccuracyMap.get(ECG).floatValue();
            	currentECGSQI.frequency = frequency;
                i = sampleArraySample(i, iValues, currentECGSQI, ice.MDC_ECG_LEAD_I.VALUE, "", 0, 
                        rosetta.MDC_DIM_DIMLESS.VALUE, frequency, sampleTime);
                ii = sampleArraySample(ii, iiValues, currentECGSQI, ice.MDC_ECG_LEAD_II.VALUE, "", 0, 
                        rosetta.MDC_DIM_DIMLESS.VALUE, frequency, sampleTime);
                iii = sampleArraySample(iii, iiiValues, currentECGSQI, ice.MDC_ECG_LEAD_III.VALUE, "", 0, 
                        rosetta.MDC_DIM_DIMLESS.VALUE, frequency, sampleTime);
                currentHeartRateSQI.accuracy = jitterAccuracyMap.get(HEART_RATE).floatValue();
                currentHeartRateSQI.frequency = FREQUENCY_HZ;
                numericSample(heartRate, (float) heartRateValue, currentHeartRateSQI, sampleTime);
                currentECGRespRateSQI.accuracy = jitterAccuracyMap.get(ECG_RESP_RATE).floatValue();
                currentECGRespRateSQI.frequency = FREQUENCY_HZ;
                numericSample(ecgRespiratoryRate, (float) respiratoryRateValue, currentECGRespRateSQI, sampleTime);
            } catch (Throwable t) {
                log.error("Error simulating ECG data", t);
            }
        }
    }

    private final SimulatedElectroCardioGram ecg;
    private final SimulatedPulseOximeter pulseox;
    private final SimulatedCapnometer capnometer;
    private final SimulatedInvasiveBloodPressure ibp;

    @Override
    public boolean connect(String str) {
        pulseox.connect(executor);
        capnometer.connect(executor);
        ecg.connect(executor);
        ibp.connect(executor);
        return super.connect(str);
    }

    @Override
    public void disconnect() {
        pulseox.disconnect();
        capnometer.disconnect();
        ecg.disconnect();
        ibp.disconnect();
        super.disconnect();
    }

    public SimMultiparameter(final Subscriber subscriber, final Publisher publisher, EventLoop eventLoop) {
        super(subscriber, publisher, eventLoop);

        DeviceClock referenceClock = getClockProvider();

        ecg = new SimulatedElectroCardioGramExt(referenceClock);
        pulseox = new SimulatedPulseOximeterExt(referenceClock);
        capnometer = new SimulatedCapnometerExt(referenceClock);
        ibp = new SimulatedInvasiveBloodPressureExt(referenceClock);

        pulse = createNumericInstance(rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, "");
        SpO2 = createNumericInstance(rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, "");
        
        respiratoryRate = createNumericInstance(rosetta.MDC_CO2_RESP_RATE.VALUE, "");
        etCO2 = createNumericInstance(rosetta.MDC_AWAY_CO2_ET.VALUE, "");

        ecgRespiratoryRate = createNumericInstance(rosetta.MDC_TTHOR_RESP_RATE.VALUE, "");
        heartRate = createNumericInstance(rosetta.MDC_ECG_HEART_RATE.VALUE, "");
        
        systolic = createNumericInstance(rosetta.MDC_PRESS_BLD_ART_ABP_SYS.VALUE, "");
        diastolic = createNumericInstance(rosetta.MDC_PRESS_BLD_ART_ABP_DIA.VALUE, "");

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
        if (obj != null && pulseox != null && capnometer != null && ecg != null && pressure != null) {
            Number value = GlobalSimulationObjectiveListener.toIntegerNumber(obj);
            if(rosetta.MDC_PULS_RATE.VALUE.equals(obj.metric_id)) {
                ecg.setTargetHeartRate(value);
                pulseox.setTargetHeartRate(value);
            } else if(rosetta.MDC_RESP_RATE.VALUE.equals(obj.metric_id)) {
                ecg.setTargetRespiratoryRate(value);
                capnometer.setRespirationRate(value);
            } else if(rosetta.MDC_PRESS_BLD_SYS.VALUE.equals(obj.metric_id)) {
                ibp.setSystolic(value);
            } else if(rosetta.MDC_PRESS_BLD_DIA.VALUE.equals(obj.metric_id)) {
                ibp.setDiastolic(value);
            } else if(rosetta.MDC_PRESS_BLD_MEAN.VALUE.equals(obj.metric_id)) {
                // for the future
            } else if (rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE.equals(obj.metric_id)) {
                pulseox.setTargetHeartRate(value);
            } else if (rosetta.MDC_PULS_OXIM_SAT_O2.VALUE.equals(obj.metric_id)) {
                pulseox.setTargetSpO2(value);
            } else if (rosetta.MDC_CO2_RESP_RATE.VALUE.equals(obj.metric_id)) {
                capnometer.setRespirationRate(value);
            } else if (rosetta.MDC_AWAY_CO2_ET.VALUE.equals(obj.metric_id)) {
                capnometer.setEndTidalCO2(value);
            } else if (rosetta.MDC_ECG_HEART_RATE.VALUE.equals(obj.metric_id)) {
                ecg.setTargetHeartRate(value);
            } else if (rosetta.MDC_TTHOR_RESP_RATE.VALUE.equals(obj.metric_id)) {
                ecg.setTargetRespiratoryRate(value);
            }
        }
    }

}
