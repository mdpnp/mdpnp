package org.mdpnp.apps.testapp.vital;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import javafx.application.Platform;

import org.junit.*;
import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.testapp.FxRuntimeSupport;
import org.mdpnp.apps.testapp.SimpleDeviceListModel;

public class VitalModelTest {

    private VitalModel model;
    private Vital heartRateVital;

    @BeforeClass
    public static void setUpClass() throws Exception {

        FxRuntimeSupport.initialize();
    }
    
    @Before
    public void setUp() throws Exception {
        model = new VitalModelImpl(new SimpleDeviceListModel(), new NumericFxList(ice.NumericTopic.VALUE));
        
    }

    @After
    public void tearDown() throws Exception {
        model = null;
        
    }

    protected Error t;
    private void testOnFxThread(Runnable run) throws InterruptedException {
        t = null;
        final CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(new Runnable() {
            public void run() {
                try {
                    run.run();
                } catch (Error t1) {
                    t = t1;
                }
                
                latch.countDown();
            }
        });
        latch.await();
        if(null != t) {
            throw t;
        }
    }
    
    @Test
    public void testAddHeartRateVitalSign() throws InterruptedException {
        testOnFxThread(() -> {
            heartRateVital = VitalSign.HeartRate.addToModel(model);
            heartRateVital.setWarningLow(50.0);
            assertEquals(1, model.size());
        });
    }
    
    @Test
    public void testAddNumericAndGenerateNoWarning() throws InterruptedException {
        testOnFxThread(() -> {
            heartRateVital = VitalSign.HeartRate.addToModel(model);
            heartRateVital.setWarningLow(50.0);
            NumericFx numeric = new NumericFx();
            numeric.setUnique_device_identifier("ABC");
            numeric.setMetric_id("METRIC");
            numeric.setInstance_id(0);
            numeric.setPresentation_time(new Date());
            numeric.setSource_timestamp(new Date());
            numeric.setDevice_time(new Date());
            numeric.setValue(60f);
            model.addNumeric(numeric);
            assertEquals("", model.getWarningText());
        });
    }
    
    @Test
    public void testAddNumericAndGenerateWarning() throws InterruptedException {
        testOnFxThread(() -> {
            heartRateVital = VitalSign.HeartRate.addToModel(model);
            heartRateVital.setWarningLow(50.0);
            NumericFx numeric = new NumericFx();
            numeric.setUnique_device_identifier("ABC");
            numeric.setMetric_id(rosetta.MDC_ECG_HEART_RATE.VALUE);
            numeric.setInstance_id(0);
            numeric.setPresentation_time(new Date());
            numeric.setSource_timestamp(new Date());
            numeric.setDevice_time(new Date());
            numeric.setValue(40f);
            model.addNumeric(numeric);
            assertNotEquals("", model.getWarningText());
        });
    }

    @Test
    public void testVitalSignLookupTable() {

        Map lookup = VitalSign.buildVitalSignLookupTable();
        Assert.assertEquals(lookup.get(rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE), VitalSign.HeartRate);
    }
}
