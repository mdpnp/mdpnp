package org.mdpnp.apps.testapp;

import ice.DeviceConnectivity;
import ice.DeviceIdentity;

import java.lang.ref.SoftReference;

import org.jfree.util.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Convenience class for storing DeviceIdentity and DeviceConnectivity instances
 * DeviceIdentity is required, DeviceConnectivity is only relevant for "connected" devices
 * and may be null.  A softreference to a DeviceIcon constructed from the DeviceIdentity raster
 * is also maintained (and reconstructed on demand). 
 * @author jplourde
 *
 */
public class Device {
    private final DeviceIdentity deviceIdentity = new DeviceIdentity();
    private DeviceConnectivity deviceConnectivity;
    
    private SoftReference<DeviceIcon> realIcon;
    
    public final static int SHORT_UDI_LENGTH = 20;
    
    private final static Logger log = LoggerFactory.getLogger(Device.class);
    
    public Device() {
        
    }
    
    public DeviceIcon getIcon() {
        DeviceIcon di = null;
        if(null != realIcon) {
            di = realIcon.get();
        }
        
        if(di != null && di.isBlank() && deviceIdentity.icon.raster != null && deviceIdentity.icon.width > 0 && deviceIdentity.icon.height > 0) {
            di = null;
            log.debug("Constructing a new Icon with new ice.Image data");
        }
        
        if(null == di) {
            di = new DeviceIcon(deviceIdentity.icon);
            realIcon = new SoftReference<DeviceIcon>(di);
        }
        
        return di;
    }

    public String getMakeAndModel() {
        if(deviceIdentity.manufacturer.equals(deviceIdentity.model)) {
            return deviceIdentity.model;
        } else {
            return deviceIdentity.manufacturer + " " + deviceIdentity.model;
        }
    }
    
    public String getShortUDI() {
        return null == deviceIdentity.universal_device_identifier ? null : deviceIdentity.universal_device_identifier.substring(0, SHORT_UDI_LENGTH);
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
