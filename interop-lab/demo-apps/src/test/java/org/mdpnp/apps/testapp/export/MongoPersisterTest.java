package org.mdpnp.apps.testapp.export;

import ice.Patient;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

/**
 * Test and simple driver to help debug js script w/out main app.
 */
public class MongoPersisterTest {

    private static final Logger log = LoggerFactory.getLogger(MongoPersisterTest.class);

    private static int N_DEVICES=10;
    private static long SLEEP_MS=500;

    @Test
    public void testInitJSRuntime() throws Exception {

        MongoPersister mongo = new MongoPersister();

        boolean cpOk=mongo.initJSRuntime("MongoPersisterTest.js");
        Assert.assertTrue("Should have located resource on classpath", cpOk);

        URL u = getClass().getResource("MongoPersisterTest.js");
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

    // Simple driver to test the script
    //
    // you can to point it to some local database:
    //
    // MongoPersisterTest.js 192.168.99.100 local 10
    //
    // Or to test warfighter demo on arvi:
    //
    // MongoPersisterWF.js 192.168.7.21 warfighter 10
    //
    public static void main(String[] args) throws Exception {

        if(args.length==0)
            throw new IllegalStateException("Usage: <scriptname> host dbName [nrecords]");

        String scriptName=args[0];
        String host = args.length==1?"localhost":args[1];
        String database = args.length==2?"local":args[2];

        MongoPersister mongo = new MongoPersister();

        boolean scriptOk = mongo.initJSRuntime(scriptName);
        if(!scriptOk)
            throw new IllegalStateException("Cannot load the script");

        boolean ok = mongo.makeMongoClient(host, 27017, database);
        if(!ok)
            throw new IllegalStateException("Cannot connect to the database");

        Patient p = new Patient();
        p.mrn = "12345";

        int sz = args.length==3?10:Integer.parseInt(args[3]);

        for(int n=0; n<sz; n++) {

            int m = (int) Math.floor(Math.random()*N_METRICS.length);
            int d = (int) Math.floor(Math.random()*N_DEVICES);

            long now = System.currentTimeMillis();

            NumericsDataCollector.NumericSampleEvent evt =  NumericsDataCollector.toEvent("DEVICE_"+d, N_METRICS[m], 0, now,  (float)Math.sin(n));
            log.info("Observation: " + evt);

            mongo.handleDataSampleEvent(evt);

            Thread.sleep((long) Math.floor(Math.random()*SLEEP_MS));
        }

        mongo.stop();
    }


    static String N_METRICS [] = {

            rosetta.MDC_PRESS_BLD_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_AORT_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_ART_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_ART_ABP_SYS.VALUE,
            rosetta.MDC_PRESS_INTRA_CRAN_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_ART_FEMORAL_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_ART_PULM_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_ART_UMB_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_ATR_LEFT_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_ATR_RIGHT_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_PULM_CAP_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_VEN_UMB_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_VENT_LEFT_SYS.VALUE,
            rosetta.MDC_PRESS_BLD_VENT_RIGHT_SYS.VALUE,
            rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE,
            rosetta.MDC_PULS_RATE_NON_INV.VALUE,
            rosetta.MDC_BLD_PULS_RATE_INV.VALUE,
            rosetta.MDC_ECG_CARD_BEAT_RATE.VALUE,
            rosetta.MDC_ECG_HEART_RATE.VALUE,
            rosetta.MDC_PULS_RATE.VALUE
    };
}
