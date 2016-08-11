package org.mdpnp.apps.testapp;

import java.io.InputStream;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

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
    public void testCommandLineParse0() throws Exception {

        Configuration v = Configuration.read(new String[]{"-help"});
        Assert.assertNull("Should have printed help", v);
    }

    @Test
    public void testCommandLineParse1() throws Exception {

        try {
            Configuration.read(new String[]{"-app", "xyz", "-domain", "1"});
        } catch (IllegalArgumentException ex) {
            Assert.assertEquals("Invalid app name: xyz", ex.getMessage());
            return;
        }
        Assert.fail("Should have failed on invalid args");
    }

    @Test
    public void testCommandLineParse2() throws Exception {

        try {
            Configuration.read(new String[]{"-app", "ICE_Device_Interface", "-domain", "1"});
        }
        catch(IllegalArgumentException ex) {
            Assert.assertEquals("Missing device specification", ex.getMessage());
            return;
        }
        Assert.fail("Should have failed on invalid args");
    }

    @Test
    public void testCommandLineParse3() throws Exception {

        try {
            Configuration.read(
                    new String[]{"-app", "ICE_Device_Interface", "-device", "Ivy450C", "-domain", "1"});
        }
        catch(IllegalArgumentException ex) {
            Assert.assertEquals("Missing address specification", ex.getMessage());
            return;
        }
        Assert.fail("Should have failed on invalid args");
    }

    @Test
    public void testCommandLineParse4() throws Exception {

        Configuration v = Configuration.read(
                new String[]{"-app", "ICE_Device_Interface", "-device", "Ivy450C", "-address", "127.0.0.1:8080", "-domain", "1"});

        Assert.assertEquals(v.getAddress(), "127.0.0.1:8080");
    }

    @Test
    public void testCreateContext() throws Exception {

        Configuration c = new Configuration(false, Configuration.Application.ICE_Device_Interface, 1234, null, null, "");

        ApplicationContext ctx =
                c.createContext("classpath*:/org/mdpnp/apps/testapp/ConfigurationTestContext.0.xml");

        Assert.assertNotNull("Failed to load child context", ctx);
        Integer i = (Integer)ctx.getBean("domainId");

        Assert.assertEquals("Failed to set property", new Integer(1234), i);
    }
}
