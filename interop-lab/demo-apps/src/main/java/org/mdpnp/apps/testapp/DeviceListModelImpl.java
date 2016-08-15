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

import java.util.HashMap;
import java.util.Map;

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
 *
 * That said, if we never get DeviceIdentity message, there is not much we can do with the
 * broken/half filled device object so we might as well not know about it. So, if we cannot
 * look up the device, ALL important messages that are not heart-beats are kept around until (and
 * if) we get a real identity update. Once that happens, the pending data is applied to the device
 * object. The heart-beats for unresolved devices are dropped as they will come again at some
 * point in the future. We loose a bit on memory footprint, but the external interactions are much
 * more strait-forward.
 *
 * @author Jeff Plourde, Mike Feinberg
 *
 */
public class DeviceListModelImpl implements TimeManagerListener, DeviceListModel {

    /**
     * @param udi
     * @return device stub - will never be null, but is not guaranteed to be properly populated.
     */
    @Override
    public Device getByUniqueDeviceIdentifier(String udi) {
        Device device = findDevice(udi);
        if(device==null) {
            device = new Device(udi);
            pendingContents.put(udi, device);
        }
        return device;
    }

    @Override
    public ObservableList<Device> getContents() {
        return contents;
    }

    protected void notADevice(String unique_device_identifier, boolean alive) {
        
    }
    
    @Override
    public void aliveHeartbeat(final String unique_device_identifier, final String type, final String host_name) {
        if("Device".equals(type)) {
            runLaterOnPlatform(new Runnable() {
                public void run() {
                    createOrUpdateDevice(unique_device_identifier, null, host_name);
                }
            });
      } else {
          notADevice(unique_device_identifier, true);
      }

    }
    
    @Override
    public void notAliveHeartbeat(final String unique_device_identifier, final String type) {
        if("Device".equals(type)) {
            log.debug(unique_device_identifier + " IS NO LONGER ALIVE");
            runLaterOnPlatform(new Runnable() {
                public void run() {
                    Device d = findDevice(unique_device_identifier);
                    if(d != null)
                        deactivateDevice(d);
                }
            });
            
        } else {
            notADevice(unique_device_identifier, false);
        }

    }
    
    @Override
    public void synchronization(String remote_udi, Duration_t latency, Duration_t clockDifference) {
        final long clockDifferenceMs = 1000L * clockDifference.sec + clockDifference.nanosec / 1000000L;
        final long roundtripLatencyMs = 1000L * latency.sec + latency.nanosec / 1000000L;
        runLaterOnPlatform(new Runnable() {
            public void run() {
                Device device = findDevice(remote_udi);
                if(null != device) {
                    device.setClockDifference(clockDifferenceMs);
                    device.setRoundtripLatency(roundtripLatencyMs);
                }
                else
                    pendingSynchronization.put(remote_udi, new SynchronizationData(clockDifferenceMs, roundtripLatencyMs));
            }
        });

    }

    private static class SynchronizationData {
        final long clockDifference;
        final long roundtripLatency;

        public SynchronizationData(long clockDifference, long roundtripLatency) {
            this.clockDifference = clockDifference;
            this.roundtripLatency = roundtripLatency;
        }
    }

    protected void update(final DeviceConnectivity deviceConnectivity) {

        assertEventLoopThread();

        final ice.DeviceConnectivity dc = new ice.DeviceConnectivity(deviceConnectivity);
        runLaterOnPlatform(new Runnable() {
            public void run() {
                Device device = findDevice(dc.unique_device_identifier);
                if(device != null)
                    device.setDeviceConnectivity(dc);
                else
                    pendingDeviceConnectivity.put(dc.unique_device_identifier, dc);
            }
        });
    }

    private void update(final DeviceIdentity di, final ParticipantBuiltinTopicData data) {

        assertEventLoopThread();

        final ice.DeviceIdentity identity = new ice.DeviceIdentity(di);
        final String hostname = TimeManager.getHostname(data);

        runLaterOnPlatform(new Runnable() {
            public void run() {
                createOrUpdateDevice(identity.unique_device_identifier, identity, hostname);
            }
        });
    }

    private void deactivateDevice(final Device device) {

        assertPlatformThread();

        if(null == device) {
            log.debug("Tried to remove a null device");
            return;
        }
        
        contents.remove(device);
        recycledContents.put(device.getUDI(), device);
    }

    final Device findDevice(final String udi) {

        assertPlatformThread();

        if(null == udi) {
            throw new IllegalArgumentException("Missing devive id");
        }

        for(Device d : contents) {
            if(udi.equals(d.getUDI())) {
                return d;
            }
        }

        Device d = recycledContents.get(udi);
        return d;
    }

    private Device createOrUpdateDevice(String udi, ice.DeviceIdentity data, String hostName) {

        assertPlatformThread();

        // first look for the device on the list of active entities.
        //
        boolean isActiveContent=false;
        Device device = null;
        for(Device d : contents) {
            if(udi.equals(d.getUDI())) {
                device=d;
                isActiveContent=true;
                break;
            }
        }

        // maybe it was deactivated?
        //
        if(device==null)
            device = recycledContents.remove(udi);

        if(device==null)
            device = pendingContents.remove(udi);

        // must be a new one
        //
        if(device == null)
            device = new Device(udi);

        // now fill in all the available information about the entity
        //
        device.setDeviceIdentity(data, hostName);

        DeviceConnectivity dc = pendingDeviceConnectivity.remove(udi);
        if (dc != null) {
            device.setDeviceConnectivity(dc);
        }

        SynchronizationData sd = pendingSynchronization.remove(udi);
        if(sd != null) {
            device.setClockDifference(sd.clockDifference);
            device.setRoundtripLatency(sd.roundtripLatency);
        }

        // at the very end we can add the fully populated object to the observable list
        //
        if(!isActiveContent)
            contents.add(0, device);

        return device;
    }

    /**
     * APIs to be overridden in tests.
     */

    protected void assertPlatformThread() {
        if(!Platform.isFxApplicationThread()) {
            throw new IllegalThreadStateException("call getDevice only from the FX App Thread");
        }
    }

    protected void assertEventLoopThread() {
        if (!eventLoop.isCurrentServiceThread()) {
            throw new IllegalStateException("Not called from EventLoop service thread, instead:" + Thread.currentThread());
        }
    }

    protected void runLaterOnPlatform(Runnable r) {
        Platform.runLater(r);
    }

    private static final Logger log = LoggerFactory.getLogger(DeviceListModelImpl.class);


    private final Map<String, SynchronizationData> pendingSynchronization = new HashMap<>();
    private final Map<String, DeviceConnectivity> pendingDeviceConnectivity = new HashMap<>();

    private final Map<String, Device> pendingContents = new HashMap<>();
    private final Map<String, Device> recycledContents = new HashMap<>();

    private final ObservableList<Device> contents = FXCollections.observableArrayList(new Callback<Device, Observable[]>() {

        @Override
        public Observable[] call(Device param) {
            return new Observable[] { param.connectedProperty(), param.imageProperty(), param.makeAndModelProperty(), param.hostnameProperty() };
        }
        
    });

    private final Subscriber subscriber;
    private final EventLoop eventLoop;
    private final DeviceIdentityInstanceModel idModel;
    private final DeviceConnectivityInstanceModel connModel;
    private final TimeManager timeManager;

    @Override
    public void start() {
        timeManager.addListener(this);
        idModel.startReader(subscriber, eventLoop, QosProfiles.ice_library, QosProfiles.device_identity);
        connModel.startReader(subscriber, eventLoop, QosProfiles.ice_library, QosProfiles.state);
    }
    
    public DeviceListModelImpl(final Subscriber subscriber, final EventLoop eventLoop, final TimeManager timeManager) {
        this.eventLoop = eventLoop;
        this.subscriber = subscriber;
        this.timeManager = timeManager;

        idModel = new DeviceIdentityInstanceModelImpl(ice.DeviceIdentityTopic.VALUE);
        connModel = new DeviceConnectivityInstanceModelImpl(ice.DeviceConnectivityTopic.VALUE);
        
        idModel.addListener(idListener);
        connModel.addListener(connListener);
    }

    @Override
    public void tearDown() {
        timeManager.removeListener(this);
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
