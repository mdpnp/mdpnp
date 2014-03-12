/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.devices.io;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jeff Plourde
 *
 */
public class TestMergeBytesInputStream {
    @Before
    public void setUp() {
        TEST_DATA = new byte[] { 0x44, (byte) 0x85, 0x55, (byte) 0x80, 0x05, 0x00, (byte) 0x80, 0x00, 0x77 };
    }

    @After
    public void tearDown() {
        TEST_DATA = null;
    }

    private byte[] TEST_DATA;

    private static final long TIMEOUT = 2000L;

    @Test(timeout = TIMEOUT)
    public void testSingleByteRead() throws IOException {
        InputStream is = new MergeBytesInputStream(new ByteArrayInputStream(TEST_DATA));
        assertEquals(0x44, is.read());
        assertEquals(MergeBytesInputStream.BEGIN_FRAME, is.read());
        assertEquals(0x55, is.read());
        assertEquals(0x85, is.read());
        assertEquals(0x00, is.read());
        assertEquals(0x80, is.read());
        is.close();
    }

    @Test(timeout = TIMEOUT)
    public void testReadArray() throws IOException {
        InputStream is = new MergeBytesInputStream(new ByteArrayInputStream(TEST_DATA));
        byte out[] = new byte[50];
        int n = is.read(out, 0, 2);
        // it is awkward but a HEADER anywhere in the array returns HEADER
        // the caller should call read() when a HEADER is expected
        assertTrue(MergeBytesInputStream.BEGIN_FRAME == n);
        // Reads an internal 0x80->0x85 sequence and a 0x80 at the end so the
        // MergeBytesInputStream will read another byte to complete the sequence
        n = is.read(out, 0, 5);
        assertEquals(4, n);
        byte[] expected = new byte[] { 0x55, (byte) 0x85, 0x00, (byte) 0x80 };
        assertArrayEquals(expected, Arrays.copyOfRange(out, 0, 4));
        n = is.read(out, 0, 1);
        assertEquals(1, n);
        assertArrayEquals(new byte[] { 0x77 }, Arrays.copyOfRange(out, 0, 1));
        is.close();

    }
}
