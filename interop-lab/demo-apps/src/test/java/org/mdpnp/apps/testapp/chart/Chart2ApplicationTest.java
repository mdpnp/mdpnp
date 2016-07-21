package org.mdpnp.apps.testapp.chart;

import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

/**
 *
 */
public class Chart2ApplicationTest {

    @Test
    public void testReadObservationTypes() throws Exception {

        Chart2Application srv = new Chart2Application();
        Collection<Chart2Application.ObservationType> list = srv.readObservationTypes();
        Assert.assertTrue("Failed to read ObservationCodes", list.size() > 0);
    }

}

