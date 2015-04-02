package org.mdpnp.apps.testapp.patient;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * @author mfeinberg
 */
public class EMRImplTest {

    private static final Logger log = LoggerFactory.getLogger(PatientApplicationFactoryTest.class);

    @Test
    public void testFetchPatients() throws Exception {

        InputStream is = getClass().getResourceAsStream("/ice.properties");
        Properties p = new Properties();
        p.load(is);

        String url = p.getProperty("mdpnp.fhir.url");

        FhirEMRImpl emr = new FhirEMRImpl();
        emr.setUrl(url);

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

        FhirEMRImpl emr = new FhirEMRImpl();
        emr.setUrl(url);

        String id = Long.toHexString(System.currentTimeMillis());
        String fn = "First" + id;
        String ln = "Last" + id;

        PatientInfo pi = new PatientInfo(id, fn, ln);

        boolean created = emr.createPatient(pi);
        Assert.assertTrue("Failed to create patients", created);
    }
}
