package org.mdpnp.apps.testapp.export;


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
    public void testCVSLineNumeric() throws Exception {

        SimpleDateFormat dateFormat = DataCollector.dateFormats.get();
        Date d0 = dateFormat.parse("20150203.235809.985-0500");

        NumericsDataCollector.NumericSampleEvent evt =
                NumericsDataCollector.toEvent("DEVICE0", "METRIC0", 0, d0.getTime(), 13.31);

        String line = CSVPersister.toCSVLine(evt);
        Assert.assertEquals("Invalid csv line", "1,DEVICE0,METRIC0,0,20150203235809-0500,UNDEFINED,1,13.31", line);
    }
    
    @Test
    public void testCVSLineNumericGroupingOff() throws Exception {

        SimpleDateFormat dateFormat = DataCollector.dateFormats.get();
        Date d0 = dateFormat.parse("20150203.235809.985-0500");

        NumericsDataCollector.NumericSampleEvent evt =
                NumericsDataCollector.toEvent("DEVICE0", "METRIC0", 0, d0.getTime(), 1001);

        String line = CSVPersister.toCSVLine(evt);
        Assert.assertEquals("Invalid csv line", "1,DEVICE0,METRIC0,0,20150203235809-0500,UNDEFINED,1,1001.00", line);
    }

    @Test
    public void testCVSLineArray() throws Exception {

        SimpleDateFormat dateFormat = DataCollector.dateFormats.get();
        Date d0 = dateFormat.parse("20150203.235809.985-0500");

        SampleArrayDataCollector.SampleArrayEvent evt0 =
                SampleArrayDataCollector.toEvent("DEVICE0", "METRIC0", 0, d0.getTime(),
                                                 new Double[] { 1.0, 1.1, 1.2, 1.3, 1.4 });

        String line0 = CSVPersister.toCSVLine(evt0);
        Assert.assertEquals("Invalid csv line", "2,DEVICE0,METRIC0,0,20150203235809-0500,UNDEFINED,5,1.000E0,1.100E0,1.200E0,1.300E0,1.400E0", line0);

        SampleArrayDataCollector.SampleArrayEvent evt1 =
                SampleArrayDataCollector.toEvent("DEVICE0", "METRIC0", 0, d0.getTime(),
                        new Double[] { 0.0001, 0.0002, 0.0003, 0.0004 });

        String line1 = CSVPersister.toCSVLine(evt1);
        Assert.assertEquals("Invalid csv line", "2,DEVICE0,METRIC0,0,20150203235809-0500,UNDEFINED,4,1.000E-4,2.000E-4,3.000E-4,4.000E-4", line1);

    }

    @Test
    public void testCVSLineObservation() throws Exception {

        SimpleDateFormat dateFormat = DataCollector.dateFormats.get();
        Date d0 = dateFormat.parse("20150203.235809.985-0500");

        PatientAssessmentDataCollector.PatientAssessmentEvent evt =
                PatientAssessmentDataCollector.toEvent("NURSE0", d0.getTime(), "OID0", "OBSERVATION0");


        String line = CSVPersister.toCSVLine(evt);
        Assert.assertEquals("Invalid csv line", "3,NURSE0,20150203235809-0500,UNDEFINED,1,OID0,OBSERVATION0", line);
    }

    @Test
    public void testPersistValue() throws Exception {

        CSVPersister p = new CSVPersister();
        p.setup(1, "1MB");
        p.start();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        for(int i=0; i<20; i++) {

            long now = calendar.getTime().getTime();

            NumericsDataCollector.NumericSampleEvent evt =  NumericsDataCollector.toEvent("DEVICE0", "METRIC0", 0, now,  (float)Math.sin(i));

            p.handleDataSampleEvent(evt);

            calendar.add(Calendar.MINUTE, 1);

        }
    }
}
