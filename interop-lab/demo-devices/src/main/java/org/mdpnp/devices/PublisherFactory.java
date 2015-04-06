package org.mdpnp.devices;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.publication.PublisherQos;


public class PublisherFactory implements FactoryBean<Publisher>, DisposableBean {

    private final DomainParticipant participant;
    private final String initialPartition;
    private Publisher instance;
    
    public PublisherFactory(DomainParticipant participant) {
        this.participant = participant;
        this.initialPartition = null;
    }
    
    public PublisherFactory(DomainParticipant participant, String initialPartition) {
        this.participant = participant;
        this.initialPartition = initialPartition;
    }
    
    @Override
    public Publisher getObject() throws Exception {
        if(null == instance) {
            if(null != initialPartition) {
                PublisherQos publisherQos = new PublisherQos();
                participant.get_default_publisher_qos(publisherQos);
                publisherQos.partition.name.clear();
                publisherQos.partition.name.add(initialPartition);
                instance = participant.create_publisher(publisherQos, null, StatusKind.STATUS_MASK_NONE);
            } else {
                instance = participant.create_publisher(DomainParticipant.PUBLISHER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
            }
        }
        return instance;
    }

    @Override
    public Class<Publisher> getObjectType() {
        return Publisher.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void destroy() throws Exception {
        if(null != instance) {
            participant.delete_publisher(instance);
        }
    }

}
