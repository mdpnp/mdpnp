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

import ice.Numeric;
import ice.NumericDataReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.mdpnp.rtiapi.data.QosProfiles;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderListener;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.LivelinessChangedStatus;
import com.rti.dds.subscription.RequestedDeadlineMissedStatus;
import com.rti.dds.subscription.RequestedIncompatibleQosStatus;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriptionMatchedStatus;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;

/**
 * @author Jeff Plourde
 *
 */
public class ReceiveNumerics implements DataReaderListener {
    public static void main(String[] args) throws IOException {

        ReceiveNumerics receiver = new ReceiveNumerics();

        // For testing we use 0
        // For our ICU devices (Ivy Monitor, Capnograph, etc.) we use 15 ...
        // this is probably the one you want for DC but a way to change it on
        // the fly would be great
        // For our OR devices (not bring all of them to DC) we use 3
        int domainId = 0;

        DomainParticipant participant = DomainParticipantFactory.get_instance().create_participant(domainId,
                DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        Subscriber subscriber = participant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        ice.NumericTypeSupport.register_type(participant, ice.NumericTypeSupport.get_type_name());
        Topic topic = participant.create_topic(ice.NumericTopic.VALUE, ice.NumericTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT,
                null, StatusKind.STATUS_MASK_NONE);

        ice.NumericDataReader reader = (NumericDataReader) subscriber.create_datareader_with_profile(topic, QosProfiles.ice_library,
                QosProfiles.numeric_data, receiver, StatusKind.DATA_AVAILABLE_STATUS);

        BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Press any key to exit");

        stdin.read();

        System.out.println("Tearing down");

        subscriber.delete_datareader(reader);
        reader = null;

        participant.delete_topic(topic);
        topic = null;

        ice.NumericTypeSupport.unregister_type(participant, ice.NumericTypeSupport.get_type_name());

        participant.delete_subscriber(subscriber);
        subscriber = null;

        DomainParticipantFactory.get_instance().delete_participant(participant);
        participant = null;

        DomainParticipantFactory.finalize_instance();

    }

    private final ice.NumericSeq numeric_seq = new ice.NumericSeq();
    private final SampleInfoSeq sampleinfo_seq = new SampleInfoSeq();
    private final Date source_time = new Date(), receipt_time = new Date();
    private final DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

    @Override
    public void on_data_available(DataReader arg0) {
        ice.NumericDataReader reader = (NumericDataReader) arg0;
        // I tend to loop to make sure the reader is completely drained but I
        // don't think it's strictly necessary
        for (;;) {
            try {
                // The shortcut versions of read and take can be confusing so I
                // find myself using the fully expressed ones a lot
                // "read"ing samples leaves them in the reader (as opposed to
                // take)
                // There are practical limits on the length of the result but I
                // don't have any limit in mind so I use LENGTH_UNLIMITED
                // Specifying the NOT_READ_SAMPLE_STATE is important or else
                // we'd keep reading the same samples!
                // The viewstate refers to this reader's view of the instance
                // (new or not)
                // The instancestate tells the liveliness of the writers of the
                // instance
                reader.read(numeric_seq, sampleinfo_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.NOT_READ_SAMPLE_STATE,
                        ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                for (int i = 0; i < sampleinfo_seq.size(); i++) {
                    SampleInfo si = (SampleInfo) sampleinfo_seq.get(i);
                    ice.Numeric numeric = (Numeric) numeric_seq.get(i);
                    if (si.valid_data) {
                        source_time.setTime(si.source_timestamp.sec * 1000L + si.source_timestamp.nanosec / 1000000L);
                        receipt_time.setTime(si.reception_timestamp.sec * 1000L + si.source_timestamp.nanosec / 1000000L);
                        System.out.println("Source Time:" + dateFormat.format(source_time) + " Receipt Time:" + dateFormat.format(receipt_time)
                                + " name=" + numeric.metric_id + " value=" + numeric.value + " udi=" + numeric.unique_device_identifier);
                    }
                }
                // I don't like that they use this to report no more data
            } catch (RETCODE_NO_DATA noData) {
                break;
            } finally {
                // important to allow DDS to reuse the resources it lent out to
                // us when we read samples from the reader
                reader.return_loan(numeric_seq, sampleinfo_seq);
            }
        }
    }

    @Override
    public void on_liveliness_changed(DataReader arg0, LivelinessChangedStatus arg1) {

    }

    @Override
    public void on_requested_deadline_missed(DataReader arg0, RequestedDeadlineMissedStatus arg1) {

    }

    @Override
    public void on_requested_incompatible_qos(DataReader arg0, RequestedIncompatibleQosStatus arg1) {

    }

    @Override
    public void on_sample_lost(DataReader arg0, SampleLostStatus arg1) {

    }

    @Override
    public void on_sample_rejected(DataReader arg0, SampleRejectedStatus arg1) {

    }

    @Override
    public void on_subscription_matched(DataReader arg0, SubscriptionMatchedStatus arg1) {

    }
}
