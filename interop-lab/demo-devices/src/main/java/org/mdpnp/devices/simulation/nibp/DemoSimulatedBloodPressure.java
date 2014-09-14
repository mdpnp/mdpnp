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
package org.mdpnp.devices.simulation.nibp;

import ice.Numeric;

import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;
import org.mdpnp.rtiapi.data.EventLoop;

/**
 * @author Jeff Plourde
 *
 */
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
        // pulse =
        // createNumericInstance(ice.Physio.MDC_PULS_RATE_NON_INV.value());

        numericSample(state, ice.MDC_EVT_STAT_OFF.VALUE, null);
    }

    @Override
    public boolean connect(String str) {
        bloodPressure.connect(str);
        return super.connect(str);
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
