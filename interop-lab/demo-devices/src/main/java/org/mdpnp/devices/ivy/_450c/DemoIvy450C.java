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
            return rosetta.MDC_ECG_AMPL_ST_III.VALUE;
        } else if (lbl.startsWith(ECG_II_PREFIX)) {
            return rosetta.MDC_ECG_AMPL_ST_II.VALUE;
        } else if (lbl.startsWith(ECG_I_PREFIX)) {
            return rosetta.MDC_ECG_AMPL_ST_I.VALUE;
        } else {
            log.warn("Unknown ECG:" + lbl);
            return null;
        }
    }

    private static final Logger log = LoggerFactory.getLogger(DemoIvy450C.class);

    public DemoIvy450C(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);
        deviceIdentity.manufacturer = "Ivy";
        deviceIdentity.model = "450C";
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        writeDeviceIdentity();
    }

    private InstanceHolder<ice.Numeric> heartRate, respiratoryRate, spo2, etco2, t1, t2, pulseRate, nibpSystolic, nibpDiastolic, nibpMean, nibpPulse,
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
        protected void receiveLine(String line) {
            reportConnected();
            super.receiveLine(line);
        }

        @Override
        protected void receiveEndTidalCO2(Integer value, String label) {
            etco2 = numericSample(etco2, value, rosetta.MDC_AWAY_CO2_ET.VALUE, null);
        }

        @Override
        protected void receiveECGWave(int[] data, int count, int msPerSample, String label) {
            String ecg = nameOfECGWave(label);
            if (ecg != null) {
                ecgWave = sampleArraySample(ecgWave, data, count, msPerSample, ecg, 0);
            } else {
                if (ecgWave != null) {
                    unregisterSampleArrayInstance(ecgWave, null);
                    ecgWave = null;
                }
            }
        }

        @Override
        protected void receiveRespWave(int[] data, int count, int msPerSample) {
            // This is less than ideal but if the device is reporting etCO2 we'll treat this as a capnogram
            // otherwise it is from respiratory impedance
            if(null != etco2) {
                co2Wave = sampleArraySample(co2Wave, data, count, msPerSample, rosetta.MDC_AWAY_CO2.VALUE, 0);
                impThorWave = sampleArraySample(impThorWave, null, 0, 0, rosetta.MDC_TTHOR_RESP_RATE.VALUE, 0);
            } else {
                impThorWave = sampleArraySample(impThorWave, data, count, msPerSample, rosetta.MDC_TTHOR_RESP_RATE.VALUE, 0);
                co2Wave = sampleArraySample(co2Wave, null, 0, 0, rosetta.MDC_AWAY_CO2.VALUE, 0);
            }
        }

        @Override
        protected void receivePlethWave(int[] data, int count, int msPerSample) {
            plethWave = sampleArraySample(plethWave, data, count, msPerSample, rosetta.MDC_PULS_OXIM_PLETH.VALUE, 0);
        }

        @Override
        protected void receiveP1Wave(int[] data, int count, int msPerSample) {
            p1Wave = sampleArraySample(p1Wave, data, count, msPerSample, rosetta.MDC_PRESS_BLD.VALUE, 0);
        }

        @Override
        protected void receiveP2Wave(int[] data, int count, int msPerSample) {
            p2Wave = sampleArraySample(p2Wave, data, count, msPerSample, rosetta.MDC_PRESS_BLD.VALUE, 1);
        }

        @Override
        protected void receiveHeartRate(Integer value, String label) {
            // should be ECG heart rate? or should it .. depends upon mode
            heartRate = numericSample(heartRate, value, rosetta.MDC_ECG_HEART_RATE.VALUE, null);

        }

        @Override
        protected void receiveNIBP(Integer systolic, Integer diastolic, Integer mean, Integer pulse, String label) {
            nibpSystolic = numericSample(nibpSystolic, systolic, rosetta.MDC_PRESS_CUFF_SYS.VALUE, null);
            nibpDiastolic = numericSample(nibpDiastolic, diastolic, rosetta.MDC_PRESS_CUFF_DIA.VALUE, null);
            nibpPulse = numericSample(nibpPulse, pulse, rosetta.MDC_PULS_RATE_NON_INV.VALUE, null);
            nibpMean = numericSample(nibpMean, mean, rosetta.MDC_PRESS_CUFF_MEAN.VALUE, null);
        }

        @Override
        protected void receivePressure1(Integer systolic, Integer diastolic, Integer mean, String label) {
            ibpSystolic1 = numericSample(ibpSystolic1, systolic, rosetta.MDC_PRESS_BLD_SYS.VALUE, 0, null);
            ibpDiastolic1 = numericSample(ibpDiastolic1, diastolic, rosetta.MDC_PRESS_BLD_DIA.VALUE, 0, null);
            ibpMean1 = numericSample(ibpMean1, mean, rosetta.MDC_PRESS_BLD_MEAN.VALUE, 0, null);
        }

        @Override
        protected void receivePressure2(Integer systolic, Integer diastolic, Integer mean, String label) {
            ibpSystolic2 = numericSample(ibpSystolic2, systolic, rosetta.MDC_PRESS_BLD_SYS.VALUE, 1, null);
            ibpDiastolic2 = numericSample(ibpDiastolic2, diastolic, rosetta.MDC_PRESS_BLD_DIA.VALUE, 1, null);
            ibpMean2 = numericSample(ibpMean2, mean, rosetta.MDC_PRESS_BLD_MEAN.VALUE, 1, null);
        }

        @Override
        protected void receiveRespiratoryRate(Integer value, String label) {
            System.err.println("RR="+label);
            respiratoryRate = numericSample(respiratoryRate, value, rosetta.MDC_CO2_RESP_RATE.VALUE, null);
        }

        @Override
        protected void receiveSpO2(Integer value, String label, Integer pulseRate) {
            spo2 = numericSample(spo2, value, rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, null);
            DemoIvy450C.this.pulseRate = numericSample(DemoIvy450C.this.pulseRate, pulseRate, rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, null);

        }

        @Override
        protected void receiveTemperature1(Float value, String label) {
            t1 = numericSample(t1, value, rosetta.MDC_TEMP_BLD.VALUE, 0, null);
        }

        @Override
        protected void receiveTemperature2(Float value, String label) {
            t2 = numericSample(t2, value, rosetta.MDC_TEMP_BLD.VALUE, 1, null);
        }
    }

    @Override
    protected AnsarB buildDelegate(InputStream in, OutputStream out) {
        return new MyAnsarB(in, out);
    }

    @Override
    protected boolean delegateReceive(AnsarB delegate) throws IOException {
        return delegate.receive();
    }

    @Override
    protected long getMaximumQuietTime() {
        return 1100L;
    }

    @Override
    protected long getConnectInterval() {
        return 2000L;
    }

    @Override
    protected long getNegotiateInterval() {
        return 200L;
    }

    @Override
    public SerialProvider getSerialProvider() {
        SerialProvider serialProvider = super.getSerialProvider();
        serialProvider.setDefaultSerialSettings(9600, DataBits.Eight, Parity.None, StopBits.One);
        return serialProvider;
    }
}
