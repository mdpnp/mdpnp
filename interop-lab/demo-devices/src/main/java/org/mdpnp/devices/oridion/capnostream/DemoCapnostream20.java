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

import ice.AlarmLimit;
import ice.LocalAlarmLimitObjective;
import ice.NumericSQI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.DeviceClock;
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

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

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


    protected InstanceHolder<ice.AlarmLimit> spo2AlarmLimitLow, pulserateAlarmLimitLow, rrAlarmLimitLow, etco2AlarmLimitLow,
                                             spo2AlarmLimitHigh, pulserateAlarmLimitHigh, rrAlarmLimitHigh, etco2AlarmLimitHigh;
    

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
    protected InstanceHolder<AlarmLimit> alarmLimitSample(InstanceHolder<AlarmLimit> holder, String unit_id, Float newValue, String metric_id, ice.LimitType limit_type)  {
    	if (newValue != null && limit_type==ice.LimitType.low_limit) {
            currentLow.put(metric_id, (int) (float) newValue);
        }
        if (newValue != null && limit_type==ice.LimitType.high_limit) {
            currentHigh.put(metric_id, (int) (float) newValue);
        }
        return super.alarmLimitSample(holder, unit_id, newValue, metric_id, limit_type);
    }

    public DemoCapnostream20(final Subscriber subscriber, final Publisher publisher, EventLoop eventLoop) {
        super(subscriber, publisher, eventLoop, Capnostream.class);
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
            if(!ice.ConnectionState.Connected.equals(getState())) {
                // TODO settings need to be re-synchronized every time we re-enter
                // the Connected state
                return;
            }
            
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
    public void unsetAlarmLimit(String metricId, ice.LimitType limit_type) {
        super.unsetAlarmLimit(metricId, limit_type);
        SetupItem si = null;
        
        switch(limit_type.ordinal()) {
        case ice.LimitType._high_limit:
            si = upperAlarm(metricId);
            if(null != si) {
                log.warn("Resetting " + metricId + " high limit to " + priorSafeHigh.get(metricId));
                setupItemHandler.send(si, priorSafeHigh.get(metricId));
            } else {
                // Do not deacknowledge this unknown alarm limit setting
                return;
            }
            break;
        case ice.LimitType._low_limit:
            si = lowerAlarm(metricId);
            if(null != si) {
                log.warn("Resetting " + metricId + " low limit to " + priorSafeLow.get(metricId));
                setupItemHandler.send(si, priorSafeLow.get(metricId));
            } else {
             // Do not deacknowledge this unknown alarm limit setting
                return;
            }
            break;
        default:
            // explicitly returning so that we do NOT acknowledge the global setting with a local objective
            return;
        }

        // TODO Does this really belong here or in a parent?
        localAlarmLimit.put(metricId +"_"+limit_type,
                alarmLimitObjectiveSample(localAlarmLimit.get(metricId+"-"+limit_type), null, "", metricId, limit_type));
    }
    

    private Map<String, InstanceHolder<ice.LocalAlarmLimitObjective>> localAlarmLimit = new HashMap<String, InstanceHolder<ice.LocalAlarmLimitObjective>>();

    @Override
    protected void unregisterAlarmLimitObjectiveInstance(InstanceHolder<LocalAlarmLimitObjective> holder) {
        localAlarmLimit.clear();
        super.unregisterAlarmLimitObjectiveInstance(holder);
    }

    @Override
    public void setAlarmLimit(ice.GlobalAlarmLimitObjective obj){
        super.setAlarmLimit(obj);
        SetupItem si = null;
        
        switch(obj.limit_type.ordinal()) {
        case ice.LimitType._high_limit:
            si = upperAlarm(obj.metric_id);
            if(null != si) {
                priorSafeHigh.put(obj.metric_id, currentHigh.get(obj.metric_id));
                setupItemHandler.send(si, (int) obj.value);
            } else {
                log.debug("Ignoring unsettable global upper alarm objective for " + obj.metric_id);
                // explicitly returning so that we do NOT acknowledge the global setting with a local objective
                return;                
            }
            break;
        case ice.LimitType._low_limit:
            si = lowerAlarm(obj.metric_id);
            if(null != si) {
                priorSafeLow.put(obj.metric_id, currentLow.get(obj.metric_id));
                setupItemHandler.send(si, (int) obj.value);
            } else {
                log.debug("Ignoring unsettable global lower alarm objective for " + obj.metric_id);
                // explicitly returning so that we do NOT acknowledge the global setting with a local objective
                return;
            }
            break;
        default:
            // explicitly returning so that we do NOT acknowledge the global setting with a local objective
            return;
        }

        // TODO Does this really belong here or in a parent?
        localAlarmLimit.put(obj.metric_id +"_"+obj.limit_type,
                alarmLimitObjectiveSample(localAlarmLimit.get(obj.metric_id+"-"+obj.limit_type), obj.value, obj.unit_identifier, obj.metric_id, obj.limit_type));
    }

    public void init() {
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

    public class CapnostreamExt extends Capnostream {
        public CapnostreamExt(DeviceClock referenceClock, InputStream in, OutputStream out) {
            super(referenceClock, in, out);
        }

        private final StringBuilder messageBuilder = new StringBuilder();
        
        private CO2Units currentUnits = null;
        
        @Override
        public boolean receiveNumerics(DeviceClock.Reading sampleTime, int etCO2, int FiCO2, int respiratoryRate, int spo2, int pulserate, int slowStatus,
                int co2ActiveAlarms, int spO2ActiveAlarms, int noBreathPeriodSeconds, int etCo2AlarmHigh, int etCo2AlarmLow, int rrAlarmHigh,
                int rrAlarmLow, int fico2AlarmHigh, int spo2AlarmHigh, int spo2AlarmLow, int pulseAlarmHigh, int pulseAlarmLow, CO2Units units,
                int extendedCO2Status) {
            
            writePatientAlert("CO2", CO2ActiveAlarms.build(co2ActiveAlarms, messageBuilder));
            writePatientAlert("SPO2", SpO2ActiveAlarms.build(spO2ActiveAlarms, messageBuilder));
            writeDeviceAlert(SlowStatus.build(slowStatus, messageBuilder));
            writeTechnicalAlert("CO2", ExtendedCO2Status.build(extendedCO2Status, messageBuilder));


            DemoCapnostream20.this.spo2 = numericSample(DemoCapnostream20.this.spo2, 0xFF == spo2 ? null : spo2, rosetta.MDC_PULS_OXIM_SAT_O2.VALUE,
                    "", rosetta.MDC_DIM_PERCENT.VALUE,
                    sampleTime);

            rr = numericSample(rr, 0xFF == respiratoryRate ? null : respiratoryRate, rosetta.MDC_CO2_RESP_RATE.VALUE, 
                    "", rosetta.MDC_DIM_RESP_PER_MIN.VALUE, sampleTime);

            this.currentUnits = units;

            etco2 = numericSample(etco2, 0xFF == etCO2 ? null : etCO2 / divisor(units), rosetta.MDC_AWAY_CO2_ET.VALUE, "", units(units), sampleTime);

            DemoCapnostream20.this.pulserate = numericSample(DemoCapnostream20.this.pulserate, 0xFF == pulserate ? null : pulserate,
                    rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, "", rosetta.MDC_DIM_BEAT_PER_MIN.VALUE, sampleTime);
            
            
            //Shall we use rosetta.XXX.VALUE for UNITS as well as metric_ID???
            //spo2Alarm rosetta.MDC_PULS_OXIM_SAT_O2
            if(0xFF == spo2AlarmLow)
            	DemoCapnostream20.this.spo2AlarmLimitLow = alarmLimitSample(DemoCapnostream20.this.spo2AlarmLimitLow, rosetta.MDC_DIM_PERCENT.VALUE, null, rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, ice.LimitType.low_limit);
            else 
            	DemoCapnostream20.this.spo2AlarmLimitLow = alarmLimitSample(DemoCapnostream20.this.spo2AlarmLimitLow, rosetta.MDC_DIM_PERCENT.VALUE, (float) spo2AlarmLow, rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, ice.LimitType.low_limit);
            
            if(0xFF == spo2AlarmHigh)
              	DemoCapnostream20.this.spo2AlarmLimitHigh = alarmLimitSample(DemoCapnostream20.this.spo2AlarmLimitHigh, rosetta.MDC_DIM_PERCENT.VALUE, null, rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, ice.LimitType.high_limit);
            else 
            	DemoCapnostream20.this.spo2AlarmLimitHigh = alarmLimitSample(DemoCapnostream20.this.spo2AlarmLimitHigh, rosetta.MDC_DIM_PERCENT.VALUE, (float) spo2AlarmHigh, rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, ice.LimitType.high_limit);
            
            //etco2Alarm rosetta.MDC_AWAY_CO2_ET
            if(0xFF == etCo2AlarmLow)
            	DemoCapnostream20.this.etco2AlarmLimitLow = alarmLimitSample(DemoCapnostream20.this.etco2AlarmLimitLow, units(units), null, rosetta.MDC_AWAY_CO2_ET.VALUE, ice.LimitType.low_limit);
            else
            	DemoCapnostream20.this.etco2AlarmLimitLow = alarmLimitSample(DemoCapnostream20.this.etco2AlarmLimitLow, units(units), (float) etCo2AlarmLow, rosetta.MDC_AWAY_CO2_ET.VALUE, ice.LimitType.low_limit);
            
            if(0xFF == etCo2AlarmHigh)
            	DemoCapnostream20.this.etco2AlarmLimitHigh = alarmLimitSample(DemoCapnostream20.this.etco2AlarmLimitHigh, units(units), null, rosetta.MDC_AWAY_CO2_ET.VALUE, ice.LimitType.high_limit);
            else
            	DemoCapnostream20.this.etco2AlarmLimitHigh = alarmLimitSample(DemoCapnostream20.this.etco2AlarmLimitHigh, units(units), (float) etCo2AlarmHigh, rosetta.MDC_AWAY_CO2_ET.VALUE, ice.LimitType.high_limit);
            
            //pulserateAlarms rosetta.MDC_PULS_OXIM_PULS_RATE
            if(0xFF == pulseAlarmLow)
            	DemoCapnostream20.this.pulserateAlarmLimitLow = alarmLimitSample(DemoCapnostream20.this.pulserateAlarmLimitLow, rosetta.MDC_DIM_BEAT_PER_MIN.VALUE, null, rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, ice.LimitType.low_limit);
            else
            	DemoCapnostream20.this.pulserateAlarmLimitLow = alarmLimitSample(DemoCapnostream20.this.pulserateAlarmLimitLow, rosetta.MDC_DIM_BEAT_PER_MIN.VALUE, (float) pulseAlarmLow, rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, ice.LimitType.low_limit);
            
            if(0xFF == pulseAlarmHigh)
            	DemoCapnostream20.this.pulserateAlarmLimitHigh = alarmLimitSample(DemoCapnostream20.this.pulserateAlarmLimitHigh, rosetta.MDC_DIM_BEAT_PER_MIN.VALUE, null, rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, ice.LimitType.high_limit);
            else
            	DemoCapnostream20.this.pulserateAlarmLimitHigh = alarmLimitSample(DemoCapnostream20.this.pulserateAlarmLimitHigh, rosetta.MDC_DIM_BEAT_PER_MIN.VALUE, (float) pulseAlarmHigh, rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, ice.LimitType.high_limit);
            
            //rosetta.MDC_RESP_RATE
            if(0xFF == rrAlarmLow)
            	DemoCapnostream20.this.rrAlarmLimitLow = alarmLimitSample(DemoCapnostream20.this.rrAlarmLimitLow, rosetta.MDC_DIM_RESP_PER_MIN.VALUE, null, rosetta.MDC_RESP_RATE.VALUE, ice.LimitType.low_limit);
            else
            	DemoCapnostream20.this.rrAlarmLimitLow = alarmLimitSample(DemoCapnostream20.this.rrAlarmLimitLow, rosetta.MDC_DIM_RESP_PER_MIN.VALUE, (float) rrAlarmLow, rosetta.MDC_RESP_RATE.VALUE, ice.LimitType.low_limit);
            
            if(0xFF == rrAlarmHigh)
            	DemoCapnostream20.this.rrAlarmLimitHigh = alarmLimitSample(DemoCapnostream20.this.rrAlarmLimitHigh, rosetta.MDC_DIM_RESP_PER_MIN.VALUE, null, rosetta.MDC_RESP_RATE.VALUE, ice.LimitType.high_limit);
            else
            	DemoCapnostream20.this.rrAlarmLimitHigh = alarmLimitSample(DemoCapnostream20.this.rrAlarmLimitHigh, rosetta.MDC_DIM_RESP_PER_MIN.VALUE, (float) rrAlarmHigh, rosetta.MDC_RESP_RATE.VALUE, ice.LimitType.high_limit);
            
            
            return true;
        }

        private final int END_OF_BREATH_BIT = FastStatus.END_OF_BREATH_INDICATION.getBit();
        @Override
        public boolean receiveCO2Wave(DeviceClock.Reading sampleTime, int messageNumber, double co2, int status) {

            reportConnected("received CO2 message");

            writeTechnicalAlert("Fast CO2", FastStatus.build(status, messageBuilder));
            
            if(0 != (END_OF_BREATH_BIT & status)) {
                endOfBreath = numericSample(endOfBreath, 0, ice.MDC_END_OF_BREATH.VALUE, "",
                        rosetta.MDC_DIM_DIMLESS.VALUE, sampleTime);
            }

            if (0 != (0x40 & status)) {
                // filter line not connected
                // TODO should we flush a partially filled buffer first?
                DemoCapnostream20.this.co2 = sampleArraySample(DemoCapnostream20.this.co2, null,
                		 									   new NumericSQI(),
                                                               rosetta.MDC_AWAY_CO2.VALUE, "",
                                                               units(this.currentUnits), 20,
                                                               sampleTime);
                realtimeBufferCount = 0;
                return true;
            }

            realtimeBuffer[realtimeBufferCount++] = co2 / divisor(this.currentUnits);
            if (realtimeBufferCount == realtimeBuffer.length) {
                realtimeBufferCount = 0;
                DemoCapnostream20.this.co2 = sampleArraySample(DemoCapnostream20.this.co2, realtimeBuffer,
                		 									   new NumericSQI(),
                                                               rosetta.MDC_AWAY_CO2.VALUE, "",
                                                               units(this.currentUnits), 20,
                                                               sampleTime);

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
        return new CapnostreamExt(getClockProvider(), in, out);
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
