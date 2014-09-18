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
package org.mdpnp.devices.simulation.pulseox;

import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;
import org.mdpnp.rtiapi.data.EventLoop;

/**
 * @author Jeff Plourde
 *
 */
public class MultiSimPulseOximeter extends AbstractSimulatedConnectedDevice {

    protected final InstanceHolder<ice.SampleArray> pleth[];

    private class MySimulatedPulseOximeter extends SimulatedPulseOximeter {
        private final int ordinal;

        public MySimulatedPulseOximeter(final int ordinal) {
            this.ordinal = ordinal;
        }

        @Override
        protected void receivePulseOx(long timestamp, int heartRate, int SpO2, Number[] plethValues, int frequency) {
            pleth[ordinal] = sampleArraySample(pleth[ordinal], plethValues, rosetta.MDC_PULS_OXIM_PLETH.VALUE, ordinal, 
                    rosetta.MDC_DIM_DIMLESS.VALUE, frequency, null);
        }
    }

    private final MySimulatedPulseOximeter pulseox[];

    @Override
    public boolean connect(String str) {
        for (int i = 0; i < pulseox.length; i++) {
            pulseox[i].connect(executor);
        }
        return super.connect(str);
    }

    @Override
    public void disconnect() {
        for (int i = 0; i < pulseox.length; i++) {
            pulseox[i].disconnect();
        }
        super.disconnect();
    }

    private final static int N = 10000;

    @SuppressWarnings("unchecked")
    public MultiSimPulseOximeter(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);

        this.pulseox = new MySimulatedPulseOximeter[N];
        this.pleth = new InstanceHolder[N];

        for (int i = 0; i < pleth.length; i++) {
            this.pulseox[i] = new MySimulatedPulseOximeter(i);
        }

        deviceIdentity.model = "Pulse Ox (Simulated)";
        writeDeviceIdentity();
    }

    @Override
    protected String iconResourceName() {
        return "pulseox.png";
    }
}
