package org.mdpnp.devices;

import com.google.common.collect.Iterables;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import com.google.common.base.Splitter;

public class DomainParticipantFactory implements FactoryBean<DomainParticipant>, DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(DomainParticipantFactory.class);

    // if not set (empty), default from ice_library.xml are used.
    private final List<String> discoveryPeers=new ArrayList<>();
    private final int  domain;

    private DomainParticipant instance;

    private static final String TOKENIZER_REGEX = "[\\s,;]+";

    public DomainParticipantFactory(int domain) {
        this(domain, null);
    }

    public DomainParticipantFactory(int domain, String discoveryPeers) {
        this.domain = domain;
        if(discoveryPeers != null) {
            List<String>l = parse(discoveryPeers);
            this.discoveryPeers.addAll(l);
        }
    }

    static List<String> parse(String s) {
        Iterable<String> ss = Splitter.onPattern(TOKENIZER_REGEX).trimResults().omitEmptyStrings().split(s);
        String[] strings = Iterables.toArray(ss, String.class);
        List<String> l = Arrays.asList(strings);
        return l;
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

            if(!discoveryPeers.isEmpty()) {
                dpQos.discovery.multicast_receive_addresses.clear();
                dpQos.discovery.initial_peers.clear();

                // mimic logic described here:
                // http://community.rti.com/rti-doc/510/ndds/doc/html/api_java/classcom_1_1rti_1_1dds_1_1infrastructure_1_1DiscoveryQosPolicy.html
                //
                // If NDDS_DISCOVERY_PEERS does not contain a multicast address, then the string
                // sequence com.rti.dds.infrastructure.DiscoveryQosPolicy.multicast_receive_addresses
                // is cleared and the RTI discovery process will not listen for discovery messages via
                // multicast.
                //
                // If NDDS_DISCOVERY_PEERS contains one or more multicast addresses, the addresses will
                // be stored in com.rti.dds.infrastructure.DiscoveryQosPolicy.multicast_receive_addresses,
                // starting at element 0. They will be stored in the order they appear NDDS_DISCOVERY_PEERS.
                //
                // Note: Currently, RTI Connext will only listen for discovery traffic on the first multicast
                // address (element 0) in com.rti.dds.infrastructure.DiscoveryQosPolicy.multicast_receive_addresses.
                //

                for(int i=0; i<discoveryPeers.size(); i++) {
                    String addr = discoveryPeers.get(i);
                    String s = "udpv4://" + addr;
                    log.warn("Overriding default discovery settings to use " + s);
                    InetAddress ip = InetAddress.getByName(addr);
                    dpQos.discovery.initial_peers.add(s);
                    if (ip.isMulticastAddress() && dpQos.discovery.multicast_receive_addresses.isEmpty()) {
                        dpQos.discovery.multicast_receive_addresses.add(s);
                    }
                }
            }

			if(dpQos.discovery.multicast_receive_addresses.size() != 0)
				log.warn("Discovery 'multicast_receive_addresses' size:" + dpQos.discovery.multicast_receive_addresses.size() +
						 " first entry:" + dpQos.discovery.multicast_receive_addresses.get(0).toString());

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
