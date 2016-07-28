package org.mdpnp.rtiapi.data;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.topic.Topic;
import himss.PatientAssessmentDataWriter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

/**
 *
 */
public class PatientAssessmentDataWriterFactory implements FactoryBean<PatientAssessmentDataWriter>, DisposableBean {
    private Topic topic;
    private PatientAssessmentDataWriter instance;

    private final DomainParticipant participant;
    private final Publisher publisher;

    @Override
    public PatientAssessmentDataWriter getObject() throws Exception {
        if(instance == null) {

            himss.PatientAssessmentTypeSupport.register_type(publisher.get_participant(),
                                                             himss.PatientAssessmentTypeSupport.get_type_name());

            topic = TopicUtil.findOrCreateTopic(participant,
                                                himss.PatientAssessmentTopic.VALUE,
                                                himss.PatientAssessmentTypeSupport.class);

            instance = (PatientAssessmentDataWriter)
                    publisher.create_datawriter_with_profile(topic,
                                                             QosProfiles.ice_library,
                                                             QosProfiles.state,
                                                             null,
                                                             StatusKind.STATUS_MASK_NONE);
        }
        return instance;
    }

    @Override
    public Class<?> getObjectType() {
        return PatientAssessmentDataWriter.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public PatientAssessmentDataWriterFactory(DomainParticipant participant, Publisher publisher) {
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
