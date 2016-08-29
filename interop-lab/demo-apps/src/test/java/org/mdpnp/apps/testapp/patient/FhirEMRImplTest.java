package org.mdpnp.apps.testapp.patient;

import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mdpnp.apps.testapp.EmbeddedDB;
import org.mdpnp.apps.testapp.FxRuntimeSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.InputStream;
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
        org.junit.Assume.assumeTrue(url + " is not running", FhirEMRImpl.isServerThere(url));

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
    public void testMergePatients() throws Exception {

        String url = config.getProperty("mdpnp.fhir.url");

        EmbeddedDB db = new EmbeddedDB("jdbc:hsqldb:mem:icepatientdb");
        db.setSchemaDef("/org/mdpnp/apps/testapp/patient/DbSchema.sql");
        db.setDataDef("/org/mdpnp/apps/testapp/patient/DataBootstrap.sql");
        DataSource ds = db.getDataSource();

        try {
            JdbcFhirEMRImpl emr = new JdbcFhirEMRImpl(new FxRuntimeSupport.CurrentThreadExecutor());
            emr.setUrl(url);
            emr.setFhirContext(ca.uhn.fhir.context.FhirContext.forDstu2());
            emr.setDataSource(ds);

            List<PatientInfo> listdb = emr.getDatabaseHandle().fetchAllPatients();
            Assert.assertTrue("Database had no patients", listdb.size() != 0);

            List<PatientInfo> listfhir = emr.getFhirHandle().fetchAllPatients();
            Assert.assertTrue("Fhir server had no patients", listfhir.size() != 0);

            // Create a new record with the same MRN as in the fhir server in the local
            // database.
            PatientInfo pi = new PatientInfo(listfhir.get(0).getMrn(), "", "", PatientInfo.Gender.U, new Date());
            Assert.assertFalse("Database should not have patient with this MRN", listdb.contains(pi));

            boolean created = emr.getDatabaseHandle().createPatient(pi);
            Assert.assertTrue("Failed to create patients", created);

            emr.updateLocal(listfhir);

            List<PatientInfo> finalList = emr.getDatabaseHandle().fetchAllPatients();
            Assert.assertEquals("Database does not have updated record", listdb.size()+1, finalList.size());

            int idx = finalList.indexOf(pi);
            Assert.assertTrue("Database should have patient with this MRN", idx>0);
            pi = finalList.get(idx);

            // Confirm that name had been filled in from the fhir record.
            //
            Assert.assertTrue("Invalid data", pi.getLastName().length() != 0);
            Assert.assertTrue("Invalid data", pi.getFirstName().length() != 0);
        }
        finally {
            db.destroy();
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
