package org.mdpnp.guis.waveform;

import ice.SampleArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.infrastructure.RETCODE_ALREADY_DELETED;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.ViewStateKind;

public class SampleArrayWaveformSource extends AbstractDdsWaveformSource<ice.SampleArrayDataReader, ice.SampleArray, ice.SampleArraySeq> implements WaveformSource {
    private static final Logger log = LoggerFactory.getLogger(SampleArrayWaveformSource.class);

    public SampleArrayWaveformSource(final ice.SampleArrayDataReader reader, InstanceHandle_t instanceHandle) {
        super(reader, instanceHandle, ice.SampleArray.class, ice.SampleArraySeq.class);
    }
    
    public SampleArrayWaveformSource(final ice.SampleArrayDataReader reader, ice.SampleArray keyHolder) {
        super(reader, keyHolder, ice.SampleArray.class, ice.SampleArraySeq.class);
        log.debug("Created a SampleArrayWaveformSource for " + keyHolder.unique_device_identifier + " " + keyHolder.metric_id + " " + keyHolder.instance_id);
    }
    

    @Override
    public void iterate(final WaveformIterator itr) {
        try {
            itr.begin();

            if(null == instanceHandle || instanceHandle.is_nil()) {
                log.warn("Tried to iterate a null or nil instance ");
                return;
            }
            
            SampleInfoSeq sample_info_seq = this.sample_info_seq.get();
            ice.SampleArraySeq sample_array_seq = this.data_seq.get();
            try {
                reader.read_instance(sample_array_seq, sample_info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, instanceHandle, SampleStateKind.ANY_SAMPLE_STATE, ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ANY_INSTANCE_STATE);
                for(int i = 0; i < sample_info_seq.size(); i++) {
                    SampleInfo si = (SampleInfo) sample_info_seq.get(i);
                    
                    ice.SampleArray sampleArray = (SampleArray) sample_array_seq.get(i);
                    ice.Time_t t = sampleArray.presentation_time;
                    long baseTime = t.sec * 1000L + t.nanosec / 1000000L;
                    
                    final int sz = sampleArray.values.userData.size();
//                    log.debug(sz + " samples " + keyHolder.unique_device_identifier + " " + keyHolder.metric_id + " " + keyHolder.instance_id);
                    if(si.valid_data) {
                        if(0 < sampleArray.frequency) {
                            int msPerSample = 1000 / sampleArray.frequency;
                            for(int j = 0; j < sz; j++) {
                                long tm = baseTime - (sz-j) * msPerSample;
                                float value = sampleArray.values.userData.getFloat(j);
                                itr.sample(tm, value);
                            }
                        } else {
                            log.warn("Invalid frequency " + sampleArray.frequency + " for " + sampleArray.unique_device_identifier + " " + sampleArray.metric_id + " " + sampleArray.instance_id);
                        }
                    } else {
                        // instance lifecycle event with no attached data.
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
