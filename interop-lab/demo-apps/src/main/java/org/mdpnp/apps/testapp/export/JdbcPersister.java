package org.mdpnp.apps.testapp.export;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.google.common.eventbus.Subscribe;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class JdbcPersister extends FileAdapterApplicationFactory.PersisterUIController {

    private Connection conn = null;
    private PreparedStatement ps = null;

    @FXML TextField fDriver, fURL, fUser;
    @FXML PasswordField fPassword;

    public void persist(Value value) throws Exception {

        if(ps != null) {
            ps.setString   (1, value.getUniqueDeviceIdentifier());
            ps.setString   (2, value.getMetricId());
            ps.setInt      (3, value.getInstanceId());
            ps.setTimestamp(4, new java.sql.Timestamp(value.getDevTime()));
            ps.setDouble   (5, value.getValue());

            ps.execute();

            conn.commit();
        }
    }

    @Subscribe
    public void handleDataSampleEvent(NumericsDataCollector.NumericSampleEvent evt) throws Exception {
        Value vital = evt.getValue();
        persist(vital);
    }

    static void createSchema(Connection conn) throws SQLException {
        conn.createStatement().execute( "CREATE TABLE VITAL_VALUES " +
                                        "(DEVICE_ID VARCHAR(25), " +
                                        "METRIC_ID VARCHAR(25), " +
                                        "INSTANCE_ID INTEGER, " +
                                        "TIME_TICK TIMESTAMP, " +
                                        "VITAL_VALUE DOUBLE)");
    }

    @Override
    public String getName() {
        return "sql";
    }

    @Override
    public boolean start() throws Exception {
        conn = createConnection();
        if(conn != null)
            ps = conn.prepareStatement("INSERT INTO VITAL_VALUES (DEVICE_ID, METRIC_ID, INSTANCE_ID, TIME_TICK, VITAL_VALUE) VALUES(?,?,?,?,?)");
        return conn != null;
    }

    @Override
    public void stop() throws Exception {
        if(ps != null) ps.close();
        if(conn != null) conn.close();
        ps = null;
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