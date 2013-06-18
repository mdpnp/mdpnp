package org.mdpnp.apps.testapp;

import java.util.HashSet;
import java.util.Set;

import ice.DeviceConnectivity;
import ice.DeviceIdentity;
import ice.Numeric;
import ice.SampleArray;

public class Device {
    private final DeviceIdentity deviceIdentity = new DeviceIdentity();
    private DeviceConnectivity deviceConnectivity;
    
    public Device() {
        
    }
    
    public Device(DeviceIdentity di) {
        deviceIdentity.copy_from(di);
    }
    
    public DeviceIdentity getDeviceIdentity() {
        return deviceIdentity;
    }
    
    public DeviceConnectivity getDeviceConnectivity() {
        return deviceConnectivity;
    }
    
    public void setDeviceConnectivity(DeviceConnectivity deviceConnectivity) {
        if(null == deviceConnectivity) {
            this.deviceConnectivity = null;
        } else {
            if(null == this.deviceConnectivity) {
                this.deviceConnectivity = new DeviceConnectivity(deviceConnectivity);
            } else {
                this.deviceConnectivity.copy_from(deviceConnectivity);
            }
        }
    }
}
