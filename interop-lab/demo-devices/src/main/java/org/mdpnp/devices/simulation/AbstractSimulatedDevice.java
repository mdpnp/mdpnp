/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.simulation;

import ice.DeviceIdentity;

import java.util.UUID;

import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSimulatedDevice extends AbstractDevice {
    private static final Logger log = LoggerFactory.getLogger(AbstractSimulatedDevice.class);

    public static String randomUDI() {
        UUID uuid = UUID.randomUUID();
        return Long.toHexString(uuid.getMostSignificantBits()) +
        Long.toHexString(uuid.getLeastSignificantBits());
    }

    public static void randomUDI(DeviceIdentity di) {
        di.universal_device_identifier = randomUDI();
        log.debug("Created Random UDI:"+di.universal_device_identifier);
    }

    public AbstractSimulatedDevice(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);
        randomUDI(deviceIdentity);
        writeDeviceIdentity();
    }
}
