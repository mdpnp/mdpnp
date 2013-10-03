package org.mdpnp.devices.cpc.bernoulli;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestDemoBernoulli {
    @Test
    public void testMappingFileContents() throws Exception  {
        Map<String, String> numerics = new HashMap<String, String>();
        Map<String, String> waveforms = new HashMap<String, String>();
        DemoBernoulli.populateMap(numerics, waveforms);
        System.out.println(numerics);
        System.out.println(waveforms);
    }
}
