package org.mdpnp.apps.testapp.vital;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.concurrent.CountDownLatch;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mdpnp.apps.testapp.SimpleDeviceListModel;
import org.mdpnp.apps.testapp.pca.VitalSign;

public class TestVitalModel {

    private VitalModel model;
    private Vital heartRateVital;
    protected static CountDownLatch latch = new CountDownLatch(1);
    
    public static class AsNonApp extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            latch.countDown();
        }
        
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        new Thread(new Runnable() {
            public void run() {
                Application.launch(AsNonApp.class);
            }
        }).start();
        latch.await();
    }
    
    @Before
    public void setUp() throws Exception {
        model = new VitalModelImpl(new SimpleDeviceListModel());
        
    }

    @After
    public void tearDown() throws Exception {
        model = null;
        
    }
    
    @AfterClass
    public static void tearDownClass() throws Exception {
        Platform.exit();
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
            model.updateNumeric("ABC", "METRIC", 0, System.currentTimeMillis(), 60f);
            assertEquals("", model.getWarningText());
        });
    }
    
    @Test
    public void testAddNumericAndGenerateWarning() throws InterruptedException {
        testOnFxThread(() -> {
            heartRateVital = VitalSign.HeartRate.addToModel(model);
            heartRateVital.setWarningLow(50.0);
            model.updateNumeric("ABC", rosetta.MDC_ECG_HEART_RATE.VALUE, 0, System.currentTimeMillis(), 40f);
            assertNotEquals("", model.getWarningText());
        });
    }    

    
}
