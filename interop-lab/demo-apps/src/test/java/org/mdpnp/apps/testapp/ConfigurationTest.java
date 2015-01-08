package org.mdpnp.apps.testapp;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.URL;

/**
 *
 */
public class ConfigurationTest {

    private final static Logger log = LoggerFactory.getLogger(ConfigurationTest.class);

    @Test
    public void testReadJumpStartSettings() throws Exception {
        testReadJumpStartSettings("JumpStartSettings.0.txt");
    }

    public void testReadJumpStartSettings(String fname) throws Exception
    {
        URL u = getClass().getResource("JumpStartSettings.0.txt");
        Assert.assertNotNull("Failed to read locate on classpath:" + fname, u);
        InputStream fis = u.openStream();
        try {
            Configuration runConf = Configuration.read(fis);
            Assert.assertNotNull("Failed to read config", runConf.getDeviceFactory());
            Assert.assertEquals("Failed to read config", "PO_Simulator", runConf.getDeviceFactory().getDeviceType().getAlias());
        }
        finally {
            fis.close();
        }
    }

    @Test
    public void testPrintHelp() throws Exception {
        Configuration.help(ConfigurationTest.class, System.out);
    }

}
