package org.mdpnp.devices;


import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Time_t;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

public class DomainClock implements DeviceClock  {

    private static final Logger log = LoggerFactory.getLogger(AbstractDevice.class);

    public static void toDDSTime(long timestamp, Time_t t) {
        t.sec = (int) (timestamp / 1000L);
        t.nanosec = (int) (timestamp % 1000L * 1000000L);
    }
    
    public static void toDDSTime(long timestamp, ice.Time_t t) {
        t.sec = (int) (timestamp / 1000L);
        t.nanosec = (int) (timestamp % 1000L * 1000000L);
    }    
    
    public static Time_t toDDSTime(long timestamp) {
        Time_t t = new Time_t( (int) (timestamp / 1000L), (int) (timestamp % 1000L * 1000000L));
        return t;
    }

    public static void toDDSTime(Instant timestamp, Time_t t) {
        t.sec = (int)timestamp.getEpochSecond();
        t.nanosec = timestamp.getNano();
    }
    
    public static void toDDSTime(Instant timestamp, ice.Time_t t) {
        t.sec = (int)timestamp.getEpochSecond();
        t.nanosec = timestamp.getNano();
    }    
    
    public static Time_t toDDSTime(Instant timestamp) {
        Time_t t = new Time_t((int)timestamp.getEpochSecond(), timestamp.getNano());
        return t;
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

    static final int DEFAULT_SAMPLE_ARRAY_RESOLUTION = 1000000000;

    // Resolution of SampleArray samples will be reduced
    // dynamically based upon what SampleArrays are registered
    // at what frequency.
    private int currentArrayResolutionNsPerSample = DEFAULT_SAMPLE_ARRAY_RESOLUTION;

    private final DomainParticipant domainParticipant;

    static int ensureResolutionForFrequency(int currentResolutionNsPerSample, int hertz, int size) {
        // It's important to multiply first because hertz may not divide 1000000000ns evenly
        long periodNs = 1000000000L * size / hertz;
        if(periodNs < currentResolutionNsPerSample) {
            if(periodNs < 0)
                throw new IllegalStateException("Frequency " + hertz + "Hz overflow for size " +  size);

            log.info("Increase resolution arrayResolutionNs for " + size + " samples at " + hertz +
                     "Hz from minimum period of " + currentResolutionNsPerSample + "ns to " + periodNs + "ns");

            currentResolutionNsPerSample = (int) periodNs;
        }
        return currentResolutionNsPerSample;
    }

    static Time_t timeSampleArrayResolution(int resolutionNsPerSample, Time_t t) {
        if(resolutionNsPerSample >=1000000000) {
            int secondsMod = resolutionNsPerSample / 1000000000;
            int nanosecondsMod = resolutionNsPerSample % 1000000000;

            t.sec -= 0 == secondsMod ? 0 : (t.sec % secondsMod);
            if(nanosecondsMod == 0) {
                // max res (min sample period) is an even number of seconds
                t.nanosec = 0;
            } else {
                t.nanosec -= 0 == nanosecondsMod ? 0 : (t.nanosec % nanosecondsMod);
            }
        } else {
            t.nanosec -= 0 == resolutionNsPerSample ? 0 : (t.nanosec % resolutionNsPerSample);
        }
        return t;
    }

    static Instant timeSampleArrayResolution(int resolutionNsPerSample, Instant t) {

        long sec     = t.getEpochSecond();
        long nanosec = t.getNano();

        if(resolutionNsPerSample >=1000000000) {
            int secondsMod = resolutionNsPerSample / 1000000000;
            int nanosecondsMod = resolutionNsPerSample % 1000000000;

            sec -= 0 == secondsMod ? 0 : (sec % secondsMod);
            if(nanosecondsMod == 0) {
                // max res (min sample period) is an even number of seconds
                nanosec = 0;
            } else {
                nanosec -= 0 == nanosecondsMod ? 0 : (nanosec % nanosecondsMod);
            }
        } else {
            nanosec -= 0 == resolutionNsPerSample ? 0 : (nanosec % resolutionNsPerSample);
        }
        return Instant.ofEpochSecond(sec, nanosec);
    }


    Instant currentTime() {
        Time_t dds = new Time_t(0, 0);
        domainParticipant.get_current_time(dds);
        Instant t = Instant.ofEpochSecond(dds.sec, dds.nanosec);
        return t;
    }

    @Override
    public DeviceClock.Reading instant() {
        return new DeviceClock.Reading() {

            private final Instant ms = currentTime();

            @Override
            public Instant getDeviceTime() {
                return null;
            }

            @Override
            public boolean hasDeviceTime() {
                return false;
            }

            @Override
            public Instant getTime() {
                Instant adjusted = timeSampleArrayResolution(currentArrayResolutionNsPerSample, ms);
                return adjusted;
            }

            @Override
            public DeviceClock.Reading refineResolutionForFrequency(int hertz, int size) {
                currentArrayResolutionNsPerSample = ensureResolutionForFrequency(currentArrayResolutionNsPerSample, hertz, size);
                return this;
            }
        };
    }

}

