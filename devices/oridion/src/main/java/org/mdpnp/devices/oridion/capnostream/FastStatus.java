package org.mdpnp.devices.oridion.capnostream;


public enum FastStatus implements Bits {
    INVALID_CO2_VALUE(0x01, "Invalid CO2 value", null),
    INITIALIZATION(0x02, "Initialization", null),
    OCCLUSION_IN_GAS_INPUT_LINE(0x04, "Occlusion in gas input line", null),
    // TODO End of breath indication goes in a different place than as an alert, but where?
    END_OF_BREATH_INDICATION(0x08, null, null),
    SFM_IN_PROGRESS(0x10, "SFM in progress", null),
    PURGING_IN_PROGRESS(0x20, "Purging in progress", null),
    FILTER_LINE_NOT_CONNECTED(0x40, "FilterLine not connected", null),
    CO2_MALFUNCTION(0x80, "CO2 malfunction", null);
    
    private final int bit;
    private final String on, off;
    FastStatus(int bit, String on, String off) {
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