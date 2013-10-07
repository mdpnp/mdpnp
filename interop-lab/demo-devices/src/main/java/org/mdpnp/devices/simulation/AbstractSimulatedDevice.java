/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.simulation;

import ice.DeviceIdentity;
import ice.DeviceIdentityTypeCode;

import java.util.UUID;

import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSimulatedDevice extends AbstractDevice {
    private static final Logger log = LoggerFactory.getLogger(AbstractSimulatedDevice.class);
    private static final int UDI_LENGTH = 36;
    private static final char[] UDI_CHARS = new char[26*2+10];
    static {
        int x = 0;
        for(char i = 'A'; i <= 'Z'; i++) {
            UDI_CHARS[x++] = i;
        }
        for(char i = 'a'; i <= 'z'; i++) {
            UDI_CHARS[x++] = i;
        }
        for(char i = '0'; i <= '9'; i++) {
            UDI_CHARS[x++] = i;
        }
    }

    public static String randomUDI() {
        StringBuilder sb = new StringBuilder();
        java.util.Random random = new java.util.Random(System.currentTimeMillis());
        for(int i = 0; i < UDI_LENGTH; i++) {
            sb.append(UDI_CHARS[random.nextInt(UDI_CHARS.length)]);
        }
        return sb.toString();
    }

    public static void randomUDI(DeviceIdentity di) {
        di.unique_device_identifier = randomUDI();
        log.debug("Created Random UDI:"+di.unique_device_identifier);
    }

    public AbstractSimulatedDevice(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);
        randomUDI(deviceIdentity);
        writeDeviceIdentity();
    }
}
