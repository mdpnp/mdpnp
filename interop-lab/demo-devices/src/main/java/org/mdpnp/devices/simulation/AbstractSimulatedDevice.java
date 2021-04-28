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
package org.mdpnp.devices.simulation;

import ice.DeviceIdentity;
import ice.NumericSQI;

import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.DeviceIdentityBuilder;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

public abstract class AbstractSimulatedDevice extends AbstractDevice {
	private static final double DEFAULT_JITTER_CEILING = 100.0;
	private static final double DEFAULT_JITTER_FLOOR = 90.0;
	private static final double DEFAULT_JITTER_STEP_AMT = 0.25;
	private static final double DEFAULT_JITTER_START = 95.0;
	
	// Used to simulate accuracy SQI across all simulated devices
	protected NumberWithJitter<Float> accuracyJitter = new NumberWithJitter<Float>(DEFAULT_JITTER_START, DEFAULT_JITTER_STEP_AMT,
			DEFAULT_JITTER_FLOOR, DEFAULT_JITTER_CEILING);
	
	private NumericSQI defaultSQI = new NumericSQI();
	
    private static final Logger log = LoggerFactory.getLogger(AbstractSimulatedDevice.class);

    public static void randomUDI(DeviceIdentity di) {
        di.unique_device_identifier = DeviceIdentityBuilder.randomUDI();
        log.debug("Created Random UDI:" + di.unique_device_identifier);
    }

    public AbstractSimulatedDevice(final Subscriber subscriber, final Publisher publisher, EventLoop eventLoop) {
        super(subscriber, publisher, eventLoop);
        randomUDI(deviceIdentity);
        writeDeviceIdentity();
    }
    
    public NumericSQI getSQI() {
    	defaultSQI.accuracy = accuracyJitter.floatValue();
    	return defaultSQI;
    }
}


