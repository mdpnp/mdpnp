package org.mdpnp.devices.nellcor.pulseox;

import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mdpnp.devices.io.ASCIIFieldDelegate;

public class NellcorN595 extends ASCIIFieldDelegate {
    public NellcorN595() throws NoSuchFieldException, SecurityException, IOException {
        super(NellcorN595.class.getResource("nellcor-n595.spec"));
    }

    private Integer heartRate, spo2, pulseAmplitude;
    private Date lastPoint;
    private Status[] status = new Status[10];

    private String version, crc, spO2Units, pRUnits;
    private Integer spO2Lower, spO2Upper, pRLower, pRUpper, satS;
    private LimitsType limitsType;
    private SpO2RespMode spO2RespMode;


    public Date getTimestamp() {
        return lastPoint;
    }

    public Integer getPulseAmplitude() {
        return pulseAmplitude;
    }

    public String getCRC() {
        return crc;
    }

    public Integer getPRLower() {
        return pRLower;
    }

    public String getPRUnits() {
        return pRUnits;
    }

    public Integer getPRUpper() {
        return pRUpper;
    }

    public Integer getSpO2Lower() {
        return spO2Lower;
    }

    public String getSpO2Units() {
        return spO2Units;
    }

    public Integer getSpO2Upper() {
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
        AlarmOff,
        AlarmSilence,
        LowBattery,
        LossOfPulseInterference,
        LossOfPulse,
        InterferenceDetected,
        PulseRateUpperLimitAlarm,
        PulseRateLowerLimitAlarm,
        PulseSearch,
        SaturationUpperLimitAlarm,
        SaturationLowerLimitAlarm,
        SensorDisconnect,
        SensorOff
    }
    enum LimitsType {
        Adult,
        NeoNatal
    }

    enum SpO2RespMode {
        Normal,
        Fast
    }
    private static final Pattern number = Pattern.compile("^(\\d+)(\\*?)$");
    @SuppressWarnings("unused")
    private static final String filterStar(String val) {
        if(null == val) {
            return null;
        }
        Matcher m1 = number.matcher(val);
        if(m1.matches()) {
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
        SensorOff,
        PoorSignal,

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
