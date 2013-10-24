package org.mdpnp.devices;

import java.lang.reflect.Method;

import org.omg.dds.domain.DomainParticipant;
import org.omg.dds.topic.TopicDescription;

public class TopicUtil {
    public static <T extends org.omg.CORBA.portable.IDLEntity> TopicDescription<T>  lookupOrCreateTopic(DomainParticipant participant, String topicName, Class<T> clazz) {
        TopicDescription<T> topic = participant.lookupTopicDescription(topicName);
        if(null == topic) {
            try {
                Method get_type_name = clazz.getMethod("get_type_name");
                String typeName = (String) get_type_name.invoke(null);
                clazz.getMethod("register_type", DomainParticipant.class, String.class).invoke(null, participant, typeName);
                topic = participant.createTopic(topicName, clazz);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
        return topic;
    }
}
