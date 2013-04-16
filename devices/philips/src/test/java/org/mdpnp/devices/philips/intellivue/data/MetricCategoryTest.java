package org.mdpnp.devices.philips.intellivue.data;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MetricCategoryTest {
    @Test
    public void testEnumValues() {
        for(MetricCategory mc : MetricCategory.values()) {
            assertEquals(mc, MetricCategory.valueOf(mc.asInt()));
        }
    }
}
