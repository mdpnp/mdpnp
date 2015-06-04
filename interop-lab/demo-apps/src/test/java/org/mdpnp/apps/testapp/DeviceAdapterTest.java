package org.mdpnp.apps.testapp;

import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.devices.DeviceDriverProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class DeviceAdapterTest {

    private final static Logger log = LoggerFactory.getLogger(DeviceAdapterTest.class);

    static {
        System.setProperty("mdpnp.domain", "0");
    }

    private long adapterUptime = Long.getLong("test.timeout", 5);


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
        testHeadlessAdapter(ddp);
    }

    @Test
    public void testGUIAdapter() throws Exception
    {
        DeviceDriverProvider ddp = DeviceFactory.getDeviceDriverProvider("PO_Simulator");
        testGUIAdapter(ddp);
    }

    private void testGUIAdapter(DeviceDriverProvider ddp) throws Exception {

        final AbstractApplicationContext context =
                new ClassPathXmlApplicationContext(new String[]{"DeviceAdapterContext.xml"});

        try {
            FxRuntimeSupport fxRt = FxRuntimeSupport.initialize();

            IceApplication app = new DeviceAdapterCommand.GUIAdapter(ddp, context);
            app.init();

            final Stage ui = fxRt.show(app);
            Assert.assertNotNull(ui);

            // stay up for a little while to show user something is going on
            Thread.sleep(adapterUptime * 1000);

            boolean isOk=fxRt.run((new Callable<Boolean>() {
                    @Override
                    public Boolean call() throws Exception {
                        app.stop();
                        return Boolean.TRUE;
                    }
            }));

            if(!isOk)
                Assert.fail("Failed to stop the adapter");
        }
        catch(Exception ex) {
            log.error("Failed to run the adapter", ex);
            throw ex;
        }
        finally {
            // technically, we do not need this - app::close will shut down the context, but have
            // it here just in case of a colossal failure.
            context.destroy();
        }
    }

    private void testHeadlessAdapter(DeviceDriverProvider ddp) throws Exception {

        final AbstractApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"DeviceAdapterContext.xml"});

        try {
            DeviceAdapterCommand.HeadlessAdapter da = new DeviceAdapterCommand.HeadlessAdapter(ddp, context, false);

            final CountDownLatch runCompleted = new CountDownLatch(1);

            da.init();

            (new Thread(new Runnable() {
                @Override
                public void run() {
                    // start will block until stopped.
                    try {
                        da.run();
                        runCompleted.countDown();
                        log.info("Adapter run loop complete");
                    }
                    catch(Exception ex)  {
                        log.error("Failed to run the adapter", ex);
                    }
                }
            }, "DeviceAdapterTest::testDriverAdapter")).start();


            // stay up for a little while to show user something is going on
            Thread.sleep(adapterUptime * 1000);

            da.stop();
            boolean isOk=runCompleted.await(5, TimeUnit.SECONDS);
            if(!isOk)
                Assert.fail("Failed to stop the adapter");
        }
        catch(Exception ex) {
            log.error("Failed to run the adapter", ex);
            throw ex;
        }
        finally {
            context.destroy();
        }
    }
}
