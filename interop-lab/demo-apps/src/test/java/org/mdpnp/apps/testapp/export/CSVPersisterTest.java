package org.mdpnp.apps.testapp.export;


import com.rti.dds.subscription.SampleInfo;
import ice.Numeric;
import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.apps.testapp.vital.ValueImpl;
import org.mdpnp.apps.testapp.vital.Vital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CSVPersisterTest {

    private final static Logger log = LoggerFactory.getLogger(CSVPersisterTest.class);

    private static final SampleInfo si = new SampleInfo();

    @Test
    public void testCVSLine() throws Exception {

        SimpleDateFormat dateFormat = DataCollector.dateFormats.get();
        Date d0 = dateFormat.parse("20150203.235809.985-0500");

        Vital vital = new MockedVital();

        Value v =  new ValueImpl("DEVICE0", "METRIC0", 0, vital);
        Numeric n = new Numeric();
        n.value = 13.31f;
        n.device_time = new ice.Time_t();
        n.device_time.sec = (int)(d0.getTime()/1000);

        v.updateFrom(n, si);

        String line = CSVPersister.toCSVLine(v);
        Assert.assertEquals("Invalid csv line", "DEVICE0,METRIC0,0,20150203235809-0500,13.31", line);
    }

    @Test
    public void testPersistValue() throws Exception {

        CSVPersister p = new CSVPersister();
        p.start();

        long now = System.currentTimeMillis()/1000;

        Vital vital = new MockedVital();

        for(int i=0; i<20; i++) {
            Value v =  new ValueImpl("DEVICE0", "METRIC0", 0, vital);
            Numeric n = new Numeric();
            n.value = (float)Math.sin(i);
            n.device_time = new ice.Time_t();
            n.device_time.sec = (int)(now + i);

            v.updateFrom(n, si);

            DataCollector.DataSampleEvent evt = new DataCollector.DataSampleEvent(v);
            p.handleDataSampleEvent(evt);
        }
    }
}
