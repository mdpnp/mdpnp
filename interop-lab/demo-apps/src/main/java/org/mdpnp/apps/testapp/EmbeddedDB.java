package org.mdpnp.apps.testapp;

import org.hsqldb.jdbc.JDBCDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 */
@SuppressWarnings("serial")
public class EmbeddedDB extends JDBCDataSource {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedDB.class);

    private String schemaDef;
    private String dataDef;

    public EmbeddedDB() {
        super.setUrl("jdbc:hsqldb:mem:icepatientdb"); // jdbc:hsqldb:file:icepatientdb
        super.setUser("sa");
        super.setPassword("");
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

    public void init() throws Exception {
        int v = getSchemaVersion();
        if (v < 1) {
            load(schemaDef);
            load(dataDef);
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

    public void shutdown() throws Exception {
        Connection conn = super.getConnection();
        conn.createStatement().execute("SHUTDOWN");
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
