package org.mdpnp.apps.testapp.patient;

import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.apps.testapp.FxRuntimeSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * @author mfeinberg
 */
public class FhirEMRImplTest {

    private static final Logger log = LoggerFactory.getLogger(PatientApplicationFactoryTest.class);

    @Test
    public void testFetchPatients() throws Exception {

        InputStream is = getClass().getResourceAsStream("/ice.properties");
        Properties p = new Properties();
        p.load(is);

        String url = p.getProperty("mdpnp.fhir.url");

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

        InputStream is = getClass().getResourceAsStream("/ice.properties");
        Properties p = new Properties();
        p.load(is);

        String url = p.getProperty("mdpnp.fhir.url");

        FhirEMRImpl emr = new FhirEMRImpl(new FxRuntimeSupport.CurrentThreadExecutor());
        emr.setUrl(url);
        emr.setFhirContext(ca.uhn.fhir.context.FhirContext.forDstu2());

        long now = System.currentTimeMillis();
        String id = Long.toHexString(now);
        String fn = "FhirEMRImplTest";
        String ln = "Last" + id;

        PatientInfo pi = new PatientInfo(id, fn, ln, PatientInfo.Gender.M, new Date(now));

        boolean created = emr.createPatient(pi);
        Assert.assertTrue("Failed to create patients", created);
    }
}
