package org.mdpnp.devices;

import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.devices.AbstractDevice.ArrayContainer;
import org.mdpnp.devices.AbstractDevice.CollectionContainer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class AbstractDeviceTest {

    @Test
    public void testNullSaveArrayContainer() throws Exception {

        Double f[] = new Double[] { 1.0, 2.0, 3.0, 4.0, 5.0 };

        AbstractDevice.ArrayContainer<Number> a0 = new AbstractDevice.ArrayContainer<>(null);
        Assert.assertTrue(a0.isNull());
        Assert.assertFalse(a0.iterator().hasNext());

        int n;

        /*AbstractDevice.ArrayContainer<Number> a1 */ ArrayContainer<Double> a1= new AbstractDevice.ArrayContainer<>(f);
        Assert.assertFalse(a1.isNull());
        Assert.assertEquals(a1.size(), 5);
        n=0;
        for(Iterator i=a1.iterator(); i.hasNext(); i.next()) {
            n++;
        }
        Assert.assertEquals(n, 5);

        ArrayContainer<Double> a2 /*AbstractDevice.ArrayContainer<Number> a2*/ = new AbstractDevice.ArrayContainer<>(f, 2);
        Assert.assertFalse(a2.isNull());
        Assert.assertEquals(a2.size(), 2);
        n=0;
        for(Iterator i=a2.iterator(); i.hasNext(); i.next()) {
            n++;
        }
        Assert.assertEquals(n, 2);
    }

    @Test
    public void testNullSaveCollectionContainer() throws Exception {

        Double f[] = new Double[]{1.0, 2.0, 3.0, 4.0, 5.0};

        AbstractDevice.CollectionContainer<Number> a0 = new AbstractDevice.CollectionContainer<>(null);
        Assert.assertTrue(a0.isNull());
        Assert.assertFalse(a0.iterator().hasNext());

        List<Double> l /*List<Number> l*/ = Arrays.asList(f);

        CollectionContainer<Double> a1 /* AbstractDevice.CollectionContainer<Number> a1 */= new AbstractDevice.CollectionContainer<>(l);
        Assert.assertFalse(a1.isNull());
        Assert.assertEquals(a1.size(), 5);
        int n = 0;
        for (Iterator i = a1.iterator(); i.hasNext(); i.next()) {
            n++;
        }
        Assert.assertEquals(n, 5);
    }
}
