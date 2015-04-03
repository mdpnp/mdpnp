package org.mdpnp.apps.testapp.patient;

import java.util.ArrayList;
import java.util.List;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;

import javax.sql.DataSource;

import static ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum.MALE;
import static ca.uhn.fhir.model.dstu2.valueset.IdentifierUseEnum.OFFICIAL;

/**
 * @author mfeinberg
 */
class FhirEMRImpl implements EMRFacade {

    private String      fhirURL;
    private JdbcEMRImpl jdbcEMR = new JdbcEMRImpl();

    public String getUrl() {
        return fhirURL;
    }
    public void setUrl(String url) {
        fhirURL = url;
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
    public List<PatientInfo> getPatients() {

        FhirContext fhirContext = FhirContext.forDstu2();
        IGenericClient fhirClient = fhirContext.newRestfulGenericClient(fhirURL);
        ca.uhn.fhir.model.api.Bundle bundle = fhirClient
                .search()
                .forResource(Patient.class)
                .execute();

        List<PatientInfo> toRet = new ArrayList<>();
        List<Patient> patients = bundle.getResources(Patient.class);
        String official = ca.uhn.fhir.model.dstu2.valueset.IdentifierUseEnum.OFFICIAL.getCode();
        for(Patient p : patients) {
            for(HumanNameDt n : p.getName()) {
                if(official.equals(n.getUse()) || null == n.getUse()) {
                    PatientInfo pi = new PatientInfo(n.getElementSpecificId(),
                                                     n.getFamilyAsSingleString(),
                                                     n.getGivenAsSingleString());
                    toRet.add(pi);
                    break;
                }
            }
        }
        return toRet;
    }

    public boolean createPatient(PatientInfo p) {

        FhirContext fhirContext = FhirContext.forDstu2();
        IGenericClient fhirClient = fhirContext.newRestfulGenericClient(fhirURL);

        String id = Long.toHexString(System.currentTimeMillis());

        Patient patient = new Patient();
        patient.addIdentifier().setUse(OFFICIAL).setSystem("urn:fake:mrns").setValue(id);
        HumanNameDt name = patient.addName();
        name.addFamily(p.getLastName());
        name.addGiven(p.getFirstName());
        patient.setGender(MALE);

        MethodOutcome outcome = fhirClient.update()
                .resource(patient)
                .conditional()
                .where(Patient.IDENTIFIER.exactly().systemAndIdentifier("urn:fake:mrns", id))
                .execute();

        return outcome.getCreated();
    }

}