package org.mdpnp.devices.philips.intellivue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mdpnp.devices.philips.intellivue.data.Label;
import org.mdpnp.devices.philips.intellivue.data.ObservedValue;

public class TestDemoIntellivue {
    @Test
    public void testLoadMap() throws Exception  {
        Map<ObservedValue, String> numericMetricIds = new HashMap<ObservedValue, String>();
        Map<ObservedValue, Label> numericLabels = new HashMap<ObservedValue, Label>();
        Map<ObservedValue, String> sampleArrayMetricIds = new HashMap<ObservedValue, String>();
        Map<ObservedValue, Label> sampleArrayLabels = new HashMap<ObservedValue, Label>();
        DemoEthernetIntellivue.loadMap(numericMetricIds, numericLabels, sampleArrayMetricIds, sampleArrayLabels);
        System.out.println(numericMetricIds);
        System.out.println(numericLabels);
        System.out.println(sampleArrayMetricIds);
        System.out.println(sampleArrayLabels);
    }
}
