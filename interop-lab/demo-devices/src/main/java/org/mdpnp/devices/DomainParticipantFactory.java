package org.mdpnp.devices;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

public class DomainParticipantFactory implements FactoryBean<DomainParticipant>, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(DomainParticipantFactory.class);

    // if not set (empty), default from ice_library.xml are used.
    private final List<String> discoveryAddress=new ArrayList<>();
    private final int  domain;

    private DomainParticipant instance;

    static final String TOKENIZER_REGEX = "[\\s,;]+";

    public DomainParticipantFactory(int domain) {
        this(domain, null);
    }

    public DomainParticipantFactory(int domain, String discoveryAddress) {
        this.domain = domain;
        if(discoveryAddress != null) {
            String [] arr = discoveryAddress.split(TOKENIZER_REGEX);
            for(String s:arr) {
                this.discoveryAddress.add(s.trim());
            }
        }
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
            // TODO I neutered this for the time being because other participants on
            // the localhost might be outside of this process.
//            dpQos.wire_protocol.participant_id = nextParticipantId++;

            if(dpQos.discovery.multicast_receive_addresses.size() != 0)
                log.warn(dpQos.discovery.multicast_receive_addresses.size() + " " + dpQos.discovery.multicast_receive_addresses.get(0).toString());

            if(!discoveryAddress.isEmpty()) {
                dpQos.discovery.multicast_receive_addresses.clear();
                dpQos.discovery.initial_peers.clear();

                for(int i=0; i<discoveryAddress.size(); i++) {
                    String addr = discoveryAddress.get(i);
                    String s = "udpv4://" + addr;
                    log.warn("Overriding default discovery settings to use " + s);
                    InetAddress ip = InetAddress.getByName(addr);
                    dpQos.discovery.initial_peers.add(s);
                    if (i == 0 && ip.isMulticastAddress()) {
                        dpQos.discovery.multicast_receive_addresses.add(s);
                    }
                }
            }


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
