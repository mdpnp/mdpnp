package org.mdpnp.apps.testapp.patient;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.client.IGenericClient;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author mfeinberg
 */
class EMRImpl implements PatientApplicationFactory.EMRFacade {

    private FhirEMRImpl fhirEMR = new FhirEMRImpl();
    private DataSource jdbcDB;


    public String getUrl() {
        return fhirEMR.getUrl();
    }
    public void setUrl(String url) {
        fhirEMR.setUrl(url);
    }

    public DataSource getJdbcDB() {
        return jdbcDB;
    }
    public void setJdbcDB(DataSource jdbcDB) {
        this.jdbcDB = jdbcDB;
    }


    public void deleteDevicePatientAssociation(DevicePatientAssociation assoc) {
        DevicePatientAssociation.delete(jdbcDB, assoc);
    }

    public DevicePatientAssociation updateDevicePatientAssociation(DevicePatientAssociation assoc) {
        return DevicePatientAssociation.update(jdbcDB, assoc);
    }

    public boolean createPatient(PatientInfo p) {
        return fhirEMR.createPatient(p);
    }


    @Override
    public List<PatientInfo> getPatients() {

        List<PatientInfo> p;
        if (fhirEMR != null)
            p =fhirEMR.getPatients();

        else
            p = PatientInfo.queryAll(jdbcDB);
        return p;
    }
}