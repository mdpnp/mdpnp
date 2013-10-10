package org.mdpnp.devices.philips.intellivue;

import org.mdpnp.devices.io.SplitBytesOutputStream;

public final class IntellivueByteSplitter implements SplitBytesOutputStream.Splitter {
    @Override
    public int split(byte b, byte[] b1) {
        switch(0xFF & b) {
        case 0x7D:
        case 0xC1:
        case 0xC0:
            b1[0] = 0x7D;
            b1[1] = (byte) (0x20 ^ b);
            return 2;
        default:
            b1[0] = b;
            return 1;
        }
    }
}