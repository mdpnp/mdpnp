package org.mdpnp.apps.testapp.patient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.sql.DataSource;

import static ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum.FEMALE;
import static ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum.MALE;
import static ca.uhn.fhir.model.dstu2.valueset.IdentifierUseEnum.OFFICIAL;

/**
 * @author mfeinberg
 */
class FhirEMRImpl implements EMRFacade {

    private static final String HL7_ICE_OID = "urn:oid:2.16.840.1.113883.3.1974";

    private String      fhirURL;
    private JdbcEMRImpl jdbcEMR = new JdbcEMRImpl();
    private final ObservableList<PatientInfo> patients = FXCollections.observableArrayList();

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
    public ObservableList<PatientInfo> getPatients() {
        return patients;
    }
    
    @Override
    public void refresh() {

        FhirContext fhirContext = FhirContext.forDstu2();
        IGenericClient fhirClient = fhirContext.newRestfulGenericClient(fhirURL);
        ca.uhn.fhir.model.api.Bundle bundle = fhirClient
                .search()
                .forResource(Patient.class)
                .execute();

        final List<PatientInfo> toRet = new ArrayList<>();
        List<Patient> patients = bundle.getResources(Patient.class);
        String official = ca.uhn.fhir.model.dstu2.valueset.IdentifierUseEnum.OFFICIAL.getCode();
        for(Patient p : patients) {
            IdentifierDt id = p.getIdentifierFirstRep();
            if(!HL7_ICE_OID.equals(id.getSystem()))
                continue;
            String mrn = p.getIdentifierFirstRep().getValue();
            // now find the official name used on the record.
            for(HumanNameDt n : p.getName()) {
                if(official.equals(n.getUse()) || null == n.getUse()) {
                    PatientInfo pi = new PatientInfo(mrn,
                                                     n.getGivenAsSingleString(),
                                                     n.getFamilyAsSingleString());
                    toRet.add(pi);
                    break;
                }
            }
        }
        Platform.runLater(() -> {
            patients.retainAll(toRet);
            Iterator<PatientInfo> itr = toRet.iterator();
            while(itr.hasNext()) {
                PatientInfo pi = itr.next();
                if(!patients.contains(pi)) {
                    this.patients.add(pi);
                }
            }
        });
    }

    public boolean createPatient(final PatientInfo p) {
        Platform.runLater( () -> {
            patients.add(p);
        });
        
        FhirContext fhirContext = FhirContext.forDstu2();
        IGenericClient fhirClient = fhirContext.newRestfulGenericClient(fhirURL);

        String mrnId = p.getMrn();

        Patient patient = new Patient();
        patient.addIdentifier().setUse(OFFICIAL).setSystem(HL7_ICE_OID).setValue(mrnId);
        HumanNameDt name = patient.addName();
        name.addFamily(p.getLastName());
        name.addGiven(p.getFirstName());
        patient.setGender(p.getGender().equals(PatientInfo.Gender.male)?MALE:FEMALE);
        DateDt dob = new DateDt(p.getDob());
        patient.setBirthDate(dob);

        MethodOutcome outcome = fhirClient.update()
                .resource(patient)
                .conditional()
                .where(Patient.IDENTIFIER.exactly().systemAndIdentifier(HL7_ICE_OID, mrnId))
                .execute();

        return outcome.getCreated();
    }

}