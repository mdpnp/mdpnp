package org.mdpnp.devices.simulation.pulseox;

import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.devices.DeviceClock;

/**
 * @author mfeinberg
 */
public class SimulatedPulseOximeterTest {

    @Test
    public void testDeviceSetup() throws Exception {

        DeviceClock clock = new DeviceClock.Metronome(1000);
        SimulatedPulseOximeter spo = new SimulatedPulseOximeter(clock);

        int val[] = spo.nextDraw();

        Assert.assertEquals("invalid initial heartRate", 60.0, val[0], 2.0);
        Assert.assertEquals("invalid initial spo2",      98.0, val[1], 2.0);
    }
}
