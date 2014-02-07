package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;

public class AlertFlags implements Formatable, Parseable {
    private int state;

    private static final int BEDSIDE_AUDIBLE = 0x4000;
    private static final int CENTRAL_AUDIBLE = 0x2000;
    private static final int VISUAL_LATCHING = 0x1000;
    private static final int AUDIBLE_LATCHING = 0x0800;
    private static final int SHORT_YELLOW_EXTENSION = 0x0400;
    private static final int DERIVED = 0x0200;

    @Override
    public void format(ByteBuffer bb) {
        Bits.putUnsignedShort(bb, state);
    }

    @Override
    public void parse(ByteBuffer bb) {
        state = Bits.getUnsignedShort(bb);
    }

    public boolean isBedsideAudible() {
        return 0 != (BEDSIDE_AUDIBLE & state);
    }

    public boolean isCentralAudible() {
        return 0 != (CENTRAL_AUDIBLE & state);
    }

    public boolean isVisualLatching() {
        return 0 != (VISUAL_LATCHING & state);
    }

    public boolean isAudibleLatching() {
        return 0 != (AUDIBLE_LATCHING & state);
    }

    public boolean isShortYellowExtension() {
        return 0 != (SHORT_YELLOW_EXTENSION & state);
    }

    public boolean isDerived() {
        return 0 != (DERIVED & state);
    }

    @Override
    public java.lang.String toString() {
        StringBuilder sb = new StringBuilder("[");
        if(isBedsideAudible()) {
            sb.append("BEDSIDE_AUDIBLE ");
        }
        if(isCentralAudible()) {
            sb.append("CENTRAL_AUDIBLE ");
        }
        if(isVisualLatching()) {
            sb.append("VISUAL_LATCHING ");
        }
        if(isAudibleLatching()) {
            sb.append("AUDIBLE_LATCHING ");
        }
        if(isShortYellowExtension()) {
            sb.append("SHORT_YELLOW_EXTENSION ");
        }
        if(isDerived()) {
            sb.append("DERIVED ");
        }
        if(sb.charAt(sb.length()-1)==' ') {
            sb.delete(sb.length()-1, sb.length());
        }
        sb.append("]");
        return sb.toString();
    }
}
