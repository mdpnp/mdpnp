package org.mdpnp.apps.testapp.patient;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.EmbeddedDB;
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

    /**
     * test jdbc back end for the app.
     */
    @Test
    public void testDbCreate() throws Exception {

        EmbeddedDB db = new EmbeddedDB("jdbc:hsqldb:mem:icepatientdb");

        try {
            Connection conn = db.getDataSource().getConnection();
            InputStream is0 = getClass().getResourceAsStream("DbSchema.sql");
            EmbeddedDB.applySchemaFile(conn, is0);
            InputStream is1 = getClass().getResourceAsStream("DbData.0.sql");
            EmbeddedDB.applySchemaFile(conn, is1);

            ResultSet rs = conn.createStatement().executeQuery("SELECT COUNT(*) FROM  PATIENT_INFO");
            rs.next();
            int sz=rs.getInt(1);
            Assert.assertEquals("Could not get size of Simpsons' family", 5, sz);

        } finally {
            db.destroy();
        }
    }


    /**
     * Over all test for the patient assignment app. As a part of automated test
     * suite will bring the app up, populate it with mock data and then bring the
     * ui down. if 'PatientApplicationFactoryTest.UpTimeMS' environment variable
     * set to -1, this test wil not exit automatically and ui will stay up forever.
     * It is suitable to be used a boot-strap entry point for app development.
     */
    @Test
    public void testAppSetupViaSpring() throws Exception {

        final CountDownLatch stopOk = new CountDownLatch(1);

        final ApplicationExt app = new ApplicationExt() {
            @Override
            public void stop() throws Exception {
                super.stop();
                stopOk.countDown();
            }
        };

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

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            ui.close();
                        }
                    });
                }
            } catch (Exception ex) {
                log.error("failed to run data pump", ex);
            }

        });
        guillotine.start();

        try {
            // if <0, keep the ui up forever - used to debugging.
            if (UI_UP_MS > 0) {
                boolean isOk = stopOk.await(5 * UI_UP_MS, TimeUnit.MILLISECONDS);
                if (!isOk)
                    Assert.fail("Failed to close the dialog");
            } else {
                stopOk.await();
            }
        }
        finally {
            app.stop();
        }
    }


    private long dataPump(final ApplicationExt app ) {
        long wait = 0;
        try {
            for (int i = 0; i < N_DATA_POINTS; i++) {
                Thread.sleep(500);
                wait += 500;

                final Device d = new Device(Integer.toHexString(i));
                d.setHostname("192.168.1." + i);
                d.setMakeAndModel(devNames[i%devNames.length]);

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        app.handleDeviceLifecycleEvent(d);
                    }
                });
            }

            // Now that the lists are populated, fake the assignment event
            //
            Thread.sleep(500);
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    Parent ui = app.app.getUI();
                    ((TableView)ui.lookup("#patientView")).getSelectionModel().select(0);
                    ((TableView)ui.lookup("#deviceView")).getSelectionModel().select(0);
                    ui.lookup("#connectBtn").fireEvent(new ActionEvent());
                }
            });
            Thread.sleep(500);


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

            // 'real' controller will send out the 'proposed device association event', wait for the
            // device to respond, and only after that update the database and ui. For the
            // purpose of testing, we do not want to sent events out as there as the devices
            // are faked and go directly to handling the event as if the response came back.
            //
            EventHandler<ActionEvent> ae = new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    Device d = app.getSelectedDevice();
                    PatientInfo p = app.getSelectedPatient();
                    if (d != null && p != null) {
                        app.addDeviceAssociation(d, p);
                    }
                }
            };
            app.setConnectHandler(ae);
        }

        void handleDeviceLifecycleEvent(Device d) {
            app.handleDeviceLifecycleEvent(d, true);
        }

        @Override
        public void stop() throws Exception {
            app.destroy();
            super.stop();
            context.destroy();
        }
    };

    private static final int N_DATA_POINTS = 5;
    private static final int UI_UP_MS = Integer.getInteger("PatientApplicationFactoryTest.UpTimeMS", N_DATA_POINTS*500+1000);

    private static final String [] devNames = {
            "Pulse Oximeter",
            "ElectroCardioGram",
            "Capnometer",
            "Prosim 6/8",
            "Intellivue (MIB/RS232)",
            "N-595",
            "Dräger Apollo",
            "Dräger EvitaXL",
            "Dräger V500",
            "Zephyr BioPatch"
    };
}
