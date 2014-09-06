package org.mdpnp.devices.oridion.capnostream;

public enum CO2ActiveAlarms implements Bits {
    NO_BREATH(0x01, "No Breath", null),
    ETCO2_HIGH(0x02, "EtCO2 High", null),
    ETCO2_LOW(0x04, "EtCO2 Low", null),
    RR_HIGH(0x08, "RR High", null),
    RR_LOW(0x10, "RR Low", null),
    FICO2_HIGH(0x20, "FiCO2 High", null);
    
    private final int bit;
    private final String on, off;
    CO2ActiveAlarms(int bit, String on, String off) {
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