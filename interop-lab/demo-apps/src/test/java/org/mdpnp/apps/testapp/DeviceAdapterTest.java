package org.mdpnp.apps.testapp;

import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.devices.DeviceDriverProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class DeviceAdapterTest {

    private final static Logger log = LoggerFactory.getLogger(DeviceAdapterTest.class);

    /*
    @Test
    public void testHeadlessAdapterAll() throws Exception
    {
        DeviceDriverProvider[] all = DeviceFactory.getAvailableDevices();
        for(DeviceDriverProvider ddp : all)
            testDriverAdapter(ddp, false);

    }
    */

    @Test
    public void testHeadlessAdapter() throws Exception
    {
        DeviceDriverProvider ddp = DeviceFactory.getDeviceDriverProvider("PO_Simulator");
        testDriverAdapter(ddp, false);
    }

    // TODO Jeff Plourde apologizes for this and needs to update these tests.
//    @Test
//    public void testGUIAdapter() throws Exception
//    {
//        DeviceDriverProvider ddp = DeviceFactory.getDeviceDriverProvider("PO_Simulator");
//        testDriverAdapter(ddp, true);
//    }

    private void testDriverAdapter(DeviceDriverProvider ddp, boolean isUI) throws Exception
    {
        final CountDownLatch stopOk = new CountDownLatch(1);

        System.setProperty("mdpnp.domain", "0");

        final AbstractApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"DriverContext.xml"});
        try {

            final DeviceAdapter da = isUI ?
                    new DeviceAdapter.GUIAdapter(ddp, context) : new DeviceAdapter.HeadlessAdapter(ddp, context, false);

            (new Thread(new Runnable() {
                @Override
                public void run() {
                    // start will block until stopped.
                    try {
                        da.start(null);
                        stopOk.countDown();
                        log.info("Adapter run loop complete");
                    }
                    catch(Exception ex)  {
                        log.error("Failed to start the adapter", ex);
                    }
                }
            }, "DeviceAdapterTest::testHeadlessAdapter")).start();

            // default 2 seconds, when need to debug start logic set test.timeout to smth larger.
            long timeout = Long.getLong("test.timeout", 2);
            Thread.sleep(timeout*1000);

            da.stop();

            // wait for the middleware to settle
            Thread.sleep(2000);

            boolean isOk=stopOk.await(timeout, TimeUnit.SECONDS);
            if(!isOk)
                Assert.fail("Failed to stop the adapter");


        }
        finally {
            context.destroy();
        }

        int n = Thread.activeCount() + 10;
        Thread[] threads = new Thread[n];
        n = Thread.enumerate(threads);
        for (int i = 0; i < n; i++) {
            if (threads[i].isAlive() && !threads[i].isDaemon() && !Thread.currentThread().equals(threads[i])) {
                log.warn("Non-Daemon thread could block exit: " + threads[i].getName());
            }
        }
    }

}
