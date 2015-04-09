package org.mdpnp.devices.serial;

import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author mfeinberg
 */
public class SerialProviderFactoryTest {

    @Test
    public void testDefaultProviderPortNames() throws Exception {

        SerialProvider sp = SerialProviderFactory.locateDefaultProvider();
        Assert.assertNotNull("Could not locate provider", sp);
        List<String> ports =  sp.getPortNames();
        Assert.assertNotNull("Invalid port list", ports);
        Assert.assertTrue("Port list is empty", ports.size() != 0);
    }
}
