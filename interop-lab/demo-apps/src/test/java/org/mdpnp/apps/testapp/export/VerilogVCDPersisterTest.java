package org.mdpnp.apps.testapp.export;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VerilogVCDPersisterTest {

    private final static Logger log = LoggerFactory.getLogger(VerilogVCDPersisterTest.class);

    @Test
    public void testBasicDataStream() throws Exception {

        File f = File.createTempFile("VerilogVCDPersisterTest-", ".vcd");
        f.deleteOnExit();
        final OutputStream fos = new FileOutputStream(f);

        VerilogVCDPersister.OneWavePerVCD p = new VerilogVCDPersister.OneWavePerVCD(null, VerilogVCDPersister.FZ_1MB) {
            @Override
            protected OutputStream makeStream(String key) {
                Assert.assertEquals("Invalid file name", "DEVICE0-METRIC0-0", key);
                return fos;
            }
        };

        p.start();

        // does not matter what date it is, as long as we have smth
        // to compare to the 'gold' file
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date d = sdf.parse("01/12/2014");

        long now = d.getTime();

        for(int n=0; n<400; n++) {
            NumericsDataCollector.NumericSampleEvent evt = NumericsDataCollector.toEvent("DEVICE0", "METRIC0", 0, now + n * 1000L, mockData(n));
            p.persist(evt);
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
    public void testFileSizeEnforcement() throws Exception {

        File f = File.createTempFile("VerilogVCDPersisterTest-", ".vcd");
        f.deleteOnExit();
        final OutputStream fos = new FileOutputStream(f);

        long sizeLimit=10000;
        VerilogVCDPersister.OneWavePerVCD p = new VerilogVCDPersister.OneWavePerVCD(null, sizeLimit) {
            @Override
            protected OutputStream makeStream(String key) {
                return fos;
            }
        };

        p.start();

        long now = System.currentTimeMillis();

        // if no cap, the loop of 1000 values will produce a 20K bytes file
        for (int n = 0; n < 1000; n++) {
            NumericsDataCollector.NumericSampleEvent evt = NumericsDataCollector.toEvent("DEVICE0", "METRIC0", 0, now + n * 1000L, mockData(n));
            p.persist(evt);
        }

        p.stop();

        long fileSize = f.length();
        // allow 1K rounding for the 'last' append
        Assert.assertTrue("Invalid file size - limit exceeded", Math.abs(sizeLimit-fileSize)<1000);
    }

    @Test
    public void testHandleRandomDatStream() throws Exception {

        // Make a directory where data will be dumped.

        File root = File.createTempFile("VCD-", "-TEST");
        root.delete();
        root.mkdirs();

        try {
            VerilogVCDPersister.OneWavePerVCD p = new VerilogVCDPersister.OneWavePerVCD(root, VerilogVCDPersister.FZ_1MB);
            p.start();

            long now = System.currentTimeMillis();

            for (int n = 0; n < 50; n++) {
                long t = (n + (int) (Math.random() * 10))*1000;

                for (int m = (int) (Math.random() * 10); m > 0; m--) {
                    NumericsDataCollector.NumericSampleEvent evt = NumericsDataCollector.toEvent("DEVICE0", "METRIC" + m, 0, now + t, mockData(n));
                    p.persist(evt);
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
