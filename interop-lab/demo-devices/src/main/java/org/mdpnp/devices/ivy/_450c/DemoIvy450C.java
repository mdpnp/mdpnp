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
package org.mdpnp.devices.ivy._450c;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.mdpnp.devices.cpc.ansarB.AnsarB;
import org.mdpnp.devices.serial.AbstractDelegatingSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class DemoIvy450C extends AbstractDelegatingSerialDevice<AnsarB> {

    private static final String ECG_I_PREFIX = "ECG-I", ECG_II_PREFIX = "ECG-II", ECG_III_PREFIX = "ECG-III";

    private final static String nameOfECGWave(String lbl) {
        if (lbl == null || lbl.isEmpty()) {
            return null;
        }
        if (lbl.startsWith(ECG_III_PREFIX)) {
            return ice.MDC_ECG_LEAD_III.VALUE;
        } else if (lbl.startsWith(ECG_II_PREFIX)) {
            return ice.MDC_ECG_LEAD_II.VALUE;
        } else if (lbl.startsWith(ECG_I_PREFIX)) {
            return ice.MDC_ECG_LEAD_I.VALUE;
        } else {
            log.warn("Unknown ECG:" + lbl);
            return null;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(DemoIvy450C.class);

    public DemoIvy450C(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop, AnsarB.class);
        deviceIdentity.manufacturer = "Ivy";
        deviceIdentity.model = "450C";
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        writeDeviceIdentity();
    }

    private InstanceHolder<ice.Numeric> heartRate, co2RespiratoryRate, tthorRespiratoryRate, spo2, etco2, t1, t2, pulseRate, nibpSystolic, nibpDiastolic, nibpMean, nibpPulse,
            ibpSystolic1, ibpDiastolic1, ibpMean1, ibpSystolic2, ibpDiastolic2, ibpMean2;

    private InstanceHolder<ice.SampleArray> ecgWave, impThorWave, co2Wave, plethWave, p1Wave, p2Wave;

    @Override
    protected String iconResourceName() {
        return "450c.png";
    }

    private class MyAnsarB extends AnsarB {
        public MyAnsarB(InputStream in, OutputStream out) {
            super(in, out);
        }

        @Override
        public boolean receiveMessage(byte[] message, int off, int len) throws IOException {
            // Any patient alert that does not get updated will cease to exist
            markOldPatientAlertInstances();
            boolean res = super.receiveMessage(message, off, len);
            clearOldPatientAlertInstances();
            return res;
        }
        
        @Override
        protected void receiveLine(String line) {
            reportConnected("message received");
            super.receiveLine(line);
        }
        
        @Override
        protected void receiveEndTidalCO2(Integer value, String label, String alarm) {
            etco2 = numericSample(etco2, value, rosetta.MDC_AWAY_CO2_ET.VALUE, null);
            alarmIfPresent("ETCO2", alarm);
        }

        @Override
        protected void receiveECGWave(float[] data, int count, int frequency, String label) {
            String ecg = nameOfECGWave(label);
            
            if (ecg != null) {
                ecgWave = sampleArraySample(ecgWave, data, count, ecg, 0, frequency);
            } else {
                if (ecgWave != null) {
                    unregisterSampleArrayInstance(ecgWave, null);
                    ecgWave = null;
                }
            }
        }

        @Override
        protected void receiveRespWave(float[] data, int count, int frequency) {
            // This is less than ideal but if the device is reporting etCO2 we'll treat this as a capnogram
            // otherwise it is from respiratory impedance
            if(null != etco2 && etco2.data.value > 0) {
                co2Wave = sampleArraySample(co2Wave, data, count, rosetta.MDC_AWAY_CO2.VALUE, 0, frequency);
                impThorWave = sampleArraySample(impThorWave, null, 0, rosetta.MDC_IMPED_TTHOR.VALUE, 0, frequency);
            } else {
                impThorWave = sampleArraySample(impThorWave, data, count, rosetta.MDC_IMPED_TTHOR.VALUE, 0, frequency);
                co2Wave = sampleArraySample(co2Wave, null, 0, rosetta.MDC_AWAY_CO2.VALUE, 0, frequency);
            }
        }

        @Override
        protected void receivePlethWave(float[] data, int count, int frequency) {
            plethWave = sampleArraySample(plethWave, data, count, rosetta.MDC_PULS_OXIM_PLETH.VALUE, 0, frequency);
        }

        @Override
        protected void receiveP1Wave(float[] data, int count, int frequency) {
            p1Wave = sampleArraySample(p1Wave, data, count, rosetta.MDC_PRESS_BLD.VALUE, 0, frequency);
        }

        @Override
        protected void receiveP2Wave(float[] data, int count, int frequency) {
            p2Wave = sampleArraySample(p2Wave, data, count, rosetta.MDC_PRESS_BLD.VALUE, 1, frequency);
        }

        @Override
        protected void receiveHeartRate(Integer value, String label, String alarm) {
            // should be ECG heart rate? or should it .. depends upon mode
            heartRate = numericSample(heartRate, value, rosetta.MDC_ECG_HEART_RATE.VALUE, null);
            alarmIfPresent("HR", alarm);
        }

        @Override
        protected void receiveNIBP(Integer systolic, Integer diastolic, Integer mean, Integer pulse, String label, String alarm) {
            nibpSystolic = numericSample(nibpSystolic, systolic, rosetta.MDC_PRESS_CUFF_SYS.VALUE, null);
            nibpDiastolic = numericSample(nibpDiastolic, diastolic, rosetta.MDC_PRESS_CUFF_DIA.VALUE, null);
            nibpPulse = numericSample(nibpPulse, pulse, rosetta.MDC_PULS_RATE_NON_INV.VALUE, null);
            nibpMean = numericSample(nibpMean, mean, rosetta.MDC_PRESS_CUFF_MEAN.VALUE, null);
            alarmIfPresent("NIBP", alarm);
        }

        @Override
        protected void receivePressure1(Integer systolic, Integer diastolic, Integer mean, String label, String alarm) {
            ibpSystolic1 = numericSample(ibpSystolic1, systolic, rosetta.MDC_PRESS_BLD_SYS.VALUE, 0, null);
            ibpDiastolic1 = numericSample(ibpDiastolic1, diastolic, rosetta.MDC_PRESS_BLD_DIA.VALUE, 0, null);
            ibpMean1 = numericSample(ibpMean1, mean, rosetta.MDC_PRESS_BLD_MEAN.VALUE, 0, null);
            alarmIfPresent("P1", alarm);
        }

        @Override
        protected void receivePressure2(Integer systolic, Integer diastolic, Integer mean, String label, String alarm) {
            ibpSystolic2 = numericSample(ibpSystolic2, systolic, rosetta.MDC_PRESS_BLD_SYS.VALUE, 1, null);
            ibpDiastolic2 = numericSample(ibpDiastolic2, diastolic, rosetta.MDC_PRESS_BLD_DIA.VALUE, 1, null);
            ibpMean2 = numericSample(ibpMean2, mean, rosetta.MDC_PRESS_BLD_MEAN.VALUE, 1, null);
            alarmIfPresent("P2", alarm);
        }

        @Override
        protected void receiveRespiratoryRate(Integer value, String label, String alarm) {
            if(null != etco2 && etco2.data.value > 0) {
                co2RespiratoryRate = numericSample(co2RespiratoryRate, value, rosetta.MDC_CO2_RESP_RATE.VALUE, null);
                tthorRespiratoryRate = numericSample(tthorRespiratoryRate, (Integer)null, rosetta.MDC_TTHOR_RESP_RATE.VALUE, null);
            } else {
                tthorRespiratoryRate = numericSample(tthorRespiratoryRate, value, rosetta.MDC_TTHOR_RESP_RATE.VALUE, null);
                co2RespiratoryRate = numericSample(co2RespiratoryRate, (Integer)null, rosetta.MDC_CO2_RESP_RATE.VALUE, null);
            }
            alarmIfPresent("RR", alarm);
        }

        @Override
        protected void receiveSpO2(Integer value, String label, Integer pulseRate, String alarm) {
            spo2 = numericSample(spo2, value, rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, null);
            DemoIvy450C.this.pulseRate = numericSample(DemoIvy450C.this.pulseRate, pulseRate, rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, null);
            alarmIfPresent("SPO2", alarm);
        }

        @Override
        protected void receiveTemperature1(Float value, String label, String alarm) {
            t1 = numericSample(t1, value, rosetta.MDC_TEMP_BLD.VALUE, 0, null);
            alarmIfPresent("T1", alarm);
        }

        @Override
        protected void receiveTemperature2(Float value, String label, String alarm) {
            t2 = numericSample(t2, value, rosetta.MDC_TEMP_BLD.VALUE, 1, null);
            alarmIfPresent("T2", alarm);
        }
    }

    @Override
    protected AnsarB buildDelegate(int idx, InputStream in, OutputStream out) {
        return new MyAnsarB(in, out);
    }

    @Override
    protected boolean delegateReceive(int idx, AnsarB delegate) throws IOException {
        return delegate.receive();
    }

    @Override
    protected long getMaximumQuietTime(int idx) {
        return 1100L;
    }

    @Override
    protected long getConnectInterval(int idx) {
        return 2000L;
    }

    @Override
    protected long getNegotiateInterval(int idx) {
        return 200L;
    }
    
    protected void alarmIfPresent(String label, String alarm) {
        if(null != alarm && !"".equals(alarm)) {
            writePatientAlert(label, alarm);
        }
    }

    @Override
    public SerialProvider getSerialProvider(int idx) {
        SerialProvider serialProvider = super.getSerialProvider(idx);
        serialProvider.setDefaultSerialSettings(9600, DataBits.Eight, Parity.None, StopBits.One);
        return serialProvider;
    }
}
