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

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.simulation.NumberWithJitter;

/*
 * Can't do these imports from this project without circular dependencies becoming an issue.
 * IF we need to have simulation control here, we either need to do it on the demo-device
 * class that extends this one, or we need to refactor GlobalSimulationObjective
 */
//import ice.GlobalSimulationObjective;
//import org.mdpnp.devices.simulation.GlobalSimulationObjectiveListener;

/**
 * @author Jeff Plourde
 *
 */
public class SimulatedThermometer /*implements GlobalSimulationObjectiveListener*/  {

    
    
    private final class MyTask implements Runnable {
        @Override
        public void run() {
            DeviceClock.Reading  t = deviceClock.instant();
            receiveTemp1(simTemperature1.floatValue(), t);
            receiveTemp2(temperature2, t);
        }

    };

    protected void receiveTemp1(float temperature1, DeviceClock.Reading time) {

    }

    protected void receiveTemp2(float temperature2, DeviceClock.Reading time) {

    }
    
    /**
     * Use this to replace temperature1 fixed value, and register this as a global simulation objective so it can be replaced by the sim controller.
     */
    private Number simTemperature1 = new NumberWithJitter<>(37.0, 0.1, 2.0);
    
//    @Override
//    public void simulatedNumeric(ice.GlobalSimulationObjective obj) {
//    	//Need to pass this to something inside thermometer (the SimulatedThermometerExt instance)
//    }

    private static final long UPDATE_PERIOD = 1000L;
    //protected int temperature1 = 37;
    protected int temperature2 = 39;

    private ScheduledFuture<?> task;

    public void connect(ScheduledExecutorService executor) {
        if (task != null) {
            task.cancel(false);
            task = null;
        }
        task = executor.scheduleAtFixedRate(new MyTask(), 0L, UPDATE_PERIOD, TimeUnit.MILLISECONDS);
    }

    public void disconnect() {
        if (task != null) {
            task.cancel(false);
            task = null;
        }
    }
    private final DeviceClock deviceClock;
    public SimulatedThermometer(final DeviceClock referenceClock) {
        deviceClock = new DeviceClock() {
            final DeviceClock dev=new DeviceClock.Metronome(UPDATE_PERIOD);
            @Override
            public Reading instant() {
                return new CombinedReading(referenceClock.instant(), dev.instant());
            }
        };
    }

}
