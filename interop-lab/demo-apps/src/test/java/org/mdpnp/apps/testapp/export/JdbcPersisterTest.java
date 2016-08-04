package org.mdpnp.apps.testapp.export;

import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.apps.fxbeans.NumericFx;

import java.sql.*;
import java.util.Calendar;

public class JdbcPersisterTest {

    @Test
    public void testSQLUpdate() throws Exception {

        JdbcPersister p = new JdbcPersister() {
            @Override
            Connection createConnection() throws Exception {
                Connection c = createConnection("org.hsqldb.jdbcDriver",
                                                "jdbc:hsqldb:mem:test",
                                                "sa", "");
                JdbcPersister.createSchema(c);
                return c;
            }
        };

        p.start();


        long now = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(now));

        // seed the database with known number of records.
        //
        for (int i = 1; i <= 5; i++) {

            long ms = calendar.getTime().getTime();
            NumericsDataCollector.NumericSampleEvent evt = DataCollector.toValue("DEVICE"+i, "METRIC"+i, 100+i, ms, 3.14*i);
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
                Assert.assertEquals("Row #"+n, new Timestamp(ts), rs.getTimestamp(4));
                Assert.assertEquals("Row #"+n, 3.14*n,             rs.getFloat(5), 0.0001);

                calendar.add(Calendar.MINUTE, 1);
            }
            Assert.assertEquals("Database is missing records", 5, n);


        } finally {
            conn.createStatement().execute("SHUTDOWN");
        }

        p.stop();
    }
}

