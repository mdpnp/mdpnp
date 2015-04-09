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

import org.mdpnp.devices.DeviceClock;
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

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

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

    public DemoIvy450C(final Subscriber subscriber, final Publisher publisher, EventLoop eventLoop) {
        super(subscriber, publisher, eventLoop, AnsarB.class);
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

    private class AnsarBExt extends AnsarB {

        public AnsarBExt(DeviceClock referenceClock, InputStream in, OutputStream out) {
            super(referenceClock, in, out);
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
        protected void receiveLine(DeviceClock.Reading timeStamp, String line) {
            reportConnected("message received");
            super.receiveLine(timeStamp, line);
        }
        
        @Override
        protected void receiveEndTidalCO2(DeviceClock.Reading timeStamp, Integer value, String label, String alarm) {
            etco2 = numericSample(etco2, value, rosetta.MDC_AWAY_CO2_ET.VALUE, label, rosetta.MDC_DIM_DIMLESS.VALUE, timeStamp);
            alarmIfPresent("ETCO2", alarm);
        }

        @Override
        protected void receiveECGWave(DeviceClock.Reading timeStamp, Float[] data, int count, int frequency, String label) {

            String ecg = nameOfECGWave(label);
            
            if (ecg != null) {
                ecgWave = sampleArraySample(ecgWave, data, count, ecg, label, 0, rosetta.MDC_DIM_DIMLESS.VALUE, frequency, timeStamp);
            } else {
                if (ecgWave != null) {
                    unregisterSampleArrayInstance(ecgWave);
                    ecgWave = null;
                }
            }
        }

        @Override
        protected void receiveRespWave(DeviceClock.Reading timeStamp, Float[] data, int count, int frequency) {

            // This is less than ideal but if the device is reporting etCO2 we'll treat this as a capnogram
            // otherwise it is from respiratory impedance
            if(null != etco2 && etco2.data.value > 0) {
                co2Wave = sampleArraySample(co2Wave, data, count, rosetta.MDC_AWAY_CO2.VALUE, "", 0, rosetta.MDC_DIM_DIMLESS.VALUE, frequency, timeStamp);
                impThorWave = sampleArraySample(impThorWave, null, 0, rosetta.MDC_IMPED_TTHOR.VALUE, "", 0, rosetta.MDC_DIM_DIMLESS.VALUE, frequency, timeStamp);
            } else {
                impThorWave = sampleArraySample(impThorWave, data, count, rosetta.MDC_IMPED_TTHOR.VALUE, "", 0, rosetta.MDC_DIM_DIMLESS.VALUE, frequency, timeStamp);
                co2Wave = sampleArraySample(co2Wave, null, 0, rosetta.MDC_AWAY_CO2.VALUE, "", 0, rosetta.MDC_DIM_DIMLESS.VALUE, frequency, timeStamp);
            }
        }

        @Override
        protected void receivePlethWave(DeviceClock.Reading timeStamp, Float[] data, int count, int frequency) {
            plethWave = sampleArraySample(plethWave, data, count, rosetta.MDC_PULS_OXIM_PLETH.VALUE, "", 0, rosetta.MDC_DIM_DIMLESS.VALUE, frequency, timeStamp);
        }

        @Override
        protected void receiveP1Wave(DeviceClock.Reading timeStamp, Float[] data, int count, int frequency) {
            p1Wave = sampleArraySample(p1Wave, data, count, rosetta.MDC_PRESS_BLD.VALUE, "", 0, rosetta.MDC_DIM_DIMLESS.VALUE, frequency, timeStamp);
        }

        @Override
        protected void receiveP2Wave(DeviceClock.Reading timeStamp, Float[] data, int count, int frequency) {
            p2Wave = sampleArraySample(p2Wave, data, count, rosetta.MDC_PRESS_BLD.VALUE, "", 1, rosetta.MDC_DIM_DIMLESS.VALUE, frequency, timeStamp);
        }

        @Override
        protected void receiveHeartRate(DeviceClock.Reading timeStamp, Integer value, String label, String alarm) {
            // should be ECG heart rate? or should it .. depends upon mode
            heartRate = numericSample(heartRate, value, rosetta.MDC_ECG_HEART_RATE.VALUE, label, rosetta.MDC_DIM_DIMLESS.VALUE, timeStamp);
            alarmIfPresent("HR", alarm);
        }

        @Override
        protected void receiveNIBP(DeviceClock.Reading timeStamp, Integer systolic, Integer diastolic, Integer mean, Integer pulse, String label, String alarm) {
            nibpSystolic = numericSample(nibpSystolic, systolic, rosetta.MDC_PRESS_CUFF_SYS.VALUE, label, rosetta.MDC_DIM_DIMLESS.VALUE, timeStamp);
            nibpDiastolic = numericSample(nibpDiastolic, diastolic, rosetta.MDC_PRESS_CUFF_DIA.VALUE, label, rosetta.MDC_DIM_DIMLESS.VALUE, timeStamp);
            nibpPulse = numericSample(nibpPulse, pulse, rosetta.MDC_PULS_RATE_NON_INV.VALUE, label, rosetta.MDC_DIM_DIMLESS.VALUE, timeStamp);
            nibpMean = numericSample(nibpMean, mean, rosetta.MDC_PRESS_CUFF_MEAN.VALUE, label, rosetta.MDC_DIM_DIMLESS.VALUE, timeStamp);
            alarmIfPresent("NIBP", alarm);
        }

        @Override
        protected void receivePressure1(DeviceClock.Reading timeStamp, Integer systolic, Integer diastolic, Integer mean, String label, String alarm) {
            ibpSystolic1 = numericSample(ibpSystolic1, systolic, rosetta.MDC_PRESS_BLD_SYS.VALUE, label, 0, timeStamp);
            ibpDiastolic1 = numericSample(ibpDiastolic1, diastolic, rosetta.MDC_PRESS_BLD_DIA.VALUE, label, 0, timeStamp);
            ibpMean1 = numericSample(ibpMean1, mean, rosetta.MDC_PRESS_BLD_MEAN.VALUE, label, 0, timeStamp);
            alarmIfPresent("P1", alarm);
        }

        @Override
        protected void receivePressure2(DeviceClock.Reading timeStamp, Integer systolic, Integer diastolic, Integer mean, String label, String alarm) {
            ibpSystolic2 = numericSample(ibpSystolic2, systolic, rosetta.MDC_PRESS_BLD_SYS.VALUE, label, 1, timeStamp);
            ibpDiastolic2 = numericSample(ibpDiastolic2, diastolic, rosetta.MDC_PRESS_BLD_DIA.VALUE, label, 1, timeStamp);
            ibpMean2 = numericSample(ibpMean2, mean, rosetta.MDC_PRESS_BLD_MEAN.VALUE, label, 1, timeStamp);
            alarmIfPresent("P2", alarm);
        }

        @Override
        protected void receiveRespiratoryRate(DeviceClock.Reading timeStamp, Integer value, String label, String alarm) {
            if(null != etco2 && etco2.data.value > 0) {
                co2RespiratoryRate = numericSample(co2RespiratoryRate, value, rosetta.MDC_CO2_RESP_RATE.VALUE, label, rosetta.MDC_DIM_DIMLESS.VALUE, timeStamp);
                tthorRespiratoryRate = numericSample(tthorRespiratoryRate, (Integer)null, rosetta.MDC_TTHOR_RESP_RATE.VALUE, label, rosetta.MDC_DIM_DIMLESS.VALUE, timeStamp);
            } else {
                tthorRespiratoryRate = numericSample(tthorRespiratoryRate, value, rosetta.MDC_TTHOR_RESP_RATE.VALUE, label, rosetta.MDC_DIM_DIMLESS.VALUE, timeStamp);
                co2RespiratoryRate = numericSample(co2RespiratoryRate, (Integer)null, rosetta.MDC_CO2_RESP_RATE.VALUE, label, rosetta.MDC_DIM_DIMLESS.VALUE, timeStamp);
            }
            alarmIfPresent("RR", alarm);
        }

        @Override
        protected void receiveSpO2(DeviceClock.Reading timeStamp, Integer value, String label, Integer pulseRate, String alarm) {
            spo2 = numericSample(spo2, value, rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, label, rosetta.MDC_DIM_DIMLESS.VALUE, timeStamp);
            DemoIvy450C.this.pulseRate = numericSample(DemoIvy450C.this.pulseRate, pulseRate, rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, label, rosetta.MDC_DIM_BEAT_PER_MIN.VALUE, timeStamp);
            alarmIfPresent("SPO2", alarm);
        }

        @Override
        protected void receiveTemperature1(DeviceClock.Reading timeStamp, Float value, String label, String alarm) {
            t1 = numericSample(t1, value, rosetta.MDC_TEMP_BLD.VALUE, label, 0, timeStamp);
            alarmIfPresent("T1", alarm);
        }

        @Override
        protected void receiveTemperature2(DeviceClock.Reading timeStamp, Float value, String label, String alarm) {
            t2 = numericSample(t2, value, rosetta.MDC_TEMP_BLD.VALUE, label, 1, timeStamp);
            alarmIfPresent("T2", alarm);
        }
    }

    @Override
    protected AnsarB buildDelegate(int idx, InputStream in, OutputStream out) {
        DeviceClock clock = super.getClockProvider();
        return new AnsarBExt(clock, in, out);
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
