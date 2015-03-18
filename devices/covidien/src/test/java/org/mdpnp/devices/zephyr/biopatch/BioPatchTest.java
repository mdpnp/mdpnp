package org.mdpnp.devices.zephyr.biopatch;

import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.devices.DeviceClock;

import java.nio.ByteBuffer;
import java.util.Calendar;

/**
 *
 */
public class BioPatchTest {

    @Test
    public void testBioPatchClock()
    {
        BioPatch.BioPatchClock clock = new BioPatch.BioPatchClock(new DeviceClock.WallClock());

        ByteBuffer bb = ByteBuffer.allocate(100);
        bb.putShort((short)2015);
        bb.put((byte) 10);
        bb.put((byte) 13);
        bb.putInt(123457890);
        bb.rewind();

        DeviceClock.Reading t = clock.instant(bb);
        long ms = t.getDeviceTime().toEpochMilli();

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(ms);
        Assert.assertEquals("Bad year", 2015, cal.get(Calendar.YEAR));
        Assert.assertEquals("Bad day",    14, cal.get(Calendar.DAY_OF_MONTH)); // 0-based
        Assert.assertEquals("Bad day",    10, cal.get(Calendar.HOUR_OF_DAY));
        Assert.assertEquals("Bad day",    17, cal.get(Calendar.MINUTE));
    }
}
