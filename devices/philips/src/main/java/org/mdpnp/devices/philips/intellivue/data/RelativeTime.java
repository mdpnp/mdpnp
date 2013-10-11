package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.Bits;

public class RelativeTime implements Value {
    private long relativeTime;
    private static final long RESOLUTION_MICROSECONDS = 125L;

    public RelativeTime() {
        this(60000L);
    }

    public RelativeTime(long milliseconds) {
        fromMilliseconds(milliseconds);
    }

    @Override
    public void parse(ByteBuffer bb) {
        relativeTime = Bits.getUnsignedInt(bb);
    }

    public long getRelativeTime() {
        return relativeTime;
    }

    public void setRelativeTime(long relativeTime) {
        this.relativeTime = relativeTime;
    }
    @Override
    public java.lang.String toString() {
        return Long.toString(relativeTime) + " (" + Long.toString(toMilliseconds()) + "ms)";
    }

    @Override
    public void format(ByteBuffer bb) {
        Bits.putUnsignedInt(bb, relativeTime);
    }

    public long toMicroseconds() {
        return relativeTime * RESOLUTION_MICROSECONDS;
    }
    public void fromMicroseconds(long microseconds) {
        this.relativeTime = microseconds / RESOLUTION_MICROSECONDS;
    }
    public long toMilliseconds() {
        return toMicroseconds() / 1000L;
    }
    public void fromMilliseconds(long milliseconds) {
        fromMicroseconds(1000L * milliseconds);
    }

}
