package org.mdpnp.devices.oridion.capnostream;

import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.devices.DeviceClock;


public class CapnostreamTest {


    @Test
    public void testReceiveDeviceIdSoftwareVersion() {

        DeviceClock wall = new DeviceClock.WallClock();

        Capnostream c = new Capnostream(wall, null, null);
        boolean isOk = c.receiveDeviceIdSoftwareVersion("V45.67 02/24/2008 B355987654  ");

        Assert.assertTrue("Failed to handle software revision", isOk);
    }


}
