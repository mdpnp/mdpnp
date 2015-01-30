package org.mdpnp.apps.testapp.export;


import com.rti.dds.subscription.SampleInfo;
import ice.Numeric;
import org.junit.Test;
import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.apps.testapp.vital.ValueImpl;
import org.mdpnp.apps.testapp.vital.Vital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.PrintStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class VerilogVCDPersisterTest {

    private final static Logger log = LoggerFactory.getLogger(VerilogVCDPersisterTest.class);

    Vital vital = new MockedVital();
    SampleInfo si = new SampleInfo();

    @Test
    public void testBasicDataStream() throws Exception {

        VerilogVCDPersister.OneWavePerVCD p = new VerilogVCDPersister.OneWavePerVCD(null) {
            @Override
            protected PrintStream makeStream(String key) {
                return System.out;
            }
        };

        p.start();

        long now = System.currentTimeMillis()/1000;

        for(int n=0; n<200; n++) {
            Value v = build("DEVICE0", "METRIC0", (int) (now + n), mockData(n));
            p.persist(v);
        }

        p.stop();
    }


    @Test
    public void testHandleRandomDatStream() throws Exception {

        // Make a directory where data will be dumped.

        File root = File.createTempFile("VCD-", "-TEST");
        root.delete();
        root.mkdirs();

        try {
            VerilogVCDPersister.OneWavePerVCD p = new VerilogVCDPersister.OneWavePerVCD(root);
            p.start();

            long now = System.currentTimeMillis() / 1000;

            for (int n = 0; n < 50; n++) {
                int t = n + (int) (Math.random() * 10);

                for (int m = (int) (Math.random() * 10); m > 0; m--) {
                    Value v = build("DEVICE0", "METRIC" + m, (int) (now + t), mockData(n));
                    p.persist(v);
                }
            }

            p.stop();
        }
        finally {
            // clean up files created by the persister
            File files[] = root.listFiles();
            for (File f : files) {
                f.delete();
            }
            root.delete();
        }
    }


    Value build(String dev, String metric, int t, double val)
    {
        Value v = new ValueImpl(dev, metric, 0, vital);
        Numeric numeric = new Numeric();
        numeric.value = (float) val;
        numeric.device_time = new ice.Time_t();
        numeric.device_time.sec = t;

        v.updateFrom(numeric, si);
        return v;
    }

    private static double mockData(int n)
    {
        double v = Math.sin(Math.toRadians(n))*10;
        return v;
    }
}
