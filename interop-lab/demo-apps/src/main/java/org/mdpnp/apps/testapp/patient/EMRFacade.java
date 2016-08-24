package org.mdpnp.apps.testapp.patient;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import ca.uhn.fhir.context.FhirContext;
import javafx.collections.ObservableList;

import javax.sql.DataSource;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author mfeinberg
 */
public abstract class EMRFacade {

    private final ListHandler listHandler;

    public EMRFacade(Executor executor) {
        listHandler = new ListHandler(FXCollections.observableArrayList(), executor);

    }

    public EMRFacade(ListHandler handler) {
        listHandler = handler;
    }

    public void refresh() {
        List<PatientInfo> currentPatients = fetchAllPatients();
        listHandler.refresh(currentPatients);
    }

    public ObservableList<PatientInfo> getPatients() {
        return listHandler.getPatients();
    }

    abstract List<PatientInfo> fetchAllPatients();

    public boolean createPatient(PatientInfo p) {
        listHandler.createPatient(p);
        return true;
    }

    public boolean deletePatient(PatientInfo p) {
        listHandler.deletePatient(p);
        return true;
    }

    public abstract void deleteDevicePatientAssociation(DevicePatientAssociation assoc);
    public abstract DevicePatientAssociation updateDevicePatientAssociation(DevicePatientAssociation assoc);

    static class ListHandler {

        private final ObservableList<PatientInfo> patientList; //  = FXCollections.observableArrayList();
        private final Executor collectionUpdateHandler;

        private ListHandler(ObservableList<PatientInfo> patients, Executor executor) {
            patientList = patients;
            collectionUpdateHandler = executor;
        }

        public void refresh(List<PatientInfo> currentPatients) {

            collectionUpdateHandler.execute(() -> {
                patientList.retainAll(currentPatients);
                Iterator<PatientInfo> itr = currentPatients.iterator();
                while (itr.hasNext()) {
                    PatientInfo pi = itr.next();
                    if (!patientList.contains(pi)) {
                        patientList.add(pi);
                    }
                }
            });
        }

        public ObservableList<PatientInfo> getPatients() {
            return patientList;
        }

        public void createPatient(PatientInfo p) {
            collectionUpdateHandler.execute(() -> {
                patientList.add(p);
            });
        }

        public void deletePatient(PatientInfo p) {
            collectionUpdateHandler.execute(() -> {
                patientList.remove(p);
            });
        }
    }

    static final ListHandler NOOP_HANDLER = new ListHandler(null, null)
    {
        @Override
        public void createPatient(PatientInfo p) {
        }

        @Override
        public void deletePatient(PatientInfo p) {
        }

        @Override
        public ObservableList<PatientInfo> getPatients() {
            throw new UnsupportedOperationException("UNDEFINED");
        }

        @Override
        public void refresh(List<PatientInfo> currentPatients) {
        }
    };

    public static class EMRFacadeFactory implements FactoryBean<EMRFacade> {

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
