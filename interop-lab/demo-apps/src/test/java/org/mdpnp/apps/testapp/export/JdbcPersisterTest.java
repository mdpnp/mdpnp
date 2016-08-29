package org.mdpnp.apps.testapp.export;

import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.apps.testapp.EmbeddedDB;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Calendar;

public class JdbcPersisterTest {

    @Test
    public void testVitalUpdate() throws Exception {

        JdbcPersister p = new JdbcPersisterExt();
        p.start();


        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(now));

        // seed the database with known number of records.
        //
        for (int i = 1; i <= 5; i++) {

            long ms = calendar.getTime().getTime();
            NumericsDataCollector.NumericSampleEvent evt = NumericsDataCollector.toEvent("DEVICE"+i, "METRIC"+i, 100+i, ms, 3.14*i);
            p.handleDataSampleEvent(evt);

            calendar.add(Calendar.MINUTE, 1);
        }


        Connection conn = p.getConnection();

        try {
            // roll back the calendar to a 'starting point'
            calendar.setTime(new Date(now));

            ResultSet rs = conn.createStatement().executeQuery("select DEVICE_ID,METRIC_ID,INSTANCE_ID,TIME_TICK,VITAL_VALUE from VITAL_VALUES order by TIME_TICK asc");
            int n=0;
            while(rs.next()) {
                n++;

                long ts = calendar.getTime().getTime();

                Assert.assertEquals("Row #"+n, "DEVICE"+n,         rs.getString(1));
                Assert.assertEquals("Row #"+n, "METRIC"+n,         rs.getString(2));
                Assert.assertEquals("Row #"+n, 100+n,              rs.getInt(3));
                Assert.assertEquals("Row #"+n, new Timestamp(ts),  rs.getTimestamp(4));
                Assert.assertEquals("Row #"+n, 3.14*n,             rs.getFloat(5), 0.0001);

                calendar.add(Calendar.MINUTE, 1);
            }
            Assert.assertEquals("Database is missing records", 5, n);


        } finally {
            p.stop();
        }
    }


    @Test
    public void testObservationUpdate() throws Exception {

        JdbcPersister p = new JdbcPersisterExt();
        p.start();


        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(now));

        // seed the database with known number of records.
        //
        for (int i = 1; i <= 5; i++) {

            long ms = calendar.getTime().getTime();
            PatientAssessmentDataCollector.PatientAssessmentEvent evt = PatientAssessmentDataCollector.toEvent("NURSE"+i, ms, "OID"+i, "OBSERVATION"+i);
            p.handleDataSampleEvent(evt);

            calendar.add(Calendar.MINUTE, 1);
        }

        Connection conn = p.getConnection();

        try {
            // roll back the calendar to a 'starting point'
            calendar.setTime(new Date(now));

            ResultSet rs = conn.createStatement().executeQuery("select MD_ID,TIME_TICK,OBSERVATION from OBSERVATION_VALUES order by TIME_TICK asc");
            int n=0;
            while(rs.next()) {
                n++;

                long ts = calendar.getTime().getTime();

                Assert.assertEquals("Row #"+n, "NURSE"+n,          rs.getString(1));
                Assert.assertEquals("Row #"+n, new Timestamp(ts),  rs.getTimestamp(2));
                Assert.assertEquals("Row #"+n, "OID"+n,    rs.getString(3));

                calendar.add(Calendar.MINUTE, 1);
            }
            Assert.assertEquals("Database is missing records", 5, n);


        } finally {
            p.stop();
        }
    }

    class JdbcPersisterExt extends JdbcPersister {

        final EmbeddedDB db = new EmbeddedDB("jdbc:hsqldb:mem:test");
        final DataSource ds;

        JdbcPersisterExt() throws Exception {
            super();
            db.setSchemaDef("/org/mdpnp/apps/testapp/export/DbSchema.sql");
            ds = db.getDataSource();
        }

        @Override
        Connection createConnection() throws Exception {
            return ds.getConnection();
        }

        @Override
        public void stop() throws Exception {
            super.stop();
            db.destroy();
        }
    }
}

