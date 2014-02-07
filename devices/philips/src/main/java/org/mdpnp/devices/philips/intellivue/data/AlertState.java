package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;

public class AlertState implements Parseable, Formatable {
    private int state;

    private static final int AL_INHIBITED = 0x8000;
    private static final int AL_SUSPENDED = 0x4000;
    private static final int AL_LATCHED = 0x2000;
    private static final int AL_SILENCED_RESET = 0x1000;
    private static final int AL_DEV_IN_TEST_MODE = 0x0400;
    private static final int AL_DEV_IN_STANDBY = 0x0200;
    private static final int AL_DEV_IN_DEMO_MODE = 0x0100;
    private static final int AL_NEW_ALERT = 0x0008;

    @Override
    public void format(ByteBuffer bb) {
        Bits.putUnsignedShort(bb, state);
    }

    @Override
    public void parse(ByteBuffer bb) {
        state = Bits.getUnsignedShort(bb);
    }

    public boolean isAlInhibited() {
        return 0 != (AL_INHIBITED & state);
    }

    public boolean isAlSuspended() {
        return 0 != (AL_SUSPENDED & state);
    }

    public boolean isAlLatched() {
        return 0 != (AL_LATCHED & state);
    }

    public boolean isAlSilencedReset() {
        return 0 != (AL_SILENCED_RESET & state);
    }

    public boolean isAlDevInTestMode() {
        return 0 != (AL_DEV_IN_TEST_MODE & state);
    }

    public boolean isAlDevInStandby() {
        return 0 != (AL_DEV_IN_STANDBY & state);
    }

    public boolean isAlDevInDemoMode() {
        return 0 != (AL_DEV_IN_DEMO_MODE & state);
    }

    public boolean isAlNewAlert() {
        return 0 != (AL_NEW_ALERT & state);
    }

    @Override
    public java.lang.String toString() {
        StringBuilder sb = new StringBuilder("[");
        if(isAlInhibited()) {
            sb.append("INHIBITED ");
        }
        if(isAlSuspended()) {
            sb.append("SUSPENDED ");
        }
        if(isAlLatched()) {
            sb.append("LATCHED ");
        }
        if(isAlSilencedReset()) {
            sb.append("SILENCED_RESET ");
        }
        if(isAlDevInTestMode()) {
            sb.append("AL_DEV_IN_TEST_MODE ");
        }
        if(isAlDevInStandby()) {
            sb.append("AL_DEV_IN_STANDBY ");
        }
        if(isAlDevInDemoMode()) {
            sb.append("AL_DEV_IN_DEMO_MODE ");
        }
        if(isAlNewAlert()) {
            sb.append("NEW_ALERT ");
        }
        if(sb.charAt(sb.length()-1)==' ') {
            sb.delete(sb.length()-1, sb.length());
        }
        sb.append("]");
        return sb.toString();
    }
}

