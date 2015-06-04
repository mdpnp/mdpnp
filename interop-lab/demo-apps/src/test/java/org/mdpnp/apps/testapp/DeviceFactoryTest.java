package org.mdpnp.apps.testapp;

import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.DeviceDriverProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 */
public class DeviceFactoryTest {

    private final static Logger log = LoggerFactory.getLogger(DeviceFactoryTest.class);

    @Test
    public void testLocateDrivers()
    {
        DeviceDriverProvider[] all =  DeviceFactory.getAvailableDevices();
        Assert.assertNotNull(all);
        Assert.assertNotEquals(all.length, 0);
        for (DeviceDriverProvider ddp : all) {
            DeviceDriverProvider.DeviceType dt=ddp.getDeviceType();
            Assert.assertNotNull("Device provider failed to identify type " + ddp, dt);
            log.info(dt.getModel() + " " + dt.getManufacturer() + " " + dt.getAlias());
        }
    }

    @Test
    public void testLifecycleCompatibility()
    {

        AbstractApplicationContext parentContext =
                new ClassPathXmlApplicationContext(new String[] { "DeviceAdapterContext.xml" });

        try {
            DeviceDriverProvider[] all = DeviceFactory.getAvailableDevices();
            for (DeviceDriverProvider ddp : all) {
                DeviceDriverProvider.DeviceType dt = ddp.getDeviceType();

                try {
                    AbstractDevice ad = ddp.newInstance(parentContext);
                    Assert.assertNotNull(ddp.getClass().getSimpleName() + " failed to create instance of type " + dt, ad);
                    log.info("Device provider " + dt + " verified");
                } catch (Exception ex) {
                    String mdg = ddp.getClass().getSimpleName() + " failed to create instance of type " + dt;
                    log.error(mdg, ex);
                    Assert.fail(mdg);
                }
            }
        }
        finally {
            parentContext.destroy();
        }
    }

}
