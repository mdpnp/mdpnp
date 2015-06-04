package org.mdpnp.devices;


import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

public class DeviceClockTest {

  private static final Logger log = LoggerFactory.getLogger(DeviceClockTest.class);

  /**
   * test the clock drift, etc on the implementation that returns a simple clock
   * reading. i.e device does not have an external reference clock to generate
   * a combined reading.
   *
   * @throws Exception
   */
  @Test
  public void testSimpleClockImpl() throws Exception {

    final Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR,        1986);
    cal.set(Calendar.MONTH,       Calendar.JANUARY);
    cal.set(Calendar.DATE,        28);
    cal.set(Calendar.HOUR_OF_DAY, 11);
    cal.set(Calendar.MINUTE,      39);
    cal.set(Calendar.SECOND,      13);
    cal.set(Calendar.MILLISECOND, 40);

    DeviceClockImpl deviceClock = new DeviceClockImpl() {
      @Override
      long systemCurrentTimeMillis() {
        return cal.getTimeInMillis();
      }
    };

    // start with the system clock being in sync with device
    long delta0 = deviceClock.receiveDateTime(new Date(cal.getTimeInMillis()));
    Assert.assertEquals("Invalid time delta", 0L, delta0);

    DeviceClock.Reading r0 = deviceClock.instant();
    long dt0 = r0.getTime().toEpochMilli();
    Assert.assertEquals("Invalid device time",  new Date(cal.getTimeInMillis()), new Date(dt0));

    // pretend the device got faster than the system clock
    cal.add(Calendar.MILLISECOND, -20);

    long dt1 = deviceClock.instant().getTime().toEpochMilli();
    Assert.assertEquals("Invalid device time",  new Date(dt0), new Date(dt1));

    // device is still slower, but there is a drift.
    cal.add(Calendar.MILLISECOND, 200);
    long delta1 = deviceClock.receiveDateTime(new Date(cal.getTimeInMillis()-50));
    Assert.assertEquals("Invalid time delta", -50L, delta1);

    long dt3 = deviceClock.instant().getTime().toEpochMilli();
    Assert.assertEquals("Invalid device time",  new Date(cal.getTimeInMillis()-50L), new Date(dt3));

  }

  @Test
  public void testReferenceClockImpl() throws Exception {

    final Calendar cal = Calendar.getInstance();
    cal.set(Calendar.YEAR,        1986);
    cal.set(Calendar.MONTH,       Calendar.JANUARY);
    cal.set(Calendar.DATE,        28);
    cal.set(Calendar.HOUR_OF_DAY, 11);
    cal.set(Calendar.MINUTE,      39);
    cal.set(Calendar.SECOND,      13);
    cal.set(Calendar.MILLISECOND, 0);

    final long wallTime = cal.getTimeInMillis();

    DeviceClock wallClock = new DeviceClock.WallClock() {
      @Override
      protected long getTimeInMillis() {
        return wallTime;
      }
    };

    DeviceClockImpl deviceClock = new DeviceClockImpl(wallClock) {
      @Override
      long systemCurrentTimeMillis() {
        return cal.getTimeInMillis();
      }
    };

    // start with the system clock being in sync with device
    long delta0 = deviceClock.receiveDateTime(new Date(cal.getTimeInMillis()));
    Assert.assertEquals("Invalid time delta",  0L, delta0);

    DeviceClock.Reading r0 = deviceClock.instant();
    long dt0 = r0.getTime().toEpochMilli();
    Assert.assertEquals("Invalid device time",  new Date(cal.getTimeInMillis()), new Date(dt0));

    // pretend the device got faster than the system clock
    cal.add(Calendar.MILLISECOND, -20);

    // The clock would return the reference reading so setting device should not have any effect
    // on the reading
    long dt1 = deviceClock.instant().getTime().toEpochMilli();
    Assert.assertEquals("Invalid device time",  new Date(dt0), new Date(dt1));

    // device is still slower, but there is a drift.
    cal.add(Calendar.MILLISECOND, 200);
    long delta1 = deviceClock.receiveDateTime(new Date(cal.getTimeInMillis()-50));
    Assert.assertEquals("Invalid time delta", -50L, delta1);

    long dt3 = deviceClock.instant().getDeviceTime().toEpochMilli();
    Assert.assertEquals("Invalid device time",  new Date(cal.getTimeInMillis()-50L), new Date(dt3));

  }

  // This was modeled after the Draeger Clock
  //
  static class DeviceClockImpl implements DeviceClock  {
    final DeviceClock referenceClock;

    public DeviceClockImpl(final DeviceClock referenceClock) {
      this.referenceClock = referenceClock;
    }

    public DeviceClockImpl() {
      this(null);
    }

    private final ThreadLocal<Long> currentTime = new ThreadLocal<Long>() {
      protected Long initialValue() {
        return 0L;
      };
    };

    protected long deviceClockOffset = 0L;

    protected long receiveDateTime(Date date) {
      deviceClockOffset = date.getTime() - systemCurrentTimeMillis();
      log.debug("Device says date is: " + date + " - Local clock offset " + deviceClockOffset + "ms from device");
      return deviceClockOffset;
    }

    long systemCurrentTimeMillis() {
      return System.currentTimeMillis();
    }

    @Override
    public Reading instant() {
      Reading r;

      if(referenceClock != null) {
        r = new DeviceClock.CombinedReading(
            referenceClock.instant(),
            new DeviceClock.ReadingImpl(currentTimeAdjusted()));
      }
      else {
        r = new DeviceClock.ReadingImpl(currentTimeAdjusted());
      }

      return r;
    }

    protected long currentTimeAdjusted() {
      long now =  systemCurrentTimeMillis() + deviceClockOffset;
      long then = currentTime.get();
      if (then - now > 0L) {
        // This happens too routinely to expend the I/O here
        // tried using the destination_order.source_timestamp_tolerance but
        // that was even too tight
        // TODO reconsider how we are deriving a device timestamp
        // log.warn("Not emitting timestamp="+new
        // Date(now)+" where last timestamp was "+new Date(then));
        return then;
      } else {
        currentTime.set(now);
        return now;
      }
    }
  }

}
