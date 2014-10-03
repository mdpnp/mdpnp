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
    protected InstanceHolder<ice.AlarmSettings> inspPressure, exhaledMV, exhaledMandTidalVolume, exhaledSpontTidalVolume, respRateAlarm, inspiredTidalVolume;
    
    protected final List<InstanceHolder<ice.Numeric>> otherFields = new ArrayList<InstanceHolder<ice.Numeric>>();
    
    private static final String[] fieldNames = new String[] {
        "PB_TIME", null, "PB_DATE", "PB_VENT_TYPE", "PB_MODE", "PB_MANDATORY_TYPE", "PB_SPONTANEOUS_TYPE", "PB_TRIGGER_TYPE",
        "PB_SETTING_RESP_RATE", "PB_SETTING_TIDAL_VOLUME", "PB_SETTING_PEAK_FLOW", "PB_SETTING_O2PCT",
        "PB_SETTING_PRESS_SENSITIVITY", "PB_SETTING_PEEP_CPAP", "PB_SETTING_PLATEAU", "PB_SETTING_APNEA_INTERVAL",
        "PB_SETTING_APNEA_TIDAL_VOLUME", "PB_SETTING_APNEA_RESPIRATORY_RATE", "PB_SETTING_APNEA_PEAK_FLOW",
        "PB_SETTING_APNEA_O2PCT", "PB_SETTING_PCV_APNEA_INSP_PRESSURE", "PB_SETTING_PCV_APNEA_INSP_TIME",
        "PB_SETTING_APNEA_FLOW_PATTERN", "PB_SETTING_MANDATORY", "PB_APNEA_IE_INSP_COMPONENT",
        "PB_SETTING_IE_EXP_COMPONENT", "PB_SETTING_SUPPORT_PRESSURE", "PB_SETTING_FLOW_PATTERN",
        "PB_SETTING_100PCT_O2_SUCTION", null /* insp press high alarm*/, null /* exp press low alarm*/,
        null /* exhaled MV high*/, null /* exhaled MV low*/, null /* exhaled mand tidal volume high*/,
        null /* exhaled mand tidal volume low*/, null /* exhaled spont tidal volume high*/,
        null /* exhaled spont tidal volume low*/, null /* high resp rate */, null /* high inspired tidal volume*/,
        "PB_SETTING_BASE_FLOW", "PB_SETTING_FLOW_SENSITIVITY", "PB_SETTING_PCV_INSP_PRESSURE",
        "PB_SETTING_PCV_INSP_TIME", "PB_SETTING_IE_INSP_COMPONENT", "PB_SETTING_IE_EXP_COMPONENT",
        "PB_SETTING_CONSTANT_DURING_RATE_CHANGE", "PB_SETTING_TUBE_ID", "PB_SETTING_TUBE_TYPE",
        "PB_SETTING_HUMIDIFICATION_TYPE", "PB_SETTING_HUMIDIFIER_VOLUME", "PB_SETTING_O2_SENSOR",
        "PB_SETTING_DISCONNECT_SENSITIVITY", "PB_SETTING_RISE_TIME_PCT", "PB_SETTING_PAVPCT_SUPPORT",
        "PB_SETTING_EXP_SENSITIVITY", "PB_SETTING_IBW", "PB_SETTING_TARGET_SUPP_VOLUME", 
    };
    
    protected static Float parseFloat(String s) {
        if(s == null || s.isEmpty() || "OFF".equals(s)) {
            return null;
        } else {
            return Float.parseFloat(s);
        }
    }
    
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
            inspPressure = alarmSettingsSample(inspPressure, parseFloat(fieldValues.get(30)), parseFloat(fieldValues.get(29)), "PB_INSP_PRESSURE");
            exhaledMV = alarmSettingsSample(exhaledMV, parseFloat(fieldValues.get(32)), parseFloat(fieldValues.get(31)), "PB_EXHALED_MV");
            exhaledMandTidalVolume = alarmSettingsSample(exhaledMandTidalVolume, parseFloat(fieldValues.get(34)), parseFloat(fieldValues.get(33)), "PB_EXHALED_MAND_TIDAL_VOLUME");
            exhaledSpontTidalVolume = alarmSettingsSample(exhaledSpontTidalVolume, parseFloat(fieldValues.get(36)), parseFloat(fieldValues.get(35)), "PB_EXHALED_SPONT_TIDAL_VOLUME");
            respRateAlarm = alarmSettingsSample(respRateAlarm, null, parseFloat(fieldValues.get(37)), "PB_RESP_RATE");
            inspiredTidalVolume = alarmSettingsSample(inspiredTidalVolume, null, parseFloat(fieldValues.get(38)), "PB_INSPIRED_TIDAL_VOLUME");
            
            markOldTechnicalAlertInstances();
            for(int i = 0; i < fieldValues.size(); i++) {
                String name = i < fieldNames.length ? fieldNames[i] : ("PB_F_"+(i+5));
                if(null != name) {
                    try {
                        float f = Float.parseFloat(fieldValues.get(i));
                        while(i >= otherFields.size()) {
                            otherFields.add(null);
                        }
                        otherFields.set(i, numericSample(otherFields.get(i), f, name, rosetta.MDC_DIM_DIMLESS.VALUE, null));
                    } catch(NumberFormatException nfe) {
                        writeTechnicalAlert(name, fieldValues.get(i));
                    }
                }
            }
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
            requestSlowData = executor.scheduleWithFixedDelay(new RequestSlowData(), 1000L, 1000L, TimeUnit.MILLISECONDS);
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
