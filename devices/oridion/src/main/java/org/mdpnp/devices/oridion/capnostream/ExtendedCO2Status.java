package org.mdpnp.devices.oridion.capnostream;

public enum ExtendedCO2Status implements Bits {
    CHECK_CALIBRATION(0x01, "Check calibration", null),
    CHECK_FLOW(0x02, "Check flow", null),
    PUMP_OFF(0x04, "Pump off", null),
    BATTERY_LOW(0x80, "Battery low", null);
    
    private final int bit;
    private final String on, off;
    ExtendedCO2Status(int bit, String on, String off) {
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