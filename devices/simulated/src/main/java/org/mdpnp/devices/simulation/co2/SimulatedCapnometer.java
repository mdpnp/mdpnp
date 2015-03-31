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
package org.mdpnp.devices.simulation.co2;

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
public class SimulatedCapnometer {

    private static final Logger log = LoggerFactory.getLogger(SimulatedCapnometer.class);

    private int count;

    protected int postIncrCount() {
        int count = this.count;
        this.count = ++this.count >= co2.length ? 0 : this.count;
        return count;
    }

    private final class DataPublisher implements Runnable {
        private final Number[] values = new Number[SAMPLES_PER_UPDATE];

        public DataPublisher() {
        }
        
        @Override
        public void run() {

            for (int i = 0; i < values.length; i++) {
                values[i] = SimulatedCapnometer.this.co2[postIncrCount()];
            }

            DeviceClock.Reading  t = deviceClock.instant();

            int rRate = respiratoryRate.intValue();
            int co2   = etCO2.intValue();

            receiveCO2(t, values, rRate, co2, FREQUENCY);
        }

    };

    protected void receiveCO2(DeviceClock.Reading time, Number[] co2, int respiratoryRate, int etCO2, int frequency) {

    }

    private final DeviceClock deviceClock;

    protected static final long UPDATE_PERIOD = 1000L;
    protected static final double MILLISECONDS_PER_SAMPLE = 50;
    protected static final int FREQUENCY = (int)(1000.0 / MILLISECONDS_PER_SAMPLE);
    protected static final int SAMPLES_PER_UPDATE = (int) Math.floor(UPDATE_PERIOD / MILLISECONDS_PER_SAMPLE);

    private final double[] co2Coeffs = new double[] { 149.66002885691225, -25.293660981458554, -85.58222030802641, 10.266184380818338,
            -19.487174406798864, 23.466991505742733, 6.862712563574145, 8.216199199092642, 6.851492164494071, -11.068777425796783,
            1.5119842193989312, -10.113962673328832, 0.8300844077075563, 0.4504107143987201, 2.0660365676358983, 4.75309854339552,
            -0.5793457742858097, 1.8213028259766824, -3.934241619502538, -1.2334706430103939, -0.24483186289061662, -0.1801127442535287,
            1.2569661709564057, 0.6227322089742545, 2.422187530290737, -0.1512521298643852, 0.03660015699501427, -0.7178430223302408,
            -0.7926102438877431, 0.6263357988003349, -1.14793539542347, 0.4521350699510741, -0.5012923766911255, 0.6675900332384909,
            0.25962921807032036, 0.5294043425525827, 1.3197786469873654, -0.047119198503997914, 0.02161163744194785, -0.754360137903569,
            -0.29834912601437424, -0.21134322086640625, 0.9101951714854563, 0.2206146220441592, 0.09553284406531953, 0.25356329843998404,
            0.613193894574464, -0.02900906292001993, -0.8567730123904702, -0.764228373507708, -0.28117493698814067, 0.5791694782321603,
            0.3749084259138257, 0.8083123233052693, 0.0465937689256733, -0.098306017086347, -1.1071981565620364, -0.7313166951808587,
            0.6511228206796118, 0.12476993483795351, 0.5830531993593916, -0.11062947331636298, -0.17789266788854582, -0.26483328081002094,
            -0.7128340075521026, 0.10314102435833576, -1.0285663372574272, -2.428948661825071E-4, -0.14899927687303355, 0.1098176169180203,
            -0.11903647185300023, -0.4452919227761163, 0.5197026432935289, -0.6325914373365761, 0.46109876415951534, -0.8724107699471632,
            0.26939036897293916, -0.0904701971793937, -0.7810921669698734, 0.1120738113242933, -0.6479773726649353, 0.27856716837197787,
            -0.7656824829134345, -0.2350246570389894, 0.5032357613707102, -0.41155231296386235, 0.24359894481602737, -0.17759148348524065,
            -0.33315287258643744, 0.04515924646026637, -0.561927692186718, 0.7799330968687627, -0.33990498476784836, 0.32590698371138915 };
    private final double[] co2 = new double[co2Coeffs.length];

    private Number respiratoryRate = new NumberWithJitter(13, 1, 5);
    private Number etCO2 = new NumberWithJitter(29, 1, 5);

    private void initWaves() {
        DCT.idct(co2Coeffs, co2);
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

    public SimulatedCapnometer(final DeviceClock referenceClock) {
        deviceClock = new DeviceClock() {
            final DeviceClock dev=new DeviceClock.Metronome(UPDATE_PERIOD);
            @Override
            public Reading instant() {
                return new CombinedReading(referenceClock.instant(), dev.instant());
            }

        };
        initWaves();
    }

    public void setEndTidalCO2(Number targetEndTidalCO2) {
        this.etCO2 = new NumberWithGradient(etCO2, targetEndTidalCO2, 5);
        log.debug("Set etCO2 to " + this.etCO2);
    }

    public void setRespirationRate(Number targetRespirationRate) {
        this.respiratoryRate = new NumberWithGradient(respiratoryRate, targetRespirationRate, 1);
        log.debug("Set respiratoryRate to " + this.respiratoryRate);
    }

}
