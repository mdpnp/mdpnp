package org.mdpnp.apps.testapp.patient;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum.FEMALE;
import static ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum.MALE;
import static ca.uhn.fhir.model.dstu2.valueset.IdentifierUseEnum.OFFICIAL;

/**
 * @author mfeinberg
 */
class FhirEMRImpl extends EMRFacade {

    private static final Logger log = LoggerFactory.getLogger(FhirEMRImpl.class);

    private static final String HL7_ICE_URN_OID = "urn:oid:2.16.840.1.113883.3.1974";
    private FhirContext       fhirContext;
    private String            fhirURL;

    public FhirEMRImpl(Executor executor) {
        super(executor);
    }

    public FhirEMRImpl() {
        super(NOOP_HANDLER);
    }

    public String getUrl() {
        return fhirURL;
    }
    public void setUrl(String url) {
        fhirURL = url;
    }
    public FhirContext getFhirContext() {
        return fhirContext;
    }
    public void setFhirContext(FhirContext fhirContext) {
        this.fhirContext = fhirContext;
    }

    public static boolean isServerThere(String u) throws Exception {
        try {
            URL url = new URL(u + "/metadata");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // This will throw if server is not there.
            InputStream is = conn.getInputStream();
            return is != null;
        }
        catch(Exception ex) {
            return false;
        }
    }

    @Override
    public void deleteDevicePatientAssociation(DevicePatientAssociation assoc) {
        // NO-OP
    }

    @Override
    public DevicePatientAssociation updateDevicePatientAssociation(DevicePatientAssociation assoc) {
        // NO-OP
        return assoc;
    }

    @Override
    public List<PatientInfo> fetchAllPatients() {

        IGenericClient fhirClient = getFhirClient();

        ca.uhn.fhir.model.api.Bundle bundle = fhirClient
                .search()
                .forResource(Patient.class)
                .execute();

        final List<PatientInfo> toRet = new ArrayList<>();
        List<Patient> patients = bundle.getResources(Patient.class);

        String official = ca.uhn.fhir.model.dstu2.valueset.IdentifierUseEnum.OFFICIAL.getCode();
        for (Patient p : patients) {
            IdentifierDt id = p.getIdentifierFirstRep();
            if (!HL7_ICE_URN_OID.equals(id.getSystem()))
                continue;
            String mrn = p.getIdentifierFirstRep().getValue();

            // now find the official name used on the record.
            for (HumanNameDt n : p.getName()) {
                if (official.equals(n.getUse()) || null == n.getUse()) {
                    String lName = n.getFamilyAsSingleString();
                    String fName = n.getGivenAsSingleString();
                    Date bDay = p.getBirthDate();
                    String g = p.getGender();
                    if (lName != null && fName != null && bDay != null && g != null) {
                        PatientInfo pi = new PatientInfo(mrn, fName, lName, fromFhire(g), bDay);
                        toRet.add(pi);
                        break;
                    }
                }
            }
        }

        patients.retainAll(toRet);

        return toRet;
    }


    public boolean createPatient(final PatientInfo p) {
        boolean ok = super.createPatient(p);
        MethodOutcome mo = createPatientImpl(p);
        log.info("Created new patient; id=" + mo.getId());
        return ok && mo.getCreated();
    }

    public boolean deletePatient(PatientInfo p) {
        return false;
    }

    MethodOutcome createPatientImpl(final PatientInfo p) {

        IGenericClient fhirClient = getFhirClient();

        String mrnId = p.getMrn();

        Patient patient = new Patient();
        patient.addIdentifier().setUse(OFFICIAL).setSystem(HL7_ICE_URN_OID).setValue(mrnId);
        HumanNameDt name = patient.addName();
        name.addFamily(p.getLastName());
        name.addGiven(p.getFirstName());
        patient.setGender(toFhire(p.getGender()));
        DateDt dob = new DateDt(p.getDob());
        patient.setBirthDate(dob);

        MethodOutcome outcome = fhirClient.update()
                .resource(patient)
                .conditional()
                .where(Patient.IDENTIFIER.exactly().systemAndIdentifier(HL7_ICE_URN_OID, mrnId))
                .execute();

        return outcome;
    }

    IGenericClient getFhirClient() {
        return fhirContext.newRestfulGenericClient(fhirURL);
    }

    static AdministrativeGenderEnum toFhire(PatientInfo.Gender g) {
        switch (g) {
            default:
            case M: return MALE;
            case F: return FEMALE;
        }
    }

    static PatientInfo.Gender fromFhire(String g) {
        return fromFhire(AdministrativeGenderEnum.UNKNOWN.forCode(g));
    }

    static PatientInfo.Gender fromFhire(AdministrativeGenderEnum g) {
        switch (g) {
            default:     throw new IllegalArgumentException("Unknown conversion " + g);
            case MALE:   return PatientInfo.Gender.M;
            case FEMALE: return PatientInfo.Gender.F;
        }
    }
}
