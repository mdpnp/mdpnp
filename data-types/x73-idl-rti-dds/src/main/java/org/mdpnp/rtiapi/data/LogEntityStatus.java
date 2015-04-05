package org.mdpnp.rtiapi.data;

import org.slf4j.Logger;

import com.rti.dds.domain.DomainParticipantListener;
import com.rti.dds.infrastructure.Cookie_t;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.Locator_t;
import com.rti.dds.publication.AcknowledgmentInfo;
import com.rti.dds.publication.DataWriter;
import com.rti.dds.publication.DataWriterListener;
import com.rti.dds.publication.LivelinessLostStatus;
import com.rti.dds.publication.OfferedDeadlineMissedStatus;
import com.rti.dds.publication.OfferedIncompatibleQosStatus;
import com.rti.dds.publication.PublicationMatchedStatus;
import com.rti.dds.publication.PublisherListener;
import com.rti.dds.publication.ReliableReaderActivityChangedStatus;
import com.rti.dds.publication.ReliableWriterCacheChangedStatus;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderListener;
import com.rti.dds.subscription.LivelinessChangedStatus;
import com.rti.dds.subscription.RequestedDeadlineMissedStatus;
import com.rti.dds.subscription.RequestedIncompatibleQosStatus;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberListener;
import com.rti.dds.subscription.SubscriptionMatchedStatus;
import com.rti.dds.topic.InconsistentTopicStatus;
import com.rti.dds.topic.Topic;
import com.rti.dds.topic.TopicListener;

public class LogEntityStatus implements DataReaderListener, DataWriterListener, TopicListener, SubscriberListener, PublisherListener, DomainParticipantListener {
    private final Logger log;
    private final String label;
    
    public LogEntityStatus(final Logger log, final String label) {
        this.log = log;
        this.label = label;
    }
    
    @Override
    public void on_application_acknowledgment(DataWriter arg0, AcknowledgmentInfo arg1) {
        if(log.isDebugEnabled()) {
            log.debug("{} application_acknowledgment: {}", label, arg1);
        }
    }

    @Override
    public Object on_data_request(DataWriter arg0, Cookie_t arg1) {
        if(log.isDebugEnabled()) {
            log.debug("{} data_request: {}", label, arg1);
        }
        return null;
    }

    @Override
    public void on_data_return(DataWriter arg0, Object arg1, Cookie_t arg2) {
        if(log.isDebugEnabled()) {
            log.debug("{} data_return: {} {}", label, arg1, arg2);
        }
    }

    @Override
    public void on_destination_unreachable(DataWriter arg0, InstanceHandle_t arg1, Locator_t arg2) {
        if(log.isDebugEnabled()) {
            log.debug("{} destination_unreachable: {} {}", label, arg1, arg2);
        }
    }

    @Override
    public void on_instance_replaced(DataWriter arg0, InstanceHandle_t arg1) {
        if(log.isDebugEnabled()) {
            log.debug("{} instance_replaced: {}", label, arg1);
        }
    }

    @Override
    public void on_liveliness_lost(DataWriter arg0, LivelinessLostStatus arg1) {
        if(log.isDebugEnabled()) {
            log.debug("{} liveliness_lost: {}", label, arg1);
        }
    }

    @Override
    public void on_offered_deadline_missed(DataWriter arg0, OfferedDeadlineMissedStatus arg1) {
        if(log.isDebugEnabled()) {
            log.debug("{} offered_deadline_missed: {}", label, arg1);
        }
    }

    @Override
    public void on_offered_incompatible_qos(DataWriter arg0, OfferedIncompatibleQosStatus arg1) {
        if(log.isDebugEnabled()) {
            log.debug("{} offered_incompatible_qos: {}", label, arg1);
        }
    }

    @Override
    public void on_publication_matched(DataWriter arg0, PublicationMatchedStatus arg1) {
        if(log.isDebugEnabled()) {
            log.debug("{} publication_matched: {}", label, arg1);
        }
    }

    @Override
    public void on_reliable_reader_activity_changed(DataWriter arg0, ReliableReaderActivityChangedStatus arg1) {
        if(log.isDebugEnabled()) {
            log.debug("{} reliable_reader_activity_changed: {}", label, arg1);
        }
    }

    @Override
    public void on_reliable_writer_cache_changed(DataWriter arg0, ReliableWriterCacheChangedStatus arg1) {
        if(log.isDebugEnabled()) {
            log.debug("{} reliable_writer_cache_changed: {}", label, arg1);
        }
    }

    @Override
    public void on_sample_removed(DataWriter arg0, Cookie_t arg1) {
        if(log.isDebugEnabled()) {
            log.debug("{} sample_removed: {}", label, arg1);
        }
    }

    @Override
    public void on_data_on_readers(Subscriber arg0) {
        if(log.isDebugEnabled()) {
            log.debug("{} data_on_readers", label);
        }
    }

    @Override
    public void on_inconsistent_topic(Topic arg0, InconsistentTopicStatus arg1) {
        if(log.isDebugEnabled()) {
            log.debug("{} inconsistent_topic: {}", label, arg1);
        }
    }

    @Override
    public void on_data_available(DataReader arg0) {
        if(log.isDebugEnabled()) {
            log.debug("{} data_available", label);
        }
    }

    @Override
    public void on_liveliness_changed(DataReader arg0, LivelinessChangedStatus arg1) {
        if(log.isDebugEnabled()) {
            log.debug("{} liveliness_changed: {}", label, arg1);
        }
    }

    @Override
    public void on_requested_deadline_missed(DataReader arg0, RequestedDeadlineMissedStatus arg1) {
        if(log.isDebugEnabled()) {
            log.debug("{} requested_deadline_missed: {}", label, arg1);
        }
    }

    @Override
    public void on_requested_incompatible_qos(DataReader arg0, RequestedIncompatibleQosStatus arg1) {
        if(log.isDebugEnabled()) {
            log.debug("{} requested_incompatible_qos: {}", label, arg1);
        }
    }

    @Override
    public void on_sample_lost(DataReader arg0, SampleLostStatus arg1) {
        if(log.isDebugEnabled()) {
            log.debug("{} sample_lost: {}", label, arg1);
        }
    }

    @Override
    public void on_sample_rejected(DataReader arg0, SampleRejectedStatus arg1) {
        if(log.isDebugEnabled()) {
            log.debug("{} sample_rejected: {}", label, arg1);
        }
    }

    @Override
    public void on_subscription_matched(DataReader arg0, SubscriptionMatchedStatus arg1) {
        if(log.isDebugEnabled()) {
            log.debug("{} subscription_matched: {}", label, arg1);
        }
    }
}
