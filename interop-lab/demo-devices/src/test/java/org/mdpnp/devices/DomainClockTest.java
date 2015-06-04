package org.mdpnp.devices;


import com.rti.dds.infrastructure.Time_t;
import org.junit.Assert;
import org.junit.Test;

import java.time.Instant;
import java.util.Date;

public class DomainClockTest {

    @Test
    public void testDDSTime() {

        long l0 = System.currentTimeMillis();
        Time_t t = DomainClock.toDDSTime(l0);
        long l1 = DomainClock.toMilliseconds(t);

        Assert.assertEquals(l0, l1);
    }

    @Test
    public void testTimeSampleArrayResolution() {

        long l0 = 1426017249228L; // reading of System.currentTimeMillis(); on Tue Mar 10 15:54:51 EDT 2015

        Time_t t0 = Time_t.from_millis(l0);
        Instant i0 = Instant.ofEpochMilli(l0);

        for(int size = 25; size<900; size=(int)(size*1.3)) {

            int resolutionNsPerSample = DomainClock.ensureResolutionForFrequency(DomainClock.DEFAULT_SAMPLE_ARRAY_RESOLUTION, 333, size);

            Time_t t1 = Time_t.from_millis(l0); // this will be in-place modification....
            t1 = DomainClock.timeSampleArrayResolution(resolutionNsPerSample, t1);

            System.out.println(t0.sec + "." + t0.nanosec + " @resolutionNs=" + resolutionNsPerSample + " -> " + t1.sec + "." + t1.nanosec) ;
            Instant i1 = DomainClock.timeSampleArrayResolution(resolutionNsPerSample, i0);

            Assert.assertEquals("Failed @resolutionNs=" + resolutionNsPerSample, DomainClock.toMilliseconds(t1), i1.toEpochMilli());
        }
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

        Assert.assertEquals("combined reading should return the reference time", r.getTime(), Instant.ofEpochMilli(now));
        Assert.assertTrue("combined reading must contain device time", r.hasDeviceTime());
        Assert.assertEquals("invalid value for device time", r.getDeviceTime(), Instant.ofEpochMilli(0));

    }
}
