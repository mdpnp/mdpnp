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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.builtin.ParticipantBuiltinTopicData;
import com.rti.dds.infrastructure.Duration_t;
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
    private static Image unknownImage = new Image(Device.class.getResourceAsStream("unknown.png"));
    private String udi;
    private DeviceIdentity deviceIdentity;
    private DeviceConnectivity deviceConnectivity;
    private ParticipantBuiltinTopicData participantData;
    private final Duration_t clockDifference = new Duration_t(Duration_t.DURATION_INFINITE), roundtripLatency = new Duration_t(Duration_t.DURATION_INFINITE);

    public final static int SHORT_UDI_LENGTH = 20;

    private final static Logger log = LoggerFactory.getLogger(Device.class);

    private ObjectProperty<Image> image;
    public ObjectProperty<Image> imageProperty() {
        if(null == image) {
            image = new SimpleObjectProperty<Image>(this, "image", unknownImage);
        }
        return image;
    }
    public Image getImage() {
        return image.get();
    }
    public void setImage(Image image) {
        this.image.set(image);
    }
    
    private StringProperty makeAndModel;
    public StringProperty makeAndModelProperty() {
        if(null == makeAndModel) {
            makeAndModel = new SimpleStringProperty(this, "makeAndModel", "Unknown Device");
        }
        return makeAndModel;
    }
    public String getMakeAndModel() {
        return makeAndModelProperty().get();
    }
    public void setMakeAndModel(String makeAndModel) {
        makeAndModelProperty().set(makeAndModel);
    }
    
    private BooleanProperty connected;
    public BooleanProperty connectedProperty() {
        if(null == connected) {
            connected = new SimpleBooleanProperty(this, "connected");
        }
        return connected;
    }
    public boolean getConnected() {
        return connectedProperty().get();
    }
    public void setConnected(boolean connected) {
        connectedProperty().set(connected);
    }
    
    public Device() {
    }

    public Device(String udi) {
        this.udi = udi;
    }

    public String getShortUDI() {
        return null == udi ? null : udi.substring(0, SHORT_UDI_LENGTH);
    }

    public DeviceIdentity getDeviceIdentity() {
        return deviceIdentity;
    }

    public DeviceConnectivity getDeviceConnectivity() {
        return deviceConnectivity;
    }

    public String getUDI() {
        return udi;
    }

    public void setDeviceIdentity(final DeviceIdentity deviceIdentity, ParticipantBuiltinTopicData participantData) {
        System.out.println(deviceIdentity);
        if (null == deviceIdentity) {
            this.deviceIdentity = null;
//            Platform.runLater(new Runnable() {
//                public void run() {
//                    imageProperty().set(null);
//                    makeAndModelProperty().set("");
//                }
//            });
            

        } else {
            changeUdi(deviceIdentity.unique_device_identifier);
            if (null == this.deviceIdentity) {
                log.debug("see first deviceIdentity sample for udi="+deviceIdentity.unique_device_identifier);
                this.deviceIdentity = new DeviceIdentity(deviceIdentity);
            } else {
                this.deviceIdentity.copy_from(deviceIdentity);
            }
            Task<Image> task = new Task<Image>() {
                @Override
                protected Image call() throws Exception {
                    InputStream is = new ByteArrayInputStream(deviceIdentity.icon.image.userData.toArrayByte(new byte[deviceIdentity.icon.image.userData.size()]));
                    final Image image = new Image(is);
                    System.out.println("Image Loaded " + image);
                    return image;
                }
            };
            imageProperty().bind(task.valueProperty());
            new Thread(task).start();
            
            
//            Platform.runLater(new Runnable() {
//                public void run() {
                    if (null==deviceIdentity.manufacturer||deviceIdentity.manufacturer.equals(deviceIdentity.model)||"".equals(deviceIdentity.manufacturer)) {
                        makeAndModelProperty().set(deviceIdentity.model);
                    } else {
                        makeAndModelProperty().set(deviceIdentity.manufacturer + " " + deviceIdentity.model);
                    }
        
//                    imageProperty().set(image);
//                }
//            });
            
            if(null == this.participantData) {
                this.participantData = new ParticipantBuiltinTopicData();
                this.participantData.copy_from(participantData);
            } else {
                this.participantData.copy_from(participantData);
            }
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
    
    public String getHostname() {
        return null == participantData ? null : getHostname(participantData);
    }

    public void setClockDifference(Duration_t clockDifference) {
        this.clockDifference.copy_from(clockDifference);
    }
    
    public void setRoundtripLatency(Duration_t roundtripLatency) {
        this.roundtripLatency.copy_from(roundtripLatency);
    }
    
    public Duration_t getClockDifference() {
        return clockDifference;
    }
    
    public Duration_t getRoundtripLatency() {
        return roundtripLatency;
    }
    
    public double getClockDifferenceMs() {
        return 1000.0 * clockDifference.sec + clockDifference.nanosec / 1000000.0;
    }
    
    public double getRoundtripLatencyMs() {
        return 1000.0 * roundtripLatency.sec + roundtripLatency.nanosec / 1000000.0;
    }    
    
    public void setDeviceConnectivity(DeviceConnectivity deviceConnectivity) {
        if (null == deviceConnectivity) {
            this.deviceConnectivity = null;
            connectedProperty().set(false);
        } else {
            changeUdi(deviceConnectivity.unique_device_identifier);
            if (null == this.deviceConnectivity) {
                this.deviceConnectivity = new DeviceConnectivity(deviceConnectivity);
            } else {
                this.deviceConnectivity.copy_from(deviceConnectivity);
            }
//            Platform.runLater(new Runnable() {
//                public void run() {
                    connectedProperty().set(ice.ConnectionState.Connected.equals(Device.this.deviceConnectivity.state));
//                }
//            });
        }
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
                                .getByAddress(new byte[]{locator.address[12], locator.address[13], locator.address[14], locator.address[15]});
                        break;
                    case Locator_t.KIND_UDPv6:
                    default:
                        addr = InetAddress.getByAddress(locator.address);
                        break;
                }
                sb.append(addr.getHostAddress()).append(" ");
            } catch (UnknownHostException e) {
                e.printStackTrace();
                //                log.error("getting locator address", e);
            }
        }
        return sb.toString();
    }
    @Override
    public String toString() {
        Image img = imageProperty().get();
        if(null != img) {
            return udi + " " + makeAndModelProperty().get() + " height=" + img.getHeight() + " width="+img.getWidth();
        } else {
            return udi + " " + makeAndModelProperty().get();
        }
    }

}
