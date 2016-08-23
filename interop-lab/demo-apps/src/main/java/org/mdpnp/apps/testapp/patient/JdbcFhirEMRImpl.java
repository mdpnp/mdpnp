package org.mdpnp.apps.testapp.patient;

import ca.uhn.fhir.context.FhirContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.sql.DataSource;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author mfeinberg
 */
class JdbcFhirEMRImpl implements EMRFacade {

    private final FhirEMRImpl fhirEMRImpl;
    private final JdbcEMRImpl jdbcEMR;
    private final Executor    collectionUpdateHandler;

    private final ObservableList<PatientInfo> patients = FXCollections.observableArrayList();

    public JdbcFhirEMRImpl(Executor executor) {
        this.collectionUpdateHandler = executor;
        this.jdbcEMR = new JdbcEMRImpl(executor);
        this.fhirEMRImpl = new FhirEMRImpl(executor);
    }

    public String getUrl() {
        return fhirEMRImpl.getUrl();
    }
    public void setUrl(String url) {
        fhirEMRImpl.setUrl(url);
    }
    public FhirContext getFhirContext() {
        return fhirEMRImpl.getFhirContext();
    }
    public void setFhirContext(FhirContext fhirContext) {
        fhirEMRImpl.setFhirContext(fhirContext);
    }

    public DataSource getDataSource() {
        return jdbcEMR.getDataSource();
    }
    public void setDataSource(DataSource ds) {
        jdbcEMR.setDataSource(ds);
    }

    @Override
    public void deleteDevicePatientAssociation(DevicePatientAssociation assoc) {
        // NO-OP
    }

    @Override
    public DevicePatientAssociation updateDevicePatientAssociation(DevicePatientAssociation assoc) {
        return jdbcEMR.updateDevicePatientAssociation(assoc);
    }

    @Override
    public ObservableList<PatientInfo> getPatients() {
        return patients;
    }

    @Override
    public void refresh() {

        List<PatientInfo> fhir = fhirEMRImpl.queryServer();
        final List<PatientInfo> data = updateLocal(fhir);

        collectionUpdateHandler.execute(() -> {
            Iterator<PatientInfo> itr = data.iterator();
            while (itr.hasNext()) {
                PatientInfo pi = itr.next();
                if (!patients.contains(pi)) {
                    this.patients.add(pi);
                }
            }
        });
    }

    List<PatientInfo> updateLocal(List<PatientInfo> fhir) {

        List<PatientInfo> jdbc = jdbcEMR.queryAll();
        for(PatientInfo p : fhir) {
            int i = jdbc.indexOf(p);
            if(i>=0) {
                jdbcEMR.updatePatient(p);
                jdbc.set(i, p);
            }
        }
        return jdbc;
    }

    List<PatientInfo> queryServer() {
        return fhirEMRImpl.queryServer();
    }

    List<PatientInfo> queryDatabase() {
        return jdbcEMR.queryAll();
    }

    public boolean createPatient(final PatientInfo p) {
        return jdbcEMR.createPatient(p);
    }

    public boolean deletePatient(PatientInfo p) {
        return jdbcEMR.deletePatient(p);

    }
}
