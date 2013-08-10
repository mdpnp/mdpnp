package org.mdpnp.devices.ivy._450c;

import ice.Numeric;

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

public class Ivy450C extends AbstractDelegatingSerialDevice<AnsarB> {

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

    private InstanceHolder<Numeric> heartRate, respiratoryRate, spo2, etco2, t1, t2, pulseRate, nibpSystolic, nibpDiastolic, nibpMean, nibpPulse, ibpSystolic, ibpDiastolic, ibpMean;
    
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
            etco2 = numericSample(etco2, value, ice.MDC_AWAY_CO2_EXP.VALUE);
        }
        @Override
        protected void receiveHeartRate(Integer value, String label) {
            // should be ECG heart rate?  or should it .. depends upon mode
            heartRate = numericSample(heartRate, value, ice.MDC_PULS_RATE.VALUE);
        }
        @Override
        protected void receiveNIBP(Integer systolic, Integer diastolic, Integer mean, Integer pulse, String label) {
            nibpSystolic = numericSample(nibpSystolic, systolic, ice.MDC_PRESS_CUFF_SYS.VALUE);
            nibpDiastolic = numericSample(nibpDiastolic, diastolic, ice.MDC_PRESS_CUFF_DIA.VALUE);
            nibpPulse = numericSample(nibpPulse, pulse, ice.MDC_PULS_RATE_NON_INV.VALUE);
            nibpMean = numericSample(nibpMean, mean, ice.MDC_PRESS_CUFF_MEAN.VALUE);
        }
        @Override
        protected void receivePressure1(Integer systolic, Integer diastolic, Integer mean, String label) {
            ibpSystolic = numericSample(ibpSystolic, systolic, ice.MDC_PRESS_BLD_SYS.VALUE);
            ibpDiastolic = numericSample(ibpDiastolic, diastolic, ice.MDC_PRESS_BLD_DIA.VALUE);
            ibpMean = numericSample(ibpMean, mean, ice.MDC_PRESS_BLD_MEAN.VALUE);
        }
        @Override
        protected void receivePressure2(Integer systolic, Integer diastolic, Integer mean, String label) {
            // TODO enable multiple instances of the same type of physiological identifier in future iterations
        }
        @Override
        protected void receiveRespiratoryRate(Integer value, String label) {
            respiratoryRate = numericSample(respiratoryRate, value, ice.MDC_RESP_RATE.VALUE);
        }
        @Override
        protected void receiveSpO2(Integer value, String label, Integer pulseRate) {
            spo2 = numericSample(spo2, value, ice.MDC_PULS_OXIM_SAT_O2.VALUE);
            Ivy450C.this.pulseRate = numericSample(Ivy450C.this.pulseRate, pulseRate, ice.MDC_PULS_OXIM_PULS_RATE.VALUE);
            
        }
        @Override
        protected void receiveTemperature1(Integer value, String label) {
            t1 = numericSample(t1, value, ice.MDC_TEMP_BLD.VALUE);
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
        return 3000L;
    }

    @Override
    public SerialProvider getSerialProvider() {
        SerialProvider serialProvider =  super.getSerialProvider();
        serialProvider.setDefaultSerialSettings(9600, DataBits.Eight, Parity.None, StopBits.One);
        return serialProvider;
    }
}
