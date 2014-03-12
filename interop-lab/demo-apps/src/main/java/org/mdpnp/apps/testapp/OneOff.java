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

import org.mdpnp.devices.QosProfiles;
import org.mdpnp.rti.dds.DDS;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.ConditionSeq;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.WaitSet;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;

/**
 * A quick "one off" example of how to consume Numeric data from the ICE
 * 
 * @author jplourde
 * 
 */
public class OneOff {
    public static void main(String[] args) {
        // Discovery of other participants is configured via QoS. We tend to use
        // a multicast address.
        // Participants on different logical "domains" will not discover one
        // another.
        int domainId = 0;

        // This is an MD PnP specific function that tries to automatically
        // ensure that
        // RTI DDS artifacts are in place. If you've set the NDDSHOME
        // environment variable
        // this call is not required but it's a good idea.
        if (!DDS.init()) {
            throw new RuntimeException("Unable to configure DDS artifacts with DDS.init");
        }

        // Creates a participant on the DDS domain. The participant is the
        // primary interface to the DDS domain.
        // The DomainParticipant is a heavy-weight object (allocating memory and
        // threadpools to manage communication) and
        // therefore in most cases only one DomainParticipant will be created
        // per process.
        // PARTICIPANT_QOS_DEFAULT indicates that defaults should be used for
        // QoS. These defaults can be altered at the system and user
        // levels with XML configurations.
        // A listener can optionally be set to receive callbacks from the
        // DomainParticipant. This is an easy shortcut but
        // the downside is those callbacks will be executed on the same threads
        // processing DDS traffic so they are sensitive
        // to any long processing time by the application. The WaitSet approach
        // (below) is preferred for receiving events from
        // those threads via condition variables.
        DomainParticipant participant = DomainParticipantFactory.get_instance().create_participant(domainId,
                DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null /* listener */, StatusKind.STATUS_MASK_NONE);

        // ice.Numeric* are a series of generated classes derived from the IDL
        // definition of the data model.
        // Registering the type with the domain allows us to interact with
        // topics defined to be of the Numeric type.
        ice.NumericTypeSupport.register_type(participant, ice.NumericTypeSupport.get_type_name());

        // A Topic is where publisher and subscribers meet. The name of the
        // topic was defined as a constant in the data model
        // ice.NumericTopic.VALUE)
        // Topics are strongly typed. This topic is of the type named
        // "ice::Numeric" which maps to the generated ice.Numeric type in java.
        Topic topic = participant.create_topic(ice.NumericTopic.VALUE, ice.NumericTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT,
                null, StatusKind.STATUS_MASK_NONE);

        // In DDS a subscriber does not itself receive data samples. Instead it
        // is a container for DataReaders. DataReaders contained
        // within the same subscriber can share some resources, configuration,
        // and synchronization. For this example we create only one
        // subscriber and one DataReader.
        Subscriber sub = participant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

        // The DataReader (in this case the strongly typed
        // ice.NumericDataReader) is our delegate for receiving samples from a
        // DataWriter on the
        // same Topic. Unlike other pub/sub schemes the DataReader does not
        // relay 'messages' but instead tracks (and in many cases persists)
        // data samples so that they are accessible to the application.
        // Violations of Quality of Service constraints are reported by all
        // entities
        // via status conditions. In the absence of such reports the DataReader
        // represents the authoritative "truth" provided by the DDS bus governed
        // by the guarantees of the Quality of Service settings.
        ice.NumericDataReader reader = (NumericDataReader) sub.create_datareader_with_profile(topic, QosProfiles.ice_library,
                QosProfiles.numeric_data, null, StatusKind.STATUS_MASK_NONE);

        // A WaitSet allows us to wait for other threads to set conditions.
        // Analagous to a UNIX 'select' invocation.
        WaitSet ws = new WaitSet();

        // Every DDS entity (participant, subscriber, datareader, etc.) has an
        // associated StatusCondition. For enabled statuses the entity
        // will trigger this condition (waking other interested threads) when
        // the entity has a change in status to report. Here we are configuring
        // the reader StatusCondition to report when data becomes available in
        // the Reader.
        reader.get_statuscondition().set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);

        // By attaching the StatusCondition to our WaitSet we can wait for
        // changes to the enabled statuses (above)
        ws.attach_condition(reader.get_statuscondition());

        // The WaitSet will later fill this container (sequence in DDS
        // terminology) with any triggered statuses
        // Since we have attached only one condition this container may only
        // contain that one StatusCondition (or not)
        ConditionSeq cond_seq = new ConditionSeq();

        // When we wait for Conditions we can specify a maximum time to wait.
        // Once this time has elapsed the call to
        // 'wait' will return even if no Conditions have been triggered. Here we
        // specify an infinite timeout so
        // 'wait' should only return when our Condition is triggered.
        Duration_t forever = new Duration_t(Duration_t.DURATION_INFINITE_SEC, Duration_t.DURATION_INFINITE_NSEC);

        // This will contain samples of Numeric data "loaned" to us by the
        // DataReader in either a 'read' or 'take' call
        // If a Status change does not include fresh sample data (only changes
        // to metadata are being reported) then we should
        // not examine that sample in this container. The SampleInfo.valid_data
        // indicates whether the sample data
        // has been populated
        ice.NumericSeq nu_seq = new ice.NumericSeq();

        // This will contain metadata about each of the samples loaned to us by
        // the DataReader in a 'take' or 'read' call.
        SampleInfoSeq info_seq = new SampleInfoSeq();

        // This thread will have only one job for the rest of its life
        for (;;) {
            // Blocks this thread until one of the Conditions attached to
            // WaitSet ws is triggered. Since the specified
            // Duration_t is infinite this wait will never time out.
            // In java do not confuse this with the standard Object.wait(...)
            // methods which refer to javas own thread synchronization scheme
            ws.wait(cond_seq, forever);

            // Check that our the reader StatusCondition is what triggered
            // Since it is the only Condition and there is no timeout this is
            // mostly for completeness.
            if (cond_seq.contains(reader.get_statuscondition())) {
                // The DataReader reported a change in one of the enabled
                // statuses. Now we examine which status has changed.
                // Importantly this call clears the Condition trigger so calling
                // it is like acknowledging that we have received
                // the message and will deal with the status changes
                int status_changes = reader.get_status_changes();

                // Ensure that DATA_AVAILABLE is one of the statuses that
                // changed in the DataReader.
                // Since this is the only enabled status (see above) this is
                // here mainly for completeness
                if (0 != (status_changes & StatusKind.DATA_AVAILABLE_STATUS)) {
                    try {
                        // "Take" samples from the DataReader. This means they
                        // will not be available in the future to other calls of
                        // read or take.
                        // Alternatively we could "read" samples from the
                        // DataReader; in which case they would remain for
                        // future examination.
                        // Parameters
                        // nu_seq - the collection which will receive data
                        // samples
                        // info_seq - the collection which will receive metadata
                        // (SampleInfo) about those data samples
                        // ResourceLimitsQosPolicy.LENGTH_UNLIMITED - we place
                        // no restrictions on the number of samples returned.
                        // DDS will decide the real maximum based on QoS
                        // settings related to resource management
                        // SampleStateKind.ANY_SAMPLE_STATE - Returns any sample
                        // whether or not it has been previously read
                        // ViewStateKind.ANY_VIEW_STATE - Returns any instance
                        // whether or not is has been previously "viewed" ...
                        // reported by this DataReader
                        // InstateStateKind.ANY_INSTANCE_STATE - Returns any
                        // instances regardless of the liveliness of the
                        // writer(s) of that instance
                        reader.take(nu_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.ANY_SAMPLE_STATE,
                                ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);

                        // Iterate through the samples
                        for (int i = 0; i < nu_seq.size(); i++) {
                            SampleInfo si = (SampleInfo) info_seq.get(i);
                            ice.Numeric n = (Numeric) nu_seq.get(i);

                            // If the metadata indicates a populated sample then
                            // report it to the console
                            // The code generator creates reasonable toString()
                            // for debugging.
                            if (si.valid_data) {
                                System.out.println(n);
                            }
                        }
                    } finally {
                        // Critical to return the loaned samples
                        // Might seem out of place in java but the underlying
                        // library
                        // needs to manage resources and is not garbage
                        // collected
                        reader.return_loan(nu_seq, info_seq);
                    }
                }
            }
        }

    }
}
