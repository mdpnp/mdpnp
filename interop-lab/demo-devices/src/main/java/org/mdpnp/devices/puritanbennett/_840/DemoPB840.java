package org.mdpnp.devices.puritanbennett._840;

import ice.ConnectionState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.serial.AbstractDelegatingSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.FlowControl;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DemoPB840 extends AbstractDelegatingSerialDevice<PB840> {
    private static final Logger log = LoggerFactory.getLogger(DemoPB840.class);
    private InstanceHolder<ice.SampleArray> flowSampleArray, pressureSampleArray;
    
    private class MyPB840Waveforms extends PB840Waveforms {

        public MyPB840Waveforms(InputStream input, OutputStream output) {
            super(input, output);
        }
        @Override
        public void receiveBreath(Collection<Number> flow, Collection<Number> pressure) {
            flowSampleArray = sampleArraySample(flowSampleArray, flow, rosetta.MDC_FLOW_AWAY.VALUE, 0, rosetta.MDC_DIM_L_PER_MIN.VALUE, 50, null);
            pressureSampleArray = sampleArraySample(pressureSampleArray, pressure, rosetta.MDC_PRESS_AWAY.VALUE, 0, rosetta.MDC_DIM_CM_H2O.VALUE, 50, null);
        }
    }
    
    public DemoPB840(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop, 2, PB840.class);
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        deviceIdentity.manufacturer = "Puritan Bennett";
        deviceIdentity.model = "840";
        writeDeviceIdentity();
    }
    
    protected InstanceHolder<ice.Numeric> respRateSetting, respRate,exhaledTidalVolume;
    protected final List<InstanceHolder<ice.Numeric>> otherFields = new ArrayList<InstanceHolder<ice.Numeric>>();
    
    private class MyPB840Parameters extends PB840Parameters {
        public MyPB840Parameters(InputStream input, OutputStream output) {
            super(input, output);
        }
        @Override
        public void receiveMiscF(List<String> fieldValues) {
            reportConnected("Received MISCF");
            if(!fieldValues.get(1).equals(deviceIdentity.serial_number)) {
                deviceIdentity.serial_number = fieldValues.get(1);
                writeDeviceIdentity();
            }
            markOldTechnicalAlertInstances();
            writeTechnicalAlert("PB_TIME", fieldValues.get(0));
            writeTechnicalAlert("PB_DATE", fieldValues.get(2));
            writeTechnicalAlert("PB_VENT_TYPE", fieldValues.get(3));
            writeTechnicalAlert("PB_MODE", fieldValues.get(4));
            writeTechnicalAlert("PB_MANDATORY_TYPE", fieldValues.get(5));
            writeTechnicalAlert("PB_SPONTANEOUS_TYPE", fieldValues.get(6));
            writeTechnicalAlert("PB_TRIGGER_TYPE", fieldValues.get(7));
            respRateSetting = numericSample(respRateSetting, Float.parseFloat(fieldValues.get(8)), "PB_SETTING_RESP_RATE", rosetta.MDC_DIM_RESP_PER_MIN.VALUE, null);
            for(int i = 9; i < fieldValues.size(); i++) {
                try {
                    float f = Float.parseFloat(fieldValues.get(i));
                    while(i >= otherFields.size()) {
                        otherFields.add(null);
                    }
                    otherFields.set(i, numericSample(otherFields.get(i), f, "PB_F_"+(i+5), rosetta.MDC_DIM_DIMLESS.VALUE, null));
                } catch(NumberFormatException nfe) {
                    writeTechnicalAlert("PB_F_"+(i+5), fieldValues.get(i));
                }
            }
//            respRate = numericSample(respRate, Float.parseFloat(fieldValues.get(65)), rosetta.MDC_RESP_RATE.VALUE, rosetta.MDC_DIM_RESP_PER_MIN.VALUE, null);
//            exhaledTidalVolume = numericSample(exhaledTidalVolume, Float.parseFloat(fieldValues.get(66)), rosetta.MDC_VOL_AWAY_TIDAL_EXP.VALUE, rosetta.MDC_DIM_L_PER_MIN.VALUE, null);
            clearOldTechnicalAlertInstances();
        }
    }

    private class RequestSlowData implements Runnable {
        public void run() {
            if (ice.ConnectionState.Connected.equals(getState())) {
                try {
                    PB840Parameters params = (PB840Parameters) getDelegate(0);
                    params.sendF();
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                }
            }

        }
    }
    
    @Override
    protected void doInitCommands(int idx) throws IOException {
        super.doInitCommands(idx);
        switch(idx) {
        case 0:
//            ((PB840Parameters)getDelegate(idx)).sendReset();
            ((PB840Parameters)getDelegate(idx)).sendF();
            break;
        default:
            
        }
    }
    
    private ScheduledFuture<?> requestSlowData;
    
    @Override
    protected void stateChanged(ConnectionState newState, ConnectionState oldState, String transitionNote) {

        if (ice.ConnectionState.Connected.equals(newState) && !ice.ConnectionState.Connected.equals(oldState)) {
            startRequestSlowData();
        }
        if (!ice.ConnectionState.Connected.equals(newState) && ice.ConnectionState.Connected.equals(oldState)) {
            stopRequestSlowData();
        }
        super.stateChanged(newState, oldState, transitionNote);
    }    
    
    private synchronized void startRequestSlowData() {
        if (null == requestSlowData) {
            requestSlowData = executor.scheduleWithFixedDelay(new RequestSlowData(), 0L, 500L, TimeUnit.MILLISECONDS);
            log.trace("Scheduled slow data request task");
        } else {
            log.trace("Slow data request already scheduled");
        }
    }
    private synchronized void stopRequestSlowData() {
        if (null != requestSlowData) {
            requestSlowData.cancel(false);
            requestSlowData = null;
            log.trace("Canceled slow data request task");
        } else {
            log.trace("Slow data request already canceled");
        }
    }

    
    @Override
    protected PB840 buildDelegate(int idx, InputStream in, OutputStream out) {
        switch(idx) {
        case 0:
            return new MyPB840Parameters(in, out);
        case 1:
            return new MyPB840Waveforms(in, out);
        default:
            return null;
        }
    }

    @Override
    protected boolean delegateReceive(int idx, PB840 delegate) throws IOException {
        return delegate.receive();
    }
    
    @Override
    public SerialProvider getSerialProvider(int idx) {
        SerialProvider serialProvider = super.getSerialProvider(idx).duplicate();
        switch(idx) {
        case 0:
            serialProvider.setDefaultSerialSettings(9600, DataBits.Eight, Parity.None, StopBits.One, FlowControl.None);
            break;
        case 1:
            serialProvider.setDefaultSerialSettings(38400, DataBits.Eight, Parity.None, StopBits.One, FlowControl.None);
            break;
        }
        
        return serialProvider;
    }
    
    @Override
    protected long getMaximumQuietTime(int idx) {
        return 5000L;
    }
    
    @Override
    protected String iconResourceName() {
        return "pb840.png";
    }

}
