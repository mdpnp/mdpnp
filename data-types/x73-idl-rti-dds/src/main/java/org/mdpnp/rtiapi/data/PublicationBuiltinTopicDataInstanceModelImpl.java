package org.mdpnp.rtiapi.data;

import com.rti.dds.publication.builtin.PublicationBuiltinTopicData;
import com.rti.dds.publication.builtin.PublicationBuiltinTopicDataDataReader;
import com.rti.dds.publication.builtin.PublicationBuiltinTopicDataSeq;
import com.rti.dds.publication.builtin.PublicationBuiltinTopicDataTypeSupport;

@SuppressWarnings("serial")
public class PublicationBuiltinTopicDataInstanceModelImpl extends InstanceModelImpl<PublicationBuiltinTopicData, PublicationBuiltinTopicDataDataReader> implements PublicationBuiltinTopicDataInstanceModel {
    
    public PublicationBuiltinTopicDataInstanceModelImpl(String topic) {
        super(topic, PublicationBuiltinTopicData.class, PublicationBuiltinTopicDataDataReader.class, PublicationBuiltinTopicDataTypeSupport.class, PublicationBuiltinTopicDataSeq.class);
    }
}
