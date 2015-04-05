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
import ice.DeviceConnectivityDataReader;
import ice.DeviceIdentity;
import ice.DeviceIdentityDataReader;
import ice.HeartBeat;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

import org.mdpnp.devices.TimeManager;
import org.mdpnp.devices.TimeManagerListener;
import org.mdpnp.rtiapi.data.DeviceConnectivityInstanceModel;
import org.mdpnp.rtiapi.data.DeviceConnectivityInstanceModelImpl;
import org.mdpnp.rtiapi.data.DeviceConnectivityInstanceModelListener;
import org.mdpnp.rtiapi.data.DeviceIdentityInstanceModel;
import org.mdpnp.rtiapi.data.DeviceIdentityInstanceModelImpl;
import org.mdpnp.rtiapi.data.DeviceIdentityInstanceModelListener;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.ReaderInstanceModel;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.builtin.ParticipantBuiltinTopicData;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.Subscriber;

/**
 * A data model tracking all active participants; joining participant info, device
 * identity, and device connectivity information.
 * 
 * Joining between these topics turns out to be complicated.  Especially because previous
 * instances of DeviceIdentity and DeviceConnectivity do not generate a new ALIVE notification
 * when connection to a remote participant is re-established.  Devices continually re-publishing
 * this information would add even more bandwidth consumption over and above participant assertion
 * @author Jeff Plourde 
 *
 */
public class DeviceListModelImpl 
    implements TimeManagerListener, DeviceListModel {
    
    @Override
    public Device getByUniqueDeviceIdentifier(String udi) {
        return getByUniqueDeviceIdentifier(udi, false);
    }
    
    private Device getByUniqueDeviceIdentifier(String udi, boolean create) {
        if(!Platform.isFxApplicationThread()) {
            throw new IllegalThreadStateException("call getDevice only from the FX App Thread");
        }
        
        if(null == udi) {
            return null;
        }
        for(Device d : contents) {
            if(udi.equals(d.getUDI())) {
                return d;
            }
        }
        for(Device d : recycledContents) {
            if(udi.equals(d.getUDI())) {
                if(create) {
                    log.debug("Resurrected " + udi);
                    contents.add(0, d);
                }
                return d;
            }
        }
        // Add an inactive placeholder
        Device d = new Device(udi);
        if(create) {
            contents.add(d);
        } else {
            recycledContents.add(d);
        }
        return d;
    }
    
    @Override
    public ObservableList<Device> getContents() {
        return contents;
    }
    
    private final Device getDevice(final String udi, final boolean create) {
        if(!Platform.isFxApplicationThread()) {
            throw new IllegalThreadStateException("call getDevice only from the FX App Thread");
        }
        if(null == udi) {
            log.warn("Cannot create device with null udi");
            return null;
        }
        return getByUniqueDeviceIdentifier(udi, create);
    }
    
    protected void notADevice(ice.HeartBeat heartbeat, boolean alive) {
        
    }
    
    @Override
    public void aliveHeartbeat(SampleInfo sampleInfo, HeartBeat heartbeat, final String host_name) {
        if("Device".equals(heartbeat.type)) {
            final String udi = heartbeat.unique_device_identifier;
            Platform.runLater(new Runnable() {
                public void run() {
                      getDevice(udi, true).setHostname(host_name);
                }
            });
      } else {
          notADevice(heartbeat, true);
      }

    }
    
    @Override
    public void notAliveHeartbeat(SampleInfo sampleInfo, HeartBeat heartbeat) {
        if("Device".equals(heartbeat.type)) {
            log.debug(heartbeat.unique_device_identifier + " IS NO LONGER ALIVE");
            String udi = heartbeat.unique_device_identifier;
            Platform.runLater(new Runnable() {
                public void run() {
                    remove(getDevice(udi, false));
                }
            });
            
        } else {
            notADevice(heartbeat, false);
        }

    }
    
    @Override
    public void synchronization(String remote_udi, Duration_t latency, Duration_t clockDifference) {
//        log.trace(remote_udi + " has latency="+latency+" and clockDifference="+clockDifference);
        final long clockDifference1 = 1000L * clockDifference.sec + clockDifference.nanosec / 1000000L;
        final long roundtripLatency1 = 1000L * latency.sec + latency.nanosec / 1000000L;
        Platform.runLater(new Runnable() {
            public void run() {
                Device device = getDevice(remote_udi, false);
                if(null != device) {
                    device.setClockDifference(clockDifference1);
                    device.setRoundtripLatency(roundtripLatency1);
                }
            }
        });

    }
    
    private final void update(final DeviceConnectivity dc) {
        if (!eventLoop.isCurrentServiceThread()) {
            throw new IllegalStateException("Not called from EventLoop service thread, instead:" + Thread.currentThread());
        }
        final ice.DeviceConnectivity dc1 = new ice.DeviceConnectivity(dc);
        Platform.runLater(new Runnable() {
            public void run() {
                Device device = getDevice(dc1.unique_device_identifier, true);
                device.setDeviceConnectivity(dc1);
            }
        });
    }

    private final void update(final DeviceIdentity di, final ParticipantBuiltinTopicData data) {
        
        if (!eventLoop.isCurrentServiceThread()) {
            throw new IllegalStateException("Not called from EventLoop service thread, instead:" + Thread.currentThread());
        }
        final ice.DeviceIdentity di1 = new ice.DeviceIdentity(di);
        final ParticipantBuiltinTopicData data1 = new ParticipantBuiltinTopicData();
        data1.copy_from(data);
        
        Platform.runLater(new Runnable() {
            public void run() {
                Device device = getDevice(di1.unique_device_identifier, true); 
                device.setDeviceIdentity(di1, data1);
            }
        });
    }

    
    
    private final void remove(final Device device) {
        if(!Platform.isFxApplicationThread()) {
            throw new IllegalThreadStateException("call getDevice only from the FX App Thread");
        }
        
        if(null == device) {
            log.debug("Tried to remove a null device");
            return;
        }
        
        contents.remove(device);
        recycledContents.add(device);
    }
    
    private static final Logger log = LoggerFactory.getLogger(DeviceListModelImpl.class);

    protected final List<Device> recycledContents = new ArrayList<Device>();
    protected final ObservableList<Device> contents = FXCollections.observableArrayList(new Callback<Device, Observable[]>() {

        @Override
        public Observable[] call(Device param) {
            return new Observable[] { param.connectedProperty(), param.imageProperty(), param.makeAndModelProperty(), param.hostnameProperty() };
        }
        
    });

    private final Subscriber subscriber;
    private final EventLoop eventLoop;
    protected DeviceIdentityInstanceModel idModel;
    protected DeviceConnectivityInstanceModel connModel;
    protected final TimeManager timeManager;

    @Override
    public void start() {
        idModel.startReader(subscriber, eventLoop, QosProfiles.ice_library, QosProfiles.device_identity);
        connModel.startReader(subscriber, eventLoop, QosProfiles.ice_library, QosProfiles.state);
    }
    
    public DeviceListModelImpl(final Subscriber subscriber, final EventLoop eventLoop, final TimeManager timeManager) {
        this.eventLoop = eventLoop;
        this.subscriber = subscriber;
        this.timeManager = timeManager;
        timeManager.addListener(this);
        idModel = new DeviceIdentityInstanceModelImpl(ice.DeviceIdentityTopic.VALUE);
        connModel = new DeviceConnectivityInstanceModelImpl(ice.DeviceConnectivityTopic.VALUE);
        
        idModel.addListener(idListener);
        connModel.addListener(connListener);
    }

    @Override
    public void tearDown() {
        idModel.stopReader();
        connModel.stopReader();
    }
    
    private DeviceIdentityInstanceModelListener idListener = new DeviceIdentityInstanceModelListener() {
        
        @Override
        public void instanceSample(ReaderInstanceModel<DeviceIdentity, DeviceIdentityDataReader> model, DeviceIdentityDataReader reader, DeviceIdentity di,
                SampleInfo sampleInfo) {
            if (sampleInfo.valid_data) {
                ParticipantBuiltinTopicData data = null;
                try {
                    data = new ParticipantBuiltinTopicData();
                    reader.get_matched_publication_participant_data(data, sampleInfo.publication_handle);
                } catch(Exception e) {
                    log.warn("Unable to get participant information for DeviceIdentity publication");
                }
                update(di, data);
            }
            
        }
        
        @Override
        public void instanceNotAlive(ReaderInstanceModel<DeviceIdentity, DeviceIdentityDataReader> model, DeviceIdentityDataReader reader,
                DeviceIdentity keyHolder, SampleInfo sampleInfo) {
        }
        
        @Override
        public void instanceAlive(ReaderInstanceModel<DeviceIdentity, DeviceIdentityDataReader> model, DeviceIdentityDataReader reader, DeviceIdentity data,
                SampleInfo sampleInfo) {
        }
    };
    
    private DeviceConnectivityInstanceModelListener connListener = new DeviceConnectivityInstanceModelListener() {
        
        @Override
        public void instanceSample(ReaderInstanceModel<DeviceConnectivity, DeviceConnectivityDataReader> model, DeviceConnectivityDataReader reader,
                DeviceConnectivity data, SampleInfo sampleInfo) {
            if (sampleInfo.valid_data) {
                update(data);
            }
        }
        
        @Override
        public void instanceNotAlive(ReaderInstanceModel<DeviceConnectivity, DeviceConnectivityDataReader> model, DeviceConnectivityDataReader reader,
                DeviceConnectivity keyHolder, SampleInfo sampleInfo) {
            
        }
        
        @Override
        public void instanceAlive(ReaderInstanceModel<DeviceConnectivity, DeviceConnectivityDataReader> model, DeviceConnectivityDataReader reader,
                DeviceConnectivity data, SampleInfo sampleInfo) {
            
        }
    };
    

}
