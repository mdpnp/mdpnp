package org.mdpnp.apps.testapp.patient;

import ca.uhn.fhir.context.FhirContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.sql.DataSource;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author mfeinberg
 */
class JdbcFhirEMRImpl extends EMRFacade {

    private final FhirEMRImpl fhirEMR;
    private final JdbcEMRImpl jdbcEMR;

    public JdbcFhirEMRImpl(Executor executor) {
        super(executor);
        this.jdbcEMR = new JdbcEMRImpl();
        this.fhirEMR = new FhirEMRImpl();
    }

    public String getUrl() {
        return fhirEMR.getUrl();
    }
    public void setUrl(String url) {
        fhirEMR.setUrl(url);
    }
    public FhirContext getFhirContext() {
        return fhirEMR.getFhirContext();
    }
    public void setFhirContext(FhirContext fhirContext) {
        fhirEMR.setFhirContext(fhirContext);
    }

    public DataSource getDataSource() {
        return jdbcEMR.getDataSource();
    }
    public void setDataSource(DataSource ds) {
        jdbcEMR.setDataSource(ds);
    }

    @Override
    public void deleteDevicePatientAssociation(DevicePatientAssociation assoc) {
        jdbcEMR.deleteDevicePatientAssociation(assoc);
    }

    @Override
    public DevicePatientAssociation updateDevicePatientAssociation(DevicePatientAssociation assoc) {
        return jdbcEMR.updateDevicePatientAssociation(assoc);
    }

    @Override
    public List<PatientInfo> fetchAllPatients() {
        List<PatientInfo> fhir = fhirEMR.fetchAllPatients();
        final List<PatientInfo> currentPatients = updateLocal(fhir);
        return currentPatients;
    }

    List<PatientInfo> updateLocal(List<PatientInfo> fhir) {

        List<PatientInfo> jdbc = jdbcEMR.fetchAllPatients();
        for(PatientInfo p : fhir) {
            int i = jdbc.indexOf(p);
            if(i>=0) {
                jdbcEMR.updatePatient(p);
                jdbc.set(i, p);
            }
        }
        return jdbc;
    }

    public boolean createPatient(final PatientInfo p) {
        boolean ok1 = jdbcEMR.createPatient(p);
        // Do not create patient records from the information that is incomplete -
        // this could be a case of a device being assigned to a patient partition that
        // fhir server does not know about. We would create a local jdbc entry for it,
        // but will not push it out to the master server.
        boolean ok2 = PatientInfo.UNKNOWN_NAME.equals(p.getLastName()) || fhirEMR.createPatient(p);
        return ok1 && ok2 && super.createPatient(p);

    }

    public boolean deletePatient(PatientInfo p) {
        boolean ok1 = jdbcEMR.deletePatient(p);
        boolean ok2 = fhirEMR.deletePatient(p);
        return ok1 && ok2 && super.deletePatient(p);
    }

    // package-protected to be used in tests
    //
    EMRFacade getFhirHandle() {
        return fhirEMR;
    }

    EMRFacade getDatabaseHandle() {
        return jdbcEMR;
    }
}
