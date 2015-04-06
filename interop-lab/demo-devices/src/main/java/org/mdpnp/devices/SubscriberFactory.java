package org.mdpnp.devices;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;

public class SubscriberFactory implements FactoryBean<Subscriber>, DisposableBean {

    private final DomainParticipant participant;
    private final String initialPartition;
    private Subscriber instance;
    
    public SubscriberFactory(DomainParticipant participant) {
        this.participant = participant;
        this.initialPartition = null;
    }
    
    public SubscriberFactory(DomainParticipant participant, String initialPartition) {
        this.participant = participant;
        this.initialPartition = initialPartition;
    }
    
    @Override
    public Subscriber getObject() throws Exception {
        if(null == instance) {
    
            
            if(null != initialPartition) {
                SubscriberQos subscriberQos = new SubscriberQos();
                participant.get_default_subscriber_qos(subscriberQos);
                subscriberQos.partition.name.clear();
                subscriberQos.partition.name.add(initialPartition);
                instance = participant.create_subscriber(subscriberQos, null, StatusKind.STATUS_MASK_NONE);
            } else {
                instance = participant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
            }
        }
        return instance;
    }

    @Override
    public Class<Subscriber> getObjectType() {
        return Subscriber.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() throws Exception {
        if(null != instance) {
            participant.delete_subscriber(instance);
        }
    }

}
