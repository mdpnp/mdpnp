package org.mdpnp.apps.testapp.export;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcPersister extends DataCollectorAppFactory.PersisterUIController {

    private static final Logger log = LoggerFactory.getLogger(JdbcPersister.class);

    private Connection conn = null;
    private PreparedStatement insertVital = null;
    private PreparedStatement insertObservation = null;

    @FXML TextField fDriver, fURL, fUser;
    @FXML PasswordField fPassword;

    void persistVital(DataCollector.DataSampleEvent value, long ms, double v) throws Exception {

        if(insertVital != null) {
            insertVital.setString   (1, value.getUniqueDeviceIdentifier());
            insertVital.setString   (2, value.getMetricId());
            insertVital.setInt      (3, value.getInstanceId());
            insertVital.setTimestamp(4, new java.sql.Timestamp(ms));
            insertVital.setString   (5, value.getPatientId());
            insertVital.setDouble   (6, v);

            insertVital.execute();

            conn.commit();
        }
    }

    void persistObservation(DataCollector.DataSampleEvent value, long ms, String v) throws Exception {

        if(insertObservation != null) {
            insertObservation.setString   (1, value.getUniqueDeviceIdentifier());
            insertObservation.setTimestamp(2, new java.sql.Timestamp(ms));
            insertObservation.setString   (3, value.getPatientId());
            insertObservation.setString   (4, v);

            insertObservation.execute();

            conn.commit();
        }
    }

    @Subscribe
    public void handleDataSampleEvent(NumericsDataCollector.NumericSampleEvent evt) throws Exception {
        persistVital(evt, evt.getDevTime(), evt.getValue());
    }

    @Subscribe
    public void handleDataSampleEvent(SampleArrayDataCollector.SampleArrayEvent evt) throws Exception {
        SampleArrayDataCollector.ArrayToNumeric.convert(evt, (DataCollector.DataSampleEvent meta, long ms, double v)->{
            persistVital(meta, ms, v);
        });
    }


    @Subscribe
    public void handleDataSampleEvent(PatientAssessmentDataCollector.PatientAssessmentEvent evt) throws Exception {
        persistObservation(evt, evt.getDevTime(), evt.getValue().getKey());
    }





    static void createSchema(Connection conn) throws SQLException {
        conn.createStatement().execute( "CREATE TABLE VITAL_VALUES " +
                                        "(DEVICE_ID VARCHAR(25), " +
                                        "METRIC_ID VARCHAR(25), " +
                                        "INSTANCE_ID INTEGER, " +
                                        "TIME_TICK TIMESTAMP, " +
                                        "PATIENT_ID VARCHAR(25), " +
                                        "VITAL_VALUE DOUBLE)");

        conn.createStatement().execute( "CREATE TABLE OBSERVATION_VALUES " +
                                        "(MD_ID VARCHAR(25), " +
                                        "TIME_TICK TIMESTAMP, " +
                                        "PATIENT_ID VARCHAR(25), " +
                                        "OBSERVATION VARCHAR(255))");
    }

    @Override
    public String getName() {
        return "sql";
    }

    @Override
    public boolean start() throws Exception {
        conn = createConnection();
        if(conn != null) {
            insertVital = conn.prepareStatement("INSERT INTO VITAL_VALUES (DEVICE_ID, METRIC_ID, INSTANCE_ID, TIME_TICK, PATIENT_ID, VITAL_VALUE) VALUES(?,?,?,?,?,?)");
            insertObservation = conn.prepareStatement("INSERT INTO OBSERVATION_VALUES (MD_ID, TIME_TICK, PATIENT_ID, OBSERVATION) VALUES(?,?,?,?)");
        }
        return conn != null;
    }

    @Override
    public void stop() throws Exception {
        if(insertVital != null) insertVital.close();
        if(conn != null) conn.close();
        insertVital = null;
        conn = null;
    }

    Connection createConnection() throws Exception {

        String driver = fDriver.getText();
        String url = fURL.getText();
        String user = fUser.getText();
        String password = fPassword.getText();

        if (isEmpty(driver) || isEmpty(url) || isEmpty(user))
            return null;

        return createConnection(driver, url, user, password);
    }

    Connection createConnection(String driver, String url, String user, String password) throws Exception {

        try {
            Class.forName(driver.trim());
        }
        catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Invalid driver: " + driver.trim());
        }

        Connection conn= DriverManager.getConnection(url.trim(), user.trim(), password.trim());
        if(conn == null)
            throw new IllegalStateException("Failed to create a connection");
        return conn;
    }

    Connection getConnection() {
        return conn;
    }

    private static boolean isEmpty(String s) {
        return s == null || s.trim().length()==0;
    }
    
    @Override
    public void setup() {
        
    }

    public JdbcPersister() {

        super();
    }
}