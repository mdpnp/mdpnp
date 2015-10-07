package org.mdpnp.apps.testapp;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;

public class FHIRTest {
    public static void main(String[] args) {
        FhirContext ctx = FhirContext.forDstu2();
        String serverBase = "https://fhir.openice.info/fhir";

        IGenericClient client = ctx.newRestfulGenericClient(serverBase);
        Patient patient = new Patient();
        // ..populate the patient object..
        patient.addIdentifier().setSystem("urn:system").setValue("12345");
        patient.addName().addFamily("Smith").addGiven("John");

        // Invoke the server create method (and send pretty-printed JSON
        // encoding to the server
        // instead of the default which is non-pretty printed XML)
        MethodOutcome outcome = client.create().resource(patient).prettyPrint().encodedJson().execute();

        // The MethodOutcome object will contain information about the
        // response from the server, including the ID of the created
        // resource, the OperationOutcome response, etc. (assuming that
        // any of these things were provided by the server! They may not
        // always be)
        IdDt id = outcome.getId();
        System.out.println("Got ID: " + id.getValue());
    }
}
