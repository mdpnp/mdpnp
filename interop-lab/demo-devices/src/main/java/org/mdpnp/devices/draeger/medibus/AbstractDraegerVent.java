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
package org.mdpnp.devices.draeger.medibus;

import ice.ConnectionState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.draeger.medibus.RTMedibus.RTTransmit;
import org.mdpnp.devices.draeger.medibus.types.Command;
import org.mdpnp.devices.draeger.medibus.types.MeasuredDataCP1;
import org.mdpnp.devices.draeger.medibus.types.RealtimeData;
import org.mdpnp.devices.serial.AbstractDelegatingSerialDevice;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.Time_t;

public abstract class AbstractDraegerVent extends AbstractDelegatingSerialDevice<RTMedibus> {

    private static final Logger log = LoggerFactory.getLogger(AbstractDraegerVent.class);

    private Map<Enum<?>, String> numerics = new HashMap<Enum<?>, String>();
    private Map<Enum<?>, String> waveforms = new HashMap<Enum<?>, String>();

    protected Map<Enum<?>, InstanceHolder<ice.Numeric>> numericUpdates = new HashMap<Enum<?>, InstanceHolder<ice.Numeric>>();
    protected Map<Enum<?>, InstanceHolder<ice.SampleArray>> sampleArrayUpdates = new HashMap<Enum<?>, InstanceHolder<ice.SampleArray>>();
    protected Map<Enum<?>, InstanceHolder<ice.AlarmSettings>> alarmSettingsUpdates = new HashMap<Enum<?>, InstanceHolder<ice.AlarmSettings>>();
    
    
    protected InstanceHolder<ice.Numeric> startInspiratoryCycleUpdate;

    protected long deviceClockOffset = 0L;
    private final ThreadLocal<Time_t> currentTime = new ThreadLocal<Time_t>() {
        protected Time_t initialValue() {
            return new Time_t(0, 0);
        };
    };

    protected Time_t currentTime() {
        long now = System.currentTimeMillis() + deviceClockOffset;
        Time_t currentTime = this.currentTime.get();
        long then = currentTime.sec * 1000L + currentTime.nanosec / 1000000L;

        if (then - now > 0L) {
            // This happens too routinely to expend the I/O here
            // tried using the desination_order.source_timestamp_tolerance but
            // that was even too tight
            // TODO reconsider how we are deriving a device timestamp
            // log.warn("Not emitting timestamp="+new
            // Date(now)+" where last timestamp was "+new Date(then));
        } else {
            currentTime.sec = (int) (now / 1000L);
            currentTime.nanosec = (int) (now % 1000L * 1000000L);
        }
        return currentTime;
    }

    protected void processStartInspCycle() {
        // TODO This should not be triggered as a numeric; it's a bad idea
        startInspiratoryCycleUpdate = numericSample(startInspiratoryCycleUpdate, 0, ice.MDC_START_OF_BREATH.VALUE, null);
    }

    private static final int BUFFER_SAMPLES = 10;

    // Theoretical maximum 16 streams, practical limit seems to be 3
    // Buffering ten points is for testing, size of this buffer might be
    // a function of the sampling rate
    private final Number[][] realtimeBuffer = new Number[16][BUFFER_SAMPLES];
    private final int[] realtimeBufferCount = new int[16];
    private long lastRealtime;

    protected void processRealtime(RTMedibus.RTDataConfig config, int multiplier, int streamIndex, Object code, double value) {
        lastRealtime = System.currentTimeMillis();
        if (streamIndex >= realtimeBuffer.length) {
            log.warn("Invalid realtime streamIndex=" + streamIndex);
            return;
        }
        realtimeBuffer[streamIndex][realtimeBufferCount[streamIndex]++] = value;
        if (realtimeBufferCount[streamIndex] == realtimeBuffer[streamIndex].length) {
            realtimeBufferCount[streamIndex] = 0;

            // flush
            if (code instanceof Enum<?>) {
                String metric_id = waveforms.get(code);
                if (null != metric_id) {
                    sampleArrayUpdates.put(
                            (Enum<?>) code,
                            sampleArraySample(sampleArrayUpdates.get(code), realtimeBuffer[streamIndex],
                                   metric_id, config.interval * multiplier, currentTime()));
                } else {
                    log.trace("No nomenclature code for enum code=" + code + " class=" + code.getClass().getName());
                }
            } else {
                log.trace("No enumerated type for code=" + code + " class=" + code.getClass().getName());
            }
        }
    }

    @Override
    protected void unregisterAllNumericInstances() {
        super.unregisterAllNumericInstances();
        numericUpdates.clear();
    }

    @Override
    protected void unregisterAllSampleArrayInstances() {
        super.unregisterAllSampleArrayInstances();
        sampleArrayUpdates.clear();
    }
    
    @Override
    protected void unregisterAllAlarmSettingsInstances() {
        super.unregisterAllAlarmSettingsInstances();
        alarmSettingsUpdates.clear();
    }

    protected void processCorrupt() {
    }

    private class MyRTMedibus extends RTMedibus {
        public MyRTMedibus(InputStream in, OutputStream out) {
            super(in, out);
        }

        private final RTDataConfig currentRTConfig(RealtimeData rd, RTDataConfig[] currentRTDataConfig) {
            for (int i = 0; i < currentRTDataConfig.length; i++) {
                if (rd.equals(currentRTDataConfig[i].realtimeData)) {
                    return currentRTDataConfig[i];
                }
            }
            return null;
        }

        @Override
        protected void receiveRealtimeConfig(RTDataConfig[] currentRTDataConfig) {
            super.receiveRealtimeConfig(currentRTDataConfig);
            if (ice.ConnectionState.Connected.equals(getState())) {
                List<RTTransmit> transmits = new ArrayList<RTTransmit>();
                for (RealtimeData rd : REQUEST_REALTIME) {
                    RTDataConfig config = currentRTConfig(rd, currentRTDataConfig);
                    if (null != config) {
                        transmits.add(new RTTransmit(rd, 1, config));
                    } else {
                        log.warn("Device does not support requested " + rd);
                    }
                }

                try {
                    log.trace("Realtime configuration received and Connected so sending RT xmit command: " + transmits);
                    sendRTTransmissionCommand(transmits.toArray(new RTTransmit[0]));
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }

        @Override
        protected void receiveResponse(byte[] response, int len) {
            super.receiveResponse(response, len);
            Object cmdEcho = Command.fromByteIf(response[0]);
            if (cmdEcho instanceof Command) {
                switch ((Command) cmdEcho) {
                case InitializeComm:
                    initializeCommAcknowledged();
                    break;
                case ConfigureRealtime:
                    realtimeTransmitAcknowledged();
                    break;
                default:
                }
            }
        }

        @Override
        protected void receiveDeviceIdentification(String idNumber, String name, String revision) {
            receiveDeviceId(idNumber, name);
        }

        @Override
        protected void receiveTextMessage(byte[] response, int len) {
            try {
                lastReqDateTime = System.currentTimeMillis();
                getDelegate().sendCommand(Command.ReqDateTime);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            super.receiveTextMessage(response, len);
        }
        
        @Override
        protected void receiveTextMessage(Data[] data, int n) {
            markOldPatientAlertInstances();
            for(Data d : data) {
                writePatientAlert(d.code.toString(), d.data);
            }
            clearOldPatientAlertInstances();
        }

        @Override
        protected void receiveDeviceSetting(Data[] data, int n) {
            // TODO EMIT DEVICE SETTINGS
            try {
                getDelegate().sendCommand(Command.ReqMeasuredDataCP1);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }

        }

        @Override
        protected void receiveDataCodes(Command cmdEcho, byte[] response, int len) {
            try {
                switch(cmdEcho) {
                case ReqMeasuredDataCP1:
                    getDelegate().sendCommand(Command.ReqMeasuredDataCP2);
                    break;
                case ReqMeasuredDataCP2:
                    getDelegate().sendCommand(Command.ReqLowAlarmLimitsCP1);
                    break;
                case ReqLowAlarmLimitsCP1:
                    getDelegate().sendCommand(Command.ReqLowAlarmLimitsCP2);
                    break;
                case ReqLowAlarmLimitsCP2:
                    getDelegate().sendCommand(Command.ReqHighAlarmLimitsCP1);
                    break;
                case ReqHighAlarmLimitsCP1:
                    markOldTechnicalAlertInstances();
                    getDelegate().sendCommand(Command.ReqAlarmsCP1);
                    break;
                default:
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            super.receiveDataCodes(cmdEcho, response, len);
        }
        @Override
        protected void receiveMeasuredData(Data[] data, int n) {
            for(Data d : data) {
                try {
                    if(d.code instanceof Enum<?>) {
                        String metric = numerics.get(d.code);
                        if(null != metric) {
                            String s = null == d.data ? null : d.data.toString().trim();
                            if (null != s) {
                                numericUpdates.put((Enum<?>) d.code,  numericSample(numericUpdates.get(d.code), Float.parseFloat(s), metric, currentTime()));
                            } else {
                                numericUpdates.put((Enum<?>) d.code,  numericSample(numericUpdates.get(d.code), (Float) null, metric, currentTime()));
                            }
                        } else {
                            log.debug("No metric mapped for code=" + d.code +" data="+d.data);
                        }
                    } else {
                        log.debug("Unknown code="+d.code+" data="+d.data);
                    }
                } catch (NumberFormatException nfe) {
                    log.error("Bad number format " + d.data, nfe);
                }
            }
        }

        @Override
        protected void receiveCorruptResponse() {
            processCorrupt();
        }

        @Override
        public void receiveDataValue(RTMedibus.RTDataConfig config, int multiplier, int streamIndex, Object realtimeData, double data) {
            processRealtime(config, multiplier, streamIndex, realtimeData, data);
        }

        @Override
        protected void receiveAlarmCodes(Command cmdEcho, byte[] response, int len) {
            try {
                switch(cmdEcho) {
                case ReqAlarmsCP1:
                    getDelegate().sendCommand(Command.ReqAlarmsCP2);
                    break;
                case ReqAlarmsCP2:
                    getDelegate().sendCommand(Command.ReqTextMessages);
                    break;
                default:
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            super.receiveAlarmCodes(cmdEcho, response, len);
        }
        
        @Override
        protected void receiveLowAlarmLimits(Data[] data, int n) {
            for(Data d : data) {
                try {
                    InstanceHolder<ice.AlarmSettings> a = alarmSettingsUpdates.get(d.code);
                    String metric = numerics.get(d.code);
                    if(null != metric) {
                        alarmSettingsUpdates.put((Enum<?>) d.code, alarmSettingsSample(a, Float.parseFloat(d.data), null==a?null:a.data.upper, metric));
                    } else {
                        log.debug("No metric for alarm code " + d.code);
                    }
                } catch (NumberFormatException nfe) {
                    log.error("Badly formatted number " + d.data, nfe);
                }
            }
        }
        
        @Override
        protected void receiveHighAlarmLimits(Data[] data, int n) {
            for(Data d : data) {
                try {
                    InstanceHolder<ice.AlarmSettings> a = alarmSettingsUpdates.get(d.code);
                    String metric = numerics.get(d.code);
                    if(null != metric) {
                        alarmSettingsUpdates.put((Enum<?>) d.code, alarmSettingsSample(a, null==a?null:a.data.lower, Float.parseFloat(d.data), metric));
                    } else {
                        log.debug("No metric for alarm code " + d.code);
                    }
                } catch (NumberFormatException nfe) {
                    log.error("Badly formatted number " + d.data, nfe);
                }
            }
            
        }
        
        @Override
        protected void receiveAlarms(Alarm[] alarms) {
            for(Alarm a : alarms) {
                if(a.alarmCode instanceof Enum<?>) {
                    writeTechnicalAlert(a.alarmCode.toString(), a.alarmPhrase);
                } else {
                    log.debug("Unknown alarmCode="+a.alarmCode+" alarmPhrase="+a.alarmPhrase);
                }
            }
        }

        @Override
        protected void receiveDateTime(byte[] response, int len) {
            try {
                
                getDelegate().sendCommand(Command.ReqDeviceSetting);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            super.receiveDateTime(response, len);
        }
        
        @Override
        protected void receiveDateTime(Date date) {
            deviceClockOffset = date.getTime() - System.currentTimeMillis();
            log.debug("Local clock offset " + deviceClockOffset + "ms from device");
        }

        @Override
        public void startInspiratoryCycle() {
            processStartInspCycle();
        }

    }

    private static final RealtimeData[] REQUEST_REALTIME = new RealtimeData[] { RealtimeData.AirwayPressure, RealtimeData.FlowInspExp,
            RealtimeData.ExpiratoryCO2mmHg, RealtimeData.O2InspExp };

    private long lastReqDateTime;

    private class RequestSlowData implements Runnable {
        public void run() {
            if (ice.ConnectionState.Connected.equals(getState())) {
                try {
                    RTMedibus medibus = AbstractDraegerVent.this.getDelegate();
                    if ((System.currentTimeMillis() - lastRealtime) >= 10000L) {
                        log.warn("" + (System.currentTimeMillis() - lastRealtime) + "ms since realtime data, requesting realtime config");
                        // Starts a process by requesting the realtime
                        // configuration
                        // see receiveRealtimeConfig(...)
                        lastRealtime = System.currentTimeMillis();
                        medibus.sendCommand(Command.ReqRealtimeConfig);
                        return;
                    }
                    long now = System.currentTimeMillis();

                    if (now - lastReqDateTime >= 15000L) {
                        log.debug("Slow data too old, requesting DateTime");
                        lastReqDateTime = now;
                        medibus.sendCommand(Command.ReqDateTime);
                        return;
                    }

                    // Data is sparse in standby mode; trying to keep alive
                    // TODO need to externalize all these timing settings
                    // eventually
                    if ((now - timeAwareInputStream.getLastReadTime()) >= (getMaximumQuietTime() / 2L)) {
                        medibus.sendCommand(Command.NoOperation);
                        return;
                    }
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                }
            }

        }
    }

    @Override
    public void disconnect() {
        stopRequestSlowData();
        RTMedibus medibus = null;
        synchronized (this) {
            medibus = getDelegate(false);
        }
        if (null != medibus) {
            try {
                medibus.sendCommand(Command.StopComm);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            log.debug("rtMedibus was already null in disconnect");
        }
        super.disconnect();
    }

    private void init() {
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        deviceIdentity.manufacturer = "Dr\u00E4ger";
        deviceIdentity.model = "???";
        writeDeviceIdentity();
    }

    protected static void loadMap(Map<Enum<?>, String> numerics, Map<Enum<?>, String> waveforms) {

        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(AbstractDraegerVent.class.getResourceAsStream("draeger.map")));
            String line = null;
            String draegerPrefix = MeasuredDataCP1.class.getPackage().getName() + ".";

            while (null != (line = br.readLine())) {
                line = line.trim();
                if ('#' != line.charAt(0)) {
                    String v[] = line.split("\t");

                    if (v.length < 3) {
                        log.debug("Bad line:" + line);
                    } else {
                        String c[] = v[0].split("\\.");
                        @SuppressWarnings({ "unchecked", "rawtypes" })
                        Enum<?> draeger = (Enum<?>) Enum.valueOf((Class<? extends Enum>) Class.forName(draegerPrefix + c[0]), c[1]);
                        String tag = getValue(v[1]);
                        if (tag == null) {
                            log.warn("cannot find value for " + v[1]);
                            continue;
                        }
                        log.trace("Adding " + draeger + " mapped to " + tag);
                        v[2] = v[2].trim();
                        if ("W".equals(v[2])) {
                            waveforms.put(draeger, tag);
                        } else if ("N".equals(v[2])) {
                            numerics.put(draeger, tag);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ScheduledFuture<?> requestSlowData;

    @Override
    protected void stateChanged(ConnectionState newState, ConnectionState oldState) {

        if (ice.ConnectionState.Connected.equals(newState) && !ice.ConnectionState.Connected.equals(oldState)) {
            startRequestSlowData();
        }
        if (!ice.ConnectionState.Connected.equals(newState) && ice.ConnectionState.Connected.equals(oldState)) {
            stopRequestSlowData();
        }
        super.stateChanged(newState, oldState);
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

    private synchronized void startRequestSlowData() {
        if (null == requestSlowData) {
            requestSlowData = executor.scheduleWithFixedDelay(new RequestSlowData(), 0L, 250L, TimeUnit.MILLISECONDS);
            log.trace("Scheduled slow data request task");
        } else {
            log.trace("Slow data request already scheduled");
        }
    }

    private static String getValue(String name) throws Exception {
        try {
            Class<?> cls = Class.forName(name);
            return (String) cls.getField("VALUE").get(null);
        } catch (ClassNotFoundException e) {
            // If it's not a class then maybe it's a static member
            int lastIndexOfDot = name.lastIndexOf('.');
            if (lastIndexOfDot < 0) {
                throw e;
            }
            Class<?> cls = Class.forName(name.substring(0, lastIndexOfDot));
            Object obj = cls.getField(name.substring(lastIndexOfDot + 1, name.length())).get(null);
            return (String) obj.getClass().getMethod("value").invoke(obj);

        }

    }

    public AbstractDraegerVent(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);
        init();
        loadMap(numerics, waveforms);
    }

    @Override
    protected RTMedibus buildDelegate(InputStream in, OutputStream out) {
        log.trace("Creating an RTMedibus");
        return new MyRTMedibus(in, out);
    }

    @Override
    protected boolean delegateReceive(RTMedibus delegate) throws IOException {
        return delegate.receive();
    }

    protected synchronized void receiveDeviceId(String guid, String name) {
        log.trace("receiveDeviceId:guid=" + guid + ", name=" + name);

        boolean writeIt = false;
        if (null != guid) {
            deviceIdentity.serial_number = guid;
            writeIt = true;

        }
        if (null != name) {
            deviceIdentity.model = name;
            writeIt = true;
        }
        if (writeIt) {
            writeDeviceIdentity();
        }
        reportConnected();
    }

    @Override
    protected void doInitCommands() throws IOException {
        super.doInitCommands();
        RTMedibus rtMedibus = getDelegate();

        rtMedibus.sendCommand(Command.InitializeComm);
    }

    protected void realtimeTransmitAcknowledged() {
        if (ice.ConnectionState.Connected.equals(getState())) {
            RTTransmit[] lastTransmitted = getDelegate().getLastTransmitted();
            int[] traces = new int[lastTransmitted.length];
            for (int i = 0; i < traces.length; i++) {
                traces[i] = lastTransmitted[i].rtDataConfig.ordinal;
            }
            try {
                log.trace("Realtime transmits acknowledged so enabling realtime traces:" + traces);
                getDelegate().sendEnableRealtime(traces);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    protected void initializeCommAcknowledged() {

        try {
            getDelegate().sendCommand(Command.ReqDeviceId);
        } catch (IOException ioe) {
            log.error("Unable to request device id", ioe);
        }
    }

    @Override
    protected long getMaximumQuietTime() {
        return 3000L;
    }

    @Override
    protected long getConnectInterval() {
        return 3000L;
    }

    @Override
    protected long getNegotiateInterval() {
        return 1000L;
    }

    @Override
    protected void process(InputStream inputStream, OutputStream outputStream) throws IOException {

        Thread t = new Thread(new Runnable() {
            public void run() {
                try {
                    // Will block until the delegate is available
                    final RTMedibus rtMedibus = getDelegate(false);
                    rtMedibus.receiveFast();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, "Medibus FAST data");
        t.setDaemon(true);
        t.start();
        log.trace("spawned a fast data processor");

        // really the RTMedibus thread will block until
        // the super.process populates an InputStream to allow
        // building of the delegate
        super.process(inputStream, outputStream);

    }

}
