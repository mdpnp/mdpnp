package org.mdpnp.x73.prototype;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.TimeZone;

import ice.DTI_MDC_ATTR_GRP_METRIC_VAL_OBS_Numeric;
import ice.DTI_MDC_ATTR_GRP_METRIC_VAL_OBS_NumericDataReader;
import ice.DTI_MDC_ATTR_GRP_METRIC_VAL_OBS_NumericDataWriter;
import ice.DTI_MDC_ATTR_GRP_METRIC_VAL_OBS_NumericTypeCode;
import ice.DTI_MDC_ATTR_GRP_METRIC_VAL_OBS_NumericTypeSupport;
import ice.NuObsValue;

import org.mdpnp.devices.io.util.Bits;
import org.mdpnp.rti.dds.DDS;
import org.mdpnp.types.polymorphism.DIMT_VMO;
import org.mdpnp.types.polymorphism.DIMT_VMODataReader;
import org.mdpnp.types.polymorphism.DIMT_VMOTypeSupport;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
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

public class RealPrototype implements DataReaderListener {
    
    public static final int get(byte b) {
        int x = (0xFF & b);
        int digit1 = (0xF0 & x)>>4;
        int digit2 = (0x0F & x);
        
        if(digit1 > 9 || digit2 > 9) {
            return (short)(0xFF & b);
        } else {
            return (short)(digit1 * 10 + digit2);
        }
        
    }

    public static final byte put(int s) {
        return (byte)((0xF0 & ((s / 10) << 4)) | (0x0F & (s%10)));
    }
    
    static final int MDC_PULS_OXIM_SAT_O2 = 19384;
    public static void main(String[] args) throws IOException, InterruptedException {
        if(args.length < 1) {
            throw new IllegalArgumentException("Specify a domain_id");
        }
        
        int domainId = Integer.parseInt(args[0]);
        
        if(!DDS.init()) {
            throw new IllegalStateException("Unable to init DDS");
        }
        
        DomainParticipant participant = DomainParticipantFactory.get_instance().create_participant(domainId, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
//        final String typeCodeName = DTI_MDC_ATTR_GRP_METRIC_VAL_OBS_NumericTypeSupport.get_type_name();
        final String typeCodeName = "DTI_MDC_ATTR_GRP_METRIC_VAL_OBS_Numeric";
        
        
        final String topicName = ice.TOPIC_AG_METRIC_VAL_OBS_NU.VALUE;
        
        DTI_MDC_ATTR_GRP_METRIC_VAL_OBS_NumericTypeSupport.register_type(participant, typeCodeName);
        
        Topic topic = participant.create_topic(topicName, typeCodeName, DomainParticipant.TOPIC_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        
        DTI_MDC_ATTR_GRP_METRIC_VAL_OBS_NumericDataWriter writer = (DTI_MDC_ATTR_GRP_METRIC_VAL_OBS_NumericDataWriter) participant.create_datawriter(topic, Publisher.DATAWRITER_QOS_DEFAULT, null, StatusKind.DATA_AVAILABLE_STATUS);
        DTI_MDC_ATTR_GRP_METRIC_VAL_OBS_Numeric nu = (DTI_MDC_ATTR_GRP_METRIC_VAL_OBS_Numeric) DTI_MDC_ATTR_GRP_METRIC_VAL_OBS_Numeric.create();
        
        
        NuObsValue nov = (NuObsValue) NuObsValue.create();
        nov.metric_id = MDC_PULS_OXIM_SAT_O2;
        nov.state = 0;
        nov.unit_code = 0;
        nov.value = 100.0;
        
        nu.nu_obs_value_cmp.values.add(nov);
        
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        
        for(int i = 0; i < 120; i++) {
            cal.setTimeInMillis(System.currentTimeMillis());
            int decimalCentury = cal.get(Calendar.YEAR)/100+1;
            int bcdCentury = put(decimalCentury);
//            System.out.println("decimalCentury:"+decimalCentury+ "  bcdCentury:"+Integer.toHexString(nu.absolute_timestamp.century));
            nu.absolute_timestamp.century = (byte) bcdCentury;
            
            nu.absolute_timestamp.year = (byte)(0xFF & put(cal.get(Calendar.YEAR)%100));
            nu.absolute_timestamp.month = put(cal.get(Calendar.MONTH)+1);
            nu.absolute_timestamp.day = put(cal.get(Calendar.DAY_OF_MONTH));
            nu.absolute_timestamp.hour = put(cal.get(Calendar.HOUR_OF_DAY));
            nu.absolute_timestamp.minute = put(cal.get(Calendar.MINUTE));
            nu.absolute_timestamp.second = put(cal.get(Calendar.SECOND));
            nu.absolute_timestamp.sec_fractions = put(cal.get(Calendar.MILLISECOND)/125);
            System.out.println(nu);
            writer.write(nu, InstanceHandle_t.HANDLE_NIL);
            

            
            Thread.sleep(500L);
        }
        
        System.out.println("ALL DONE");
        participant.delete_datawriter(writer);
        writer = null;
        
//        DataReader reader = participant.create_datareader(topic, Subscriber.DATAREADER_QOS_DEFAULT, new RealPrototype(), StatusKind.DATA_AVAILABLE_STATUS);
//        System.out.println("I'm Listening:");
//        System.in.read();
        
//        participant.delete_datareader(reader);
//        reader = null;
        
        participant.delete_topic(topic);
        
        topic = null;
        
        DTI_MDC_ATTR_GRP_METRIC_VAL_OBS_NumericTypeSupport.unregister_type(participant, typeCodeName);
        
        DomainParticipantFactory.get_instance().delete_participant(participant);
        
        participant = null;
        DomainParticipantFactory.finalize_instance();

    }

    @Override
    public void on_data_available(DataReader arg0) {
        DTI_MDC_ATTR_GRP_METRIC_VAL_OBS_NumericDataReader reader = (DTI_MDC_ATTR_GRP_METRIC_VAL_OBS_NumericDataReader) arg0;

        SampleInfo si = new SampleInfo();
        DTI_MDC_ATTR_GRP_METRIC_VAL_OBS_Numeric nu = (DTI_MDC_ATTR_GRP_METRIC_VAL_OBS_Numeric) DTI_MDC_ATTR_GRP_METRIC_VAL_OBS_Numeric.create();
        
        try {
            reader.take_next_sample(nu, si);

            while(si.valid_data) {
                System.out.println(nu);
                reader.take_next_sample(nu, si);
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
}
