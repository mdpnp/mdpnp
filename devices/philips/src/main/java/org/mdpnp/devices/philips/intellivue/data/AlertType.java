package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;

public class AlertType implements Parseable, Formatable {
    private int state;

    private static final int NO_ALERT = 0x0000;
    private static final int LOW_PRI_T_AL = 0x0001;
    private static final int MED_PRI_T_AL = 0x0002;
    private static final int HI_PRI_T_AL = 0x0004;
    private static final int LOW_PRI_P_AL = 0x0100;
    private static final int MED_PRI_P_AL = 0x0200;
    private static final int HI_PRI_P_AL = 0x0400;

    @Override
    public void format(ByteBuffer bb) {
        Bits.putUnsignedShort(bb, state);
    }

    @Override
    public void parse(ByteBuffer bb) {
        state = Bits.getUnsignedShort(bb);
    }

    public boolean isNoAlert() {
        return NO_ALERT == state;
    }

    public boolean isLowPriorityTechnicalAlarm() {
        return 0 != (LOW_PRI_T_AL & state);
    }

    public boolean isMediumPriorityTechnicalAlarm() {
        return 0 != (MED_PRI_T_AL & state);
    }

    public boolean isHighPriorityTechnicalAlarm() {
        return 0 != (HI_PRI_T_AL & state);
    }

    public boolean isLowPriorityPatientAlarm() {
        return 0 != (LOW_PRI_P_AL & state);
    }

    public boolean isMediumPriorityPatientAlarm() {
        return 0 != (MED_PRI_P_AL & state);
    }

    public boolean isHighPriorityPatientAlarm() {
        return 0 != (HI_PRI_P_AL & state);
    }


    @Override
    public java.lang.String toString() {
        StringBuilder sb = new StringBuilder("[");
        if(isNoAlert()) {
            sb.append("NO_ALERT ");
        }
        if(isLowPriorityTechnicalAlarm()) {
            sb.append("LOW_PRI_T_AL ");
        }
        if(isMediumPriorityTechnicalAlarm()) {
            sb.append("MED_PRI_T_AL ");
        }
        if(isHighPriorityTechnicalAlarm()) {
            sb.append("HI_PRI_T_AL ");
        }
        if(isLowPriorityPatientAlarm()) {
            sb.append("LOW_PRI_P_AL ");
        }
        if(isMediumPriorityPatientAlarm()) {
            sb.append("MED_PRI_P_AL ");
        }
        if(isHighPriorityPatientAlarm()) {
            sb.append("HI_PRI_P_AL ");
        }
        if(sb.charAt(sb.length()-1)==' ') {
            sb.delete(sb.length()-1, sb.length());
        }
        sb.append("]");
        return sb.toString();
    }
}

