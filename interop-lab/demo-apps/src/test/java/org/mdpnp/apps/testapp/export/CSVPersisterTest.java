package org.mdpnp.apps.testapp.export;


import com.rti.dds.subscription.SampleInfo;
import ice.Numeric;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CSVPersisterTest {

    private final static Logger log = LoggerFactory.getLogger(CSVPersisterTest.class);

    @Test
    public void testCVSLine() throws Exception {

        SimpleDateFormat dateFormat = DataCollector.dateFormats.get();
        Date d0 = dateFormat.parse("20150203.235809.985-0500");

        Value v =  DataCollector.toValue("DEVICE0", "METRIC0", 0, d0.getTime(), 13.31);

        String line = CSVPersister.toCSVLine(v);
        Assert.assertEquals("Invalid csv line", "DEVICE0,METRIC0,0,20150203235809-0500,13.31", line);
    }

    @Test
    public void testPersistValue() throws Exception {

        CSVPersister p = new CSVPersister();
        p.start();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        for(int i=0; i<20; i++) {

            long now = calendar.getTime().getTime();

            Value v =  DataCollector.toValue("DEVICE0", "METRIC0", 0, now,  (float)Math.sin(i));

            DataCollector.DataSampleEvent evt = new DataCollector.DataSampleEvent(v);
            p.handleDataSampleEvent(evt);

            calendar.add(Calendar.MINUTE, 1);

        }
    }
}
