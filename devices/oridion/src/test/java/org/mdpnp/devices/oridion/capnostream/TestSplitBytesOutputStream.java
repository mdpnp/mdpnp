package org.mdpnp.devices.oridion.capnostream;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSplitBytesOutputStream {
    @Before
    public void setUp() {
        TEST_DATA = new byte[] {0x44, (byte) 0x85, 0x55, (byte) 0x80, 0x00, 0x77};
        EXPECTED_DATA = new byte[] {(byte) 0x85, 0x44, (byte) 0x80, 0x05, 0x55, (byte) 0x80, 0x00, 0x00, 0x77, (byte) 0x85, 0x00};
    }
    @After
    public void tearDown() {
        TEST_DATA = null;
        EXPECTED_DATA = null;
    }

    private byte[] TEST_DATA, EXPECTED_DATA;



    @Test
    public void testSingleByteWrite() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SplitBytesOutputStream os = new SplitBytesOutputStream(baos);
        os.writeHeader();
        for(int i = 0; i < TEST_DATA.length; i++) {
            os.write(0xFF&TEST_DATA[i]);
        }
        os.writeHeader();
        os.write(0x00);
        os.close();
        assertArrayEquals(EXPECTED_DATA, baos.toByteArray());
    }

    @Test
    public void testWriteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SplitBytesOutputStream os = new SplitBytesOutputStream(baos);
        os.writeHeader();
        os.write(TEST_DATA);
        os.writeHeader();
        os.write(0x00);
        os.close();
        assertArrayEquals(EXPECTED_DATA, baos.toByteArray());
    }

    @Test
    public void testBothSingleByte() throws IOException {
        PipedInputStream pis = new PipedInputStream(EXPECTED_DATA.length);
        InputStream is = new MergeBytesInputStream(pis);
        SplitBytesOutputStream os = new SplitBytesOutputStream(new PipedOutputStream(pis));
        os.writeHeader();
        os.write(TEST_DATA);
        os.writeHeader();
        os.write(0x00);
        os.close();
        assertEquals(MergeBytesInputStream.HEADER, is.read());
        for(int i = 0; i < TEST_DATA.length; i++) {
            assertEquals(0xFF&TEST_DATA[i], is.read());
        }
        assertEquals(MergeBytesInputStream.HEADER, is.read());
        assertEquals(0x00, is.read());
        is.close();
    }


    @Test
    public void testBothArray() throws IOException {
        PipedInputStream pis = new PipedInputStream(EXPECTED_DATA.length);
        InputStream is = new MergeBytesInputStream(pis);
        SplitBytesOutputStream os = new SplitBytesOutputStream(new PipedOutputStream(pis));
        os.writeHeader();
        os.write(TEST_DATA);
        os.writeHeader();
        os.write(0x00);
        os.close();
        assertEquals(MergeBytesInputStream.HEADER, is.read());
        byte[] data = new byte[EXPECTED_DATA.length];
        assertEquals(6, is.read(data, 0, 8));
        assertArrayEquals(TEST_DATA, Arrays.copyOfRange(data, 0, 6));
        assertEquals(MergeBytesInputStream.HEADER, is.read());
        assertEquals(0x00, is.read());
        is.close();
    }
}
