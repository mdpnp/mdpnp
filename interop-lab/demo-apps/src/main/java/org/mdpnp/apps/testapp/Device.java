/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.apps.testapp;

import ice.DeviceConnectivity;
import ice.DeviceIdentity;

import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.builtin.ParticipantBuiltinTopicData;
import com.rti.dds.infrastructure.Locator_t;
import com.rti.dds.infrastructure.Property_t;

/**
 * Convenience class for storing DeviceIdentity and DeviceConnectivity instances
 * DeviceIdentity is required, DeviceConnectivity is only relevant for
 * "connected" devices and may be null. A softreference to a DeviceIcon
 * constructed from the DeviceIdentity raster is also maintained (and
 * reconstructed on demand).
 * 
 * @author Jeff Plourde
 * 
 */
public class Device {
    private String udi;
    private final ParticipantBuiltinTopicData participantData = new ParticipantBuiltinTopicData();
    private DeviceIdentity deviceIdentity;
    private DeviceConnectivity deviceConnectivity;

    private SoftReference<DeviceIcon> realIcon;

    public final static int SHORT_UDI_LENGTH = 20;

    private final static Logger log = LoggerFactory.getLogger(Device.class);

    public Device() {
    }

    public Device(ParticipantBuiltinTopicData participantData) {
        setParticipantData(participantData);
    }

    public static final String getHostname(ParticipantBuiltinTopicData participantData) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < participantData.property.value.size(); i++) {
            Property_t prop = (Property_t) participantData.property.value.get(i);
            if ("dds.sys_info.hostname".equals(prop.name)) {
                sb.append(prop.value).append(" ");
            }
        }

        for (int i = 0; i < participantData.default_unicast_locators.size(); i++) {
            Locator_t locator = (Locator_t) participantData.default_unicast_locators.get(i);
            try {
                InetAddress addr = null;
                switch (locator.kind) {
                case Locator_t.KIND_TCPV4_LAN:
                case Locator_t.KIND_TCPV4_WAN:
                case Locator_t.KIND_TLSV4_LAN:
                case Locator_t.KIND_TLSV4_WAN:
                case Locator_t.KIND_UDPv4:
                    addr = InetAddress
                            .getByAddress(new byte[] { locator.address[12], locator.address[13], locator.address[14], locator.address[15] });
                    break;
                case Locator_t.KIND_UDPv6:
                default:
                    addr = InetAddress.getByAddress(locator.address);
                    break;
                }
                sb.append(addr.getHostAddress()).append(" ");
            } catch (UnknownHostException e) {
                log.error("getting locator address", e);
            }
        }
        return sb.toString();
    }

    public String getHostname() {
        return getHostname(participantData);
    }

    public DeviceIcon getIcon() {
        if (null == deviceIdentity) {
            return null;
        }

        DeviceIcon di = null;
        if (null != realIcon) {
            di = realIcon.get();
        }

        if (di != null && di.isBlank() && deviceIdentity.icon.raster != null && deviceIdentity.icon.width > 0 && deviceIdentity.icon.height > 0) {
            di = null;
            log.debug("Constructing a new Icon with new ice.Image data");
        }

        if (null == di) {
            di = new DeviceIcon(deviceIdentity.icon);
            realIcon = new SoftReference<DeviceIcon>(di);
        }

        return di;
    }

    public String getMakeAndModel() {
        if(null == deviceIdentity) {
            return null;
        }
        if (null==deviceIdentity.manufacturer||deviceIdentity.manufacturer.equals(deviceIdentity.model)||"".equals(deviceIdentity.manufacturer)) {
            return deviceIdentity.model;
        } else {
            return deviceIdentity.manufacturer + " " + deviceIdentity.model;
        }
    }

    public String getShortUDI() {
        return null == udi ? null : udi.substring(0, SHORT_UDI_LENGTH);
    }

    public DeviceIdentity getDeviceIdentity() {
        return deviceIdentity;
    }

    public ParticipantBuiltinTopicData getParticipantData() {
        return participantData;
    }

    public DeviceConnectivity getDeviceConnectivity() {
        return deviceConnectivity;
    }

    public String getUDI() {
        return udi;
    }

    public void setDeviceIdentity(DeviceIdentity deviceIdentity) {
        this.realIcon = null;
        if (null == deviceIdentity) {
            this.deviceIdentity = null;
        } else {
            changeUdi(deviceIdentity.unique_device_identifier);
            if (null == this.deviceIdentity) {
                log.debug("see first deviceIdentity sample for udi="+deviceIdentity.unique_device_identifier);
                this.deviceIdentity = new DeviceIdentity(deviceIdentity);
            } else {
                this.deviceIdentity.copy_from(deviceIdentity);
            }
        }
    }

    private static final String udiFromParticipantData(ParticipantBuiltinTopicData participantData) {
        byte[] arrbyte = new byte[participantData.user_data.value.size()];
        participantData.user_data.value.toArrayByte(arrbyte);
        try {
            return new String(arrbyte, "ASCII");
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
    
    private void changeUdi(String udi) {
        if(null != udi) {
            if(this.udi == null) {
                this.udi = udi;
            } else {
                if(!udi.equals(this.udi)) {
                    throw new IllegalArgumentException("UDI currently " + this.udi + " not changing to " + udi + " found in user QoS");
                }
            }
        }
    }
    
    public void setParticipantData(ParticipantBuiltinTopicData participantData) {
        if (null != participantData) {
            this.participantData.copy_from(participantData);
            if (!participantData.user_data.value.isEmpty()) {
                changeUdi(udiFromParticipantData(participantData));
            }
        }
    }

    public void setDeviceConnectivity(DeviceConnectivity deviceConnectivity) {
        if (null == deviceConnectivity) {
            this.deviceConnectivity = null;
        } else {
            changeUdi(deviceConnectivity.unique_device_identifier);
            if (null == this.deviceConnectivity) {
                this.deviceConnectivity = new DeviceConnectivity(deviceConnectivity);
            } else {
                this.deviceConnectivity.copy_from(deviceConnectivity);
            }
        }
    }
}
