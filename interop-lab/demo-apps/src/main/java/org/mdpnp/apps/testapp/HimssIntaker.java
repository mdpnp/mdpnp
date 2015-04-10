package org.mdpnp.apps.testapp;

import himss.PatientAssessmentDataReader;

import org.mdpnp.rtiapi.data.LogEntityStatus;
import org.mdpnp.rtiapi.data.TopicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderListener;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;

public class HimssIntaker {
    private Subscriber subscriber;
    private Topic topic;
    private PatientAssessmentDataReader reader;
    private final DataReaderListener listener;
    
    protected final Logger log = LoggerFactory.getLogger(HimssIntaker.class);
    
    public HimssIntaker() {
        this.listener = new LogEntityStatus(log, "HIMSS PAT. ASS. READER") {
            private final himss.PatientAssessmentSeq seq = new himss.PatientAssessmentSeq();
            private final SampleInfoSeq si_seq = new SampleInfoSeq();
            
            @Override
            public void on_data_available(DataReader arg0) {
                for(;;) {
                    try {
                        reader.read(seq, si_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, SampleStateKind.NOT_READ_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                        for(int i = 0; i < si_seq.size(); i++) {
                            SampleInfo si = (SampleInfo) si_seq.get(i);
                            if(si.valid_data) {
                                log.info(seq.get(i).toString());
                            }
                        }
                    } catch (RETCODE_NO_DATA noData) {
                        break;
                    } finally {
                        reader.return_loan(seq, si_seq);
                    }
                }
            }
        };
    }
    
    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public void start() {
        topic = TopicUtil.findOrCreateTopic(subscriber.get_participant(), himss.PatientAssessmentTopic.VALUE, himss.PatientAssessmentTypeSupport.class);
        reader = (PatientAssessmentDataReader) subscriber.create_datareader_with_profile(topic, "ice_library", "himss", listener, StatusKind.DATA_AVAILABLE_STATUS);
        reader.enable();
    }
    public void stop() {
        subscriber.delete_datareader(reader);
        subscriber.get_participant().delete_topic(topic);        
    }
}
