package org.mdpnp.apps.testapp.export;


import org.mdpnp.apps.testapp.vital.Value;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

//
//CREATE TABLE VITAL_VALUES (METRIC_ID VARCHAR(25), TIME_TICK TIMESTAMP, INSTANCE_ID INTEGER, VITAL_VALUE DOUBLE)
//
class JdbcPersister extends FileAdapterApplicationFactory.PersisterUI implements DataCollector.DataSampleEventListener  {

    Connection conn = null;
    PreparedStatement ps = null;

    final JTextField fDriver   = new JTextField();
    final JTextField fURL      = new JTextField();
    final JTextField fUser     = new JTextField();
    final JTextField fPassword = new JPasswordField();

    public void persist(Value value) throws Exception {

        if(ps != null) {
            ps.setString(1, value.getMetricId());
            ps.setTimestamp(2, new Timestamp(value.getNumeric().device_time.sec));
            ps.setInt(3, value.getInstanceId());
            ps.setDouble(4, value.getNumeric().value);

            ps.execute();

            conn.commit();
        }
    }

    @Override
    public void handleDataSampleEvent(DataCollector.DataSampleEvent evt) throws Exception {
        Value vital = (Value)evt.getSource();
        persist(vital);
    }

    public boolean start() throws Exception {
        conn = createConnection();
        if(conn != null)
            ps = conn.prepareStatement("INSERT INTO VITAL_VALUES (METRIC_ID , TIME_TICK, INSTANCE_ID, VITAL_VALUE) VALUES(?,?,?,?)");
        return conn != null;
    }

    public void stop() throws Exception {
        if(ps != null) ps.close();
        if(conn != null) conn.close();
        ps = null;
        conn = null;
    }

    public Connection createConnection() throws Exception {

        String driver = fDriver.getText();
        String url = fURL.getText();
        String user = fUser.getText();
        String password= fPassword.getText();

        if(isEmpty(driver) || isEmpty(url) || isEmpty(user))
            return null;

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

    private static boolean isEmpty(String s) {
        return s == null || s.trim().length()==0;
    }

    public JdbcPersister() {

        super();

        this.setLayout(new GridLayout(0, 2));
        this.add(new JLabel("JDBC Driver", JLabel.RIGHT));  this.add(fDriver);
        this.add(new JLabel("Database URL", JLabel.RIGHT)); this.add(fURL);
        this.add(new JLabel("User", JLabel.RIGHT));         this.add(fUser);
        this.add(new JLabel("Password", JLabel.RIGHT));     this.add(fPassword);
        this.add(new JLabel(""));
        this.add(new JButton(new AbstractAction("Connect") {
            public void actionPerformed(ActionEvent e) {
                try {
                    stop();
                    boolean v=start();
                    JdbcPersister.this.setBackground(v ? Color.lightGray : Color.red);
                }
                catch (Exception ex) {
                    JdbcPersister.this.setBackground(Color.red);
                    JOptionPane.showMessageDialog(null,ex.getMessage(),"Error",JOptionPane.ERROR_MESSAGE);
                }
            }
        }) );

        fDriver.setText("org.hsqldb.jdbcDriver");
        fURL.setText("jdbc:hsqldb:hsql://localhost/testdb");
        //fUser.setText("SA");
        fPassword.setText("");
    }
}