package org.mdpnp.devices.draeger.medibus;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

public class TestAbstractDraegerVent {
    @Test
    public void testLoadMap() throws Exception  {
        Map<Enum<?>, String> numerics = new HashMap<Enum<?>, String>();
        Map<Enum<?>, String> waveforms = new HashMap<Enum<?>, String>();
        AbstractDraegerVent.loadMap(numerics, waveforms);
        System.out.println(numerics);
        System.out.println(waveforms);
    }
}
