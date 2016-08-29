package org.mdpnp.apps.testapp.patient;

import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.apps.testapp.EmbeddedDB;
import org.mdpnp.apps.testapp.FxRuntimeSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.Date;
import java.util.List;

/**
 * @author mfeinberg
 */
public class JdbcEMRImplTest {

    private static final Logger log = LoggerFactory.getLogger(PatientApplicationFactoryTest.class);

    @Test
    public void testFetchPatients() throws Exception {

        EmbeddedDB db = new EmbeddedDB("jdbc:hsqldb:mem:icepatientdb");
        db.setSchemaDef("/org/mdpnp/apps/testapp/patient/DbSchema.sql");
        db.setDataDef("/org/mdpnp/apps/testapp/patient/DbData.0.sql");
        DataSource ds = db.getDataSource();

        try {
            JdbcEMRImpl emr = new JdbcEMRImpl(new FxRuntimeSupport.CurrentThreadExecutor());
            emr.setDataSource(ds);

            emr.refresh();
            List<PatientInfo> l = emr.getPatients();
            Assert.assertEquals("Failed to load patients", 5, l.size());
            for (PatientInfo pi : l) {
                log.info(pi.toString());
            }
        }
        finally {
            db.destroy();
        }
    }


    @Test
    public void testCreatePatient() throws Exception {

        EmbeddedDB db = new EmbeddedDB("jdbc:hsqldb:mem:icepatientdb");
        db.setSchemaDef("/org/mdpnp/apps/testapp/patient/DbSchema.sql");
        DataSource ds = db.getDataSource();

        try {
            JdbcEMRImpl emr = new JdbcEMRImpl(new FxRuntimeSupport.CurrentThreadExecutor());
            emr.setDataSource(ds);

            String id = Long.toHexString(System.currentTimeMillis());
            String fn = "First" + id;
            String ln = "Last" + id;

            PatientInfo pi = new PatientInfo(id, fn, ln, PatientInfo.Gender.F, new Date(0));

            boolean created = emr.createPatient(pi);
            Assert.assertTrue("Failed to create patients", created);

            List<PatientInfo> l = emr.getPatients();
            Assert.assertEquals("Failed to load patients", 1, l.size());

            PatientInfo frmDb=l.get(0);
            Assert.assertEquals("Failed to load patient", id, frmDb.getMrn());
            Assert.assertEquals("Failed to load patient", fn, frmDb.getFirstName());
            Assert.assertEquals("Failed to load patient", ln, frmDb.getLastName());
            Assert.assertEquals("Failed to load patient", PatientInfo.Gender.F, frmDb.getGender());
        }
        finally {
            db.destroy();
        }
    }

    @Test
    public void testUpdateDeletePatient() throws Exception {

        EmbeddedDB db = new EmbeddedDB("jdbc:hsqldb:mem:icepatientdb");
        db.setSchemaDef("/org/mdpnp/apps/testapp/patient/DbSchema.sql");
        DataSource ds = db.getDataSource();

        try {
            JdbcEMRImpl emr = new JdbcEMRImpl(new FxRuntimeSupport.CurrentThreadExecutor());
            emr.setDataSource(ds);

            String id = Long.toHexString(System.currentTimeMillis());

            PatientInfo pi0 = new PatientInfo(id+"-0", "F0", "L0", PatientInfo.Gender.F, new Date(0));
            emr.createPatient(pi0);
            PatientInfo pi1 = new PatientInfo(id+"-1", "F1", "L1", PatientInfo.Gender.F, new Date(0));
            emr.createPatient(pi1);

            PatientInfo pi2 = new PatientInfo(id+"-0", "F2", "L2", PatientInfo.Gender.F, new Date(0));
            emr.updatePatient(pi2);

            List<PatientInfo> l0 = JdbcEMRImpl.fetchAllPatients(ds);
            Assert.assertEquals("Failed to load patients", 2, l0.size());

            emr.deletePatient(pi1);
            List<PatientInfo> l1 = JdbcEMRImpl.fetchAllPatients(ds);
            Assert.assertEquals("Failed to load patients", 1, l1.size());

            PatientInfo frmDb =l1.get(0);
            Assert.assertEquals("Failed to load patient", "F2", frmDb.getFirstName());
            Assert.assertEquals("Failed to load patient", "L2", frmDb.getLastName());
        }
        finally {
            db.destroy();
        }
    }

}
