package org.mdpnp.rtiapi.data;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.topic.Topic;
import com.rti.dds.topic.TopicDescription;
import ice.InfusionObjectiveDataWriter;
import org.springframework.beans.factory.FactoryBean;

/**
 *
 */
public class InfusionObjectiveDataWriterFactory implements FactoryBean<InfusionObjectiveDataWriter> {

    private InfusionObjectiveDataWriter instance;

    private final DomainParticipant participant;
    private final Publisher publisher;

    @Override
    public InfusionObjectiveDataWriter getObject() throws Exception {
        if(instance == null) {
            TopicDescription infusionObjectiveTopic = TopicUtil.lookupOrCreateTopic(participant, ice.InfusionObjectiveTopic.VALUE,  ice.InfusionObjectiveTypeSupport.class);
            instance = (InfusionObjectiveDataWriter) publisher.create_datawriter_with_profile((Topic) infusionObjectiveTopic, QosProfiles.ice_library,
                    QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);
        }
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return InfusionObjectiveDataWriter.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public InfusionObjectiveDataWriterFactory(DomainParticipant participant, Publisher publisher) {
        this.participant = participant;
        this.publisher = publisher;
    }
}
