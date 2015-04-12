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
package org.mdpnp.devices.simulation.ibp;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.simulation.NumberWithGradient;
import org.mdpnp.devices.simulation.NumberWithJitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class SimulatedInvasiveBloodPressure {
    private int count = 0;
    private static final Logger log = LoggerFactory.getLogger(SimulatedInvasiveBloodPressure.class);

    protected int postIncrCount() {
        int count = this.count;
        this.count = ++this.count >= wave.length ? 0 : this.count;
        return count;
    }

    private final class DataPublisher implements Runnable {
        private final Number[] waveValues = new Number[SAMPLES_PER_UPDATE];

        public DataPublisher() {
        }

        @Override
        public void run() {
            try {
                for (int i = 0; i < waveValues.length; i++) {
                    waveValues[i] = wave[postIncrCount()];
                }

                int val[] = nextDraw();

                DeviceClock.Reading t = deviceClock.instant();

                receivePressure(t, val[0], val[1], waveValues, FREQUENCY);

            } catch (Throwable t) {
                log.error("Error sending simulated pulse oximetry data", t);
            }
        }

    };

    protected void receivePressure(DeviceClock.Reading timestamp, int systolic, int diastolic, Number[] waveValues, int frequency) {

    }

    private final DeviceClock deviceClock;

    protected static final long UPDATE_PERIOD = 1000L;
    protected static final int FREQUENCY = 120;
    protected static final int SAMPLES_PER_UPDATE = (int) Math.floor(1000L * FREQUENCY / UPDATE_PERIOD);

    private final double[] wave = new double[] { 594, 594, 592, 590, 587, 584, 581, 578, 574, 569, 563, 558, 552, 546, 540, 532, 524, 517, 511, 506,
            502, 499, 498, 500, 502, 504, 505, 507, 508, 507, 505, 502, 498, 492, 485, 478, 472, 466, 460, 456, 452, 449, 445, 441, 436, 433, 431,
            428, 427, 425, 423, 422, 421, 419, 418, 416, 415, 413, 411, 410, 409, 407, 406, 404, 403, 401, 401, 401, 401, 400, 399, 398, 397, 396,
            396, 396, 396, 396, 396, 395, 395, 396, 396, 396, 396, 396, 396, 396, 396, 395, 395, 396, 396, 396, 396, 396, 396, 396, 401, 414, 433,
            453, 472, 489, 505, 518, 529, 539, 548, 555, 562, 569, 573, 577, 581, 583, 586, 590, 592, 593, };

    private ScheduledFuture<?> task;

    public void connect(ScheduledExecutorService executor) {
        if (task != null) {
            task.cancel(false);
            task = null;
        }
        long now = System.currentTimeMillis();
        task = executor.scheduleAtFixedRate(new DataPublisher(), UPDATE_PERIOD - now % UPDATE_PERIOD, UPDATE_PERIOD, TimeUnit.MILLISECONDS);
    }

    public void disconnect() {
        if (task != null) {
            task.cancel(false);
            task = null;
        }
    }

    public SimulatedInvasiveBloodPressure(final DeviceClock referenceClock) {
        deviceClock = new DeviceClock() {
            final DeviceClock dev = new DeviceClock.Metronome(UPDATE_PERIOD);

            @Override
            public Reading instant() {
                return new CombinedReading(referenceClock.instant(), dev.instant());
            }
        };
    }

    private Number systolic = new NumberWithJitter<Integer>(120, 2, 60, 180);
    private Number diastolic = new NumberWithJitter<Integer>(80, 2, 40, 100);

    int[] nextDraw() {
        return new int[] { systolic.intValue(), diastolic.intValue() };
    }

    public void setSystolic(Number systolic) {
        this.systolic = new NumberWithGradient(this.systolic, systolic, 5);
        log.debug("Set systolic to " + this.systolic);
    }

    public void setDiastolic(Number diastolic) {
        this.diastolic = new NumberWithGradient(this.diastolic, diastolic, 2);
        log.debug("Set diastolic to " + this.diastolic);
    }
}
