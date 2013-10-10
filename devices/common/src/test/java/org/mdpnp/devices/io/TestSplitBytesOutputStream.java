package org.mdpnp.devices.io;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.reflect.Field;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mdpnp.devices.io.MergeBytesInputStream;
import org.mdpnp.devices.io.SplitBytesOutputStream;

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

    private static final long TIMEOUT = 2000L;

    @Test(timeout = TIMEOUT)
    public void testSingleByteWrite() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SplitBytesOutputStream os = new SplitBytesOutputStream(baos);
        os.writeProtected(0x85);
        for(int i = 0; i < TEST_DATA.length; i++) {
            os.write(0xFF&TEST_DATA[i]);
        }
        os.writeProtected(0x85);
        os.write(0x00);
        os.close();
        assertArrayEquals(EXPECTED_DATA, baos.toByteArray());
    }

    @Test(timeout=TIMEOUT)
    public void testWriteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SplitBytesOutputStream os = new SplitBytesOutputStream(baos);
        os.writeProtected(0x85);
        os.write(TEST_DATA);
        os.writeProtected(0x85);
        os.write(0x00);
        os.close();
        assertArrayEquals(EXPECTED_DATA, baos.toByteArray());
    }

    @Test(timeout=TIMEOUT)
    public void testBothSingleByte() throws IOException {
        PipedInputStream pis = new PipedInputStream(EXPECTED_DATA.length);
        InputStream is = new MergeBytesInputStream(pis);
        SplitBytesOutputStream os = new SplitBytesOutputStream(new PipedOutputStream(pis));
        os.writeProtected(0x85);
        os.write(TEST_DATA);
        os.writeProtected(0x85);
        os.write(0x00);
        os.close();
        assertEquals(MergeBytesInputStream.BEGIN_FRAME, is.read());
        for(int i = 0; i < TEST_DATA.length; i++) {
            assertEquals(0xFF&TEST_DATA[i], is.read());
        }
        assertEquals(MergeBytesInputStream.BEGIN_FRAME, is.read());
        assertEquals(0x00, is.read());
        is.close();
    }

    private static final void printArray(String prefix, byte[] arr, int off, int len) {
        System.err.print(prefix +" [0x"+Integer.toHexString(0xFF&arr[0]));
        for(int q = off+1; q < (off+len); q++) {
            System.err.print(", 0x"+Integer.toHexString(0xFF&arr[q]));
        }
        System.err.println("]");
    }

    @Test(timeout=TIMEOUT)
    public void testBothArray() throws IOException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        System.err.println("testBothArray");
        PipedInputStream pis = new PipedInputStream(EXPECTED_DATA.length);
        InputStream is = new MergeBytesInputStream(pis);
        SplitBytesOutputStream os = new SplitBytesOutputStream(new PipedOutputStream(pis));
        os.writeProtected(0x85);
        os.write(TEST_DATA);
        os.writeProtected(0x85);
        os.write(0x00);
        os.close();
        Field f = PipedInputStream.class.getDeclaredField("buffer");
        f.setAccessible(true);
        printArray("TEST_DATA", TEST_DATA, 0, TEST_DATA.length);
        printArray("EXPECTED_DATA", EXPECTED_DATA, 0, EXPECTED_DATA.length);
        printArray("pis Data", (byte[])f.get(pis), 0, ((byte[])f.get(pis)).length);

        assertArrayEquals(EXPECTED_DATA, (byte[])f.get(pis));
        assertEquals(MergeBytesInputStream.BEGIN_FRAME, is.read());
        byte[] data = new byte[EXPECTED_DATA.length];
        assertEquals(6, is.read(data, 0, 8));
        assertArrayEquals(TEST_DATA, Arrays.copyOfRange(data, 0, 6));
        assertEquals(MergeBytesInputStream.BEGIN_FRAME, is.read());
        assertEquals(0x00, is.read());
        is.close();
    }
}
