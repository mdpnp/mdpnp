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
package org.mdpnp.devices.nellcor.pulseox;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.mdpnp.devices.serial.AbstractSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.rtiapi.data.EventLoop;

import com.rti.dds.infrastructure.Time_t;

/**
 * @author Jeff Plourde
 *
 */
public class DemoN595 extends AbstractSerialDevice {
    protected InstanceHolder<ice.Numeric> pulse, spo2, pulseAmplitude;
    protected InstanceHolder<ice.AlarmSettings> pulseAlarmSettings, spo2AlarmSettings;

    private class MyNellcorN595 extends NellcorN595 {
        public MyNellcorN595() throws NoSuchFieldException, SecurityException, IOException {
            super();
        }

        private final Time_t sampleTime = new Time_t(0, 0);
        private final Set<Status> statusSet = new HashSet<Status>();
        private final StringBuilder builder = new StringBuilder();

        @Override
        public void firePulseOximeter() {
            long tm = getTimestamp().getTime();
            sampleTime.sec = (int) (tm / 1000L);
            sampleTime.nanosec = (int) (tm % 1000L * 1000000L);
            pulse = numericSample(pulse, getHeartRate(), rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, 
                    rosetta.MDC_DIM_BEAT_PER_MIN.VALUE, sampleTime);
            spo2 = numericSample(spo2, getSpO2(), rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, 
                    rosetta.MDC_DIM_PERCENT.VALUE, sampleTime);
            pulseAmplitude = numericSample(pulseAmplitude, getPulseAmplitude(), "NELLCOR_PULSE_AMPLITUDE", 
                    rosetta.MDC_DIM_DIMLESS.VALUE, sampleTime);
            markOldPatientAlertInstances();
            statusSet.clear();
            for(Status s : getStatus()) {
                if(null != s) {
                    statusSet.add(s);
                }
            }
            
            if(statusSet.contains(Status.PulseRateLowerLimitAlarm)) {
                statusSet.remove(Status.PulseRateLowerLimitAlarm);
                writePatientAlert("PR", Status.PulseRateLowerLimitAlarm.name());
            }
            if(statusSet.contains(Status.PulseRateUpperLimitAlarm)) {
                statusSet.remove(Status.PulseRateUpperLimitAlarm);
                writePatientAlert("PR", Status.PulseRateUpperLimitAlarm.name());
            }
            if(statusSet.contains(Status.SaturationLowerLimitAlarm)) {
                statusSet.remove(Status.SaturationLowerLimitAlarm);
                writePatientAlert("SPO2", Status.SaturationLowerLimitAlarm.name());
            }
            if(statusSet.contains(Status.SaturationUpperLimitAlarm)) {
                statusSet.remove(Status.SaturationUpperLimitAlarm);
                writePatientAlert("SPO2", Status.SaturationUpperLimitAlarm.name());
            }
            if(!statusSet.isEmpty()) {
                builder.delete(0, builder.length());
                for(Status s : statusSet) {
                    builder.append(s.name()+", ");
                }
                builder.delete(builder.length()-2,builder.length());
                writeDeviceAlert(builder.toString());
            } else {
                writeDeviceAlert("");
            }
                
            clearOldPatientAlertInstances();
        }

        @Override
        public void fireAlarmPulseOximeter() {
            pulseAlarmSettings = alarmSettingsSample(pulseAlarmSettings, getPRLower(), getPRUpper(), rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE);
            spo2AlarmSettings = alarmSettingsSample(spo2AlarmSettings, getSpO2Lower(), getSpO2Upper(), rosetta.MDC_PULS_OXIM_SAT_O2.VALUE);
        }
        

        @Override
        public void fireDevice() {
            reportConnected("message received");
            writeDeviceIdentity();

        }

        @Override
        protected void setName(String name) {
            deviceIdentity.model = name;
        }

        @Override
        protected void setGuid(String guid) {
            deviceIdentity.serial_number = guid;
        }
    }

    private final MyNellcorN595 fieldDelegate;
    private OutputStream outputStream;

    @Override
    protected void process(int idx, InputStream inputStream, OutputStream outputStream) throws IOException {
        this.outputStream = outputStream;
        fieldDelegate.setInputStream(inputStream);
        fieldDelegate.run();
    }

    private static final byte[] enterInteractiveMode = new byte[] { 0x03, 0x03 };
    private static final byte[] dumpInstrumentInfo = new byte[] { 0x31, 0x0D, 0x0A };
    private static final byte[] exitInteractiveMode = new byte[] { 0x30, 0x0D, 0x0A };

    @Override
    protected void doInitCommands(int idx) throws IOException {
        OutputStream outputStream = this.outputStream;
        if (null != outputStream) {
            outputStream.write(enterInteractiveMode);
            outputStream.flush();
            outputStream.write(dumpInstrumentInfo);
            outputStream.flush();
            outputStream.write(exitInteractiveMode);
            outputStream.flush();
        }
    }

    public DemoN595(int domainId, EventLoop eventLoop) throws NoSuchFieldException, SecurityException, IOException {
        super(domainId, eventLoop);
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        deviceIdentity.manufacturer = NellcorN595.MANUFACTURER_NAME;
        deviceIdentity.model = NellcorN595.MODEL_NAME;
        writeDeviceIdentity();

        this.fieldDelegate = new MyNellcorN595();
    }

    @Override
    protected long getMaximumQuietTime(int idx) {
        return 2200L;
    }

    @Override
    protected long getConnectInterval(int idx) {
        return 4000L;
    }

    @Override
    protected long getNegotiateInterval(int idx) {
        return 2000L;
    }

    @Override
    public SerialProvider getSerialProvider(int idx) {
        SerialProvider serialProvider = super.getSerialProvider(idx);
        serialProvider.setDefaultSerialSettings(9600, DataBits.Eight, Parity.None, StopBits.One);
        return serialProvider;
    }

    @Override
    protected String iconResourceName() {
        return "n595.png";
    }

    @Override
    protected void unregisterAllNumericInstances() {
        super.unregisterAllNumericInstances();
        this.pulse = null;
        this.spo2 = null;
        this.pulseAmplitude = null;
    }
    
    @Override
    protected void unregisterAllAlarmSettingsInstances() {
        super.unregisterAllAlarmSettingsInstances();
        this.spo2AlarmSettings = null;
        this.pulseAlarmSettings = null;
    }
}
