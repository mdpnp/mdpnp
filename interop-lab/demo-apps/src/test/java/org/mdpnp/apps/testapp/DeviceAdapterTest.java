package org.mdpnp.apps.testapp;

import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.devices.DeviceDriverProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Observable;
import java.util.Observer;
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

        final AbstractApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"DriverContext.xml"});

        try {
            final DeviceAdapterFxApp app = new DeviceAdapterFxApp(ddp, context);

            FxRuntimeSupport fxRt = FxRuntimeSupport.initialize();

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
            context.destroy();
        }
    }


    public static class DeviceAdapterFxApp extends IceApplication {
        final DeviceAdapterWrapper daw;

        public DeviceAdapterFxApp(DeviceDriverProvider ddp, AbstractApplicationContext context) {
            daw = new DeviceAdapterWrapper(ddp, context)
            {
                @Override
                DeviceAdapter makeDeviceAdapter(DeviceDriverProvider ddp, AbstractApplicationContext context) {
                    return new DeviceAdapter.GUIAdapter(ddp, context);
                }
            };
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
            daw.start(primaryStage);
        }

        @Override
        public void stop() throws Exception {
            daw.stop();
            super.stop();
        }
    }


    private void testHeadlessAdapter(DeviceDriverProvider ddp) throws Exception {

        final AbstractApplicationContext context = new ClassPathXmlApplicationContext(new String[]{"DriverContext.xml"});

        try {
            DeviceAdapterWrapper daw = new DeviceAdapterWrapper(ddp, context)
            {
                @Override
                DeviceAdapter makeDeviceAdapter(DeviceDriverProvider ddp, AbstractApplicationContext context) {
                    return new DeviceAdapter.HeadlessAdapter(ddp, context, false);
                }
            };

            daw.start(null);

            // stay up for a little while to show user something is going on
            Thread.sleep(adapterUptime * 1000);

            boolean isOk=daw.stop();
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

    /**
     * utility class to wrap multi threaded start/stop of the DeviceAdapter in a non-blocking
     * shell usable for testing.
     */
    public static abstract class DeviceAdapterWrapper {
        final DeviceDriverProvider ddp;
        final AbstractApplicationContext context;

        DeviceAdapter da;

        public DeviceAdapterWrapper(DeviceDriverProvider ddp, AbstractApplicationContext context) {
            this.ddp = ddp;
            this.context = context;
        }

        public DeviceAdapter start(Stage parentStage) throws Exception {
            da = makeDeviceAdapter(ddp, context);
            da.initializeDevice();

            final CountDownLatch upAndRunning = new CountDownLatch(1);

            da.addObserver(new Observer() {
                @Override
                public void update(Observable o, Object arg) {
                    if(DeviceAdapter.AdapterState.running.equals(arg))
                        upAndRunning.countDown();
                }
            });

            (new Thread(new Runnable() {
                @Override
                public void run() {
                    // start will block until stopped.
                    try {
                        da.run(null);
                        log.info("Adapter run loop complete");
                    }
                    catch(Exception ex)  {
                        log.error("Failed to run the adapter", ex);
                    }
                }
            }, "DeviceAdapterTest::testDriverAdapter")).start();

            upAndRunning.await();
            return da;
        }

        /**
         * @param ddp
         * @param context
         * @return an instance of an adapter suitable for headless or UI-based interfaces.
         */
        abstract DeviceAdapter makeDeviceAdapter(DeviceDriverProvider ddp, AbstractApplicationContext context);

        public boolean stop() throws Exception {
            da.stop();
            return true;
        }
    }

}
