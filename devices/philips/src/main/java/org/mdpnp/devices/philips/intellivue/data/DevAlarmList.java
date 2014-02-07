package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.philips.intellivue.util.Util;

public class DevAlarmList implements Value {
    private final List<DevAlarmEntry> value = new ArrayList<DevAlarmEntry>();

    @Override
    public void format(ByteBuffer bb) {
        Util.PrefixLengthShort.write(bb, value);
    }

    @Override
    public void parse(ByteBuffer bb) {
        Util.PrefixLengthShort.read(bb, value, true, DevAlarmEntry.class);
    }

    @Override
    public java.lang.String toString() {
        return value.toString();
    }

    public List<DevAlarmEntry> getValue() {
        return value;
    }

}
