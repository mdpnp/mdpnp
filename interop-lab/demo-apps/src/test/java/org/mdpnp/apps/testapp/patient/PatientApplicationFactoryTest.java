package org.mdpnp.apps.testapp.patient;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.hsqldb.jdbc.JDBCDataSource;
import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.RtConfig;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;

public class PatientApplicationFactoryTest {

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

        ApplicationExt.launch(ApplicationExt.class, new String[]{});
    }

    public static class ApplicationExt extends javafx.application.Application
    {
        PatientApplicationFactory.PatientApplication app;
        AbstractApplicationContext context;

        @Override
        public void start(Stage primaryStage) throws Exception {

            RtConfig.loadAndSetIceQos();

            context = new ClassPathXmlApplicationContext(new String[]{"IceAppContainerContext.xml"});

            PatientApplicationFactory factory = new PatientApplicationFactory();
            app = (PatientApplicationFactory.PatientApplication) factory.create(context);

            final Stage dialog = new Stage(StageStyle.DECORATED);
            dialog.setAlwaysOnTop(false);
            Scene scene = new Scene(app.getUI());
            dialog.setScene(scene);
            dialog.sizeToScene();
            dialog.show();

            Platform.runLater(new Runnable() {
                public void run() {
                    app.activate(context);
                }
            });


            // Pump the data into the screen and then shut down the app.
            Thread guillotine = new Thread(() -> {
                try {
                    long upTime = dataPump();
                    // if 0, keep the ui up forever - used to debugging.
                    if(UI_UP_MS>0) {
                        upTime = UI_UP_MS - upTime;
                        if (upTime > 0)
                            Thread.sleep(upTime);
                    }

                    dialog.close();
                    ApplicationExt.this.stop();
                } catch (Exception e) {
                    // too bad
                }

            });
            guillotine.start();
        }

        private long dataPump() {
            long wait = 0;
            try {
                for (int i = 0; i < 30; i++) {
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


        @Override
        public void stop() throws Exception {
            app.destroy();
            super.stop();
            context.destroy();
        }
    };

    private static final int UI_UP_MS = Integer.getInteger("PatientApplicationFactoryTest.UpTimeMS", 0);

}
