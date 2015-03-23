package org.mdpnp.apps.testapp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.EventLoop.ConditionHandler;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.publication.builtin.PublicationBuiltinTopicData;
import com.rti.dds.publication.builtin.PublicationBuiltinTopicDataDataReader;
import com.rti.dds.publication.builtin.PublicationBuiltinTopicDataSeq;
import com.rti.dds.publication.builtin.PublicationBuiltinTopicDataTypeSupport;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.ReadCondition;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.ViewStateKind;

public class MyPublicationBuiltinTopicDataItems {
    protected final ObservableList<String> partitions = FXCollections.observableArrayList();
    protected PublicationBuiltinTopicDataDataReader reader;
    protected ReadCondition condition;
    protected EventLoop eventLoop;
    
    public MyPublicationBuiltinTopicDataItems() {
        
    }
    
    public void stop() {
        eventLoop.removeHandler(condition);
        reader.delete_readcondition(condition);
        condition = null;
    }
    
    public MyPublicationBuiltinTopicDataItems setModel(final DomainParticipant participant, final EventLoop eventLoop) {
        this.eventLoop = eventLoop;
        reader = (PublicationBuiltinTopicDataDataReader) participant.get_builtin_subscriber().lookup_datareader(PublicationBuiltinTopicDataTypeSupport.PUBLICATION_TOPIC_NAME);
        condition = reader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE,
                InstanceStateKind.ANY_INSTANCE_STATE);
        eventLoop.addHandler(condition, new ConditionHandler() {
            
            @Override
            public void conditionChanged(Condition condition) {
                PublicationBuiltinTopicDataSeq sa_seq = new PublicationBuiltinTopicDataSeq();
                SampleInfoSeq info_seq = new SampleInfoSeq();
                InstanceHandle_t handle = InstanceHandle_t.HANDLE_NIL;
                final Set<String> partitionsSet = new HashSet<>();
                try {
                    for(;;) {
                        try {
                            reader.read_next_instance(sa_seq, info_seq, 1, handle, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE);
                            for(int i = 0; i < info_seq.size(); i++) {
                                SampleInfo sampleInfo = (SampleInfo) info_seq.get(i);
                                PublicationBuiltinTopicData data = (PublicationBuiltinTopicData) sa_seq.get(i);
                                
                                if(sampleInfo.valid_data) {
                                    for(int j = 0; j < data.partition.name.size(); j++) {
                                        partitionsSet.add((String) data.partition.name.get(j));
                                    }
                                }
                                
                                handle = sampleInfo.instance_handle;
                            }
                        } finally {
                            reader.return_loan(sa_seq, info_seq);
                        }
                    }
                } catch (RETCODE_NO_DATA noData) {
                    
                }
                Platform.runLater(() -> {
                    Iterator<String> itr = partitions.iterator();
                    while(itr.hasNext()) {
                        String s = itr.next();
                        if(!partitionsSet.contains(s)) {
                            itr.remove();
                        } else {
                            partitionsSet.remove(s);
                        }
                    }
                    partitions.addAll(partitionsSet);
                });
            }
        });
        return this;
    }
    
    
    public ObservableList<String> getPartitions() {
        return partitions;
    }
}
