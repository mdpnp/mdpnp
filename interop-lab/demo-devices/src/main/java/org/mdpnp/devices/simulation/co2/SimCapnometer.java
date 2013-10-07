/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.simulation.co2;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;

public class SimCapnometer extends AbstractSimulatedConnectedDevice {

    protected final InstanceHolder<ice.SampleArray> co2;
    protected final InstanceHolder<ice.Numeric> respiratoryRate, etCO2;

    private class MySimulatedCapnometer extends SimulatedCapnometer {
        @Override
        protected void receiveCO2(Number[] co2Values, int respiratoryRateValue, int etCO2Value, double msPerSample) {
            sampleArraySample(co2, co2Values, (int) msPerSample, null);
            numericSample(respiratoryRate, respiratoryRateValue, null);
            numericSample(etCO2, etCO2Value, null);

        }
    }

    private final MySimulatedCapnometer capnometer = new MySimulatedCapnometer();


    @Override
    public void connect(String str) {
        capnometer.connect(executor);
        super.connect(str);
    }

    @Override
    public void disconnect() {
        capnometer.disconnect();
        super.disconnect();
    }

    public SimCapnometer(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);

        co2 = createSampleArrayInstance(ice.MDC_CAPNOGRAPH.VALUE);
        respiratoryRate = createNumericInstance(rosetta.MDC_RESP_RATE.VALUE);
        etCO2 = createNumericInstance(rosetta.MDC_AWAY_CO2_EXP.VALUE);

        deviceIdentity.model = "Capnometer (Simulated)";
        writeDeviceIdentity();
    }

    @Override
    protected String iconResourceName() {
        return "co2.png";
    }
}
