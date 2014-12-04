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
package org.mdpnp.devices.oridion.capnostream;

import ice.AlarmSettings;
import ice.LocalAlarmSettingsObjective;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.oridion.capnostream.Capnostream.CO2Units;
import org.mdpnp.devices.oridion.capnostream.Capnostream.Command;
import org.mdpnp.devices.oridion.capnostream.Capnostream.SetupItem;
import org.mdpnp.devices.serial.AbstractDelegatingSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.Time_t;

/**
 * @author Jeff Plourde
 *
 */
public class DemoCapnostream20 extends AbstractDelegatingSerialDevice<Capnostream> {
    @Override
    protected long getMaximumQuietTime(int idx) {
        return 900L;
    }

    @Override
    protected long getConnectInterval(int idx) {
        return 3000L;
    }

    @Override
    protected long getNegotiateInterval(int idx) {
        return 200L;
    }

    @Override
    protected String iconResourceName() {
        return "capnostream.png";
    }
    
    protected static final String units(CO2Units units) {
        if(null == units) {
            return rosetta.MDC_DIM_DIMLESS.VALUE;
        }
        switch(units) {
        case kPa:
            return rosetta.MDC_DIM_KILO_PASCAL.VALUE;
        case mmHg:
            return rosetta.MDC_DIM_MMHG.VALUE;
        case VolPct:
            return rosetta.MDC_DIM_VOL_PERCENT.VALUE;
        default:
            return rosetta.MDC_DIM_DIMLESS.VALUE;
        }
    }
    
    protected static final float divisor(CO2Units units) {
        if(null == units) {
            return 1f;
        }
        switch(units) {
        case kPa:
        case VolPct:
            return 10f;
        case mmHg:
            return 1f;
        default:
            return 1f;
        }
    }    

    private static final long MAX_COMMAND_RESPONSE = 1500L;

    protected InstanceHolder<ice.SampleArray> co2;
    protected InstanceHolder<ice.Numeric> spo2;
    protected InstanceHolder<ice.Numeric> pulserate;
    protected InstanceHolder<ice.Numeric> rr;
    protected InstanceHolder<ice.Numeric> etco2;
    protected InstanceHolder<ice.Numeric> endOfBreath;


    protected InstanceHolder<ice.AlarmSettings> spo2AlarmSettings, pulserateAlarmSettings, rrAlarmSettings, etco2AlarmSettings;
    

    protected void linkIsActive() {
        try {
            if (ice.ConnectionState.Connected.equals(getState())) {
                // The specification is a bit vague on how quickly
                // unacknowledged commands can be sent
                getDelegate().sendCommand(Command.LinkIsActive);
                getDelegate().sendHostMonitoringId("ICE");

            }
        } catch (IOException e) {
            log.error("Error sending link is active message", e);
        }
    }

    private final Map<String, Integer> currentLow = new HashMap<String, Integer>();
    private final Map<String, Integer> currentHigh = new HashMap<String, Integer>();

    private final Map<String, Integer> priorSafeLow = new HashMap<String, Integer>();
    private final Map<String, Integer> priorSafeHigh = new HashMap<String, Integer>();

    @Override
    protected InstanceHolder<AlarmSettings> alarmSettingsSample(InstanceHolder<AlarmSettings> holder, Float newLower, Float newUpper, String metric_id) {
        if (newLower != null) {
            currentLow.put(metric_id, (int) (float) newLower);
        }
        if (newUpper != null) {
            currentHigh.put(metric_id, (int) (float) newUpper);
        }
        return super.alarmSettingsSample(holder, newLower, newUpper, metric_id);
    }

    public DemoCapnostream20(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop, Capnostream.class);
        init();
    }

    public static SetupItem lowerAlarm(String metric_id) {
        if (rosetta.MDC_PULS_OXIM_SAT_O2.VALUE.equals(metric_id)) {
            return SetupItem.SpO2Low;
        } else if (rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE.equals(metric_id)) {
            return SetupItem.PulseRateLow;
        } else if (rosetta.MDC_CO2_RESP_RATE.VALUE.equals(metric_id)) {
            return SetupItem.respiratoryRateLow;
        } else if (rosetta.MDC_AWAY_CO2_ET.VALUE.equals(metric_id)) {
            return SetupItem.EtCO2Low;
        } else {
            return null;
        }
    }

    public static SetupItem upperAlarm(String metric_id) {
        if (rosetta.MDC_PULS_OXIM_SAT_O2.VALUE.equals(metric_id)) {
            return SetupItem.SpO2High;
        } else if (rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE.equals(metric_id)) {
            return SetupItem.PulseRateHigh;
        } else if (rosetta.MDC_CO2_RESP_RATE.VALUE.equals(metric_id)) {
            return SetupItem.respiratoryRateHigh;
        } else if (rosetta.MDC_AWAY_CO2_ET.VALUE.equals(metric_id)) {
            return SetupItem.EtCO2High;
        } else {
            return null;
        }
    }

    private final SetupItemHandler setupItemHandler = new SetupItemHandler();

    private final class SetupItemHandler implements Runnable {

        private final Map<SetupItem, Integer> setupValuesToSend = new HashMap<SetupItem, Integer>();
        private SetupItem sent;
        private long sentAt;
        private Integer sentValue;

        public synchronized void send(SetupItem si, Integer value) {
            Integer oldValue = setupValuesToSend.get(si);
            if (null != oldValue) {
                log.debug("Skipping setting " + si + " to " + oldValue + " and now setting to " + value);
            }
            setupValuesToSend.put(si, value);
            executor.schedule(this, 0L, TimeUnit.MILLISECONDS);
        }

        public synchronized void receive(SetupItem si, Integer value) {
            if (si.equals(sent)) {
                if (value.equals(sentValue)) {
                    log.debug("Acknowledged " + si + " = " + value);
                } else {
                    log.warn("Acknowledged " + si + " = " + value + " but actually sent " + sentValue);
                }
                sent = null;
                sentAt = 0L;
                sentValue = null;
            } else {
                log.warn("Not sent but received acknowledgment " + si + " = " + value);
            }
            if (!setupValuesToSend.isEmpty()) {
                executor.schedule(this, 0L, TimeUnit.MILLISECONDS);
            }
        }

        @Override
        public synchronized void run() {
            long now = System.currentTimeMillis();

            if (sent != null) {
                if (now > (sentAt + MAX_COMMAND_RESPONSE)) {
                    log.warn("Timed out waiting for response to " + sent);
                    sent = null;
                    sentValue = null;
                    sentAt = 0L;
                } else {
                    executor.schedule(this, sentAt + MAX_COMMAND_RESPONSE - now, TimeUnit.MILLISECONDS);
                    return;
                }
            }
            if (!setupValuesToSend.isEmpty()) {
                Iterator<SetupItem> itr = setupValuesToSend.keySet().iterator();
                sent = itr.next();
                sentValue = setupValuesToSend.remove(sent);
                try {

                    if (getDelegate().sendConfigurableSetup(sent, sentValue)) {
                        log.debug("Sent " + sent + " = " + sentValue);
                        sentAt = System.currentTimeMillis();
                        executor.schedule(this, MAX_COMMAND_RESPONSE, TimeUnit.MILLISECONDS);
                    } else {
                        log.debug("Did NOT Send " + sent + " = " + sentValue);
                        sentAt = 0L;
                        sent = null;
                        sentValue = null;
                    }
                } catch (IOException e) {
                    log.error("error", e);
                }

            }
        }

    }

    @Override
    public void unsetAlarmSettings(String metricId) {
        super.unsetAlarmSettings(metricId);
        log.warn("Resetting " + metricId + " to [" + priorSafeLow.get(metricId) + " , " + priorSafeHigh.get(metricId));
        setupItemHandler.send(lowerAlarm(metricId), priorSafeLow.get(metricId));
        setupItemHandler.send(upperAlarm(metricId), priorSafeHigh.get(metricId));
    }

    private Map<String, InstanceHolder<ice.LocalAlarmSettingsObjective>> localAlarmSettings = new HashMap<String, InstanceHolder<ice.LocalAlarmSettingsObjective>>();

    @Override
    protected void unregisterAlarmSettingsObjectiveInstance(InstanceHolder<LocalAlarmSettingsObjective> holder) {
        localAlarmSettings.clear();
        super.unregisterAlarmSettingsObjectiveInstance(holder);
    }

    @Override
    public void setAlarmSettings(ice.GlobalAlarmSettingsObjective obj) {
        super.setAlarmSettings(obj);
        priorSafeHigh.put(obj.metric_id, currentHigh.get(obj.metric_id));
        priorSafeLow.put(obj.metric_id, currentLow.get(obj.metric_id));
        setupItemHandler.send(lowerAlarm(obj.metric_id), (int) obj.lower);
        setupItemHandler.send(upperAlarm(obj.metric_id), (int) obj.upper);
        // TODO Does this really below here?
        localAlarmSettings.put(obj.metric_id,
                alarmSettingsObjectiveSample(localAlarmSettings.get(obj.metric_id), obj.lower, obj.upper, obj.metric_id));
    }

    private void init() {
        deviceIdentity.manufacturer = "Oridion";
        deviceIdentity.model = "Capnostream20";
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        writeDeviceIdentity();

        linkIsActive = executor.scheduleAtFixedRate(new Runnable() {
            public void run() {
                linkIsActive();
            }
        }, 5000L, 5000L, TimeUnit.MILLISECONDS);
    }

    private static final int BUFFER_SAMPLES = 5;
    private final Number[] realtimeBuffer = new Number[BUFFER_SAMPLES];
    private int realtimeBufferCount = 0;
    protected static final TimeZone localTimeZone = TimeZone.getDefault();
    
    public class MyCapnostream extends Capnostream {
        public MyCapnostream(InputStream in, OutputStream out) {
            super(in, out);
        }

        private final Time_t sampleTime = new Time_t(0, 0);
        
        private final StringBuilder messageBuilder = new StringBuilder();
        
        private CO2Units currentUnits = null;
        
        @Override
        public boolean receiveNumerics(long date, int etCO2, int FiCO2, int respiratoryRate, int spo2, int pulserate, int slowStatus,
                int co2ActiveAlarms, int spO2ActiveAlarms, int noBreathPeriodSeconds, int etCo2AlarmHigh, int etCo2AlarmLow, int rrAlarmHigh,
                int rrAlarmLow, int fico2AlarmHigh, int spo2AlarmHigh, int spo2AlarmLow, int pulseAlarmHigh, int pulseAlarmLow, CO2Units units,
                int extendedCO2Status) {
            
            writePatientAlert("CO2", CO2ActiveAlarms.build(co2ActiveAlarms, messageBuilder));
            writePatientAlert("SPO2", SpO2ActiveAlarms.build(spO2ActiveAlarms, messageBuilder));
            writeDeviceAlert(SlowStatus.build(slowStatus, messageBuilder));
            writeTechnicalAlert("CO2", ExtendedCO2Status.build(extendedCO2Status, messageBuilder));

            // The device has no timezone setting ... so we're getting milliseconds since the epoch in local time
            // which is an unusual value to get... and hence we need to do something unusual to cope with it
            int offset = localTimeZone.getOffset(date);
            
            date -= offset;
            
            sampleTime.sec = (int) (date / 1000L);
            sampleTime.nanosec = (int) (date % 1000L * 1000000L);

            DemoCapnostream20.this.spo2 = numericSample(DemoCapnostream20.this.spo2, 0xFF == spo2 ? null : spo2, rosetta.MDC_PULS_OXIM_SAT_O2.VALUE,
                    rosetta.MDC_DIM_PERCENT.VALUE,
                    sampleTime);

            rr = numericSample(rr, 0xFF == respiratoryRate ? null : respiratoryRate, rosetta.MDC_CO2_RESP_RATE.VALUE, 
                    rosetta.MDC_DIM_RESP_PER_MIN.VALUE, sampleTime);

            this.currentUnits = units;

            etco2 = numericSample(etco2, 0xFF == etCO2 ? null : etCO2 / divisor(units), rosetta.MDC_AWAY_CO2_ET.VALUE, units(units), sampleTime);

            DemoCapnostream20.this.pulserate = numericSample(DemoCapnostream20.this.pulserate, 0xFF == pulserate ? null : pulserate,
                    rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, rosetta.MDC_DIM_BEAT_PER_MIN.VALUE, sampleTime);

            DemoCapnostream20.this.spo2AlarmSettings = alarmSettingsSample(DemoCapnostream20.this.spo2AlarmSettings, 0xFF == spo2AlarmLow ? null
                    : (float) spo2AlarmLow, 0xFF == spo2AlarmHigh ? null : (float) spo2AlarmHigh, rosetta.MDC_PULS_OXIM_SAT_O2.VALUE);

            DemoCapnostream20.this.etco2AlarmSettings = alarmSettingsSample(DemoCapnostream20.this.etco2AlarmSettings, 0xFF == etCo2AlarmLow ? null
                    : (float) etCo2AlarmLow, 0xFF == etCo2AlarmHigh ? null : (float) spo2AlarmLow, rosetta.MDC_AWAY_CO2_ET.VALUE);

            DemoCapnostream20.this.pulserateAlarmSettings = alarmSettingsSample(DemoCapnostream20.this.pulserateAlarmSettings,
                    0xFF == pulseAlarmLow ? null : (float) pulseAlarmLow, 0xFF == pulseAlarmHigh ? null : (float) pulseAlarmHigh,
                    rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE);

            return true;
        }

        private final int END_OF_BREATH_BIT = FastStatus.END_OF_BREATH_INDICATION.getBit();
        @Override
        public boolean receiveCO2Wave(int messageNumber, double co2, int status) {
            reportConnected("received CO2 message");
            
            writeTechnicalAlert("Fast CO2", FastStatus.build(status, messageBuilder));
            
            if(0 != (END_OF_BREATH_BIT & status)) {
                endOfBreath = numericSample(endOfBreath, 0, ice.MDC_END_OF_BREATH.VALUE, 
                        rosetta.MDC_DIM_DIMLESS.VALUE, null);
            }

            if (0 != (0x40 & status)) {
                // filter line not connected
                // TODO should we flush a partially filled buffer first?
                DemoCapnostream20.this.co2 = sampleArraySample(DemoCapnostream20.this.co2, null, rosetta.MDC_AWAY_CO2.VALUE, 
                        units(this.currentUnits), 20, null);
                realtimeBufferCount = 0;
                return true;
            }

            realtimeBuffer[realtimeBufferCount++] = co2 / divisor(this.currentUnits);
            if (realtimeBufferCount == realtimeBuffer.length) {
                realtimeBufferCount = 0;
                DemoCapnostream20.this.co2 = sampleArraySample(DemoCapnostream20.this.co2, realtimeBuffer, rosetta.MDC_AWAY_CO2.VALUE, 
                        units(this.currentUnits), 20, null);

            }
            return true;
        }

        @Override
        public boolean receiveDeviceIdSoftwareVersion(String softwareVersion, Date softwareReleaseDate, PulseOximetry pulseOximetry, String revision,
                String serial_number) {
            deviceIdentity.serial_number = serial_number;
            writeDeviceIdentity();
            setupItemHandler.send(SetupItem.CommIntIndication, 2);

            executor.schedule(new Runnable() {
                public void run() {
                    try {
                        getDelegate().sendCommand(Command.StartRTComm);
                    } catch (IOException e) {
                        log.error("error", e);
                    }
                }
            }, 0L, TimeUnit.MILLISECONDS);
            return true;

        }

        @Override
        public boolean receiveConfigurableSetup(SetupItem fromCode, int i) {
            super.receiveConfigurableSetup(fromCode, i);
            setupItemHandler.receive(fromCode, i);
            return true;
        }
    }

    private final Logger log = LoggerFactory.getLogger(DemoCapnostream20.class);

    @Override
    protected Capnostream buildDelegate(int idx, InputStream in, OutputStream out) {
        return new MyCapnostream(in, out);
    }

    private ScheduledFuture<?> linkIsActive;

    @Override
    protected void doInitCommands(int idx) throws IOException {
        super.doInitCommands(idx);
        getDelegate().sendCommand(Command.EnableComm);
    }

    @Override
    public void disconnect() {

        Capnostream capnostream = getDelegate(false);

        if (null != capnostream) {
            try {
                capnostream.sendCommand(Command.StopRTComm);
                try {
                    Thread.sleep(100L);
                } catch (InterruptedException e) {
                    log.error("Interrupted", e);
                }
                capnostream.sendCommand(Command.DisableComm);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        } else {
            log.trace(" was already null in disconnect");
        }
        super.disconnect();
    }

    @Override
    public SerialProvider getSerialProvider(int idx) {
        SerialProvider serialProvider = super.getSerialProvider(idx);
        serialProvider.setDefaultSerialSettings(115200, DataBits.Eight, Parity.None, StopBits.One);
        return serialProvider;
    }

    @Override
    protected boolean delegateReceive(int idx, Capnostream delegate) throws IOException {
        return delegate.receive();
    }

    @Override
    public void shutdown() {
        super.shutdown();
        if (null != linkIsActive) {
            linkIsActive.cancel(false);
            linkIsActive = null;
        }

    }
}
