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
            sampleArraySample(co2, co2Values, (int) msPerSample);
            numericSample(respiratoryRate, respiratoryRateValue);
            numericSample(etCO2, etCO2Value);

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
        respiratoryRate = createNumericInstance(ice.Physio.MDC_RESP_RATE.value());
        etCO2 = createNumericInstance(ice.Physio.MDC_AWAY_CO2_EXP.value());

        deviceIdentity.model = "Capnometer (Simulated)";
        writeDeviceIdentity();
    }

    @Override
    protected String iconResourceName() {
        return "co2.png";
    }
}
