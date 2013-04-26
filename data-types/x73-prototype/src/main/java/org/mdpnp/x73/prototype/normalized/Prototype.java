package org.mdpnp.x73.prototype.normalized;

import java.io.IOException;
import java.util.Calendar;

import org.mdpnp.rti.dds.DDS;
import org.mdpnp.types.Physio;
import org.mdpnp.types.denormalized.MeasurementStatusBits;
import org.mdpnp.types.normalized.AbsoluteTime;
import org.mdpnp.types.normalized.MDC_ATTR_NU_VAL_OBS;
import org.mdpnp.types.normalized.NuObsValue;
import org.mdpnp.types.normalized.NuObsValueDataReader;
import org.mdpnp.types.normalized.NuObsValueDataWriter;
import org.mdpnp.types.normalized.NuObsValueTypeSupport;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.infrastructure.Cookie_t;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.Locator_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.TransportBuiltinKind;
import com.rti.dds.publication.AcknowledgmentInfo;
import com.rti.dds.publication.DataWriter;
import com.rti.dds.publication.DataWriterListener;
import com.rti.dds.publication.LivelinessLostStatus;
import com.rti.dds.publication.OfferedDeadlineMissedStatus;
import com.rti.dds.publication.OfferedIncompatibleQosStatus;
import com.rti.dds.publication.PublicationMatchedStatus;
import com.rti.dds.publication.Publisher;
import com.rti.dds.publication.ReliableReaderActivityChangedStatus;
import com.rti.dds.publication.ReliableWriterCacheChangedStatus;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.DataReaderListener;
import com.rti.dds.subscription.LivelinessChangedStatus;
import com.rti.dds.subscription.RequestedDeadlineMissedStatus;
import com.rti.dds.subscription.RequestedIncompatibleQosStatus;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleLostStatus;
import com.rti.dds.subscription.SampleRejectedStatus;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriptionMatchedStatus;
import com.rti.dds.topic.Topic;

public class Prototype {
    public static void main(String[] args) throws IOException, InterruptedException {
        if(!DDS.init()) {
            throw new IllegalStateException("Unable to init DDS");
        }
        DomainParticipantQos dpQos = new DomainParticipantQos();
        DomainParticipantFactory.get_instance().get_default_participant_qos(dpQos);
        dpQos.transport_builtin.mask = TransportBuiltinKind.UDPv4;
        dpQos.discovery.accept_unknown_peers = true;
        dpQos.discovery.initial_peers.clear();
        dpQos.discovery.initial_peers.add("udpv4://127.0.0.1");
        DomainParticipantFactory.get_instance().set_default_participant_qos(dpQos);
        
//        HighResolutionRelativeTime hrrt = (HighResolutionRelativeTime) HighResolutionRelativeTime.create();
        
        
        final NuObsValue nov2 = (NuObsValue) NuObsValue.create();
        nov2.metric_id = (short) Physio.MDC_PULS_OXIM_PULS_RATE.ordinal();
        AbsoluteTime at = (AbsoluteTime) AbsoluteTime.create();
        Calendar cal = Calendar.getInstance();
        at.century = (byte) (cal.get(Calendar.YEAR)  / 100);
        at.day = (byte) cal.get(Calendar.DAY_OF_MONTH);
        at.hour = (byte) cal.get(Calendar.HOUR_OF_DAY);
        at.minute = (byte) cal.get(Calendar.MINUTE);
        at.month = (byte) cal.get(Calendar.MONTH);
        at.year = (byte) (cal.get(Calendar.YEAR)%100);
        at.second = (byte) cal.get(Calendar.SECOND);
        nov2.time.absolute_time(at);
        nov2.state = MeasurementStatusBits._ms_demo_data;
        nov2.value = 87.0;
        
        final NuObsValue nov1 = (NuObsValue) NuObsValue.create();
        
        DomainParticipant participant1 = DomainParticipantFactory.get_instance().create_participant(0, dpQos, null, StatusKind.STATUS_MASK_NONE);
        NuObsValueTypeSupport.register_type(participant1, NuObsValueTypeSupport.get_type_name());
        Topic topic1 = participant1.create_topic(MDC_ATTR_NU_VAL_OBS.VALUE, NuObsValueTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        NuObsValueDataReader reader1 = (NuObsValueDataReader) participant1.create_datareader(topic1, Subscriber.DATAREADER_QOS_DEFAULT, new DataReaderListener() {

            @Override
            public void on_data_available(DataReader arg0) {
                NuObsValueDataReader r = (NuObsValueDataReader) arg0;
                SampleInfo si = new SampleInfo();
                
                try {
                    r.take_next_sample(nov1, si);

                    while(si.valid_data) {
                        System.out.println(arg0.get_topicdescription().get_name() +":"+nov1);

                        r.take_next_sample(nov1, si);
                    }
                } catch(RETCODE_NO_DATA seriously) {
                    // I expect this ... it's normal ... why use a structured exception here?
                } finally {     
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
            
        }, StatusKind.DATA_AVAILABLE_STATUS);
        
        Thread.sleep(2000);
        
        DomainParticipant participant2 = DomainParticipantFactory.get_instance().create_participant(0, dpQos, null, StatusKind.STATUS_MASK_NONE);
        NuObsValueTypeSupport.register_type(participant2, NuObsValueTypeSupport.get_type_name());
        Topic topic2 = participant2.create_topic(MDC_ATTR_NU_VAL_OBS.VALUE, NuObsValueTypeSupport.get_type_name(), DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        NuObsValueDataWriter writer2 = (NuObsValueDataWriter) participant2.create_datawriter(topic2, Publisher.DATAWRITER_QOS_DEFAULT, new DataWriterListener() {

            @Override
            public void on_application_acknowledgment(DataWriter arg0, AcknowledgmentInfo arg1) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public Object on_data_request(DataWriter arg0, Cookie_t arg1) {
                // TODO Auto-generated method stub
                return null;
            }

            @Override
            public void on_data_return(DataWriter arg0, Object arg1, Cookie_t arg2) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void on_destination_unreachable(DataWriter arg0, InstanceHandle_t arg1, Locator_t arg2) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void on_instance_replaced(DataWriter arg0, InstanceHandle_t arg1) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void on_liveliness_lost(DataWriter arg0, LivelinessLostStatus arg1) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void on_offered_deadline_missed(DataWriter arg0, OfferedDeadlineMissedStatus arg1) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void on_offered_incompatible_qos(DataWriter arg0, OfferedIncompatibleQosStatus arg1) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void on_publication_matched(DataWriter arg0, PublicationMatchedStatus arg1) {
                if(arg1.current_count>0) {
                    ((NuObsValueDataWriter)arg0).write(nov2, InstanceHandle_t.HANDLE_NIL);
                    arg0.flush();
                }
            }

            @Override
            public void on_reliable_reader_activity_changed(DataWriter arg0, ReliableReaderActivityChangedStatus arg1) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void on_reliable_writer_cache_changed(DataWriter arg0, ReliableWriterCacheChangedStatus arg1) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void on_sample_removed(DataWriter arg0, Cookie_t arg1) {
                // TODO Auto-generated method stub
                
            }
            
        }, StatusKind.PUBLICATION_MATCHED_STATUS);
       
        
        System.in.read();
        
        
        
    }
}
