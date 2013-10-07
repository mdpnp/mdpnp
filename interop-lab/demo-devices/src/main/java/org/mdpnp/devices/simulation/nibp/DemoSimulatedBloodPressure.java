/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.simulation.nibp;

import ice.Numeric;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;
import org.mdpnp.devices.simulation.nibp.SimulatedNoninvasiveBloodPressure;

public class DemoSimulatedBloodPressure extends AbstractSimulatedConnectedDevice {

    protected InstanceHolder<Numeric> systolic, diastolic, pulse, inflation, nextInflationTime, state;
    // TODO needs to subscribe to an objective state for triggering a NIBP

    private final SimulatedNoninvasiveBloodPressure bloodPressure = new SimulatedNoninvasiveBloodPressure() {
        @Override
        protected void beginDeflation() {
            numericSample(state, ice.MDC_EVT_STAT_NBP_DEFL_AND_MEAS_BP.VALUE, null);
        }

        @Override
        protected void beginInflation() {
            numericSample(state, ice.MDC_EVT_STAT_NBP_INFL_TO_MAX_CUFF_PRESS.VALUE, null);
        }

        @Override
        protected void endDeflation() {
            numericSample(state, ice.MDC_EVT_STAT_OFF.VALUE, null);
        }

        @Override
        protected void updateInflation(int inflation) {
            numericSample(DemoSimulatedBloodPressure.this.inflation, inflation, null);
        }

        @Override
        protected void updateNextInflationTime(long nextInflationTime) {
            numericSample(DemoSimulatedBloodPressure.this.nextInflationTime, nextInflationTime, null);
        }

        @Override
        protected void updateReading(int systolic, int diastolic, int pulse) {
            numericSample(DemoSimulatedBloodPressure.this.systolic, systolic, null);
            numericSample(DemoSimulatedBloodPressure.this.diastolic, diastolic, null);
            numericSample(DemoSimulatedBloodPressure.this.pulse, pulse, null);
        }
    };

    public DemoSimulatedBloodPressure(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);
        deviceIdentity.model = "NIBP (Simulated)";
        writeDeviceIdentity();

        state = createNumericInstance(rosetta.MDC_PRESS_CUFF.VALUE);
        systolic = createNumericInstance(rosetta.MDC_PRESS_CUFF_SYS.VALUE);
        diastolic = createNumericInstance(rosetta.MDC_PRESS_CUFF_DIA.VALUE);
        nextInflationTime = createNumericInstance(ice.MDC_PRESS_CUFF_NEXT_INFLATION.VALUE);
        inflation = createNumericInstance(ice.MDC_PRESS_CUFF_INFLATION.VALUE);
        // TODO temporarily more interesting
        pulse = createNumericInstance(rosetta.MDC_PULS_RATE_NON_INV.VALUE);
        // pulse = createNumericInstance(ice.Physio.MDC_PULS_RATE_NON_INV.value());

        numericSample(state, ice.MDC_EVT_STAT_OFF.VALUE, null);
    }

    @Override
    public void connect(String str) {
        bloodPressure.connect(str);
        super.connect(str);
    }

    @Override
    public void disconnect() {
        bloodPressure.disconnect();
        super.disconnect();
    }

    @Override
    public void shutdown() {
        disconnect();
        super.shutdown();
    }

    @Override
    protected String iconResourceName() {
        return "nbp.png";
    }
}
