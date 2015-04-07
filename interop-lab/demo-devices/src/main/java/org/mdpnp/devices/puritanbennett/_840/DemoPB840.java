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

import org.mdpnp.devices.DeviceClock;
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

public class DemoPB840 extends AbstractDelegatingSerialDevice<PB840> {
    private static final Logger log = LoggerFactory.getLogger(DemoPB840.class);
    private InstanceHolder<ice.SampleArray> flowSampleArray, pressureSampleArray;
    private final PB840Clock deviceClock = new PB840Clock();
    protected final Map<PB840.Units, String> unitsMap = new HashMap<PB840.Units, String>();
    protected final Map<String, String> terms = new HashMap<String, String>();

    private class MyPB840Waveforms extends PB840Waveforms {

        public MyPB840Waveforms(InputStream input, OutputStream output) {
            super(input, output);
        }

        @Override
        public void receiveBreath(Collection<Number> flow, Collection<Number> pressure) {
            DeviceClock.Reading sampleTime = deviceClock.instant();

            flowSampleArray =
                    sampleArraySample(flowSampleArray, flow,
                                      rosetta.MDC_FLOW_AWAY.VALUE,
                                      rosetta.MDC_FLOW_AWAY.VALUE, 0,
                                      rosetta.MDC_DIM_L_PER_MIN.VALUE, 50,
                                      sampleTime);
            pressureSampleArray =
                    sampleArraySample(pressureSampleArray, pressure,
                                      rosetta.MDC_PRESS_AWAY.VALUE,
                                      rosetta.MDC_PRESS_AWAY.VALUE, 0,
                                      rosetta.MDC_DIM_CM_H2O.VALUE, 50,
                                      sampleTime);
        }
    }

    public DemoPB840(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop, 2, PB840.class);
        loadUnits(unitsMap);
        loadTerms(terms);
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        deviceIdentity.manufacturer = "Puritan Bennett";
        deviceIdentity.model = "";
        writeDeviceIdentity();
    }


    protected Map<String, InstanceHolder<ice.Numeric>> numericInstances = new HashMap<String, InstanceHolder<ice.Numeric>>();
//    protected Map<String, InstanceHolder<ice.AlarmSettings>> alarmSettingsInstances = new HashMap<String, InstanceHolder<ice.AlarmSettings>>();
    protected Map<String, InstanceHolder<ice.AlarmLimit>> alarmLimitInstances = new HashMap<String, InstanceHolder<ice.AlarmLimit>>();

    @Override
    protected void unregisterAllAlarmLimitInstances() {
        alarmLimitInstances.clear();
        super.unregisterAllAlarmLimitInstances();
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
        public void receiveSetting(String name, Units units, String value) {
            // TODO settings might not always be on the same topic as numerics
            receiveNumeric(name, units, value);
        }
        
        @Override
        public void receiveNumeric(String name, Units units, String value) {
            String canonicalName = terms.get(name);
            canonicalName = null == canonicalName ? name : canonicalName;
            try {
                DeviceClock.Reading sampleTime = deviceClock.instant();
                numericInstances.put(name,
                        numericSample(numericInstances.get(name),
                                      parseFloat(value),
                                      canonicalName, name,
                                      unitsMap.get(units),
                                      sampleTime));
            } catch (NumberFormatException nfe) {
                log.warn("Poorly formatted numeric " + name + " " + value);
                throw nfe;
            }
        }
        
        @Override 
        public void receiveAlarmLimit(String metricName, PB840.Units unitID, String value, String limitType) {
            try {
                // TODO using FLOAT_MIN, FLOAT_MAX as reserved values because
                // otherwise cannot publish AlarmSettings (now limits)
                // with only one boundary condition
            	ice.LimitType limit = limitType.equals(ice.LimitType.low_limit.toString())? ice.LimitType.low_limit:ice.LimitType.high_limit;
            	//XXX possible values of Alarm settings PB_LIMIT_TOTAL_RESPIRATORY_RATE & PB_LIMIT_INSPIRED_TIDAL_VOLUME: numeric or OFF
            	Float f = value.equals("OFF")?null:parseFloat(value, null);
            	alarmLimitInstances.put(
                		metricName+ "_" + limitType,//metric_id
                        alarmLimitSample(alarmLimitInstances.get(metricName+ "_" + limitType), unitID.toString(),
                                f ,//parseFloat(value, null),
                                metricName,
                                limit));
            } catch (NumberFormatException nfe) {
                log.warn("Poorly formatted alarm setting " + metricName + " value " + value 
                		+ " limit " + limitType);
                throw nfe;
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
        public void receiveVentilatorId(String model, String id) {
            if (!id.equals(deviceIdentity.serial_number) || 
                !model.equals(deviceIdentity.model)) {
                deviceIdentity.serial_number = id;
                deviceIdentity.model = model;
                writeDeviceIdentity();
            }
        }
        
        @Override
        public void receiveTime(int hour, int minute) {
            deviceClock.receiveTime(hour, minute);
        }
        
        @Override
        public void receiveDate(int month, int day, int year) {
            deviceClock.receiveDate(month, day, year);
        }
        
        @Override
        public void receiveStartResponse(String responseType) {
            reportConnected("Received "+responseType);
            // TODO If this response is not a full MISCF don't expect updates to alert conditions
            if("MISCF".equals(responseType)) {
                markOldPatientAlertInstances();
                markOldTechnicalAlertInstances();
            }
        }
        private int outstandingRequests = 0;
        private long lastRequest = 0L;
        
        @Override
        public void receiveEndResponse() {
            
            long now = System.nanoTime();
            // TODO this is not threadsafe; assuming serial traffic always
            // delivered on the same thread
            
            // decrement because of response 
            outstandingRequests--;
            
            // TODO Refine this and externalize constant to keep in sync with max quiet time
            if(outstandingRequests > 0 && (now - lastRequest) > 2000000000L) {
                log.warn("Resetting request count after 2 seconds with no response");
                outstandingRequests = 0;
            }
            
            // There is a request with no response
            if(outstandingRequests<1) {
                try {
                    sendF();
                    lastRequest = now;
                    if(outstandingRequests < 0) {
                        log.warn("Received extraneous responses");
                        outstandingRequests = 0;
                    }
                    outstandingRequests++;
                } catch (IOException e) {
                    log.error("Unable to issue SNDF", e);
                }
            }
            clearOldPatientAlertInstances();
            clearOldTechnicalAlertInstances();
        }
    }

    @Override
    protected void doInitCommands(int idx) throws IOException {
        super.doInitCommands(idx);
        switch (idx) {
        case 0:
            ((PB840Parameters) getDelegate(idx)).sendF();
            log.trace("Issued a SNDF for doInitCommands");
            break;
        default:

        }
    }

    @Override
    protected void stateChanged(ConnectionState newState, ConnectionState oldState, String transitionNote) {
        super.stateChanged(newState, oldState, transitionNote);
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
            serialProvider.setDefaultSerialSettings(9600, DataBits.Eight, Parity.None, StopBits.One, FlowControl.Hardware);
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
            return 3000L;
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


    class PB840Clock implements DeviceClock {

        private final Calendar currentDeviceTime = Calendar.getInstance();
        
        public PB840Clock() {
            currentDeviceTime.set(Calendar.SECOND, 0);
            currentDeviceTime.set(Calendar.MILLISECOND, 0);
        }

        public void receiveTime(int hour, int minute) {
            currentDeviceTime.set(Calendar.HOUR_OF_DAY, hour);
            currentDeviceTime.set(Calendar.MINUTE, minute);
        }

        public void receiveDate(int month, int day, int year) {
            currentDeviceTime.set(Calendar.MONTH, month);
            currentDeviceTime.set(Calendar.DATE, day);
            currentDeviceTime.set(Calendar.YEAR, year);
        }

        @Override
        public Reading instant() {
            // TODO I don't want to deal with this right now .. rest of the system shouldn't be messing with source_timestamp
            return new ReadingImpl(System.currentTimeMillis());
        }

    }
}
