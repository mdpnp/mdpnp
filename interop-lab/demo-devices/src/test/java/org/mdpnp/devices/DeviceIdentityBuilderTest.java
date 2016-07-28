package org.mdpnp.devices;


import ice.DeviceIdentity;
import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.devices.simulation.co2.SimCapnometer;

public class DeviceIdentityBuilderTest {

    @Test
    public void testWithBuildName() {

        DeviceIdentityBuilder b = new DeviceIdentityBuilder();
        DeviceIdentity di = b.softwareRev().build();

        Assert.assertNotNull(di.build);
    }

    @Test
    public void testWithOsName() {

        DeviceIdentityBuilder b = new DeviceIdentityBuilder();
        DeviceIdentity di = b.osName().build();

        Assert.assertNotNull(di.operating_system);
        Assert.assertNotEquals("", di.operating_system);
    }

    @Test
    public void testWithIcon() {

        DeviceIdentityBuilder b = new DeviceIdentityBuilder();
        DeviceIdentity di = b.withIcon(SimCapnometer.class, "co2.png").build();

        Assert.assertNotNull(di.icon.image.userData);
        Assert.assertEquals(16134, di.icon.image.userData.size());
    }

    @Test
    public void testRandomUDI() {

        String str = DeviceIdentityBuilder.randomUDI();
        Assert.assertNotNull(str);
        Assert.assertEquals(36, str.length());
    }
}
