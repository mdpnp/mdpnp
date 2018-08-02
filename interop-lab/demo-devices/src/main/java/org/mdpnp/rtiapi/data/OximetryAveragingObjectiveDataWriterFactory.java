package org.mdpnp.rtiapi.data;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.topic.Topic;
import com.rti.dds.topic.TopicDescription;

import ice.InfusionStatusDataWriter;
import ice.OximetryAveragingObjectiveDataWriter;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

/**
 *
 */
public class OximetryAveragingObjectiveDataWriterFactory implements FactoryBean<OximetryAveragingObjectiveDataWriter>, DisposableBean {
    private Topic topic;
    private OximetryAveragingObjectiveDataWriter instance;

    private final DomainParticipant participant;
    private final Publisher publisher;

    @Override
    public OximetryAveragingObjectiveDataWriter getObject() throws Exception {
        if(instance == null) {
            topic = TopicUtil.findOrCreateTopic(participant, ice.OximetryAveragingObjectiveTopic.VALUE,  ice.OximetryAveragingObjectiveTypeSupport.class);
            instance = (OximetryAveragingObjectiveDataWriter) publisher.create_datawriter_with_profile(topic, QosProfiles.ice_library,
                    QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);
        }
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return InfusionStatusDataWriter.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public OximetryAveragingObjectiveDataWriterFactory(DomainParticipant participant, Publisher publisher) {
        this.participant = participant;
        this.publisher = publisher;
    }

    @Override
    public void destroy() throws Exception {
        if(null != instance) {
            publisher.delete_datawriter(instance);
            publisher.get_participant().delete_topic(topic);
        }
    }
}
