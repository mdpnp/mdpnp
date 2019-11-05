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

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.scene.image.Image;

import org.mdpnp.apps.testapp.DeviceAdapterCommand.HeadlessAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public final static int SHORT_UDI_LENGTH = 20;

    @SuppressWarnings("unused")
    private final static Logger log = LoggerFactory.getLogger(Device.class);

    private ObjectProperty<Image> image;

    public ObjectProperty<Image> imageProperty() {
        if (null == image) {
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

    
    private StringProperty manufacturer;
    
    public StringProperty manufacturerProperty() {
        if(null == manufacturer) {
            manufacturer = new SimpleStringProperty(this, "manufacturer", "");
        }
        return this.manufacturer;
    }

    public java.lang.String getManufacturer() {
        return this.manufacturerProperty().get();
    }

    public void setManufacturer(final java.lang.String manufacturer) {
        this.manufacturerProperty().set(manufacturer);
    }
    
    private StringProperty model;

    public StringProperty modelProperty() {
        if(null == model) {
            model = new SimpleStringProperty(this, "model", "");
        }
        return this.model;
    }

    public java.lang.String getModel() {
        return this.modelProperty().get();
    }

    public void setModel(final java.lang.String model) {
        this.modelProperty().set(model);
    }

    
    
    
    private StringProperty makeAndModel;

    public StringProperty makeAndModelProperty() {
        if (null == makeAndModel) {
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
        if (null == connected) {
            connected = new SimpleBooleanProperty(this, "connected", true);
        }
        return connected;
    }

    public boolean getConnected() {
        return connectedProperty().get();
    }

    public void setConnected(boolean connected) {
        connectedProperty().set(connected);
    }

    private StringProperty hostname;

    public StringProperty hostnameProperty() {
        if (null == hostname) {
            hostname = new SimpleStringProperty(this, "hostname", "");
        }
        return hostname;
    }

    public String getHostname() {
        return hostnameProperty().get();
    }

    public void setHostname(String hostname) {
        this.hostnameProperty().set(hostname);
    }
    
    private StringProperty comPort;
    
    public StringProperty comPortProperty() {
    	if (null == comPort) {
    		comPort=new SimpleStringProperty(this, "comPort", "");
    	}
    	return comPort;
    }
    
    public String getComPort() {
    	return comPortProperty().get();
    }
    
    public void setComPort(String comPort) {
    	this.comPortProperty().set(comPort);
    }


    private LongProperty clockDifference;

    public LongProperty clockDifferenceProperty() {
        if (null == clockDifference) {
            clockDifference = new SimpleLongProperty(this, "clockDifference", 0L);
        }
        return clockDifference;
    }

    public long getClockDifference() {
        return clockDifferenceProperty().get();
    }

    public void setClockDifference(long clockDifference) {
        clockDifferenceProperty().set(clockDifference);
    }

    private LongProperty roundtripLatency;

    public LongProperty roundtripLatencyProperty() {
        if (null == roundtripLatency) {
            roundtripLatency = new SimpleLongProperty(this, "roundtripLatency", 0L);
        }
        return roundtripLatency;
    }

    public long getRoundtripLatency() {
        return roundtripLatencyProperty().get();
    }

    public void setRoundtripLatency(long roundtripLatency) {
        roundtripLatencyProperty().set(roundtripLatency);
    }

    private StringProperty operating_system = new SimpleStringProperty(this, "operating_system", "");
    private StringProperty build = new SimpleStringProperty(this, "build", "");
    private StringProperty serial_number = new SimpleStringProperty(this, "serial_number", "");

    public Device(String udi) {
        this.udi = udi;
        makeAndModelProperty().set(udi);
    }

    public String getShortUDI() {
        return null == udi ? null : udi.substring(0, SHORT_UDI_LENGTH);
    }

    public String getUDI() {
        return udi;
    }

    public void setDeviceIdentity(final DeviceIdentity deviceIdentity, final String hostName) {

        if(null != hostName) {
            hostnameProperty().set(hostName);
        }

        if (null != deviceIdentity) {
            changeUdi(deviceIdentity.unique_device_identifier);
            manufacturerProperty().set(deviceIdentity.manufacturer);
            modelProperty().set(deviceIdentity.model);

            if (null == deviceIdentity.manufacturer || deviceIdentity.manufacturer.equals(deviceIdentity.model)
                    || "".equals(deviceIdentity.manufacturer)) {
                makeAndModelProperty().set(deviceIdentity.model);
            } else {
                makeAndModelProperty().set(deviceIdentity.manufacturer + " " + deviceIdentity.model);
            }

            operating_systemProperty().set(deviceIdentity.operating_system);
            buildProperty().set(deviceIdentity.build);
            serial_numberProperty().set(deviceIdentity.serial_number);
            
            Task<Image> task = new Task<Image>() {
                @Override
                protected Image call() throws Exception {
                    InputStream is = new ByteArrayInputStream(
                            deviceIdentity.icon.image.userData.toArrayByte(new byte[deviceIdentity.icon.image.userData.size()]));
                    final Image image = new Image(is);
                    return image;
                }
            };
            imageProperty().bind(task.valueProperty());
            Thread t = new Thread(task, "Load Device Image");
            t.setDaemon(true);
            t.start();
        }
    }

    private void changeUdi(String udi) {
        if (null != udi) {
            if (this.udi == null) {
                this.udi = udi;
            } else {
                if (!udi.equals(this.udi)) {
                    throw new IllegalArgumentException("UDI currently " + this.udi + " not changing to " + udi + " found in user QoS");
                }
            }
        }
    }

    public void setDeviceConnectivity(DeviceConnectivity deviceConnectivity) {
        changeUdi(deviceConnectivity.unique_device_identifier);
        connectivityStateProperty().set(deviceConnectivity.state);
        connectivityInfoProperty().set(deviceConnectivity.info);
        comPortProperty().set(deviceConnectivity.comPort);
        connectedProperty().set(ice.ConnectionState.Connected.equals(deviceConnectivity.state));
    }

    @Override
    public String toString() {
        Image img = imageProperty().get();
        if (null != img) {
            return udi + " " + makeAndModelProperty().get() + " height=" + img.getHeight() + " width=" + img.getWidth();
        } else {
            return udi + " " + makeAndModelProperty().get();
        }
    }

    public final StringProperty operating_systemProperty() {
        return this.operating_system;
    }

    public final java.lang.String getOperating_system() {
        return this.operating_systemProperty().get();
    }

    public final void setOperating_system(final java.lang.String operating_system) {
        this.operating_systemProperty().set(operating_system);
    }

    public final StringProperty buildProperty() {
        return this.build;
    }

    public final java.lang.String getBuild() {
        return this.buildProperty().get();
    }

    public final void setBuild(final java.lang.String build) {
        this.buildProperty().set(build);
    }

    public final StringProperty serial_numberProperty() {
        return this.serial_number;
    }

    public final java.lang.String getSerial_number() {
        return this.serial_numberProperty().get();
    }

    public final void setSerial_number(final java.lang.String serial_number) {
        this.serial_numberProperty().set(serial_number);
    }
    
    @Override
    public int hashCode() {
        return udi.hashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Device) {
            return udi.equals(((Device)obj).udi);
        } else {
            return false;
        }
    }

    private ObjectProperty<ice.ConnectionState> connectivityState;
    private StringProperty connectivityInfo;

    public ObjectProperty<ice.ConnectionState> connectivityStateProperty() {
        if(null == connectivityState) {
            connectivityState = new SimpleObjectProperty<ice.ConnectionState>(this, "connectivityState");
        }
        return this.connectivityState;
    }

    public ice.ConnectionState getConnectivityState() {
        return this.connectivityStateProperty().get();
    }

    public void setConnectivityState(final ice.ConnectionState connectivityState) {
        this.connectivityStateProperty().set(connectivityState);
    }

    public StringProperty connectivityInfoProperty() {
        if(null == connectivityInfo) {
            connectivityInfo = new SimpleStringProperty(this, "connectivityInfo", "");
        }
        return this.connectivityInfo;
    }

    public java.lang.String getConnectivityInfo() {
        return this.connectivityInfoProperty().get();
    }

    public void setConnectivityInfo(final java.lang.String connectivityInfo) {
        this.connectivityInfoProperty().set(connectivityInfo);
    }
    
    private ObjectProperty<HeadlessAdapter> headlessAdapter;
    
    public ObjectProperty<HeadlessAdapter> headlessAdapterProperty() {
        if(null == headlessAdapter) {
            headlessAdapter = new SimpleObjectProperty<>(this, "headlessAdapter", null);
        }
        return this.headlessAdapter;
    }
    
    public HeadlessAdapter getHeadlessAdapter() {
        return this.headlessAdapterProperty().get();
    }
    
    public void setHeadlessAdapter(HeadlessAdapter headlessAdapter) {
        this.headlessAdapterProperty().set(headlessAdapter);
    }
}
