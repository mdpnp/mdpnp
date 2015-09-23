package org.mdpnp.apps.testapp.hl7;

import java.util.Date;
import java.util.List;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.base.resource.BaseOperationOutcome;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;

public class Fhir {
    public static void main(String[] args) {
        FhirContext fhirContext = FhirContext.forDstu2();
        IGenericClient fhirClient = fhirContext.newRestfulGenericClient("https://fhir.openice.info/fhir");
        
//        ca.uhn.fhir.model.api.Bundle bundle = fhirClient
//                .search()
//                .forResource(Patient.class)
//                .where(Patient.IDENTIFIER.exactly().systemAndIdentifier("urn:fake:mrns", "54321"))
//                .execute();
//        List<Patient> patients = bundle.getResources(Patient.class);
//        if(patients.size() > 1) {
//            for(Patient p : patients.subList(1, patients.size())) {
//                System.out.println(p.getId());
//                BaseOperationOutcome outcome = fhirClient
//                        .delete()
//                        .resourceById(p.getId())
//                        .execute();
//            }
//        }
        ca.uhn.fhir.model.api.Bundle bundle = fhirClient
                .search()
                .forResource(Observation.class)
                .where(Observation.SUBJECT.hasId("Patient/1"))
                .limitTo(20000)
                .execute();
        
        Date epoch = new Date(0);
        
        
        List<Observation> observations = bundle.getResources(Observation.class);
        for(Observation o : observations) {
            if(o.getApplies() instanceof DateTimeDt && ((DateTimeDt)o.getApplies()).getValue().before(epoch)) {
                System.out.println(o.getId() + " " + o.getApplies());
                BaseOperationOutcome outcome = fhirClient
                        .delete()
                        .resourceById(o.getId())
                        .execute();

            }
        }
    }
}
