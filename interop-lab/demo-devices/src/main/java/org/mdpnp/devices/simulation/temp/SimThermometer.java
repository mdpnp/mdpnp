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
package org.mdpnp.devices.simulation.temp;

import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;
import org.mdpnp.rtiapi.data.EventLoop;

import com.rti.dds.infrastructure.Tag;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

/**
 * @author Jeff Plourde
 *
 */
public class SimThermometer extends AbstractSimulatedConnectedDevice {

    protected InstanceHolder<ice.Numeric> temperature1;

    private class SimulatedThermometerExt extends SimulatedThermometer {
        public SimulatedThermometerExt(final DeviceClock referenceClock) {
            super(referenceClock);
        }
        
        @Override
        protected void receiveTemp1(float temperature1, DeviceClock.Reading time) {
        	Tag tag=new Tag("ICE_METRIC_TYPE", "TEMPERATURE");
            // TODO assign a unit type
            SimThermometer.this.temperature1 = numericSample(SimThermometer.this.temperature1, temperature1, rosetta.MDC_TEMP_BLD.VALUE, "", 0, 
                    rosetta.MDC_DIM_DIMLESS.VALUE, time);
        }

        @Override
        protected void receiveTemp2(float temperature2, DeviceClock.Reading time) {

        }
    }

    private final SimulatedThermometerExt thermometer = new SimulatedThermometerExt(getClockProvider());

    @Override
    public boolean connect(String str) {
        thermometer.connect(executor);
        return super.connect(str);
    }

    @Override
    public void disconnect() {
        thermometer.disconnect();
        super.disconnect();
    }

    public SimThermometer(final Subscriber subscriber, final Publisher publisher, EventLoop eventLoop) {
        super(subscriber, publisher, eventLoop);
        deviceIdentity.model = "Thermometer (Simulated)";
        writeDeviceIdentity();
    }

    @Override
    protected String iconResourceName() {
        return "temp.png";
    }
}
