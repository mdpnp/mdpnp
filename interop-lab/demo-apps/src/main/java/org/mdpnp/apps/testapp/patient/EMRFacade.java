package org.mdpnp.apps.testapp.patient;

import javafx.application.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import ca.uhn.fhir.context.FhirContext;
import javafx.collections.ObservableList;

import javax.sql.DataSource;
import java.util.concurrent.Executor;

/**
 * @author mfeinberg
 */
public interface EMRFacade {
    void refresh();
    ObservableList<PatientInfo> getPatients();
    boolean createPatient(PatientInfo p);
    boolean deletePatient(PatientInfo p);

    void deleteDevicePatientAssociation(DevicePatientAssociation assoc);
    DevicePatientAssociation updateDevicePatientAssociation(DevicePatientAssociation assoc);

    class EMRFacadeFactory implements FactoryBean<EMRFacade> {

        private static final Logger log = LoggerFactory.getLogger(EMRFacade.class);

        private EMRType     facadeType;
        private DataSource  jdbcDB;
        private String      fhirEMRUrl;
        private FhirContext fhirContext;

        public EMRType getFacadeType() {
            return facadeType;
        }
        public void setFacadeType(EMRType facadeType) {
            this.facadeType = facadeType;
        }

        public String getUrl() {
            return fhirEMRUrl;
        }
        public void setUrl(String url) {
            fhirEMRUrl = url;
        }
        
        public FhirContext getFhirContext() {
            return fhirContext;
        }
        public void setFhirContext(FhirContext fhirContext) {
            this.fhirContext = fhirContext;
        }

        public DataSource getJdbcDB() {
            return jdbcDB;
        }
        public void setJdbcDB(DataSource jdbcDB) {
            this.jdbcDB = jdbcDB;
        }

        EMRFacade instance = null;

        public enum EMRType { FHIR, JDBC, COMBINED }

        @Override
        public EMRFacade getObject() throws Exception {
            if(instance == null) {
                switch (facadeType) {
                    default:
                    case JDBC:
                        if(jdbcDB == null)
                            throw new IllegalStateException("JDBC for EMR cannot be null");
                        instance = new JdbcEMRImpl(new ExecutorFx());
                        ((JdbcEMRImpl)instance).setDataSource(jdbcDB);
                        break;
                    case FHIR:
                        if(!FhirEMRImpl.isServerThere(fhirEMRUrl))
                            throw new IllegalStateException("No fhir server at >" + fhirEMRUrl +"< url");
                        instance = new FhirEMRImpl(new ExecutorFx());
                        ((FhirEMRImpl)instance).setUrl(fhirEMRUrl);
                        ((FhirEMRImpl)instance).setFhirContext(fhirContext);
                        break;
                    case COMBINED:
                        if(jdbcDB == null)
                            throw new IllegalStateException("JDBC for EMR cannot be null");

                        if(FhirEMRImpl.isServerThere(fhirEMRUrl)) {
                            instance = new JdbcFhirEMRImpl(new ExecutorFx());
                            ((JdbcFhirEMRImpl) instance).setDataSource(jdbcDB);
                            ((JdbcFhirEMRImpl) instance).setUrl(fhirEMRUrl);
                            ((JdbcFhirEMRImpl) instance).setFhirContext(fhirContext);
                        }
                        else {
                            log.error("Take it easy cowboy... Requested jdbc/fhir-backed EMR, but there is no server at >" +
                                      fhirEMRUrl + "< url; defaulting to jdbc-only");

                            instance = new JdbcEMRImpl(new ExecutorFx());
                            ((JdbcEMRImpl)instance).setDataSource(jdbcDB);
                        }
                        break;
                }
            }
            new Thread( () -> instance.refresh(), "EMRFacade refresh").start();

            return instance;
        }

        @Override
        public Class<?> getObjectType() {
            return EMRFacade.class;
        }

        @Override
        public boolean isSingleton() {
            return true;
        }
    }

    public static class ExecutorFx implements Executor {

        @Override
        public void execute(Runnable command) {
            Platform.runLater(command);
        }
    }
}
