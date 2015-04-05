package org.mdpnp.apps.testapp.patient;

import org.springframework.beans.factory.FactoryBean;

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

        private DataSource jdbcDB;
        private String     fhirEMRUrl;

        public String getUrl() {
            return fhirEMRUrl;
        }
        public void setUrl(String url) {
            fhirEMRUrl = url;
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
                }
            }
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
