package org.mdpnp.devices.philips.intellivue;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.mdpnp.devices.io.SplitBytesOutputStream;

public class TestFCSOutputStream {
    @Test
    public void testOutput() throws Exception  {

        byte[] data = new byte[] {0x3A, 0x71};



        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SplitBytesOutputStream sbos = new SplitBytesOutputStream(baos, new IntellivueByteSplitter());
        FCSOutputStream fcs = new FCSOutputStream(sbos);

        baos.write(0xC0);
        fcs.write(data);
        fcs.writeFCS();
        baos.write(0xC1);
        fcs.close();

        byte[] expected = new byte[] {(byte) 0xc0, 0x3a, 0x71, (byte) 0x9b, 0x26, (byte) 0xc1};

        assertArrayEquals(expected, baos.toByteArray());
    }

    @Test
    public void testOutputWithEscape() throws Exception  {

        byte[] data = new byte[] {0x3A, (byte) 0x91};



        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SplitBytesOutputStream sbos = new SplitBytesOutputStream(baos, new IntellivueByteSplitter());
        FCSOutputStream fcs = new FCSOutputStream(sbos);

        baos.write(0xC0);
        fcs.write(data);
        fcs.writeFCS();
        baos.write(0xC1);
        fcs.close();

        byte[] expected = new byte[] {(byte) 0xc0, 0x3a, (byte) 0x91, (byte) 0x95, 0x7d, (byte) 0xe1, (byte) 0xc1};

        assertArrayEquals(expected, baos.toByteArray());
    }
}
