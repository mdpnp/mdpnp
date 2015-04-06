package org.mdpnp.apps.testapp.patient;

import org.springframework.beans.factory.FactoryBean;

import ca.uhn.fhir.context.FhirContext;
import javafx.collections.ObservableList;

import javax.sql.DataSource;

import java.util.List;

/**
 * @author mfeinberg
 */
public interface EMRFacade {
    void refresh();
    ObservableList<PatientInfo> getPatients();
    boolean createPatient(PatientInfo p);

    void deleteDevicePatientAssociation(DevicePatientAssociation assoc);
    DevicePatientAssociation updateDevicePatientAssociation(DevicePatientAssociation assoc);

    public static class EMRFacadeFactory implements FactoryBean<EMRFacade> {

        private DataSource  jdbcDB;
        private String      fhirEMRUrl;
        private FhirContext fhirContext;

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

        @Override
        public EMRFacade getObject() throws Exception {
            if(instance == null) {
                if(fhirEMRUrl == null) {
                    instance = new JdbcEMRImpl();
                    ((JdbcEMRImpl)instance).setDataSource(jdbcDB);
                }
                else {
                    instance = new FhirEMRImpl();
                    ((FhirEMRImpl)instance).setDataSource(jdbcDB);
                    ((FhirEMRImpl)instance).setUrl(fhirEMRUrl);
                    ((FhirEMRImpl)instance).setFhirContext(fhirContext);
                }
            }
            new Thread( () -> instance.refresh()).start();

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
}
