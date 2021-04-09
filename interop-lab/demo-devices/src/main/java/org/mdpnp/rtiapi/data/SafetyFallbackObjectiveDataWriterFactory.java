package org.mdpnp.rtiapi.data;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.topic.Topic;

import ice.SafetyFallbackObjectiveDataWriter;

public class SafetyFallbackObjectiveDataWriterFactory
		implements FactoryBean<SafetyFallbackObjectiveDataWriter>, DisposableBean {

	private Topic topic;
    private SafetyFallbackObjectiveDataWriter instance;

    private final DomainParticipant participant;
    private final Publisher publisher;

	@Override
	public void destroy() throws Exception {
		if(null != instance) {
            publisher.delete_datawriter(instance);
            publisher.get_participant().delete_topic(topic);
        }
	}

	@Override
	public SafetyFallbackObjectiveDataWriter getObject() throws Exception {
		if(instance == null) {
            topic = TopicUtil.findOrCreateTopic(participant, ice.SafetyFallbackObjectiveTopic.VALUE,  ice.SafetyFallbackObjectiveTypeSupport.class);
            instance = (SafetyFallbackObjectiveDataWriter) publisher.create_datawriter_with_profile(topic, QosProfiles.ice_library,
                    QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);
        }
        return instance;
	}

	@Override
	public Class<?> getObjectType() {
		return SafetyFallbackObjectiveDataWriter.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public SafetyFallbackObjectiveDataWriterFactory(DomainParticipant participant, Publisher publisher) {
		super();
		this.participant = participant;
		this.publisher = publisher;
	}
}
