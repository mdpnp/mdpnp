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
package org.mdpnp.devices.philips.intellivue;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.mdpnp.devices.io.SplitBytesOutputStream;

/**
 * @author Jeff Plourde
 *
 */
public class TestFCSOutputStream {
    @Test
    public void testOutput() throws Exception {

        byte[] data = new byte[] { 0x3A, 0x71 };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SplitBytesOutputStream sbos = new SplitBytesOutputStream(baos, new IntellivueByteSplitter());
        FCSOutputStream fcs = new FCSOutputStream(sbos);

        baos.write(0xC0);
        fcs.write(data);
        fcs.writeFCS();
        baos.write(0xC1);
        fcs.close();

        byte[] expected = new byte[] { (byte) 0xc0, 0x3a, 0x71, (byte) 0x9b, 0x26, (byte) 0xc1 };

        assertArrayEquals(expected, baos.toByteArray());
    }

    @Test
    public void testOutputWithEscape() throws Exception {

        byte[] data = new byte[] { 0x3A, (byte) 0x91 };

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        SplitBytesOutputStream sbos = new SplitBytesOutputStream(baos, new IntellivueByteSplitter());
        FCSOutputStream fcs = new FCSOutputStream(sbos);

        baos.write(0xC0);
        fcs.write(data);
        fcs.writeFCS();
        baos.write(0xC1);
        fcs.close();

        byte[] expected = new byte[] { (byte) 0xc0, 0x3a, (byte) 0x91, (byte) 0x95, 0x7d, (byte) 0xe1, (byte) 0xc1 };

        assertArrayEquals(expected, baos.toByteArray());
    }
}
