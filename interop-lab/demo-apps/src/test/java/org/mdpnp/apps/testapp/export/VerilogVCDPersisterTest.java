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

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class VerilogVCDPersisterTest {

    private final static Logger log = LoggerFactory.getLogger(VerilogVCDPersisterTest.class);

    @Test
    public void testBasicDataStream() throws Exception {

        File f = File.createTempFile("VerilogVCDPersisterTest-", ".vcd");
        f.deleteOnExit();
        OutputStream fos = new FileOutputStream(f);
        final PrintStream ps = new PrintStream(fos);

        VerilogVCDPersister.OneWavePerVCD p = new VerilogVCDPersister.OneWavePerVCD(null) {
            @Override
            protected PrintStream makeStream(String key) {
                Assert.assertEquals("Invalid file name", "DEVICE0-METRIC0-0", key);
                return ps; //System.out;
            }
        };

        p.start();

        // does not matter what date it is, as long as we have smth
        // to compare to the 'gold' file
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date d = sdf.parse("01/12/2014");

        long now = d.getTime();

        for(int n=0; n<400; n++) {
            Value v = DataCollector.toValue("DEVICE0", "METRIC0", 0, now + n * 1000L, mockData(n));
            p.persist(v);
        }

        p.stop();

        URL u = getClass().getResource("VCDTestDump0.vcd");
        Assert.assertNotNull("Failed to locate 'good' data", u);
        InputStream is = u.openStream();
        BufferedReader actual   = new BufferedReader(new InputStreamReader(is));

        BufferedReader expected = new BufferedReader(new FileReader(f));
        try {
            assertReaders(expected, actual);
        }
        finally {
            actual.close();
            expected.close();
        }
    }

    private static void assertReaders(BufferedReader expected,
                                     BufferedReader actual) throws IOException {
        String line;
        while ((line = expected.readLine()) != null) {
            Assert.assertEquals(line, actual.readLine());
        }

        Assert.assertNull("Actual had more lines then the expected.", actual.readLine());
        Assert.assertNull("Expected had more lines then the actual.", expected.readLine());
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

            long now = System.currentTimeMillis();

            for (int n = 0; n < 50; n++) {
                long t = (n + (int) (Math.random() * 10))*1000;

                for (int m = (int) (Math.random() * 10); m > 0; m--) {
                    Value v = DataCollector.toValue("DEVICE0", "METRIC" + m, 0, now + t, mockData(n));
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

    private static double mockData(int n)
    {
        double v = Math.sin(Math.toRadians(n))*10;
        return v;
    }
}
