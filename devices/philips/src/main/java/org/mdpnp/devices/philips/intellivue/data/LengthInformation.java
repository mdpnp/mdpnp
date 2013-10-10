package org.mdpnp.devices.philips.intellivue.data;

import java.nio.ByteBuffer;

import org.mdpnp.devices.io.util.HexUtil;
import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;
import org.mdpnp.devices.philips.intellivue.util.Util;

public class LengthInformation implements Parseable, Formatable {
    private int length;

    private final byte[] prefix;

    public LengthInformation() {
        this(new byte[0]);
    }

    public LengthInformation(byte[] prefix) {
        this.prefix = prefix;
    }

    @Override
    public void parse(ByteBuffer bb) {
        if(HexUtil.startsWith(bb, prefix)) {
            bb.position(bb.position()+(null==prefix?0:prefix.length));
        } else {
            length = 0;
//			return;
        }

        short first = (short)(0x00FF & bb.get());
        if(0xFF == first) {
            length = 0xFFFF & bb.getShort();
        } else {
            length = first;
        }
    }

    public int getLength() {
        return length;
    }

    @Override
    public void format(ByteBuffer bb) {
        if(length > 0) {
            bb.put(prefix);
            if(length < 255) {
                bb.put( (byte) (0xFF & length));
            } else {
                bb.put((byte)0xFF);
                bb.putShort((short)(0xFFFF&length));
            }
        }
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getByteCount() {
        if(length > 0) {
            if(length < 255) {
                return 1 + prefix.length;
            } else {
                return 3 + prefix.length;
            }
        } else {
            return 0;
        }
    }
    @Override
    public java.lang.String toString() {
        return Integer.toString(length);
    }

}
