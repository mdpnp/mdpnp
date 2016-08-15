package org.mdpnp.apps.testapp;

import ice.DeviceConnectivity;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class DeviceListModelImplTest {

    @Test
    public void testListLifecycle() throws Exception {

        DeviceListModelImpl dlm = createDeviceListModelImpl();

        DeviceConnectivity deviceConnectivity = new DeviceConnectivity();
        deviceConnectivity.unique_device_identifier = "12345";
        dlm.update(deviceConnectivity);

        Assert.assertNull("Device connectivity message should not populate the active list",
                          dlm.findDevice("12345"));

        dlm.aliveHeartbeat("12345", "Device", "localhost");
        Assert.assertEquals(1, dlm.getContents().size());

        Device d = dlm.getContents().get(0);

        dlm.notAliveHeartbeat("12345", "Device");
        Assert.assertEquals(0, dlm.getContents().size());

        dlm.aliveHeartbeat("12345", "Device", "localhost");
        Assert.assertEquals(1, dlm.getContents().size());

        Assert.assertSame(d, dlm.getContents().get(0));
    }


    DeviceListModelImpl createDeviceListModelImpl() {

        DeviceListModelImpl v = new DeviceListModelImpl(null, null, null) {

            @Override
            protected void runLaterOnPlatform(Runnable r) {
                r.run();
            }

            @Override
            protected void assertEventLoopThread() {
            }

            @Override
            protected void assertPlatformThread() {
            }
        };

        return v;
    }
}
