package org.mdpnp.devices;


import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Time_t;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DomainClock implements DeviceClock  {

    private static final Logger log = LoggerFactory.getLogger(AbstractDevice.class);

    public static Time_t toDDSTime(long timestamp) {
        Time_t t = new Time_t( (int) (timestamp / 1000L), (int) (timestamp % 1000L * 1000000L));
        return t;
    }

    public static Time_t toDDSTime(DeviceClock.Reading timestamp) {
        return toDDSTime(timestamp.getTime());
    }

    public static long toMilliseconds(Time_t timestamp) {
        long t = 1000L*timestamp.sec+timestamp.nanosec/1000000L;
        return t;
    }

    public DomainClock(DomainParticipant dp) {
        if(dp==null)
            throw new IllegalArgumentException("Domain handle cannot be null");
        this.domainParticipant = dp;
    }

    // Resolution of SampleArray samples will be reduced
    // dynamically based upon what SampleArrays are registered
    // at what frequency.
    private int sampleArrayResolutionNs = 1000000000;

    private final DomainParticipant domainParticipant;


    void ensureResolutionForFrequency(int frequency, int size) {
        int periodNs = 1000000000 / frequency;
        periodNs *= size;
        if(periodNs < sampleArrayResolutionNs) {
            log.info("Increase resolution sampleArrayResolutionNs for " + size + " samples at " + frequency + "Hz from minimum period of " + sampleArrayResolutionNs + "ns to " + periodNs + "ns");
            sampleArrayResolutionNs = periodNs;
        }
    }

    // TBD remove conversions
    static Time_t timeSampleArrayResolution(int resolutionNs, Time_t t) {
        if(resolutionNs>=1000000000) {
            int seconds = resolutionNs / 1000000000;
            t.sec -= 0 == seconds ? 0 : (t.sec % seconds);
            int nanoseconds = resolutionNs % 1000000000;
            if(nanoseconds == 0) {
                // max res (min sample period) is an even number of seconds
                t.nanosec = 0;
            } else {
                t.nanosec -= 0 == nanoseconds ? 0 : (t.nanosec % nanoseconds);
            }
        } else {
            t.nanosec -= 0 == resolutionNs ? 0 : (t.nanosec % resolutionNs);
        }
        return t;
    }

    long currentTime() {
        Time_t t = new Time_t(0, 0);
        domainParticipant.get_current_time(t);
        return DomainClock.toMilliseconds(t);
    }

    @Override
    public DeviceClock.Reading instant() {
        return new DeviceClock.Reading() {

            private final long ms = currentTime();

            @Override
            public long getDeviceTime() {
                return 0;
            }

            @Override
            public boolean hasDeviceTime() {
                return false;
            }

            @Override
            public long getTime() {
                Time_t tt = DomainClock.toDDSTime(ms);
                tt = timeSampleArrayResolution(sampleArrayResolutionNs, tt);
                return DomainClock.toMilliseconds(tt);
            }

            @Override
            public DeviceClock.Reading refineResolutionForFrequency(int frequency, int size) {
                ensureResolutionForFrequency(frequency, size);
                return this;
            }
        };
    }

}

