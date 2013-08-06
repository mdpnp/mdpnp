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

    protected final InstanceHolder<Numeric> systolic, diastolic, pulse, inflation, nextInflationTime, state;
    // TODO needs to subscribe to an objective state for triggering a NIBP

    private final SimulatedNoninvasiveBloodPressure bloodPressure = new SimulatedNoninvasiveBloodPressure() {
        @Override
        protected void beginDeflation() {
            numericSample(state, ice.MDC_EVT_STAT_NBP_DEFL_AND_MEAS_BP.VALUE);
        }

        @Override
        protected void beginInflation() {
            numericSample(state, ice.MDC_EVT_STAT_NBP_INFL_TO_MAX_CUFF_PRESS.VALUE);
        }

        @Override
        protected void endDeflation() {
            numericSample(state, ice.MDC_EVT_STAT_OFF.VALUE);
        }

        @Override
        protected void updateInflation(int inflation) {
            numericSample(DemoSimulatedBloodPressure.this.inflation, inflation);
        }

        @Override
        protected void updateNextInflationTime(long nextInflationTime) {
            numericSample(DemoSimulatedBloodPressure.this.nextInflationTime, nextInflationTime);
        }

        @Override
        protected void updateReading(int systolic, int diastolic, int pulse) {
            numericSample(DemoSimulatedBloodPressure.this.systolic, systolic);
            numericSample(DemoSimulatedBloodPressure.this.diastolic, diastolic);
            numericSample(DemoSimulatedBloodPressure.this.pulse, pulse);
        }
    };

    public DemoSimulatedBloodPressure(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);
        deviceIdentity.model = "NIBP (Simulated)";
        deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);

        state = createNumericInstance(ice.MDC_PRESS_CUFF.VALUE);
        systolic = createNumericInstance(ice.MDC_PRESS_CUFF_SYS.VALUE);
        diastolic = createNumericInstance(ice.MDC_PRESS_CUFF_DIA.VALUE);
        nextInflationTime = createNumericInstance(ice.MDC_PRESS_CUFF_NEXT_INFLATION.VALUE);
        inflation = createNumericInstance(ice.MDC_PRESS_CUFF_INFLATION.VALUE);
        // TODO temporarily more interesting
        pulse = createNumericInstance(ice.MDC_PULS_OXIM_PULS_RATE.VALUE);
        // pulse = createNumericInstance(ice.MDC_PULS_RATE_NON_INV.VALUE);

        numericSample(state, ice.MDC_EVT_STAT_OFF.VALUE);
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
