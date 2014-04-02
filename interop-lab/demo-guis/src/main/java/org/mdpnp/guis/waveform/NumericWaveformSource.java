package org.mdpnp.guis.waveform;

import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.Time_t;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.ViewStateKind;

public class NumericWaveformSource implements WaveformSource {
    private final ice.NumericDataReader reader;
    private final ice.Numeric keyHolder;
    
    private final SampleInfoSeq sample_info_seq = new SampleInfoSeq();
    private final ice.NumericSeq numeric_seq = new ice.NumericSeq();
    
    public NumericWaveformSource(final ice.NumericDataReader reader, ice.Numeric keyHolder) {
        this.reader = reader;
        this.keyHolder = new ice.Numeric(keyHolder);
    }

    @Override
    public void iterate(WaveformIterator itr) {
        try {
            itr.begin();
            InstanceHandle_t instanceHandle = reader.lookup_instance(keyHolder);
            if(instanceHandle.is_nil()) {
                return;
            }
            
            try {
                
                reader.read_instance(numeric_seq, sample_info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, instanceHandle, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                for(int i = 0; i < sample_info_seq.size(); i++) {
                    Time_t t = ((SampleInfo)sample_info_seq.get(i)).source_timestamp;
                    long tm = t.sec * 1000L + t.nanosec / 1000000L;
                    ice.Numeric n = (ice.Numeric) numeric_seq.get(i);
    //                System.out.println("At " + new Date(tm) + " " + n.value);
                    itr.sample(tm, n.value);
                }
            } catch(RETCODE_NO_DATA noData) {
                
            } finally {
                reader.return_loan(numeric_seq, sample_info_seq);
            }
        } finally {
            itr.end();
        }
    }

    @Override
    public String getIdentifier() {
        return keyHolder.instance_id + "-"+keyHolder.metric_id+"-"+keyHolder.unique_device_identifier;
    }

}
