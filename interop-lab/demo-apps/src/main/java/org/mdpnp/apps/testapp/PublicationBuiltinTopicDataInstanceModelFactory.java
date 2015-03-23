package org.mdpnp.apps.testapp;

import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.PublicationBuiltinTopicDataInstanceModel;
import org.mdpnp.rtiapi.data.PublicationBuiltinTopicDataInstanceModelImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.publication.builtin.PublicationBuiltinTopicDataTypeSupport;

public class PublicationBuiltinTopicDataInstanceModelFactory implements FactoryBean<PublicationBuiltinTopicDataInstanceModel>{

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(PublicationBuiltinTopicDataInstanceModelFactory.class);

    private PublicationBuiltinTopicDataInstanceModel instance;
    
    private final DomainParticipant participant;
    private final EventLoop eventLoop;
    
    public PublicationBuiltinTopicDataInstanceModelFactory(final DomainParticipant participant, EventLoop eventLoop) {
        this.participant = participant;
        this.eventLoop = eventLoop;
    }
    
    @Override
    public PublicationBuiltinTopicDataInstanceModel getObject() throws Exception {
        instance = new PublicationBuiltinTopicDataInstanceModelImpl(PublicationBuiltinTopicDataTypeSupport.PUBLICATION_TOPIC_NAME);
        instance.start(participant.get_builtin_subscriber(), eventLoop);
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return PublicationBuiltinTopicDataInstanceModel.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
    
    public void stop() {
        if(null != instance) {
            instance.stop();
            instance = null;
        }
    }

}
