package org.mdpnp.apps.testapp.export;


import com.rti.dds.subscription.SampleInfo;
import ice.Numeric;
import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataCollectorTest {

    @Test
    public void testBuildValue() throws Exception {

        SimpleDateFormat dateFormat = DataCollector.dateFormats.get();
        Date d0 = dateFormat.parse("20150203.235809.985-0500");

        SampleInfo si = new SampleInfo();
        Value v = DataCollector.toValue(si, "D0", "M0", 0, d0.getTime(),  13.31);
//        Assert.assertEquals("Invalid values for sec",      v.getNumeric().device_time.sec,     1423025889);
//        Assert.assertEquals("Invalid values for nanonsec", v.getNumeric().device_time.nanosec, 985000000);

//        Numeric n = v.getNumeric();
//        long ms = DataCollector.toMilliseconds(n.device_time);
//        String s = dateFormat.format(new Date(ms));
//        Assert.assertEquals("Invalid round-trip", s, "20150203.235809.985-0500");

    }
}
