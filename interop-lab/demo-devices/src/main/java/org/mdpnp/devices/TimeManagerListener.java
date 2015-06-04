package org.mdpnp.devices;

import com.rti.dds.infrastructure.Duration_t;

public interface TimeManagerListener {
    void aliveHeartbeat(String unique_device_identifier, String type, String host_name);
    
    void notAliveHeartbeat(String unique_device_identifier, String type);
    
    void synchronization(String remote_udi, Duration_t latency, Duration_t clockDifference);
}
