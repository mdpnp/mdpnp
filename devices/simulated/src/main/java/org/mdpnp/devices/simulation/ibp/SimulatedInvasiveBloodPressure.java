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
import org.mdpnp.devices.math.DCT;
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

    private final double[] coeffs = new double[] { 39, 030.5368, -45.4024, 4.0052, -44.8194, 3.5608, -48.0326, 27.6985, -43.0863, 3.8055, -43.9232,
            3.3545, -48.7591, 5.9381, -38.7356, 2.5662, -41.5681, 3.3797, -46.5581, -1.3974, -42.6509, 2.9086, -42.0149, 4.0378, -44.4343, 1.7396,
            -45.1566, 2.8512, -46.2485, 4.0909, -47.0591, 5.9694, -46.1504, 4.4873, -47.6032, 4.1856, -47.5447, 4.7229, -47.3954, 4.0734, -48.1467,
            4.2274, -49.0898, 4.4508, -49.8775, 4.5333, -51.6614, 4.1380, -51.0650, 5.7190, -54.1334, 4.8224, -54.4042, 4.5644, -55.5518, 5.7746,
            -54.8413, 4.9208, -59.0403, 6.2998, -59.4148, 5.6179, -59.2212, 7.2883, -61.8245, 6.5504, -64.7720, 9.4296, -64.6789, 8.2354, -69.6252,
            8.8376, -70.7153, 10.8268, -74.3539, 8.2064, -78.3314, 9.1081, -79.7009, 11.7072, -81.9660, 11.2289, -85.6763, 11.1920, -93.2380,
            11.1884, -100.0187, 14.9517, -101.8997, 15.9520, -109.2513, 18.8181, -125.3839, 18.0514, -129.4335, 20.5229, -142.0227, 28.1417,
            -158.6315, 25.4639, -174.8147, 36.4649, -202.0263, 44.1499, -223.4422, 38.9511, -276.8570, 58.5504, -349.9253, 67.0961, -474.2407,
            113.0750, -748.1843, 206.9512, -1, 745.0727, 1, 105.4010, 4, 613.2041, -330.6927, 972.5560, -149.4788, 541.5468, -114.0444, 390.2941,
            -66.4324, 287.4282, -61.0118, 234.2902, -53.3564, 188.4441, -41.1916, 165.2205, -42.4421, 142.6092, -36.7258, 126.8492, -34.8681,
            114.1699, -31.2159, 101.9996, -26.4529, 98.2062, -28.1134, 87.1903, -28.7930, 78.4514, -27.0467, 76.1186, -24.0022, 71.7729, -26.1218,
            65.6350, -24.9787, 61.5699, -23.5208, 56.8390, -22.1458, 56.0776, -21.7144, 52.2537, -22.8554, 49.5744, -21.9554, 47.2215, -22.1446,
            45.7933, -23.7752, 44.5914, -23.3216, 39.5720, -24.9509, 38.2835, -22.2072, 37.8528, -23.1499, 37.2178, -24.3183, 33.7657, -24.1390,
            33.4328, -23.5620, 32.7218, -25.4119, 32.7512, -25.2948, 31.4284, -26.4887, 30.7411, -26.8813, 29.6482, -27.4301, 28.9572, -30.4784,
            26.9450, -29.9302, 24.6889, -31.4316, 25.2005, -30.6575, 26.8428, -34.8148, 24.8999, -39.9000, 23.9526, -39.7786, 26.1686, -42.6627,
            29.8773, -46.8751, 22.3355, -51.9604, 23.8921, -57.7443, 28.8668, -57.9135, 21.6947, -68.5670, 25.8229, -80.2414, 28.2852, -92.8727,
            25.4821, -113.7240, 31.2491, -133.6898, 13.9492, -224.2603, 40.3966, -362.3344, 58.7148, -1, 136.0774, -801.6152, 944.0147, -13.7873,
            330.3057, -0.3246, 205.0221, -18.6824, 142.9059, 6.8394, 113.5517, 6.1760, 94.3099, 7.3800, 77.3793, 8.8222, 70.3506, 8.1238, 57.8864,
            9.2485, 59.4396, 7.1269, };
    private final double[] wave = new double[coeffs.length];

    private void initWave() {
        DCT.idct(coeffs, wave);
    }

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
        initWave();
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
