package org.mdpnp.guis.waveform;

import ice.SampleArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.Time_t;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.ViewStateKind;

public class SampleArrayWaveformSource implements WaveformSource {
    private final ice.SampleArrayDataReader reader;
    private final ice.SampleArray keyHolder;
    
    private final SampleInfoSeq sample_info_seq = new SampleInfoSeq();
    private final ice.SampleArraySeq sample_array_seq = new ice.SampleArraySeq();
    
    public SampleArrayWaveformSource(final ice.SampleArrayDataReader reader, ice.SampleArray keyHolder) {
        this.reader = reader;
        this.keyHolder = new ice.SampleArray(keyHolder);
        log.debug("Created a SampleArrayWaveformSource for " + keyHolder.unique_device_identifier + " " + keyHolder.metric_id + " " + keyHolder.instance_id);
    }
    
    private static final Logger log = LoggerFactory.getLogger(SampleArrayWaveformSource.class);

    @Override
    public void iterate(WaveformIterator itr) {
        try {
            itr.begin();
            InstanceHandle_t instanceHandle = reader.lookup_instance(keyHolder);
            if(instanceHandle.is_nil()) {
                return;
            }
            
            try {
                reader.read_instance(sample_array_seq, sample_info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, instanceHandle, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                for(int i = 0; i < sample_info_seq.size(); i++) {
                    Time_t t = ((SampleInfo)sample_info_seq.get(i)).source_timestamp;
                    long baseTime = t.sec * 1000L + t.nanosec / 1000000L;
                    ice.SampleArray sampleArray = (SampleArray) sample_array_seq.get(i);
    
                    final int sz = sampleArray.values.userData.size();
//                    log.debug(sz + " samples " + keyHolder.unique_device_identifier + " " + keyHolder.metric_id + " " + keyHolder.instance_id);
                    for(int j = 0; j < sz; j++) {
                        long tm = baseTime - (sz-j) * sampleArray.millisecondsPerSample;
                        float value = sampleArray.values.userData.getFloat(j);
                        itr.sample(tm, value);
                    }
                }
            } catch(RETCODE_NO_DATA noData) {
                
            } finally {
                reader.return_loan(sample_array_seq, sample_info_seq);
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
