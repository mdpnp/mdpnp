package org.mdpnp.devices.ivy._450c;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.cpc.ansarB.AnsarB;
import org.mdpnp.devices.serial.AbstractDelegatingSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoIvy450C extends AbstractDelegatingSerialDevice<AnsarB> {

    private static final String ECG_I_PREFIX = "ECG-I", ECG_II_PREFIX = "ECG-II", ECG_III_PREFIX = "ECG-III";

    private final static String nameOfECGWave(String lbl) {
        if(lbl==null || lbl.isEmpty()) {
            return null;
        }
        if(lbl.startsWith(ECG_III_PREFIX)) {
            return rosetta.MDC_ECG_AMPL_ST_III.VALUE;
        } else if(lbl.startsWith(ECG_II_PREFIX)) {
            return rosetta.MDC_ECG_AMPL_ST_II.VALUE;
        } else if(lbl.startsWith(ECG_I_PREFIX)) {
            return rosetta.MDC_ECG_AMPL_ST_I.VALUE;
        } else {
            log.warn("Unknown ECG:"+lbl);
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

    private InstanceHolder<ice.Numeric> heartRate, respiratoryRate, spo2, etco2, t1, t2, pulseRate, nibpSystolic, nibpDiastolic, nibpMean, nibpPulse, ibpSystolic1, ibpDiastolic1, ibpMean1, ibpSystolic2, ibpDiastolic2, ibpMean2;

    private InstanceHolder<ice.SampleArray> ecgWave, respWave, plethWave, p1Wave, p2Wave;
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
            etco2 = numericSample(etco2, value, rosetta.MDC_AWAY_CO2_EXP.VALUE);
        }

        @Override
        protected void receiveECGWave(int[] data, int count, int msPerSample, String label) {
            String ecg = nameOfECGWave(label);
            if(ecg != null) {
                ecgWave = sampleArraySample(ecgWave, data, count, msPerSample, ecg);
            } else {
                if(ecgWave != null) {
                    unregisterSampleArrayInstance(ecgWave);
                    ecgWave = null;
                }
            }
        }

        @Override
        protected void receiveRespWave(int[] data, int count, int msPerSample) {
            respWave = sampleArraySample(respWave, data, count, msPerSample, ice.MDC_CAPNOGRAPH.VALUE);
        }

        @Override
        protected void receivePlethWave(int[] data, int count, int msPerSample) {
            plethWave = sampleArraySample(plethWave, data, count, msPerSample, rosetta.MDC_PULS_OXIM_PLETH.VALUE);
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
            // should be ECG heart rate?  or should it .. depends upon mode
            heartRate = numericSample(heartRate, value, rosetta.MDC_PULS_RATE.VALUE);

        }
        @Override
        protected void receiveNIBP(Integer systolic, Integer diastolic, Integer mean, Integer pulse, String label) {
            nibpSystolic = numericSample(nibpSystolic, systolic, rosetta.MDC_PRESS_CUFF_SYS.VALUE);
            nibpDiastolic = numericSample(nibpDiastolic, diastolic, rosetta.MDC_PRESS_CUFF_DIA.VALUE);
            nibpPulse = numericSample(nibpPulse, pulse, rosetta.MDC_PULS_RATE_NON_INV.VALUE);
            nibpMean = numericSample(nibpMean, mean, rosetta.MDC_PRESS_CUFF_MEAN.VALUE);
        }
        @Override
        protected void receivePressure1(Integer systolic, Integer diastolic, Integer mean, String label) {
            ibpSystolic1 = numericSample(ibpSystolic1, systolic, rosetta.MDC_PRESS_BLD_SYS.VALUE, 0);
            ibpDiastolic1 = numericSample(ibpDiastolic1, diastolic, rosetta.MDC_PRESS_BLD_DIA.VALUE, 0);
            ibpMean1 = numericSample(ibpMean1, mean, rosetta.MDC_PRESS_BLD_MEAN.VALUE, 0);
        }
        @Override
        protected void receivePressure2(Integer systolic, Integer diastolic, Integer mean, String label) {
            ibpSystolic2 = numericSample(ibpSystolic2, systolic, rosetta.MDC_PRESS_BLD_SYS.VALUE, 1);
            ibpDiastolic2 = numericSample(ibpDiastolic2, diastolic, rosetta.MDC_PRESS_BLD_DIA.VALUE, 1);
            ibpMean2 = numericSample(ibpMean2, mean, rosetta.MDC_PRESS_BLD_MEAN.VALUE, 1);
        }
        @Override
        protected void receiveRespiratoryRate(Integer value, String label) {
            respiratoryRate = numericSample(respiratoryRate, value, rosetta.MDC_RESP_RATE.VALUE);
        }
        @Override
        protected void receiveSpO2(Integer value, String label, Integer pulseRate) {
            spo2 = numericSample(spo2, value, rosetta.MDC_PULS_OXIM_SAT_O2.VALUE);
            DemoIvy450C.this.pulseRate = numericSample(DemoIvy450C.this.pulseRate, pulseRate, rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE);

        }
        @Override
        protected void receiveTemperature1(Float value, String label) {
            t1 = numericSample(t1, value, rosetta.MDC_TEMP_BLD.VALUE, 0);
        }
        @Override
        protected void receiveTemperature2(Float value, String label) {
            t2 = numericSample(t2, value, rosetta.MDC_TEMP_BLD.VALUE, 1);
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
        SerialProvider serialProvider =  super.getSerialProvider();
        serialProvider.setDefaultSerialSettings(9600, DataBits.Eight, Parity.None, StopBits.One);
        return serialProvider;
    }
}
