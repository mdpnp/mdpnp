package org.mdpnp.devices.fhir;

import ca.uhn.fhir.model.dstu2.resource.Observation;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;
import com.sun.jna.IntegerType;
import com.sun.jna.Pointer;
import ice.ConnectionType;
import ice.Numeric;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.junit.*;
import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.devices.simulation.NumberWithJitter;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import com.sun.jna.Native;

/**
 * @author mfeinberg
 */
public class FhirDeviceTest {

    private static final Logger log = LoggerFactory.getLogger(FhirDevice.class);

    private static final long   HASHORN_UPTIME=Long.parseLong(System.getProperty("org.mdpnp.devices.HashornDevice.uptimeMs", "5000"));

    ConfigurableApplicationContext ctx=null;

    @Before
    public void setUp() throws Exception {

        String os = System.getProperty("os.name").toLowerCase();
        org.junit.Assume.assumeTrue("No gonna look for libc on windows", !isWindows(os));

        ctx = createContext();
    }

    private static boolean isWindows(String os) {
        return (os.indexOf("win") >= 0);
    }

    @After
    public void tearDown() {
        if(ctx != null) {
            ctx.close();
            ctx = null;
        }
    }

    @Test
    public void testObservationOnIce() throws Exception {

        Observation obs = FhirDevice.createObservation(rosetta.MDC_CO2_RESP_RATE.VALUE, Math.random(), new Date());

        FhirDevice.ObservationConverter converter = new FhirDevice.ObservationConverter() {
            AbstractDevice.InstanceHolder<Numeric> getInstanceHolderForCode(String code) {
                return null;
            }
        };
        FhirDevice.ObservationConverter.NumericObservation ice = converter.observationOnIce(obs);
        Assert.assertNotNull("Failed to convert observation to ice", ice);
    }


    @Test
    public void testDeviceSetup() throws Exception {

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        final Subscriber subscriber = ctx.getBean("subscriber", Subscriber.class);
        final Publisher  publisher  = ctx.getBean("publisher", Publisher.class);
        final EventLoop eventLoop   = ctx.getBean("eventLoop", EventLoop.class);

        FhirDevice device = new FhirDeviceImpl(subscriber, publisher, eventLoop);
        device.setExecutor(executor);
        device.init();
        device.connect("");

        Thread.sleep(5000);
        device.disconnect();
        Thread.sleep(1000);
        device.shutdown();
    }

    @Test
    public void testHashornAPIBridge() throws Exception {

        ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
        InputStream is=getClass().getResourceAsStream("JsDeviceDriver0.js");
        engine.eval(new InputStreamReader(is));

        Invocable invocable = (Invocable) engine;

        invocable.invokeFunction("start");
        Object result = invocable.invokeFunction("readObservations");
        invocable.invokeFunction("stop");

        Date asOf = new Date();

        Set<Observation> set = new HashSet<>();

        ScriptObjectMirror som = (ScriptObjectMirror)result;
        if(som.isArray()) {
            Set<String> keys = som.keySet();
            for(String key: keys) {
                ScriptObjectMirror o = (ScriptObjectMirror) som.get(key);
                String code = (String) ((ScriptObjectMirror) o.get("valueQuantity")).get("code");
                Number value = (Number) ((ScriptObjectMirror) o.get("valueQuantity")).get("value");
                set.add(FhirDevice.createObservation(code, value.doubleValue(), asOf));
            }
        }
        Assert.assertTrue("Failed to populate dataset", set.size() != 0);
    }


    @Test
    public void testLibCFFIBridge() throws Exception {

        final int uid = LibC.getuid();
        Assert.assertTrue(uid != 0);

        final time_t tt = LibC.time(null);
        Assert.assertTrue(tt != null);
    }

    @Test
    public void testNashornDeviceSetup() throws Exception {

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        final Subscriber subscriber = ctx.getBean("subscriber", Subscriber.class);
        final Publisher  publisher  = ctx.getBean("publisher", Publisher.class);
        final EventLoop eventLoop   = ctx.getBean("eventLoop", EventLoop.class);

        final String jsFile = "/org/mdpnp/devices/fhir/JsDeviceDriver0.js";

        FhirDevice device = new JsFhirDevice(jsFile, subscriber, publisher, eventLoop);
        device.setExecutor(executor);
        device.init();
        device.connect("");

        Thread.sleep(HASHORN_UPTIME>0?HASHORN_UPTIME:Long.MAX_VALUE);

        log.info("Shutting down the test");
        device.disconnect();
        Thread.sleep(1000);
        device.shutdown();
    }

    private ConfigurableApplicationContext createContext() throws Exception {

        ClassPathXmlApplicationContext ctx =
                new ClassPathXmlApplicationContext(new String[] { "RtConfig.xml" }, false);
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        Properties props = new Properties();
        props.load(getClass().getResourceAsStream("/RtConfig.properties"));
        ppc.setProperties(props);
        ppc.setOrder(0);

        ctx.addBeanFactoryPostProcessor(ppc);
        ctx.refresh();
        return ctx;
    }


    public static class FhirDeviceImpl extends FhirDevice {

        protected static final long UPDATE_PERIOD = 1000L;

        public FhirDeviceImpl(Subscriber subscriber, Publisher publisher, EventLoop eventLoop) {
            super(subscriber, publisher, eventLoop);
            AbstractSimulatedDevice.randomUDI(deviceIdentity);
            writeDeviceIdentity();
        }

        protected Set<Observation> getObservations(DeviceClock.Reading t) {

            Date asOf = new Date(t.getTime().toEpochMilli());
            int rRate = respiratoryRate.intValue();
            int co2   = etCO2.intValue();

            Set<Observation> set = new HashSet<>();
            set.add(createObservation(rosetta.MDC_CO2_RESP_RATE.VALUE, rRate, asOf));
            set.add(createObservation(rosetta.MDC_AWAY_CO2_ET.VALUE,   co2,   asOf));

            return set;
        }

        @Override
        protected long getSampleRateMs() {
            return UPDATE_PERIOD;
        }


        @Override
        protected ConnectionType getConnectionType() {
            return ConnectionType.Simulated;
        }

        private Number respiratoryRate = new NumberWithJitter(13, 1, 5);
        private Number etCO2 = new NumberWithJitter(29, 1, 5);
    }


    // Simplistic wrappers around c library (which should be there :-)) to demonstrate
    // native calls from the javascript driver.
    //
    public static class time_t extends IntegerType {
        public time_t() {
            super(Native.LONG_SIZE);
        }
    }

    public static class LibC {

        static {
            Native.register("c");
        }

        public static native time_t time (Pointer unused);

        public static native int getuid();

        public static native int getgid();

        private LibC() {
        }

    }
}
