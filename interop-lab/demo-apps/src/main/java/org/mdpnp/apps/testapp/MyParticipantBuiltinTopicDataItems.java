package org.mdpnp.apps.testapp;

import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantFactoryQos;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicData;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicDataDataReader;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicDataSeq;
import com.rti.dds.domain.builtin.ParticipantBuiltinTopicDataTypeSupport;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderListener;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.LivelinessChangedStatus;
import com.rti.dds.subscription.RequestedDeadlineMissedStatus;
import com.rti.dds.subscription.RequestedIncompatibleQosStatus;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.SubscriptionMatchedStatus;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.BuiltinTopicKey_t;

public class MyParticipantBuiltinTopicDataItems implements DataReaderListener {
    private final DomainParticipant participant;
    private final ParticipantBuiltinTopicDataDataReader reader;
    protected final ObservableList<MyParticipantBuiltinTopicData> items = FXCollections.observableArrayList();
    protected final Map<BuiltinTopicKey_t, MyParticipantBuiltinTopicData> participantsByKey = new HashMap<BuiltinTopicKey_t, MyParticipantBuiltinTopicData>();
    
    public MyParticipantBuiltinTopicDataItems(final int domainId) {
        DomainParticipantFactory factory = DomainParticipantFactory.get_instance();
        DomainParticipantFactoryQos qos = new DomainParticipantFactoryQos();
        factory.get_qos(qos);
        qos.entity_factory.autoenable_created_entities = false;
        factory.set_qos(qos);

        DomainParticipantQos dpQos = new DomainParticipantQos();
        factory.get_default_participant_qos(dpQos);
        dpQos.participant_name.name = "ICE  Participant Only";

        participant = factory.create_participant(domainId, dpQos, null, StatusKind.STATUS_MASK_NONE);
        reader = (ParticipantBuiltinTopicDataDataReader) participant.get_builtin_subscriber().lookup_datareader(
                ParticipantBuiltinTopicDataTypeSupport.PARTICIPANT_TOPIC_NAME);
        reader.set_listener(this, StatusKind.DATA_AVAILABLE_STATUS);

        participant.enable();

    }
    
    @Override
    public void on_data_available(DataReader arg0) {
        final ParticipantBuiltinTopicDataSeq data_seq = new ParticipantBuiltinTopicDataSeq();
        final SampleInfoSeq sample_info = new SampleInfoSeq();
        try {
            for (; ; ) {
                try {
                    reader.read(data_seq, sample_info, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.NOT_READ_SAMPLE_STATE,
                            ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                    for (int i = 0; i < data_seq.size(); i++) {
                        ParticipantBuiltinTopicData d = (ParticipantBuiltinTopicData) data_seq.get(i);
                        SampleInfo si = (SampleInfo) sample_info.get(i);

                        if (0 != (InstanceStateKind.ALIVE_INSTANCE_STATE & si.instance_state)) {
                            if (si.valid_data) {
                                int idx = -1;
                                boolean inserted = false;
                                ParticipantBuiltinTopicData d1 = new ParticipantBuiltinTopicData();
                                d1.copy_from(d);
                                Platform.runLater(new Runnable() {
                                    public void run() {
                                        MyParticipantBuiltinTopicData item = participantsByKey.get(d1.key);
                                        if(null == item) {
                                            items.add(new MyParticipantBuiltinTopicData(d1));
                                        } else {
                                            item.update(d1);
                                        }
                                    }
                                });
                            }
                        } else {
                            ParticipantBuiltinTopicData d1 = new ParticipantBuiltinTopicData();
                            reader.get_key_value(d1, si.instance_handle);
                            Platform.runLater(new Runnable() {
                                public void run() {
                                    items.remove(new MyParticipantBuiltinTopicData(d1));
                                }
                            });
                        }
                    }
                } finally {
                    reader.return_loan(data_seq, sample_info);
                }
            }
        } catch (RETCODE_NO_DATA noData) {

        }
    }

    @Override
    public void on_liveliness_changed(DataReader arg0, LivelinessChangedStatus arg1) {

    }

    @Override
    public void on_requested_deadline_missed(DataReader arg0, RequestedDeadlineMissedStatus arg1) {

    }

    @Override
    public void on_requested_incompatible_qos(DataReader arg0, RequestedIncompatibleQosStatus arg1) {

    }

    @Override
    public void on_sample_lost(DataReader arg0, SampleLostStatus arg1) {

    }

    @Override
    public void on_sample_rejected(DataReader arg0, SampleRejectedStatus arg1) {

    }

    @Override
    public void on_subscription_matched(DataReader arg0, SubscriptionMatchedStatus arg1) {

    }
    
    public void stop() {
        DomainParticipantFactory.get_instance().delete_participant(participant);
    }
    
    public ObservableList<MyParticipantBuiltinTopicData> getItems() {
        return items;
    }
}
