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
package org.mdpnp.rtiapi.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.RETCODE_TIMEOUT;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.subscription.Subscriber;
/**
 * Tracks the data instances associated with one device.
 * 
 * @author Jeff Plourde
 *
 */
public class DeviceDataMonitor {
    private final DeviceIdentityInstanceModel idModel;
    private final DeviceConnectivityInstanceModel connModel;
    private final NumericInstanceModel numModel;
    private final SampleArrayInstanceModel saModel;
    private final InfusionStatusInstanceModel isModel;

    private static final Logger log = LoggerFactory.getLogger(DeviceDataMonitor.class);
    private final String udi; 

    public String getUniqueDeviceIdentifier() {
        return udi;
    }

    public void startAndWait(ReaderInstanceModel<?, ?> instanceModel, Subscriber subscriber, EventLoop eventLoop, String identity_exp, StringSeq identity, Duration_t waitTime, String profile) {
        try {
            instanceModel.startReader(subscriber, eventLoop, identity_exp, identity, QosProfiles.ice_library, profile);
//            instanceModel.getReader().wait_for_historical_data(waitTime);
        } catch(RETCODE_TIMEOUT timeout) {
            log.warn("Non-fatal timed out waiting " + waitTime.sec + " seconds and " + waitTime.nanosec + " nanoseconds for historical " + instanceModel.getReader().get_topicdescription().get_name());
        }
    }
    
    public void start(Subscriber subscriber, EventLoop eventLoop) {
        final StringSeq identity = new StringSeq();
        identity.add("'" + udi + "'");
        final String identity_exp = "unique_device_identifier = %0";
        
        Duration_t waitTime = new Duration_t(2,0);
        startAndWait(idModel, subscriber, eventLoop, identity_exp, identity, waitTime, QosProfiles.device_identity);
        startAndWait(connModel, subscriber, eventLoop, identity_exp, identity, waitTime, QosProfiles.state);
        startAndWait(numModel, subscriber, eventLoop, identity_exp, identity, waitTime, QosProfiles.numeric_data);
        startAndWait(saModel, subscriber, eventLoop, identity_exp, identity, waitTime, QosProfiles.waveform_data);
        startAndWait(isModel, subscriber, eventLoop, identity_exp, identity,waitTime, QosProfiles.state);
    }
    
    
    public DeviceDataMonitor(final String udi) {
        this.udi = udi;
        this.idModel = new DeviceIdentityInstanceModelImpl(ice.DeviceIdentityTopic.VALUE);
        this.connModel = new DeviceConnectivityInstanceModelImpl(ice.DeviceConnectivityTopic.VALUE);
        this.numModel = new NumericInstanceModelImpl(ice.NumericTopic.VALUE);
        this.saModel = new SampleArrayInstanceModelImpl(ice.SampleArrayTopic.VALUE);
        this.isModel = new InfusionStatusInstanceModelImpl(ice.InfusionStatusTopic.VALUE);
    }

    
    public void stop() {
        idModel.stopReader();
        connModel.stopReader();
        numModel.stopReader();
        saModel.stopReader();
        isModel.stopReader();
    }
    
    public DeviceConnectivityInstanceModel getDeviceConnectivityModel() {
        return connModel;
    }
    
    public DeviceIdentityInstanceModel getDeviceIdentityModel() {
        return idModel;
    }
    
    public NumericInstanceModel getNumericModel() {
        return numModel;
    }
    
    public SampleArrayInstanceModel getSampleArrayModel() {
        return saModel;
    }
    
    public InfusionStatusInstanceModel getInfusionStatusModel() {
        return isModel;
    }

}
