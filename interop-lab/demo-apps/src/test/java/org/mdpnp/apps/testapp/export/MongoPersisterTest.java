package org.mdpnp.apps.testapp.export;

import ice.Patient;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Date;

/**
 * Test and simple driver to help debug js script w/out main app.
 */
public class MongoPersisterTest {

    private static final Logger log = LoggerFactory.getLogger(MongoPersisterTest.class);


    private static int N_METRICS=10;
    private static int N_DEVICES=10;
    private static long SLEEP_MS=500;

    @Test
    public void testInitJSRuntime() throws Exception {

        MongoPersister mongo = new MongoPersister();

        boolean cpOk=mongo.initJSRuntime("MongoPersisterWF.js");
        Assert.assertTrue("Should have located resource on classpath", cpOk);

        URL u = getClass().getResource("MongoPersisterWF.js");
        boolean fOk1=mongo.initJSRuntime(u.getFile());
        Assert.assertTrue("Should have located resource on file system", fOk1);

        boolean fOk2=mongo.initJSRuntime(u.toExternalForm());
        Assert.assertTrue("Should have located resource on file system", fOk2);
    }

    // Test disabled as it can only run with mono server present
    // @Test
    public void testDatabaseConnection() throws Exception {

        MongoPersister mongo = new MongoPersister();

        try {
            boolean noserver = mongo.makeMongoClient("127.0.0.1", 27018, "notthere"); // hopefully invalid port
            Assert.assertFalse("Should have failed on connection test", noserver);
        }
        catch(com.mongodb.MongoTimeoutException ok) {
            log.info("Timed out on the invalid server address");
        }

        boolean notOk=mongo.makeMongoClient("192.168.99.100", 27017, "notthere");
        Assert.assertFalse("Should have failed on connection test", notOk);

        boolean ok=mongo.makeMongoClient("192.168.99.100", 27017, "datasample_second");
        Assert.assertTrue("Should have passed connection test", ok);
    }

    public static void main(String[] args) throws Exception {

        String host = args.length==1?"localhost":args[0];
        MongoPersister mongo = new MongoPersister();

        mongo.initJSRuntime("MongoPersisterWF.js");

        boolean ok = mongo.makeMongoClient(host, 27017, "local");
        if(!ok)
            throw new IllegalStateException("Cannot connect to the database");

        Patient p = new Patient();
        p.mrn = "12345";

        int sz = args.length==1?10:Integer.parseInt(args[1]);
        for(int n=0; n<sz; n++) {

            int m = (int) Math.floor(Math.random()*N_METRICS);
            int d = (int) Math.floor(Math.random()*N_DEVICES);

            long now = System.currentTimeMillis();

            Value v =  DataCollector.toValue("DEVICE_"+d, "METRIC_"+m, 0, now,  (float)Math.sin(n));
            log.info("Observation: " + v);

            DataCollector.DataSampleEvent evt = new DataCollector.DataSampleEvent(v, p);
            mongo.handleDataSampleEvent(evt);

            Thread.sleep((long) Math.floor(Math.random()*SLEEP_MS));
        }

        mongo.stop();
    }
}
