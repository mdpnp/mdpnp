package org.mdpnp.apps.testapp.patient;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.FxRuntimeSupport;
import org.mdpnp.apps.testapp.IceApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.InputStream;
import java.sql.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PatientApplicationFactoryTest {

    private static final Logger log = LoggerFactory.getLogger(PatientApplicationFactoryTest.class);

    @Test
    public void testDbCreate() throws Exception {

        PatientApplicationFactory.EmbeddedDB ds = new PatientApplicationFactory.EmbeddedDB();

        try {
            Connection conn = ds.getConnection();
            InputStream is0 = getClass().getResourceAsStream("DbSchema.sql");
            PatientApplicationFactory.EmbeddedDB.applySchemaFile(conn, is0);
            InputStream is1 = getClass().getResourceAsStream("DbData.0.sql");
            PatientApplicationFactory.EmbeddedDB.applySchemaFile(conn, is1);

            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM  PATIENT_INFO");
            rs.next();
            int sz=rs.getInt(1);
            Assert.assertEquals("Could not get size of Simpsons' family", 5, sz);

        } finally {
            ds.shutdown();
        }
    }


    @Test
    public void testAppSetupViaSpring() throws Exception {

        final CountDownLatch stopOk = new CountDownLatch(1);

        final ApplicationExt app = new ApplicationExt();

        Stage ui = FxRuntimeSupport.initialize().show(app);
        Assert.assertNotNull(ui);

        // Pump the data into the screen and then shut down the app.
        Thread guillotine = new Thread(() -> {
            try {
                long upTime = dataPump(app);
                // if <0, keep the ui up forever - used to debugging.
                if(UI_UP_MS>0) {
                    upTime = UI_UP_MS - upTime;
                    if (upTime > 0)
                        Thread.sleep(upTime);
                    else
                        Assert.fail("Bad test configuration; uptime < data population");
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        ui.close();
                        stopOk.countDown();
                    }
                });
            } catch (Exception ex) {
                log.error("failed to run data pump", ex);
            }

        });
        guillotine.start();

        // if <0, keep the ui up forever - used to debugging.
        if(UI_UP_MS>0) {
            boolean isOk = stopOk.await(2 * UI_UP_MS, TimeUnit.MILLISECONDS);
            app.stop();
            if (!isOk)
                Assert.fail("Failed to close the dialog");
        }
    }


    private long dataPump(final ApplicationExt app ) {
        long wait = 0;
        try {
            for (int i = 0; i < N_DATA_POINTS; i++) {
                Thread.sleep(1000);
                wait += 1000;

                final PatientInfo p = new PatientInfo("PATIENT-" + i);
                final Device d = new Device("ID-" + i);
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        app.addDeviceAssociation(d, p);
                    }
                });
            }
        } catch (Exception ex) {
        }
        return wait;
    }

    public static class ApplicationExt extends IceApplication
    {
        PatientApplicationFactory.PatientApplication app;
        AbstractApplicationContext context;

        @Override
        public void start(Stage primaryStage) throws Exception {

            context = new ClassPathXmlApplicationContext(new String[]{"IceAppContainerContext.xml"});

            PatientApplicationFactory factory = new PatientApplicationFactory();
            app = (PatientApplicationFactory.PatientApplication) factory.create(context);

            Scene scene = new Scene(app.getUI());
            primaryStage.setScene(scene);

            app.activate(context);
        }

        public void addDeviceAssociation(Device d, PatientInfo p) {
            app.addDeviceAssociation(d, p);
        }

        @Override
        public void stop() throws Exception {
            app.destroy();
            super.stop();
            context.destroy();
        }
    };

    private static final int N_DATA_POINTS = 10;
    private static final int UI_UP_MS = Integer.getInteger("PatientApplicationFactoryTest.UpTimeMS", N_DATA_POINTS*1000+2000);

}
