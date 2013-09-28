package org.mdpnp.devices.oridion.capnostream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class TestMergeBytesInputStream {
    @Before
    public void setUp() {
        TEST_DATA = new byte[] {0x44, (byte) 0x85, 0x55, (byte) 0x80, 0x05, 0x00, (byte) 0x80, 0x00, 0x77};
    }
    @After
    public void tearDown() {
        TEST_DATA = null;
    }

    private byte[] TEST_DATA;


    @Test
    public void testSingleByteRead() throws IOException {
        InputStream is = new MergeBytesInputStream(new ByteArrayInputStream(TEST_DATA));
        assertTrue(0x44 == is.read());
        assertTrue(MergeBytesInputStream.HEADER == is.read());
        assertTrue(0x55==is.read());
        assertTrue(0x85==is.read());
        assertTrue(0x00==is.read());
        assertTrue(0x80==is.read());
        is.close();
    }

    @Test
    public void testReadArray() throws IOException {
        InputStream is = new MergeBytesInputStream(new ByteArrayInputStream(TEST_DATA));
        byte out[] = new byte[50];
        int n = is.read(out, 0, 2);
        // it is awkward but a HEADER anywhere in the array returns HEADER
        // the caller should call read() when a HEADER is expected
        assertTrue(MergeBytesInputStream.HEADER == n);
        // Reads an internal 0x80->0x85 sequence and a 0x80 at the end so the
        // MergeBytesInputStream will read another byte to complete the sequence
        n = is.read(out, 0, 5);
        assertEquals(4, n);
        byte[] expected = new byte[] {0x55, (byte) 0x85, 0x00, (byte) 0x80};
        assertArrayEquals(expected, Arrays.copyOfRange(out, 0, 4));
        n = is.read(out, 0, 1);
        assertEquals(1, n);
        assertArrayEquals(new byte[] {0x77}, Arrays.copyOfRange(out, 0, 1));
        is.close();

    }
}
