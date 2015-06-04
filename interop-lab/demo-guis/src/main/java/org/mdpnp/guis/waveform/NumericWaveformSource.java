package org.mdpnp.guis.waveform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_ALREADY_DELETED;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.ViewStateKind;

public class NumericWaveformSource extends AbstractDdsWaveformSource<ice.NumericDataReader, ice.Numeric, ice.NumericSeq> {
    private final static Logger log = LoggerFactory.getLogger(NumericWaveformSource.class);
    
    public NumericWaveformSource(final ice.NumericDataReader reader, ice.Numeric keyHolder) {
        super(reader, keyHolder, ice.Numeric.class, ice.NumericSeq.class);
    }

    public NumericWaveformSource(final ice.NumericDataReader reader, InstanceHandle_t instanceHandle) {
        super(reader, instanceHandle, ice.Numeric.class, ice.NumericSeq.class);
    }
    
    @Override
    public void iterate(WaveformIterator itr) {
        try {
            itr.begin();

            if(null == instanceHandle || instanceHandle.is_nil()) {
                log.warn("Tried to iterate a null or nil instance ");
                return;
            }
            
            SampleInfoSeq sample_info_seq = this.sample_info_seq.get();
            ice.NumericSeq data_seq = this.data_seq.get();
            
            try {
                
                reader.read_instance(data_seq, sample_info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, instanceHandle, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                for(int i = 0; i < sample_info_seq.size(); i++) {
                    ice.Numeric n = (ice.Numeric) data_seq.get(i);
                    ice.Time_t t = n.presentation_time;
                    long tm = t.sec * 1000L + t.nanosec / 1000000L;
    //                System.out.println("At " + new Date(tm) + " " + n.value);
                    itr.sample(tm, n.value);
                }
            } catch(RETCODE_NO_DATA noData) {
                
            } finally {
                reader.return_loan(data_seq, sample_info_seq);
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
