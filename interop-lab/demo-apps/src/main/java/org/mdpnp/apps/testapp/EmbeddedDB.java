package org.mdpnp.apps.testapp;

import org.hsqldb.jdbc.JDBCDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 *
 */
@SuppressWarnings("serial")
public class EmbeddedDB implements FactoryBean<JDBCDataSource>, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedDB.class);

    private String schemaDef;
    private String dataDef;
    private String dbURL;
    private Properties properties = new Properties();

    private ControlFlowHandler controlFlowHandler;
    private JDBCDataSource     instance = null;

    public EmbeddedDB() {
    }

    // jdbc:hsqldb:mem:icepatientdb
    // jdbc:hsqldb:file:icepatientdb
    //

    public EmbeddedDB(String url) {
        this();
        this.setUrl(url);
    }

    public String getSchemaDef() {
        return schemaDef;
    }

    public void setSchemaDef(String schemaDef) {
        this.schemaDef = schemaDef;
    }

    public String getDataDef() {
        return dataDef;
    }

    public void setDataDef(String dataDef) {
        this.dataDef = dataDef;
    }

    public String getUrl() {
        return dbURL;
    }

    public void setUrl(String dbURL) {
        this.dbURL = dbURL;
    }

    public void setControlFlowHandler(ControlFlowHandler controlFlowHandler) {
        this.controlFlowHandler = controlFlowHandler;
    }

    public ControlFlowHandler getControlFlowHandler() {
        return controlFlowHandler;
    }


    @Override
    public JDBCDataSource getObject() throws Exception {
        if(null == instance) {
            init();
        }
        return instance;
    }

    public JDBCDataSource getDataSource() throws Exception {
        return getObject();
    }

    @Override
    public Class<JDBCDataSource> getObjectType() {
        return JDBCDataSource.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() throws Exception {
        if(instance != null) {
            Connection conn = instance.getConnection();
            conn.createStatement().execute("SHUTDOWN");
        }
    }

    private void init() throws Exception {

        if(!verifyInstance()) {
            throw new ControlFlowHandler.ConfirmedError("Failed to initialize database.");
        }

        instance = new JDBCDataSource();
        instance.setUser("sa");
        instance.setPassword("");
        instance.setUrl(dbURL);
        instance.setProperties(properties);


        int v = getSchemaVersion();
        if (v < 1) {
            load(schemaDef);
            load(dataDef);
        }

    }

    private boolean verifyInstance() {

        // assume url is of the following format:
        //
        // jdbc:hsqldb:file:icepatientdb
        //
        String t[] = getUrl().split("[:]");
        switch(t[2]) {
            case "file":
                File f = new File(t[3] + ".lck");
                if(f.exists()) {
                    boolean ok = controlFlowHandler.confirmError(
                            "Multiple Instances of ICE Supervisor detected.",
                            "It appears that another instance of ICE supervisor is already running on this computer." +
                            "You can run a new instance, but patient management app will be disabled." +
                            "This could also be caused by inconsistent state of environment due to a system crash. " +
                            "If you suspect this could be a case, remove " + f.getAbsolutePath() +
                            " and restart the application",
                            false);

                    if(ok) {
                        properties.put("readonly", "true");
                        // Disable the app.
                        System.setProperty("NOPATIENT", "true");
                    }
                    return ok;
                }
                else
                    return true;
            case "mem":
            default:
                return true;
        }
    }

    int getSchemaVersion() {

        Connection conn = null;
        try {
            conn = getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT VERSION FROM SCHEMA_INFO");
            if (!rs.next())
                return -1;
            int v = rs.getInt("VERSION");
            log.info("Detected schema version " + v + " for " + getUrl());
            return v;
        } catch (Exception ex) {
            return -1;
        } finally {
            try {
                if (conn != null)
                    conn.close();
            } catch (Exception ex) {
                throw new IllegalStateException((ex));
            }
        }
    }

    void load(String file) throws Exception {
        if (file != null) {
            InputStream is = getClass().getResourceAsStream(file);
            if (is == null)
                throw new IllegalArgumentException("Cannot locate on classpath: " + file);
            Connection conn = getConnection();
            try {
                applySchemaFile(conn, is);
            } finally {
                conn.close();
            }
        }
    }

    Connection getConnection() throws Exception {
        return instance.getConnection();

    }

    public static boolean applySchemaFile(Connection conn, InputStream is) throws Exception {
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        Statement statement = conn.createStatement();

        try {
            String s;
            StringBuilder sql = new StringBuilder();
            while ((s = br.readLine()) != null) {
                if (s.startsWith("#") || s.trim().length() == 0)
                    continue;

                if (s.contains(";")) {
                    String arr[] = s.split("[;]");

                    if (arr.length == 1) {
                        sql.append(s);
                        statement.execute(sql.toString());
                        sql = new StringBuilder();
                    } else if (arr.length == 2) {
                        sql.append(arr[0]);
                        sql.append(";");
                        statement.execute(sql.toString());

                        // in case the next statement started on the same line, append it to the buffer.
                        sql = new StringBuilder();
                        sql.append(arr[1]);
                    } else
                        throw new IllegalStateException("Cannot handle multiple statements per line");
                } else // keep buffering
                    sql.append(s);
            }
        } catch (SQLException ex) {
            throw new SQLException("Failed to handle sql bootstrap", ex);
        } finally {
            statement.close();
        }

        return true;
    }
}
