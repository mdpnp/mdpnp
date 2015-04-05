package org.mdpnp.apps.testapp.patient;

import org.junit.Assert;
import org.junit.Test;
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
public class JdbcEMRImplTest {

    private static final Logger log = LoggerFactory.getLogger(PatientApplicationFactoryTest.class);

    @Test
    public void testFetchPatients() throws Exception {

        PatientApplicationFactory.EmbeddedDB ds = new PatientApplicationFactory.EmbeddedDB();
        ds.setSchemaDef("DbSchema.sql");
        ds.setDataDef("DbData.0.sql");
        ds.init();

        try {
            JdbcEMRImpl emr = new JdbcEMRImpl();
            emr.setDataSource(ds);

            List<PatientInfo> l = emr.getPatients();
            Assert.assertEquals("Failed to load patients", 5, l.size());
            for (PatientInfo pi : l) {
                log.info(pi.toString());
            }

            ds.shutdown();
        }
        finally {
            ds.shutdown();
        }
    }


    @Test
    public void testCreatePatient() throws Exception {

        PatientApplicationFactory.EmbeddedDB ds = new PatientApplicationFactory.EmbeddedDB();
        ds.setSchemaDef("DbSchema.sql");
        ds.init();

        try {
            JdbcEMRImpl emr = new JdbcEMRImpl();
            emr.setDataSource(ds);

            String id = Long.toHexString(System.currentTimeMillis());
            String fn = "First" + id;
            String ln = "Last" + id;

            PatientInfo pi = new PatientInfo(id, fn, ln, PatientInfo.Gender.F, new Date(0));

            boolean created = emr.createPatient(pi);
            Assert.assertTrue("Failed to create patients", created);

            List<PatientInfo> l = emr.getPatients();
            Assert.assertEquals("Failed to load patients", 1, l.size());

            PatientInfo db=l.get(0);
            Assert.assertEquals("Failed to load patient", id, db.getMrn());
            Assert.assertEquals("Failed to load patient", fn, db.getFirstName());
            Assert.assertEquals("Failed to load patient", ln, db.getLastName());
            Assert.assertEquals("Failed to load patient", PatientInfo.Gender.F, db.getGender());
        }
        finally {
            ds.shutdown();
        }
    }
}
