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
package org.mdpnp.devices.connected;

import ice.DeviceConnectivity;
import ice.DeviceConnectivityDataReader;
import ice.DeviceConnectivityObjective;
import ice.DeviceConnectivityObjectiveDataWriter;
import ice.DeviceConnectivityObjectiveTypeSupport;
import ice.DeviceConnectivitySeq;
import ice.DeviceConnectivityTypeSupport;

import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.QosProfiles;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.QueryCondition;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;

public abstract class AbstractGetConnected {
    private boolean closing = false;

    private final DomainParticipant participant;
    private final Subscriber subscriber;
    private final Publisher publisher;
    private final DeviceConnectivity deviceConnectivity;
    private final DeviceConnectivityObjective deviceConnectivityObjective;
    private final DeviceConnectivityDataReader deviceConnectivityReader;
    private final DeviceConnectivityObjectiveDataWriter deviceConnectivityObjectiveWriter;
    private final Topic deviceConnectivityTopic;
    private final Topic deviceConnectivityObjectiveTopic;
    private final QueryCondition qc;

    private final EventLoop eventLoop;

    public void shutdown() {
        eventLoop.removeHandler(qc);
        deviceConnectivityReader.delete_readcondition(qc);
        subscriber.delete_datareader(deviceConnectivityReader);
        participant.delete_topic(deviceConnectivityTopic);
        DeviceConnectivityTypeSupport.unregister_type(participant, DeviceConnectivityTypeSupport.get_type_name());

        publisher.delete_datawriter(deviceConnectivityObjectiveWriter);
        participant.delete_topic(deviceConnectivityObjectiveTopic);
        DeviceConnectivityObjectiveTypeSupport.unregister_type(participant, DeviceConnectivityObjectiveTypeSupport.get_type_name());
        participant.delete_publisher(publisher);
        participant.delete_subscriber(subscriber);

        participant.delete_contained_entities();
        DomainParticipantFactory.get_instance().delete_participant(participant);
    }

    public AbstractGetConnected(int domainId, String unique_device_identifier, EventLoop eventLoop) {
        this.eventLoop = eventLoop;
        DomainParticipantQos pQos = new DomainParticipantQos();
        DomainParticipantFactory.get_instance().get_default_participant_qos(pQos);
        pQos.participant_name.name = "AbstractGetConnected " + unique_device_identifier;
        participant = DomainParticipantFactory.get_instance().create_participant(domainId, pQos, null, StatusKind.STATUS_MASK_NONE);
        subscriber = participant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        publisher = participant.create_publisher(DomainParticipant.PUBLISHER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        deviceConnectivity = (DeviceConnectivity) DeviceConnectivity.create();
        deviceConnectivityObjective = (DeviceConnectivityObjective) DeviceConnectivityObjective.create();
        deviceConnectivityObjective.unique_device_identifier = unique_device_identifier;
        DeviceConnectivityObjectiveTypeSupport.register_type(participant, DeviceConnectivityObjectiveTypeSupport.get_type_name());
        deviceConnectivityObjectiveTopic = participant.create_topic(ice.DeviceConnectivityObjectiveTopic.VALUE,
                ice.DeviceConnectivityObjectiveTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        deviceConnectivityObjectiveWriter = (DeviceConnectivityObjectiveDataWriter) publisher.create_datawriter_with_profile(
                deviceConnectivityObjectiveTopic, QosProfiles.ice_library, QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);

        DeviceConnectivityTypeSupport.register_type(participant, DeviceConnectivityTypeSupport.get_type_name());
        deviceConnectivityTopic = participant.create_topic(ice.DeviceConnectivityTopic.VALUE, ice.DeviceConnectivityTypeSupport.get_type_name(),
                DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        deviceConnectivityReader = (DeviceConnectivityDataReader) subscriber.create_datareader_with_profile(deviceConnectivityTopic,
                QosProfiles.ice_library, QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);
        StringSeq udis = new StringSeq();
        udis.add("'" + unique_device_identifier + "'");
        qc = deviceConnectivityReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE,
                InstanceStateKind.ANY_INSTANCE_STATE, "unique_device_identifier MATCH %0", udis);
        eventLoop.addHandler(qc, new EventLoop.ConditionHandler() {
            SampleInfoSeq info_seq = new SampleInfoSeq();
            DeviceConnectivitySeq data_seq = new DeviceConnectivitySeq();

            @Override
            public void conditionChanged(Condition condition) {
                try {
                    deviceConnectivityReader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, qc);
                    for (int i = 0; i < data_seq.size(); i++) {
                        SampleInfo si = (SampleInfo) info_seq.get(i);
                        if (si.valid_data) {
                            DeviceConnectivity dc = (DeviceConnectivity) data_seq.get(i);
                            deviceConnectivity.copy_from(dc);
                            synchronized (AbstractGetConnected.this) {
                                deviceConnectivityReceived = true;
                                AbstractGetConnected.this.notifyAll();
                            }
                        }
                    }
                } catch (RETCODE_NO_DATA noData) {
                } finally {
                    deviceConnectivityReader.return_loan(data_seq, info_seq);
                }
            }
        });

    }

    private boolean deviceConnectivityReceived = false;

    public void connect() {
        issueConnect();
    }

    public void disconnect() {
        long start = System.currentTimeMillis();

        closing = true;
        deviceConnectivityObjective.connected = false;
        deviceConnectivityObjective.target = "APP IS CLOSING";

        boolean disconnected = false;

        while (!disconnected) {
            deviceConnectivityObjectiveWriter.write(deviceConnectivityObjective, InstanceHandle_t.HANDLE_NIL);
            synchronized (this) {
                disconnected = ice.ConnectionState.Disconnected.equals(deviceConnectivity.state);
                if (!disconnected) {
                    if ((System.currentTimeMillis() - start) >= 10000L) {
                        return;
                    }
                    try {
                        this.wait(1000L);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }

        }
    }

    protected abstract void abortConnect();

    protected abstract String addressFromUser();

    protected abstract String addressFromUserList(String[] list);

    protected abstract boolean isFixedAddress();

    private boolean issuingConnect;

    @SuppressWarnings("unchecked")
    private void issueConnect() {
        long giveup = System.currentTimeMillis() + 10000L;
        synchronized (this) {
            if (issuingConnect) {
                return;
            } else {
                issuingConnect = true;
                notifyAll();
            }
            long now = System.currentTimeMillis();
            while (!deviceConnectivityReceived && now < giveup) {
                try {
                    wait(500L);
                } catch (InterruptedException e) {
                }
                now = System.currentTimeMillis();
            }
            if (now >= giveup) {
                throw new IllegalStateException("No DeviceConnectivity received within 10 seconds");
            }

        }
        try {
            System.out.println(deviceConnectivity.state);
            if ("".equals(deviceConnectivityObjective.target)
                    && !closing
                    && deviceConnectivity.type != null
                    && (isFixedAddress() || !deviceConnectivity.valid_targets.userData.isEmpty() || !ice.ConnectionType.Serial
                            .equals(deviceConnectivity.type)) && ice.ConnectionState.Disconnected.equals(deviceConnectivity.state)) {
                if (ice.ConnectionType.Network.equals(deviceConnectivity.type)) {
                    deviceConnectivityObjective.target = addressFromUser();
                    if (null == deviceConnectivityObjective.target) {
                        abortConnect();
                        return;
                    }
                } else if (ice.ConnectionType.Serial.equals(deviceConnectivity.type)) {
                    deviceConnectivityObjective.target = addressFromUserList((String[]) deviceConnectivity.valid_targets.userData
                            .toArray(new String[0]));
                    if (null == deviceConnectivityObjective.target) {
                        abortConnect();
                        return;
                    }
                } else {
                    deviceConnectivityObjective.target = "";
                }
                deviceConnectivityObjective.connected = true;
                deviceConnectivityObjectiveWriter.write(deviceConnectivityObjective, InstanceHandle_t.HANDLE_NIL);
            } else {

            }
        } finally {
            synchronized (this) {
                issuingConnect = false;
                notifyAll();
            }
        }

    }
}
