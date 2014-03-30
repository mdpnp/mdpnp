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
package org.mdpnp.devices;

import static org.mdpnp.devices.TopicUtil.lookupOrCreateTopic;
import ice.DeviceConnectivityDataReader;
import ice.DeviceConnectivitySeq;
import ice.DeviceConnectivityTypeSupport;
import ice.DeviceIdentityDataReader;
import ice.DeviceIdentitySeq;
import ice.NumericDataReader;
import ice.NumericSeq;
import ice.NumericTypeSupport;
import ice.SampleArrayDataReader;
import ice.SampleArraySeq;
import ice.SampleArrayTypeSupport;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.DurabilityQosPolicyKind;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ReliabilityQosPolicyKind;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderQos;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.ReadCondition;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.ContentFilteredTopic;
import com.rti.dds.topic.Topic;
import com.rti.dds.topic.TopicDescription;

/**
 * @author Jeff Plourde
 *
 */
public class DeviceMonitor {
    private DomainParticipant participant;
    private final Set<Condition> conditions = new HashSet<Condition>();
    private final Set<DataReader> dataReaders = new HashSet<DataReader>();
    private final Set<ContentFilteredTopic> filteredTopics = new HashSet<ContentFilteredTopic>();
    private EventLoop eventLoop;
    private static final Logger log = LoggerFactory.getLogger(DeviceMonitor.class);

    private final String udi;

    private final List<DeviceMonitorListener> listeners = new ArrayList<DeviceMonitorListener>();
    private ThreadLocal<DeviceMonitorListener[]> simpleListeners = new ThreadLocal<DeviceMonitorListener[]>() {
        protected DeviceMonitorListener[] initialValue() {
            return new DeviceMonitorListener[0];
        }
    };

    private final synchronized DeviceMonitorListener[] getListeners() {
        DeviceMonitorListener[] listeners;
        if (null != this.simpleListeners) {
            simpleListeners.set(listeners = this.listeners.toArray(simpleListeners.get()));
        } else {
            listeners = new DeviceMonitorListener[0];
        }
        return listeners;
    }

    public final synchronized void addListener(DeviceMonitorListener listener) {
        this.listeners.add(listener);
    }

    public final synchronized void removeListener(DeviceMonitorListener listener) {
        this.listeners.remove(listener);
    }

    private final Condition c(Condition c) {
        conditions.add(c);
        return c;
    }

    public String getUniqueDeviceIdentifier() {
        return udi;
    }

    public DeviceMonitor(final String udi) {
        this.udi = udi;
    }

    private DeviceIdentityDataReader idReader;
    private DeviceConnectivityDataReader connReader;
    private NumericDataReader numReader;
    private SampleArrayDataReader saReader;
    private ice.InfusionStatusDataReader ipReader;
    
    public ice.DeviceIdentityDataReader getDeviceIdentityReader() {
        return idReader;
    }
    
    public ice.DeviceConnectivityDataReader getDeviceConnectivityReader() {
        return connReader;
    }
    
    public ice.NumericDataReader getNumericReader() {
        return numReader;
    }
    
    public ice.SampleArrayDataReader getSampleArrayReader() {
        return saReader;
    }
    
    public ice.InfusionStatusDataReader getInfusionStatusReader() {
        return ipReader;
    }
    
    public void start(final DomainParticipant participant, final EventLoop eventLoop) {
        this.eventLoop = eventLoop;
        eventLoop.doLater(new Runnable() {
            public void run() {
                _start(participant, eventLoop);
            }
        });
    }

    protected void _start(final DomainParticipant participant, final EventLoop eventLoop) {
        this.participant = participant;
        TopicDescription deviceIdentityTopic = lookupOrCreateTopic(participant, ice.DeviceIdentityTopic.VALUE, ice.DeviceIdentityTypeSupport.class);
        TopicDescription deviceConnectivityTopic = lookupOrCreateTopic(participant, ice.DeviceConnectivityTopic.VALUE,
                DeviceConnectivityTypeSupport.class);
        TopicDescription deviceNumericTopic = lookupOrCreateTopic(participant, ice.NumericTopic.VALUE, NumericTypeSupport.class);
        TopicDescription deviceSampleArrayTopic = lookupOrCreateTopic(participant, ice.SampleArrayTopic.VALUE, SampleArrayTypeSupport.class);
        TopicDescription deviceInfusionStatusTopic = lookupOrCreateTopic(participant, ice.InfusionStatusTopic.VALUE,
                ice.InfusionStatusTypeSupport.class);
        
        final StringSeq identity = new StringSeq();
        identity.add("'" + udi + "'");
        final String identity_exp = "unique_device_identifier = %0";
        
        ContentFilteredTopic fDeviceIdentityTopic = participant.create_contentfilteredtopic("DeviceMonitorFilteredDeviceIdentity", (Topic) deviceIdentityTopic, identity_exp, identity);
        ContentFilteredTopic fDeviceConnectivityTopic = participant.create_contentfilteredtopic("DeviceMonitorFilteredDeviceConnectivity", (Topic) deviceConnectivityTopic, identity_exp, identity);
        ContentFilteredTopic fDeviceNumericTopic = participant.create_contentfilteredtopic("DeviceMonitorFilteredNumeric", (Topic) deviceNumericTopic, identity_exp, identity);
        ContentFilteredTopic fDeviceSampleArrayTopic = participant.create_contentfilteredtopic("DeviceMonitorFilteredSampleArray", (Topic) deviceSampleArrayTopic, identity_exp, identity);
        ContentFilteredTopic fDeviceInfusionStatusTopic = participant.create_contentfilteredtopic("DeviceMonitorFilteredInfusionStatus", (Topic) deviceInfusionStatusTopic, identity_exp, identity);
        
        filteredTopics.add(fDeviceIdentityTopic);
        filteredTopics.add(fDeviceConnectivityTopic);
        filteredTopics.add(fDeviceNumericTopic);
        filteredTopics.add(fDeviceSampleArrayTopic);
        filteredTopics.add(fDeviceInfusionStatusTopic);
        
        idReader = (DeviceIdentityDataReader) participant.create_datareader_with_profile(fDeviceIdentityTopic,
                QosProfiles.ice_library, QosProfiles.invariant_state, null, StatusKind.STATUS_MASK_NONE);
        connReader = (DeviceConnectivityDataReader) participant.create_datareader_with_profile(
                fDeviceConnectivityTopic, QosProfiles.ice_library, QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);
        numReader = (NumericDataReader) participant.create_datareader_with_profile(fDeviceNumericTopic,
                QosProfiles.ice_library, QosProfiles.numeric_data, null, StatusKind.STATUS_MASK_NONE);
        DataReaderQos drQos = new DataReaderQos();
        DomainParticipantFactory.get_instance().get_datareader_qos_from_profile(drQos, QosProfiles.ice_library, QosProfiles.waveform_data);
        drQos.durability.kind = DurabilityQosPolicyKind.TRANSIENT_LOCAL_DURABILITY_QOS;
        drQos.reliability.kind = ReliabilityQosPolicyKind.RELIABLE_RELIABILITY_QOS;
        saReader = (SampleArrayDataReader) participant.create_datareader(fDeviceSampleArrayTopic,
                drQos, null, StatusKind.STATUS_MASK_NONE);
        ipReader = (ice.InfusionStatusDataReader) participant.create_datareader_with_profile(
                fDeviceInfusionStatusTopic, QosProfiles.ice_library, QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);

        dataReaders.add(idReader);
        dataReaders.add(connReader);
        dataReaders.add(numReader);
        dataReaders.add(saReader);
        dataReaders.add(ipReader);

        

        final DeviceIdentitySeq id_seq = new DeviceIdentitySeq();
        final DeviceConnectivitySeq conn_seq = new DeviceConnectivitySeq();
        final NumericSeq num_seq = new NumericSeq();
        final SampleArraySeq sa_seq = new SampleArraySeq();
        final SampleInfoSeq info_seq = new SampleInfoSeq();
        final ice.InfusionStatusSeq inf_seq = new ice.InfusionStatusSeq();

        eventLoop.addHandler(c(idReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE,
                InstanceStateKind.ALIVE_INSTANCE_STATE)), new EventLoop.ConditionHandler() {
            @Override
            public void conditionChanged(Condition condition) {
                try {
                    for (;;) {
                        try {
                            idReader.read_w_condition(id_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (ReadCondition) condition);
                            for (DeviceMonitorListener listener : getListeners()) {
                                listener.deviceIdentity(idReader, id_seq, info_seq);
                            }
                        } finally {
                            idReader.return_loan(id_seq, info_seq);
                        }
                    }
                } catch (RETCODE_NO_DATA noData) {

                } finally {

                }
            }
        });

        eventLoop.addHandler(c(connReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE,
                InstanceStateKind.ALIVE_INSTANCE_STATE)), new EventLoop.ConditionHandler() {

            @Override
            public void conditionChanged(Condition condition) {
                try {
                    for (;;) {
                        try {
                            connReader.read_w_condition(conn_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (ReadCondition) condition);
                            for (DeviceMonitorListener listener : getListeners()) {
                                listener.deviceConnectivity(connReader, conn_seq, info_seq);
                            }
                        } finally {
                            connReader.return_loan(conn_seq, info_seq);
                        }
                    }

                } catch (RETCODE_NO_DATA noData) {

                } finally {

                }
            }
        });

        eventLoop.addHandler(c(numReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE,
                InstanceStateKind.ALIVE_INSTANCE_STATE)), new EventLoop.ConditionHandler() {

            @Override
            public void conditionChanged(Condition condition) {
                try {
                    for (;;) {
                        try {
                            numReader.read_w_condition(num_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (ReadCondition) condition);
                            for (DeviceMonitorListener listener : getListeners()) {
                                listener.numeric(numReader, num_seq, info_seq);
                            }
                        } finally {
                            numReader.return_loan(num_seq, info_seq);
                        }
                    }
                } catch (RETCODE_NO_DATA noData) {

                } finally {

                }
            }
        });

        eventLoop.addHandler(c(saReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE,
                InstanceStateKind.ALIVE_INSTANCE_STATE)), new EventLoop.ConditionHandler() {

            @Override
            public void conditionChanged(Condition condition) {
                try {
                    for (;;) {
                        try {
                            saReader.read_w_condition(sa_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (ReadCondition) condition);
                            for (DeviceMonitorListener listener : getListeners()) {
                                listener.sampleArray(saReader, sa_seq, info_seq);
                            }
                        } finally {
                            saReader.return_loan(sa_seq, info_seq);
                        }
                    }
                } catch (RETCODE_NO_DATA noData) {

                } finally {

                }
            }
        });

        eventLoop.addHandler(c(ipReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE,
                InstanceStateKind.ALIVE_INSTANCE_STATE)), new EventLoop.ConditionHandler() {

            @Override
            public void conditionChanged(Condition condition) {
                for (;;) {
                    try {
                        ipReader.read_w_condition(inf_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, (ReadCondition) condition);
                        for (DeviceMonitorListener listener : getListeners()) {
                            listener.infusionPump(ipReader, inf_seq, info_seq);
                        }
                    } catch (RETCODE_NO_DATA noData) {
                        break;
                    } finally {
                        ipReader.return_loan(inf_seq, info_seq);
                    }
                }
            }

        });
    }

    public void stop() {
        EventLoop eventLoop = this.eventLoop;
        if (eventLoop != null) {
            eventLoop.doLater(new Runnable() {
                public void run() {
                    _stop();
                }
            });
        }
    }

    protected void _stop() {
        synchronized (this) {
            listeners.clear();
            simpleListeners = null;
        }
        for (Condition c : conditions) {
            eventLoop.removeHandler(c);
            if (c instanceof ReadCondition) {
                ((ReadCondition) c).get_datareader().delete_readcondition((ReadCondition) c);
            }
        }
        conditions.clear();
        for (DataReader r : dataReaders) {
            participant.delete_datareader(r);
        }
        dataReaders.clear();
        for(ContentFilteredTopic cft : filteredTopics) {
            participant.delete_contentfilteredtopic(cft);
        }
        filteredTopics.clear();
        
        
        idReader = null;
        ipReader = null;
        numReader = null;
        saReader = null;
        connReader = null;

        log.debug("Shut down a DeviceMonitor");
        this.eventLoop = null;
    }
}
