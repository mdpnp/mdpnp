package org.mdpnp.apps.testapp.patient;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;

import org.hsqldb.jdbc.JDBCDataSource;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.IceApplicationProvider;
import org.mdpnp.devices.MDSHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.List;


/**
 *
 */
public class PatientApplicationFactory implements IceApplicationProvider {

    private static final Logger log = LoggerFactory.getLogger(PatientApplicationFactory.class);

    private final AppType appType =
            new AppType("Patient ID", "NODOA",  PatientApplicationFactory.class.getResource("patient.png"), 0.75, true);

    @Override
    public AppType getAppType() {
        return appType;

    }
    @Override
    public IceApp create(ApplicationContext parentContext) throws IOException {
        return new PatientApplication((AbstractApplicationContext)parentContext);
    }

    class PatientApplication implements IceApp
    {
        final Parent ui;
        final ClassPathXmlApplicationContext ctx;
        final PatientInfoController controller;

        PatientApplication(final AbstractApplicationContext parentContext) throws IOException {

            String contextPath = "classpath*:/org/mdpnp/apps/testapp/patient/PatientAppContext.xml";

            ctx = new ClassPathXmlApplicationContext(new String[] { contextPath }, parentContext);
            parentContext.addApplicationListener(new ApplicationListener<ContextClosedEvent>()
            {
                @Override
                public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
                    // only care to trap parent close events to kill the child context
                    if(parentContext == contextClosedEvent.getApplicationContext()) {
                        log.info("Handle parent context shutdown event");
                        ctx.close();
                    }
                }
            });

            ui = ctx.getBean(Parent.class);
            controller =  ctx.getBean(PatientInfoController.class);
            ctx.getBean("wildcardMdsConnectivity", MDSHandler.class).start();
        }

        @Override
        public AppType getDescriptor() {
            return appType;
        }

        @Override
        public Parent getUI() {
            return ui;
        }

        void addDeviceAssociation(Device d , PatientInfo p)  {
            controller.addDeviceAssociation(d, p);
        }

        void handleDeviceLifecycleEvent(Device d, boolean p) {
            controller.handleDeviceLifecycleEvent(d, p);
        }

        void setConnectHandler(EventHandler<ActionEvent> a) {
            controller.setConnectHandler(a);
        }

        PatientInfo getSelectedPatient() {
            return controller.getSelectedPatient();
        }

        Device getSelectedDevice() {
            return controller.getSelectedDevice();
        }

        @Override
        public void activate(ApplicationContext context) {

        }

        @Override
        public void stop() {

        }

        @Override
        public void destroy() throws Exception {
            ctx.destroy();
        }

    }

    public interface EMRFacade {
        List<PatientInfo> getPatients();
        boolean createPatient(PatientInfo p);

        void deleteDevicePatientAssociation(DevicePatientAssociation assoc);
        DevicePatientAssociation updateDevicePatientAssociation(DevicePatientAssociation assoc);
    }


    @SuppressWarnings("serial")
    static class EmbeddedDB extends JDBCDataSource {
        private String schemaDef;
        private String dataDef;

        public EmbeddedDB() {
            super.setUrl("jdbc:hsqldb:mem:test"); // jdbc:hsqldb:file:testdb
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

        public void init() throws Exception
        {
            int v=getSchemaVersion();
            if(v<1) {
                load(schemaDef);
                load(dataDef);
            }

        }

        int getSchemaVersion() {
            Connection conn=null;
            try {
                conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT VERSION FROM SCHEMA_INFO");
                if(!rs.next())
                    return -1;
                int v = rs.getInt("VERSION");
                log.info("Detected schema version " + v + " for " + getUrl());
                return v;
            }
            catch (Exception ex) {
                return -1;
            }
            finally {
                try {
                    if (conn != null)
                        conn.close();
                }
                catch (Exception ex) {
                    throw new IllegalStateException((ex));
                }
            }
        }

        void load(String file) throws Exception
        {
            if(file != null) {
                InputStream is = getClass().getResourceAsStream(file);
                if(is == null)
                    throw new IllegalArgumentException("Cannot locate on classpath: " + file);
                Connection conn = getConnection();
                try {
                    applySchemaFile(conn, is);
                }
                finally {
                    conn.close();
                }
            }
        }

        public void shutdown() throws Exception
        {
            Connection conn = super.getConnection();
            conn.createStatement().execute("SHUTDOWN");
        }

        static boolean applySchemaFile(Connection conn, InputStream is) throws Exception
        {
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);

            Statement statement = conn.createStatement();

            try
            {
                String s;
                StringBuilder sql = new StringBuilder();
                while ((s = br.readLine()) != null) {
                    if (s.startsWith("#") || s.trim().length() == 0)
                        continue;

                    if(s.contains(";")) {
                        String arr[] = s.split("[;]");

                        if(arr.length==1) {
                            sql.append(s);
                            statement.execute(sql.toString());
                            sql = new StringBuilder();
                        }
                        else if(arr.length==2) {
                            sql.append(arr[0]);
                            sql.append(";");
                            statement.execute(sql.toString());

                            // in case the next statement started on the same line, append it to the buffer.
                            sql = new StringBuilder();
                            sql.append(arr[1]);
                        }
                        else
                            throw new IllegalStateException("Cannot handle multiple statements per line");
                    }
                    else // keep buffering
                        sql.append(s);
                }
            }
            catch (SQLException ex)
            {
                throw new SQLException("Failed to handle sql bootstrap", ex);
            }
            finally
            {
                statement.close();
            }

            return true;
        }
    }
}
