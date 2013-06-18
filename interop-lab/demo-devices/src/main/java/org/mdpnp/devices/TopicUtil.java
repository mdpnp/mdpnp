package org.mdpnp.devices;

import java.lang.reflect.Method;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.topic.TopicDescription;
import com.rti.dds.topic.TypeSupport;

public class TopicUtil {
    public static TopicDescription lookupOrCreateTopic(DomainParticipant participant, String topicName, Class<? extends TypeSupport> clazz) {
        TopicDescription topic = participant.lookup_topicdescription(topicName);
        if(null == topic) {
            try {
                Method get_type_name = clazz.getMethod("get_type_name");
                String typeName = (String) get_type_name.invoke(null);
                clazz.getMethod("register_type", DomainParticipant.class, String.class).invoke(null, participant, typeName);
                topic = participant.create_topic(topicName, typeName, DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            
        }
        return topic;
    }
}
