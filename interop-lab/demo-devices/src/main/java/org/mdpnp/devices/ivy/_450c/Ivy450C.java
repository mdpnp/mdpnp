package org.mdpnp.devices.ivy._450c;

import ice.ConnectionState;
import ice.Numeric;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

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

public class Ivy450C extends AbstractDelegatingSerialDevice<AnsarB> {
    
    private static final String ECG_I_PREFIX = "ECG-I", ECG_II_PREFIX = "ECG-II", ECG_III_PREFIX = "ECG-III";

    private final static Integer nameOfECGWave(String lbl) {
        if(lbl==null || lbl.isEmpty()) {
            return null;
        }
        if(lbl.startsWith(ECG_III_PREFIX)) {
            return ice.Physio.MDC_ECG_AMPL_ST_I.value();
        } else if(lbl.startsWith(ECG_II_PREFIX)) {
            return ice.Physio.MDC_ECG_AMPL_ST_II.value();
        } else if(lbl.startsWith(ECG_I_PREFIX)) {
            return ice.Physio.MDC_ECG_AMPL_ST_III.value();
        } else {
            log.warn("Unknown ECG:"+lbl);
            return null;
        }
    }
    private static final Logger log = LoggerFactory.getLogger(Ivy450C.class);
    
    public Ivy450C(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);
        deviceIdentity.manufacturer = "Ivy";
        deviceIdentity.model = "450C";
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        deviceIdentityHandle = deviceIdentityWriter.register_instance(deviceIdentity);
        deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);
        
        deviceConnectivity.universal_device_identifier = deviceIdentity.universal_device_identifier;
        deviceConnectivityHandle = deviceConnectivityWriter.register_instance(deviceConnectivity);
        deviceConnectivityWriter.write(deviceConnectivity, deviceConnectivityHandle);
    }

    @Override
    protected void stateChanging(ConnectionState newState, ConnectionState oldState) {
        if(ice.ConnectionState.Disconnected.equals(newState)) {
            unregisterAllNumericInstances();
            unregisterAllSampleArrayInstances();
        }
    }
    
    private InstanceHolder<ice.Numeric> heartRate, respiratoryRate, spo2, etco2, t1, t2, pulseRate, nibpSystolic, nibpDiastolic, nibpMean, nibpPulse, ibpSystolic, ibpDiastolic, ibpMean;
    
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
        protected void receiveEndTidalCO2(Integer value, String label) {
            etco2 = numericSample(etco2, value, ice.Physio.MDC_AWAY_CO2_EXP.value());
        }
        
        @Override
        protected void receiveECGWave(int[] data, int count, int msPerSample, String label) {
            Integer ecg = nameOfECGWave(label);
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
            plethWave = sampleArraySample(plethWave, data, count, msPerSample, ice.Physio.MDC_PULS_OXIM_PLETH.value());
        }
        
        @Override
        protected void receiveP1Wave(int[] data, int count, int msPerSample) {
            p1Wave = sampleArraySample(p1Wave, data, count, msPerSample, ice.Physio.MDC_PRESS_BLD.value());
        }
        
        @Override
        protected void receiveP2Wave(int[] data, int count, int msPerSample) {
            // TODO Don't know what nomenclature tag ... multiple instances needs to be enabled?
        }
        
        
        
        @Override
        protected void receiveHeartRate(Integer value, String label) {
            // should be ECG heart rate?  or should it .. depends upon mode
            heartRate = numericSample(heartRate, value, ice.Physio.MDC_PULS_RATE.value());
            
        }
        @Override
        protected void receiveNIBP(Integer systolic, Integer diastolic, Integer mean, Integer pulse, String label) {
            nibpSystolic = numericSample(nibpSystolic, systolic, ice.Physio.MDC_PRESS_CUFF_SYS.value());
            nibpDiastolic = numericSample(nibpDiastolic, diastolic, ice.Physio.MDC_PRESS_CUFF_DIA.value());
            nibpPulse = numericSample(nibpPulse, pulse, ice.Physio.MDC_PULS_RATE_NON_INV.value());
            nibpMean = numericSample(nibpMean, mean, ice.Physio.MDC_PRESS_CUFF_MEAN.value());
        }
        @Override
        protected void receivePressure1(Integer systolic, Integer diastolic, Integer mean, String label) {
            ibpSystolic = numericSample(ibpSystolic, systolic, ice.Physio.MDC_PRESS_BLD_SYS.value());
            ibpDiastolic = numericSample(ibpDiastolic, diastolic, ice.Physio.MDC_PRESS_BLD_DIA.value());
            ibpMean = numericSample(ibpMean, mean, ice.Physio.MDC_PRESS_BLD_MEAN.value());
        }
        @Override
        protected void receivePressure2(Integer systolic, Integer diastolic, Integer mean, String label) {
            // TODO enable multiple instances of the same type of physiological identifier in future iterations
        }
        @Override
        protected void receiveRespiratoryRate(Integer value, String label) {
            respiratoryRate = numericSample(respiratoryRate, value, ice.Physio.MDC_RESP_RATE.value());
        }
        @Override
        protected void receiveSpO2(Integer value, String label, Integer pulseRate) {
            spo2 = numericSample(spo2, value, ice.Physio.MDC_PULS_OXIM_SAT_O2.value());
            Ivy450C.this.pulseRate = numericSample(Ivy450C.this.pulseRate, pulseRate, ice.Physio.MDC_PULS_OXIM_PULS_RATE.value());
            
        }
        @Override
        protected void receiveTemperature1(Integer value, String label) {
            t1 = numericSample(t1, value, ice.Physio.MDC_TEMP_BLD.value());
        }
        @Override
        protected void receiveTemperature2(Integer value, String label) {
            // TODO enable multiple instances of the same type of physiological identifier in future iterations 
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
        return 5000L;
    }
    
    @Override
    protected long getConnectInterval() {
        return 2000L;
    }

    @Override
    public SerialProvider getSerialProvider() {
        SerialProvider serialProvider =  super.getSerialProvider();
        serialProvider.setDefaultSerialSettings(9600, DataBits.Eight, Parity.None, StopBits.One);
        return serialProvider;
    }
}
