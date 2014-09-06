package org.mdpnp.devices.oridion.capnostream;

public enum SpO2ActiveAlarms implements Bits {
    PULSE_NOT_FOUND(0x01, "Pulse Not Found", null),
    SPO2_HIGH(0x02, "SpO2 High", null),
    SPO2_LOW(0x04, "SpO2 Low", null),
    PULSE_RATE_HIGH(0x08, "Pulse Rate High", null),
    PULSE_RATE_LOW(0x10, "Pulse Rate Low", null),
    SPO2_SENSOR_OFF(0x20, "SpO2 Sensor Off Patient", null),
    SPO2_SENSOR_DISCONNECTED(0x40, "SpO2 Sensor Disconnected", null);
    
    private final int bit;
    private final String on, off;
    SpO2ActiveAlarms(int bit, String on, String off) {
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