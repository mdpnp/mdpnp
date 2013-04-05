package org.mdpnp.devices.philips.intellivue.data;

public enum Dimension {
    NOM_DIM_DIMLESS,
    NOM_DIM_PERCENT,
    NOM_DIM_MILLI_M,
    NOM_DIM_MILLI_L_PER_M_SQ,
    NOM_DIM_MILLI_L,
    NOM_DIM_X_G_PER_L,
    NOM_DIM_MILLI_G_PER_L,
    NOM_DIM_NANO_G_PER_L,
    NOM_DIM_X_G_PER_DL,
    NOM_DIM_MILLI_G_PER_DL,
    NOM_DIM_PICO_G_PER_ML,
    NOM_DIM_SEC,
    NOM_DIM_MILLI_SEC,
    NOM_DIM_HZ,
    NOM_DIM_BEAT_PER_MIN,
    NOM_DIM_RESP_PER_MIN,
    NOM_DIM_X_L_PER_MIN_PER_M_SQ,
    NOM_DIM_X_L_PER_MIN,
    NOM_DIM_MILLI_L_PER_KG,
    NOM_DIM_KILO_PASCAL,
    NOM_DIM_MMHG,
    NOM_DIM_MILLI_BAR,
    NOM_DIM_NANO_WATT,
    NOM_DIM_X_DYNE_PER_SEC_PER_CM5,
    NOM_DIM_MILLI_VOLT,
    NOM_DIM_MICRO_VOLT,
    NOM_DIM_X_OHM,
    NOM_DIM_FAHR,
    NOM_DIM_MILLI_MOLE_PER_L,
    NOM_DIM_DEGC,
    NOM_DIM_DECIBEL,
;

    public static final Dimension valueOf(int x) {
        switch(x) {
        case 0x0200:
            return NOM_DIM_DIMLESS;
        case 0x0220:
            return NOM_DIM_PERCENT;
        case 0x0512:
            return NOM_DIM_MILLI_M;
        case 0x0592:
            return NOM_DIM_MILLI_L_PER_M_SQ;
        case 0x0652:
            return NOM_DIM_MILLI_L;
        case 0x0800:
            return NOM_DIM_X_G_PER_L;
        case 0x0812:
            return NOM_DIM_MILLI_G_PER_L;
        case 0x0814:
            return NOM_DIM_NANO_G_PER_L;
        case 0x0840:
            return NOM_DIM_X_G_PER_DL;
        case 0x0852:
            return NOM_DIM_MILLI_G_PER_DL;
        case 0x0875:
            return NOM_DIM_PICO_G_PER_ML;
        case 0x0880:
            return NOM_DIM_SEC;
        case 0x0892:
            return NOM_DIM_MILLI_SEC;
        case 0x09C0:
            return NOM_DIM_HZ;
        case 0x0AA0:
            return NOM_DIM_BEAT_PER_MIN;
        case 0x0AE0:
            return NOM_DIM_RESP_PER_MIN;
        case 0x0B20:
            return NOM_DIM_X_L_PER_MIN_PER_M_SQ;
        case 0x0C00:
            return NOM_DIM_X_L_PER_MIN;
        case 0x0C72:
            return NOM_DIM_MILLI_L_PER_KG;
        case 0x0F03:
            return NOM_DIM_KILO_PASCAL;
        case 0x0F20:
            return NOM_DIM_MMHG;
        case 0x0F72:
            return NOM_DIM_MILLI_BAR;
        case 0x0FD4:
            return NOM_DIM_NANO_WATT;
        case 0x1020:
            return NOM_DIM_X_DYNE_PER_SEC_PER_CM5;
        case 0x10B2:
            return NOM_DIM_MILLI_VOLT;
        case 0x10B3:
            return NOM_DIM_MICRO_VOLT;
        case 0x10C0:
            return NOM_DIM_X_OHM;
        case 0x1140:
            return NOM_DIM_FAHR;
        case 0x1272:
            return NOM_DIM_MILLI_MOLE_PER_L;
        case 0x17A0:
            return NOM_DIM_DEGC;
        case 0x1920:
            return NOM_DIM_DECIBEL;
        default:
            throw new IllegalArgumentException("Unknown Dimension:"+x);
        }
    }
    public final int asint()  {
        switch(this) {
        case NOM_DIM_DIMLESS:
            return 0x0200;
        case NOM_DIM_PERCENT:
            return 0x0220;
        case NOM_DIM_MILLI_M:
            return 0x0512;
        case NOM_DIM_MILLI_L_PER_M_SQ:
            return 0x0592;
        case NOM_DIM_MILLI_L:
            return 0x0652;
        case NOM_DIM_X_G_PER_L:
            return 0x0800;
        case NOM_DIM_MILLI_G_PER_L:
            return 0x0812;
        case NOM_DIM_NANO_G_PER_L:
            return 0x0814;
        case NOM_DIM_X_G_PER_DL:
            return 0x0840;
        case NOM_DIM_MILLI_G_PER_DL:
            return 0x0852;
        case NOM_DIM_PICO_G_PER_ML:
            return 0x0875;
        case NOM_DIM_SEC:
            return 0x0880;
        case NOM_DIM_MILLI_SEC:
            return 0x0892;
        case NOM_DIM_HZ:
            return 0x09C0;
        case NOM_DIM_BEAT_PER_MIN:
            return 0x0AA0;
        case NOM_DIM_RESP_PER_MIN:
            return 0x0AE0;
        case NOM_DIM_X_L_PER_MIN_PER_M_SQ:
            return 0x0B20;
        case NOM_DIM_X_L_PER_MIN:
            return 0x0C00;
        case NOM_DIM_MILLI_L_PER_KG:
            return 0x0C72;
        case NOM_DIM_KILO_PASCAL:
            return 0x0F03;
        case NOM_DIM_MMHG:
            return 0x0F20;
        case NOM_DIM_MILLI_BAR:
            return 0x0F72;
        case NOM_DIM_NANO_WATT:
            return 0x0FD4;
        case NOM_DIM_X_DYNE_PER_SEC_PER_CM5:
            return 0x1020;
        case NOM_DIM_MILLI_VOLT:
            return 0x10B2;
        case NOM_DIM_MICRO_VOLT:
            return 0x10B3;
        case NOM_DIM_X_OHM:
            return 0x10C0;
        case NOM_DIM_FAHR:
            return 0x1140;
        case NOM_DIM_MILLI_MOLE_PER_L:
            return 0x1272;
        case NOM_DIM_DEGC:
            return 0x17A0;
        case NOM_DIM_DECIBEL:
            return 0x1920;
        default:
            throw new IllegalArgumentException("Unknown int:"+this);
        }
    }
}
