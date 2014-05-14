package org.mdpnp.devices;

import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.subscription.SampleInfo;

public interface TimeManagerListener {
    void aliveHeartbeat(SampleInfo sampleInfo, ice.HeartBeat heartbeat);
    
    void notAliveHeartbeat(SampleInfo sampleInfo, ice.HeartBeat heartbeat);
    
    void synchronization(String remote_udi, Duration_t latency, Duration_t clockDifference);
}
