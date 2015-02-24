package org.mdpnp.devices.puritanbennett._840;

import ice.ConnectionState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
            flowSampleArray = sampleArraySample(flowSampleArray, flow, "", rosetta.MDC_FLOW_AWAY.VALUE, 0, rosetta.MDC_DIM_L_PER_MIN.VALUE, 50, null);
            pressureSampleArray = sampleArraySample(pressureSampleArray, pressure, "", rosetta.MDC_PRESS_AWAY.VALUE, 0, rosetta.MDC_DIM_CM_H2O.VALUE,
                    50, null);
        }
    }

    public DemoPB840(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop, 2, PB840.class);
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        deviceIdentity.manufacturer = "Puritan Bennett";
        deviceIdentity.model = "840";
        writeDeviceIdentity();
    }


    protected Map<String, InstanceHolder<ice.Numeric>> numericInstances = new HashMap<String, InstanceHolder<ice.Numeric>>();
    protected Map<String, InstanceHolder<ice.AlarmSettings>> alarmSettingsInstances = new HashMap<String, InstanceHolder<ice.AlarmSettings>>();

    @Override
    protected void unregisterAllAlarmSettingsInstances() {
        alarmSettingsInstances.clear();
        super.unregisterAllAlarmSettingsInstances();
    }

    @Override
    protected void unregisterAllNumericInstances() {
        numericInstances.clear();
        super.unregisterAllNumericInstances();
    }

    
    protected static Float parseFloat(String s) throws NumberFormatException {
        return parseFloat(s, null);
    }

    protected static Float parseFloat(String s, Float ifNull) throws NumberFormatException {
        if (s == null || s.isEmpty() || "OFF".equals(s)) {
            return ifNull;
        } else {
            return Float.parseFloat(s);
        }
    }

    protected static String standardUnits(PB840.Units units) {
        switch(units) {
        case CMH2O:
            return rosetta.MDC_DIM_CM_H2O.VALUE;
        case LITERS:
            return rosetta.MDC_DIM_L.VALUE;
        case LITERS_PER_MIN:
            return rosetta.MDC_DIM_L_PER_MIN.VALUE;
        case PERCENT:
            return rosetta.MDC_DIM_PERCENT.VALUE;
        case SECONDS:
            return rosetta.MDC_DIM_SEC.VALUE;
        case MILLIMETERS:
            return rosetta.MDC_DIM_MILLI_M.VALUE;
        case KILOGRAMS:
            return rosetta.MDC_DIM_KILO_G.VALUE;
        case CMH2O_PER_L_PER_SEC:
            return rosetta.MDC_DIM_CM_H2O_PER_L_PER_SEC.VALUE;
        case CMH2O_PER_L:
            return rosetta.MDC_DIM_CM_H2O_PER_L.VALUE;
        case ML_PER_CMH2O:
            return rosetta.MDC_DIM_MILLI_L_PER_CM_H2O.VALUE;
        case JOULES_PER_LITER:
            return rosetta.MDC_DIM_JOULES_PER_L.VALUE;
        case BREATHS_PER_MIN:
            // TODO Can't find breaths per minute in rosetta units
        case UNKNOWN:    
        default:
            return rosetta.MDC_DIM_DIMLESS.VALUE;
        }
    }
    
    private class MyPB840Parameters extends PB840Parameters {
        public MyPB840Parameters(InputStream input, OutputStream output) {
            super(input, output);
        }

        @Override
        public void receiveNumeric(String name, Units units, String value) {
            try {
                numericInstances.put(name,
                        numericSample(numericInstances.get(name), parseFloat(value), 
                                name, name, standardUnits(units), null));
            } catch (NumberFormatException nfe) {
                log.warn("Poorly formatted numeric " + name + " " + value, nfe);
            }
        }
        
        @Override
        public void receiveAlarmSetting(String name, String lower, String upper) {
            try {
                // TODO using FLOAT_MIN, FLOAT_MAX as reserved values because
                // otherwise cannot publish AlarmSettings
                // with only one boundary condition
                alarmSettingsInstances.put(
                        name,
                        alarmSettingsSample(alarmSettingsInstances.get(name),
                                parseFloat(lower, Float.MIN_VALUE),
                                parseFloat(upper, Float.MAX_VALUE),
                                name));
            } catch (NumberFormatException nfe) {
                log.warn("Poorly formatted alarm setting " + name + " " + lower + " "
                        + upper, nfe);
            }
        }
        
        @Override
        public void receivePatientAlert(String name, String value) {
            writePatientAlert(name, value);
        }
        
        @Override
        public void receiveTechnicalAlert(String name, String value) {
            writeTechnicalAlert(name, value);
        }
        
        @Override
        public void receiveVentilatorId(String id) {
            if (!id.equals(deviceIdentity.serial_number)) {
                deviceIdentity.serial_number = id;
                writeDeviceIdentity();
            }
        }
        
        @Override
        public void receiveStartResponse(String responseType) {
            reportConnected("Received "+responseType);
            markOldPatientAlertInstances();
            markOldTechnicalAlertInstances();
        }
        
        @Override
        public void receiveEndResponse() {
            clearOldPatientAlertInstances();
            clearOldTechnicalAlertInstances();
        }
    }

    private class RequestSlowData implements Runnable {
        public void run() {
            log.trace("RequestSlowData called");
            if (ice.ConnectionState.Connected.equals(getState())) {
                try {
                    PB840Parameters params = (PB840Parameters) getDelegate(0);
                    params.sendF();
                    log.trace("Issued SENDF");
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                }
            } else {
                log.trace("Not issuing SENDF where state=" + getState());
            }

        }
    }

    @Override
    protected void doInitCommands(int idx) throws IOException {
        super.doInitCommands(idx);
        switch (idx) {
        case 0:
            ((PB840Parameters) getDelegate(idx)).sendReset();
            log.trace("Issued a RSET for doInitCommands");
            ((PB840Parameters) getDelegate(idx)).sendF();
            log.trace("Issued a SNDF for doInitCommands");
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
        switch (idx) {
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
        switch (idx) {
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
        switch (idx) {
        case 0:
            return 5000L;
        case 1:
            // There is no protocol negotiation for waveform data
            // so there is no utility in interrupting the main parameter
            // collection when waveform data is absent
            return Long.MAX_VALUE;
        default:
            return super.getMaximumQuietTime(idx);
        }
    }

    @Override
    protected String iconResourceName() {
        return "pb840.png";
    }
    

}
