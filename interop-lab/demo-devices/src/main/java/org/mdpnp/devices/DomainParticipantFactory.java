package org.mdpnp.devices;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactoryQos;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicDataTypeSupport;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.builtin.PublicationBuiltinTopicDataTypeSupport;
import com.rti.dds.subscription.builtin.SubscriptionBuiltinTopicDataTypeSupport;
import com.rti.dds.topic.builtin.TopicBuiltinTopicDataTypeSupport;

public class DomainParticipantFactory implements FactoryBean<DomainParticipant>, DisposableBean {
    private int domain;
    private DomainParticipant instance;
    
    public DomainParticipantFactory(int domain) {
        this.domain = domain;
    }
    
    private static int nextParticipantId = 0;
    
    @Override
    public DomainParticipant getObject() throws Exception {
        if(null == instance) {
            final DomainParticipantFactoryQos qos = new DomainParticipantFactoryQos();
            com.rti.dds.domain.DomainParticipantFactory.get_instance().get_qos(qos);
            boolean autoenable = qos.entity_factory.autoenable_created_entities;
            qos.entity_factory.autoenable_created_entities = false;
            
            com.rti.dds.domain.DomainParticipantFactory.get_instance().set_qos(qos);
            
            DomainParticipantQos dpQos = new DomainParticipantQos();
            
            com.rti.dds.domain.DomainParticipantFactory.get_instance().get_default_participant_qos(dpQos);
            dpQos.wire_protocol.participant_id = nextParticipantId++;
            
            instance = com.rti.dds.domain.DomainParticipantFactory.get_instance().create_participant(domain, dpQos, null,
                    StatusKind.STATUS_MASK_NONE);
            
            // Initialize this builtin reader
            instance.get_builtin_subscriber().lookup_datareader(PublicationBuiltinTopicDataTypeSupport.PUBLICATION_TOPIC_NAME);
            instance.get_builtin_subscriber().lookup_datareader(SubscriptionBuiltinTopicDataTypeSupport.SUBSCRIPTION_TOPIC_NAME);
            instance.get_builtin_subscriber().lookup_datareader(TopicBuiltinTopicDataTypeSupport.TOPIC_TOPIC_NAME);
            instance.get_builtin_subscriber().lookup_datareader(ParticipantBuiltinTopicDataTypeSupport.PARTICIPANT_TOPIC_NAME);

            instance.enable();
            
            qos.entity_factory.autoenable_created_entities = autoenable;
            com.rti.dds.domain.DomainParticipantFactory.get_instance().set_qos(qos);
            
        }
        return instance;
    }

    @Override
    public Class<DomainParticipant> getObjectType() {
        return DomainParticipant.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() throws Exception {
        if(null != instance) {
            com.rti.dds.domain.DomainParticipantFactory.get_instance().delete_participant(instance);
            instance = null;
        }
    }

}
