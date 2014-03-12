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
package org.mdpnp.devices.philips.intellivue.data;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author Jeff Plourde
 *
 */
public class IPAddressInformation implements Value {

    private final byte[] macAddress = new byte[6];
    private final byte[] ipAddress = new byte[4];
    private final byte[] subnetMask = new byte[4];

    public IPAddressInformation() {
    }

    @Override
    public void parse(ByteBuffer bb) {
        bb.get(macAddress);
        bb.get(ipAddress);
        bb.get(subnetMask);
    }

    @Override
    public void format(ByteBuffer bb) {
        bb.put(macAddress);
        bb.put(ipAddress);
        bb.put(subnetMask);
    }

    @Override
    public java.lang.String toString() {
        InetAddress inetAddress = null;
        try {
            inetAddress = getInetAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException();
        }
        return "[macAddress=" + Arrays.toString(macAddress) + ",ipAddress=" + Arrays.toString(ipAddress) + "inetAddress=" + inetAddress
                + ",subnetMask=" + Arrays.toString(subnetMask) + "]";
    }

    public void setInetAddress(InetAddress addr) {
        System.arraycopy(addr.getAddress(), 0, ipAddress, 0, 4);
    }

    public InetAddress getInetAddress() throws UnknownHostException {
        return InetAddress.getByAddress(ipAddress);
    }

    public byte[] getSubnetMask() {
        return subnetMask;
    }

    public byte[] getMacAddress() {
        return macAddress;
    }

    public byte[] getIpAddress() {
        return ipAddress;
    }
}
