package org.mdpnp.apps.testapp.export;


import org.mdpnp.apps.testapp.vital.Value;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;


class JdbcPersister extends FileAdapterApplicationFactory.PersisterUI implements DataCollector.DataSampleEventListener  {

    private Connection conn = null;
    private PreparedStatement ps = null;

    final JTextField fDriver   = new JTextField();
    final JTextField fURL      = new JTextField();
    final JTextField fUser     = new JTextField();
    final JTextField fPassword = new JPasswordField();

    public void persist(Value value) throws Exception {

        if(ps != null) {
            ps.setString   (1, value.getUniqueDeviceIdentifier());
            ps.setString   (2, value.getMetricId());
            ps.setInt      (3, value.getInstanceId());
            ps.setTimestamp(4, new Timestamp(DataCollector.toMilliseconds(value.getNumeric().device_time)));
            ps.setDouble   (5, value.getNumeric().value);

            ps.execute();

            conn.commit();
        }
    }

    @Override
    public void handleDataSampleEvent(DataCollector.DataSampleEvent evt) throws Exception {
        Value vital = (Value)evt.getSource();
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

    public JdbcPersister() {

        super();

        this.setLayout(new GridLayout(0, 2));
        this.add(new JLabel("JDBC Driver", JLabel.RIGHT)); this.add(fDriver);
        this.add(new JLabel("Database URL",JLabel.RIGHT)); this.add(fURL);
        this.add(new JLabel("User",        JLabel.RIGHT)); this.add(fUser);
        this.add(new JLabel("Password",    JLabel.RIGHT)); this.add(fPassword);

        fDriver.setText("org.hsqldb.jdbcDriver");
        fURL.setText("jdbc:hsqldb:hsql://localhost/testdb");
        //fUser.setText("SA");
        fPassword.setText("");
    }
}