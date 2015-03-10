package org.mdpnp.devices;


import com.rti.dds.infrastructure.Time_t;
import org.junit.Assert;
import org.junit.Test;

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

        AbstractDevice.ArrayContainer<Number> a1 = new AbstractDevice.ArrayContainer<>(f);
        Assert.assertFalse(a1.isNull());
        Assert.assertEquals(a1.size(), 5);
        n=0;
        for(Iterator i=a1.iterator(); i.hasNext(); i.next()) {
            n++;
        }
        Assert.assertEquals(n, 5);

        AbstractDevice.ArrayContainer<Number> a2 = new AbstractDevice.ArrayContainer<>(f, 2);
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

        List<Number> l = Arrays.asList(f);

        AbstractDevice.CollectionContainer<Number> a1 = new AbstractDevice.CollectionContainer<>(l);
        Assert.assertFalse(a1.isNull());
        Assert.assertEquals(a1.size(), 5);
        int n = 0;
        for (Iterator i = a1.iterator(); i.hasNext(); i.next()) {
            n++;
        }
        Assert.assertEquals(n, 5);
    }

    @Test
    public void testConversion() {

        long l0 = System.currentTimeMillis();
        Time_t t = DomainClock.toDDSTime(l0);
        long l1 = DomainClock.toMilliseconds(t);

        Assert.assertEquals(l0, l1);
    }

    @Test
    public void testClockDecoration() {

        final long now = System.currentTimeMillis();

        DeviceClock ref = new DeviceClock()
        {
            @Override
            public Reading instant() {
                return new DeviceClock.ReadingImpl(now);
            }
        };

        DeviceClock epoch = new DeviceClock()
        {
            @Override
            public Reading instant() {
                return new DeviceClock.ReadingImpl(0);
            }
        };

        DeviceClock.Reading r = new DeviceClock.CombinedReading(ref.instant(), epoch.instant());

        Assert.assertEquals(r.getTime(), 0);

    }

}
