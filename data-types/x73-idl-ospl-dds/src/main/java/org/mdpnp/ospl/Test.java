package org.mdpnp.ospl;

import ice.NumericDataReader;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import DDS.DataReader;
import DDS.DataReaderListener;
import DDS.DataReaderQosHolder;
import DDS.DomainParticipant;
import DDS.DomainParticipantFactory;
import DDS.DomainParticipantFactoryQosHolder;
import DDS.DomainParticipantQosHolder;
import DDS.DurabilityQosPolicyKind;
import DDS.ErrorInfo;
import DDS.HistoryQosPolicyKind;
import DDS.LivelinessChangedStatus;
import DDS.LivelinessQosPolicyKind;
import DDS.OwnershipQosPolicyKind;
import DDS.ParticipantBuiltinTopicData;
import DDS.ParticipantBuiltinTopicDataDataReader;
import DDS.ParticipantBuiltinTopicDataSeqHolder;
import DDS.ReliabilityQosPolicyKind;
import DDS.RequestedDeadlineMissedStatus;
import DDS.RequestedIncompatibleQosStatus;
import DDS.SampleInfo;
import DDS.SampleLostStatus;
import DDS.SampleRejectedStatus;
import DDS.StringHolder;
import DDS.Subscriber;
import DDS.SubscriberQosHolder;
import DDS.SubscriptionMatchedStatus;
import DDS.Topic;

public class Test implements DataReaderListener {
    public static void main(String[] args) throws IOException {
//        QosProvider provider = new QosProvider("file://qos.xml", "default_profile");
        DomainParticipantQosHolder holder = new DomainParticipantQosHolder();
//        DomainParticipantQos dpQos = new DomainParticipantQos();
        DomainParticipantFactory dpf = DomainParticipantFactory.get_instance();
        DomainParticipantFactoryQosHolder dpfQos = new DomainParticipantFactoryQosHolder();
        dpf.get_qos(dpfQos);
        dpfQos.value.entity_factory.autoenable_created_entities = false;
        dpf.set_qos(dpfQos.value);
        
        dpf.get_default_participant_qos(holder);
        DomainParticipant dp = dpf.create_participant(DDS.DOMAIN_ID_DEFAULT.value, holder.value, null, DDS.STATUS_MASK_NONE.value);
        ParticipantBuiltinTopicDataDataReader partReader = (ParticipantBuiltinTopicDataDataReader) dp.get_builtin_subscriber().lookup_datareader("DCPSParticipant");
        System.out.println(new DDS.ParticipantBuiltinTopicDataTypeSupport().get_type_name());
        partReader.set_listener(new DataReaderListener() {

            @Override
            public void on_data_available(DataReader arg0) {
                ParticipantBuiltinTopicDataDataReader reader = (ParticipantBuiltinTopicDataDataReader) arg0;
                ParticipantBuiltinTopicDataSeqHolder data_seq = new ParticipantBuiltinTopicDataSeqHolder();
                DDS.SampleInfoSeqHolder info_seq = new DDS.SampleInfoSeqHolder();
                for(;;) {
                    try {
                        int r = reader.read(data_seq, info_seq, DDS.LENGTH_UNLIMITED.value, DDS.NOT_READ_SAMPLE_STATE.value, DDS.ANY_VIEW_STATE.value, DDS.ANY_INSTANCE_STATE.value);
                        if(r != DDS.RETCODE_OK.value) {
                            break;
                        }
                        for(int i = 0; i < info_seq.value.length; i++) {
                            SampleInfo sa = info_seq.value[i];
                            boolean alive = 0 != (DDS.ALIVE_INSTANCE_STATE.value & sa.instance_state);
                            boolean newView = 0 != (DDS.NEW_VIEW_STATE.value & sa.view_state);
                            boolean notRead = 0 != (DDS.NOT_READ_SAMPLE_STATE.value & sa.sample_state);
                            System.out.println("Participant instance is " + (alive?"":"NOT ")+ "alive, view is " + (newView?"":"NOT ")+"new, sample is " + (notRead?"NOT ":"") + "read");

                            final ParticipantBuiltinTopicData id = data_seq.value[i];
                            System.out.println(Arrays.toString(id.key)+"\t"+new String(id.user_data.value, Charset.forName("ASCII")));
                        }
                    } finally {
                        reader.return_loan(data_seq, info_seq);
                    }
                }

            }

            @Override
            public void on_liveliness_changed(DataReader arg0, LivelinessChangedStatus arg1) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void on_requested_deadline_missed(DataReader arg0, RequestedDeadlineMissedStatus arg1) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void on_requested_incompatible_qos(DataReader arg0, RequestedIncompatibleQosStatus arg1) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void on_sample_lost(DataReader arg0, SampleLostStatus arg1) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void on_sample_rejected(DataReader arg0, SampleRejectedStatus arg1) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void on_subscription_matched(DataReader arg0, SubscriptionMatchedStatus arg1) {
                // TODO Auto-generated method stub
                
            }
            
        }, DDS.STATUS_MASK_ANY_V1_2.value);
        
        ErrorInfo ei = new ErrorInfo();
        ei.update();
        StringHolder sh = new StringHolder();
        ei.get_location(sh);
        System.out.println(sh.value);
        ice.NumericTypeSupport nts = new ice.NumericTypeSupport();
        new ice.NumericTypeSupport().register_type(dp, nts.get_type_name());
        ice.DeviceIdentityTypeSupport ts = new ice.DeviceIdentityTypeSupport();
        ts.register_type(dp, ts.get_type_name());
        Topic topic = dp.create_topic(ice.NumericTopic.value, nts.get_type_name(), DDS.TOPIC_QOS_DEFAULT.value, null, DDS.STATUS_MASK_NONE.value);
        Topic topic1 = dp.create_topic(ice.DeviceIdentityTopic.value, ts.get_type_name(), DDS.TOPIC_QOS_DEFAULT.value, null, DDS.STATUS_MASK_NONE.value);
        SubscriberQosHolder sQos = new SubscriberQosHolder();
        dp.get_default_subscriber_qos(sQos);
        Subscriber sub = dp.create_subscriber(sQos.value, null, DDS.STATUS_MASK_NONE.value);
        DataReaderQosHolder drQos = new DataReaderQosHolder();
        sub.get_default_datareader_qos(drQos);
        drQos.value.history.kind = HistoryQosPolicyKind.KEEP_LAST_HISTORY_QOS;
        drQos.value.history.depth = 1;
        drQos.value.liveliness.kind = LivelinessQosPolicyKind.AUTOMATIC_LIVELINESS_QOS;
        drQos.value.liveliness.lease_duration.sec = 2;
        drQos.value.liveliness.lease_duration.nanosec = 0;
        drQos.value.reliability.kind = ReliabilityQosPolicyKind.RELIABLE_RELIABILITY_QOS;
        drQos.value.durability.kind = DurabilityQosPolicyKind.TRANSIENT_LOCAL_DURABILITY_QOS;
        drQos.value.ownership.kind = OwnershipQosPolicyKind.EXCLUSIVE_OWNERSHIP_QOS;
        sub.create_datareader(topic, drQos.value, new Test(), DDS.DATA_AVAILABLE_STATUS.value);
        sub.create_datareader(topic1, drQos.value, new DataReaderListener() {
            
            @Override
            public void on_subscription_matched(DataReader arg0, SubscriptionMatchedStatus arg1) {
                System.out.println("subscription matched");
                
            }
            
            @Override
            public void on_sample_rejected(DataReader arg0, SampleRejectedStatus arg1) {
                System.out.println("sample rejected");
                
            }
            
            @Override
            public void on_sample_lost(DataReader arg0, SampleLostStatus arg1) {
                System.out.println("sample lost");
                
            }
            
            @Override
            public void on_requested_incompatible_qos(DataReader arg0, RequestedIncompatibleQosStatus arg1) {
                System.out.println("QoS incompatible");
            }
            
            @Override
            public void on_requested_deadline_missed(DataReader arg0, RequestedDeadlineMissedStatus arg1) {
                System.out.println("deadline missed");
                
            }
            
            @Override
            public void on_liveliness_changed(DataReader arg0, LivelinessChangedStatus arg1) {
                System.out.println("Liveliness Changed");
                
            }
            
            @Override
            public void on_data_available(DataReader arg0) {
                ice.DeviceIdentityDataReader reader = (ice.DeviceIdentityDataReader) arg0;
                ice.DeviceIdentitySeqHolder data_seq = new ice.DeviceIdentitySeqHolder();
                DDS.SampleInfoSeqHolder info_seq = new DDS.SampleInfoSeqHolder();
                for(;;) {
                    try {
                        int r = reader.read(data_seq, info_seq, DDS.LENGTH_UNLIMITED.value, DDS.NOT_READ_SAMPLE_STATE.value, DDS.ANY_VIEW_STATE.value, DDS.ANY_INSTANCE_STATE.value);
                        if(r != DDS.RETCODE_OK.value) {
                            break;
                        }
                        for(int i = 0; i < info_seq.value.length; i++) {
                            SampleInfo sa = info_seq.value[i];
                            boolean alive = 0 != (DDS.ALIVE_INSTANCE_STATE.value & sa.instance_state);
                            boolean newView = 0 != (DDS.NEW_VIEW_STATE.value & sa.view_state);
                            boolean notRead = 0 != (DDS.NOT_READ_SAMPLE_STATE.value & sa.sample_state);
                            System.out.println("DeviceIdentity instance is " + (alive?"":"NOT ")+ "alive, view is " + (newView?"":"NOT ")+"new, sample is " + (notRead?"NOT ":"") + "read");

                            final ice.DeviceIdentity id = data_seq.value[i];
                            System.out.println(id.unique_device_identifier+"\t"+id.manufacturer+"\t"+id.model+"\t"+id.build);
//                            if(null != sa && sa.valid_data) {
//                                SwingUtilities.invokeLater(new Runnable() {
//                                    public void run() {
//                                        try {
//                                            BufferedImage image = ImageIO.read(new ByteArrayInputStream(id.icon.raster));
//                                            JFrame frame = new JFrame(id.manufacturer + " " + id.model);
//                                            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//                                            frame.getContentPane().add(new JLabel(new ImageIcon(image)));
//                                            frame.setSize(640, 480);
//                                            frame.setVisible(true);
//                                            
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//                                        
//                                    }
//                                });
//                            }
                        }
                    } finally {
                        reader.return_loan(data_seq, info_seq);
                    }
                }
            }
        }, DDS.STATUS_MASK_ANY_V1_2.value);
        dp.enable();
        System.in.read();
    }

    @Override
    public void on_data_available(DataReader arg0) {
        ice.NumericDataReader reader = (NumericDataReader) arg0;
        ice.NumericSeqHolder data_seq = new ice.NumericSeqHolder();
        DDS.SampleInfoSeqHolder info_seq = new DDS.SampleInfoSeqHolder();
        reader.read(data_seq, info_seq, 100, DDS.NOT_READ_SAMPLE_STATE.value, DDS.ANY_VIEW_STATE.value, DDS.ANY_INSTANCE_STATE.value);
        for(int i = 0; i < info_seq.value.length; i++) {
            SampleInfo sa = info_seq.value[i];
            if(null != sa && sa.valid_data) {
                System.out.println(data_seq.value[i].unique_device_identifier+"\t"+data_seq.value[i].metric_id+"\t"+data_seq.value[i].instance_id+"\t"+data_seq.value[i].value);
            }
        }
    }

    @Override
    public void on_liveliness_changed(DataReader arg0, LivelinessChangedStatus arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void on_requested_deadline_missed(DataReader arg0, RequestedDeadlineMissedStatus arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void on_requested_incompatible_qos(DataReader arg0, RequestedIncompatibleQosStatus arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void on_sample_lost(DataReader arg0, SampleLostStatus arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void on_sample_rejected(DataReader arg0, SampleRejectedStatus arg1) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void on_subscription_matched(DataReader arg0, SubscriptionMatchedStatus arg1) {
        // TODO Auto-generated method stub
        
    }
}
