/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.simulation.temp;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;

public class SimThermometer extends AbstractSimulatedConnectedDevice {

    protected InstanceHolder<ice.Numeric> temperature1, temperature2;

    private class MySimulatedThermometer extends SimulatedThermometer {
        @Override
        protected void receiveTemp1(float temperature1) {
            SimThermometer.this.temperature1 = numericSample(SimThermometer.this.temperature1, temperature1, rosetta.MDC_TEMP_BLD.VALUE, 0, null);
        }
        @Override
        protected void receiveTemp2(float temperature2) {
            SimThermometer.this.temperature2 = numericSample(SimThermometer.this.temperature2, temperature2, rosetta.MDC_TEMP_BLD.VALUE, 1, null);
        }
    }

    private final MySimulatedThermometer thermometer = new MySimulatedThermometer();


    @Override
    public void connect(String str) {
        thermometer.connect(executor);
        super.connect(str);
    }

    @Override
    public void disconnect() {
        thermometer.disconnect();
        super.disconnect();
    }

    public SimThermometer(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);
        deviceIdentity.model = "Thermometer (Simulated)";
        writeDeviceIdentity();
    }

    @Override
    protected String iconResourceName() {
        return null;
    }
}
