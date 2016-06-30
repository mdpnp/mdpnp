package org.mdpnp.apps.testapp.patient;

import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mdpnp.apps.testapp.FxRuntimeSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author mfeinberg
 */
public class FhirEMRImplTest {

    private static final Logger log = LoggerFactory.getLogger(FhirEMRImplTest.class);

    Properties config;

    @Before
    public void setUp() throws Exception {

        InputStream is = getClass().getResourceAsStream("/ice.properties");
        config = new Properties();
        config.load(is);

        String url = config.getProperty("mdpnp.fhir.url");
        org.junit.Assume.assumeTrue(url + " is not running", isServerThere(url));

    }

    private boolean isServerThere(String u) throws Exception {
        try {
            URL url = new URL(u);
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

    @Test
    public void testFetchPatients() throws Exception {

        String url = config.getProperty("mdpnp.fhir.url");

        FhirEMRImpl emr = new FhirEMRImpl(new FxRuntimeSupport.CurrentThreadExecutor());
        emr.setUrl(url);
        emr.setFhirContext(ca.uhn.fhir.context.FhirContext.forDstu2());

        emr.refresh();
        List<PatientInfo> l = emr.getPatients();
        Assert.assertTrue("Failed to load patients", l.size() != 0);
        for (PatientInfo pi : l) {
            log.info(pi.toString());
        }

    }


    @Test
    public void testCreatePatient() throws Exception {

        String url = config.getProperty("mdpnp.fhir.url");

        FhirEMRImpl emr = new FhirEMRImpl(new FxRuntimeSupport.CurrentThreadExecutor());
        emr.setUrl(url);
        emr.setFhirContext(ca.uhn.fhir.context.FhirContext.forDstu2());

        long now = System.currentTimeMillis();
        String id = Long.toHexString(now);
        String fn = "FhirEMRImplTest";
        String ln = "Last" + id;

        PatientInfo pi = new PatientInfo(id, fn, ln, PatientInfo.Gender.M, new Date(now));

        MethodOutcome created = emr.createPatientImpl(pi);
        Assert.assertTrue("Failed to create patients", created.getCreated());

        deleteFhirRecord(emr, created.getId());
    }

    private void deleteFhirRecord(FhirEMRImpl emr, IdDt id)
    {
        IGenericClient fhirClient = emr.getFhirClient();
        fhirClient.delete().resourceById(id).execute();
        log.info("deleted " + id.getValueAsString());
    }
}
