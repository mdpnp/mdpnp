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
package org.mdpnp.devices.simulation;

import ice.DeviceIdentity;

import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSimulatedDevice extends AbstractDevice {
    private static final Logger log = LoggerFactory.getLogger(AbstractSimulatedDevice.class);
    private static final int UDI_LENGTH = 36;
    private static final char[] UDI_CHARS = new char[26 * 2 + 10];

    public static void main(String[] args) {
        System.out.println(randomUDI());
    }

    static {
        int x = 0;
        for (char i = 'A'; i <= 'Z'; i++) {
            UDI_CHARS[x++] = i;
        }
        for (char i = 'a'; i <= 'z'; i++) {
            UDI_CHARS[x++] = i;
        }
        for (char i = '0'; i <= '9'; i++) {
            UDI_CHARS[x++] = i;
        }
    }

    public static String randomUDI() {
        String udi = System.getProperty("randomUDI");
        if (null != udi && !"".equals(udi)) {
            return udi;
        } else {
            StringBuilder sb = new StringBuilder();
            java.util.Random random = new java.util.Random(System.currentTimeMillis());
            for (int i = 0; i < UDI_LENGTH; i++) {
                sb.append(UDI_CHARS[random.nextInt(UDI_CHARS.length)]);
            }
            return sb.toString();
        }
    }

    public static void randomUDI(DeviceIdentity di) {
        di.unique_device_identifier = randomUDI();
        log.debug("Created Random UDI:" + di.unique_device_identifier);
    }

    public AbstractSimulatedDevice(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);
        randomUDI(deviceIdentity);
        writeDeviceIdentity();
    }
}
