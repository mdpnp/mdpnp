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
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.io.ASCIIFieldDelegate;

/**
 * @author Jeff Plourde
 *
 */
public class NellcorN595 extends ASCIIFieldDelegate implements DeviceClock {
    public NellcorN595() throws NoSuchFieldException, SecurityException, IOException {
        super(NellcorN595.class.getResource("nellcor-n595.spec"));
    }

    private Integer heartRate, spo2, pulseAmplitude, satS;
    private Date lastPoint = new Date(0);
    private Status[] status = new Status[10];

    private String version, crc, spO2Units, pRUnits;
    private Float spO2Lower, spO2Upper, pRLower, pRUpper;
    private LimitsType limitsType;
    private SpO2RespMode spO2RespMode;

    @Override
    public Reading instant() {
        return new ReadingImpl(lastPoint.getTime());
    }

    public Integer getPulseAmplitude() {
        return pulseAmplitude;
    }

    public String getCRC() {
        return crc;
    }

    public Float getPRLower() {
        return pRLower;
    }

    public String getPRUnits() {
        return pRUnits;
    }

    public Float getPRUpper() {
        return pRUpper;
    }

    public Float getSpO2Lower() {
        return spO2Lower;
    }

    public String getSpO2Units() {
        return spO2Units;
    }

    public Float getSpO2Upper() {
        return spO2Upper;
    }

    public String getVersion() {
        return version;
    }

    public Status[] getStatus() {
        return status;
    }

    public LimitsType getLimitsType() {
        return limitsType;
    }

    public Integer getSatS() {
        return satS;
    }

    public SpO2RespMode getSpO2RespMode() {
        return spO2RespMode;
    }

    enum Status {
        AlarmOff, AlarmSilence, LowBattery, LossOfPulseInterference, LossOfPulse, InterferenceDetected, PulseRateUpperLimitAlarm, PulseRateLowerLimitAlarm, PulseSearch, SaturationUpperLimitAlarm, SaturationLowerLimitAlarm, SensorDisconnect, SensorOff
    }

    enum LimitsType {
        Adult, NeoNatal
    }

    enum SpO2RespMode {
        Normal, Fast
    }

    private static final Pattern number = Pattern.compile("^(\\d+)(\\*?)$");

    @SuppressWarnings("unused")
    private static final String filterStar(String val) {
        if (null == val) {
            return null;
        }
        Matcher m1 = number.matcher(val);
        if (m1.matches()) {
            return m1.group(1);
        } else {
            return null;
        }
    }

    public static final String MANUFACTURER_NAME = "Nellcor";
    public static final String MODEL_NAME = "";

    @SuppressWarnings("unused")
    private static final String applyRoot(String val) {
        return MODEL_NAME + " " + val;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public Integer getSpO2() {
        return spo2;
    }

    public Boolean isOutOfTrack() {
        return "MO".equals(status);
    }

    public Boolean isSensorDetached() {
        return "SO".equals(status);
    }

    public enum NellcorStatus {
        SensorOff, PoorSignal,

    }

    public void firePulseOximeter() {
    }

    public void fireAlarmPulseOximeter() {
    }

    public void fireDevice() {
    }

    protected void setName(String name) {
    }

    protected void setGuid(String guid) {
    }
}
