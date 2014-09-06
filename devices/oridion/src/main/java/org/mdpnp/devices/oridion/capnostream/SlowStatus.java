package org.mdpnp.devices.oridion.capnostream;

public enum SlowStatus implements Bits{
    PATIENT_TYPE(0x01, "Neonatal", "Adult"),
    TEMP_ALARM_SILENCE(0x02, "Temporary alarm silence", null),
    ALL_ALARMS_SILENCED(0x04, "All alarms are silenced", null),
    HIGH_PRIORITY_ACTIVE_AUDIBLE(0x08, "High priority alarm is active and audible", null),
    LOW_PRIORITY_ACTIVE_AUDIBLE(0x10, "Low priority alarm is active and audible", null),
    ADVISORY_ACTIVE_AUDIBLE(0x20, "Advisory is active and audible", null),
    PULSE_BEEPS_SILENCED(0x40, "Pulse beeps are silenced", null),
    SPO2_MANUFACTURER(0x80, "Masimo Pulse Ox", "Nellcor Pulse Ox");
    
    
    private final int bit;
    private final String on, off;
    SlowStatus(int bit, String on, String off) {
        this.bit = bit;
        this.on = on;
        this.off = off;
    }
    
    @Override
    public String off() {
        return off;
    }
    
    @Override
    public String on() {
        return on;
    }
    
    @Override
    public int getBit() {
        return bit;
    }
    
    public static String build(int status, StringBuilder builder) {
        return Capnostream.build(values(), status, builder);
    }
}