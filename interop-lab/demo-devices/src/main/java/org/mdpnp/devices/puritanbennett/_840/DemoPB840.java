package org.mdpnp.devices.puritanbennett._840;

import ice.ConnectionState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.puritanbennett._840.PB840.Units;
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

import com.rti.dds.infrastructure.Time_t;

public class DemoPB840 extends AbstractDelegatingSerialDevice<PB840> {
    private static final Logger log = LoggerFactory.getLogger(DemoPB840.class);
    private InstanceHolder<ice.SampleArray> flowSampleArray, pressureSampleArray;
    private final Calendar currentDeviceTime = Calendar.getInstance();
    private final Time_t currentDeviceTimeAsTimeT = new Time_t(0,0); 
    protected final Map<PB840.Units, String> unitsMap = new HashMap<PB840.Units, String>();
    protected final Map<String, String> terms = new HashMap<String, String>();

    private class MyPB840Waveforms extends PB840Waveforms {

        public MyPB840Waveforms(InputStream input, OutputStream output) {
            super(input, output);
        }

        @Override
        public void receiveBreath(Collection<Number> flow, Collection<Number> pressure) {
            flowSampleArray = sampleArraySample(flowSampleArray, flow, rosetta.MDC_FLOW_AWAY.VALUE, rosetta.MDC_FLOW_AWAY.VALUE, 0, rosetta.MDC_DIM_L_PER_MIN.VALUE, 50, currentDeviceTimeAsTimeT);
            pressureSampleArray = sampleArraySample(pressureSampleArray, pressure, rosetta.MDC_PRESS_AWAY.VALUE, rosetta.MDC_PRESS_AWAY.VALUE, 0, rosetta.MDC_DIM_CM_H2O.VALUE,
                    50, currentDeviceTimeAsTimeT);
        }
    }

    public DemoPB840(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop, 2, PB840.class);
        loadUnits(unitsMap);
        loadTerms(terms);
        currentDeviceTime.set(Calendar.SECOND, 0);
        currentDeviceTime.set(Calendar.MILLISECOND, 0);
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

    protected static final void loadUnits(Map<PB840.Units, String> map) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(PB840Parameters.class.getResourceAsStream("units")));
            String line = null;

            int lineNumber = 0;
            
            while (null != (line = br.readLine())) {
                lineNumber++;
                line = line.trim();
                if ('#' != line.charAt(0)) {
                    String v[] = line.split("\t");

                    if (v.length != 2) {
                        log.warn("Bad line" + lineNumber + ":" + line);
                    } else {
                        map.put(Units.valueOf(v[0]), v[1]);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    protected static final void loadTerms(Map<String, String> map) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(PB840Parameters.class.getResourceAsStream("terms")));
            String line = null;

            int lineNumber = 0;
            
            while (null != (line = br.readLine())) {
                lineNumber++;
                line = line.trim();
                if ('#' != line.charAt(0)) {
                    String v[] = line.split("\t");

                    if (v.length != 2) {
                        log.warn("Bad line" + lineNumber + ":" + line);
                    } else {
                        map.put(v[0], v[1]);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private class MyPB840Parameters extends PB840Parameters {
        public MyPB840Parameters(InputStream input, OutputStream output) {
            super(input, output);
        }

        @Override
        public void receiveNumeric(String name, Units units, String value) {
            String canonicalName = terms.get(name);
            canonicalName = null == canonicalName ? name : canonicalName;
            try {
                numericInstances.put(name,
                        numericSample(numericInstances.get(name), parseFloat(value), 
                                canonicalName, name, unitsMap.get(units), currentDeviceTimeAsTimeT));
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
        public void receiveTime(int hour, int minute) {
            currentDeviceTime.set(Calendar.HOUR_OF_DAY, hour);
            currentDeviceTime.set(Calendar.MINUTE, minute);
            long tm = currentDeviceTime.getTimeInMillis();
            currentDeviceTimeAsTimeT.sec = (int) (tm / 1000L);
            currentDeviceTimeAsTimeT.nanosec = (int)(1000000L * (tm % 1000L));
        }
        
        @Override
        public void receiveDate(int month, int day, int year) {
            currentDeviceTime.set(Calendar.MONTH, month);
            currentDeviceTime.set(Calendar.DATE, day);
            currentDeviceTime.set(Calendar.YEAR, year);
            long tm = currentDeviceTime.getTimeInMillis();
            currentDeviceTimeAsTimeT.sec = (int) (tm / 1000L);
            currentDeviceTimeAsTimeT.nanosec = (int)(1000000L * (tm % 1000L));
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
