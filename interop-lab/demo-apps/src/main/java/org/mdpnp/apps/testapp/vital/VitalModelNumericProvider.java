package org.mdpnp.apps.testapp.vital;

import ice.Numeric;
import ice.NumericDataReader;

import org.mdpnp.rtiapi.data.InstanceModel;
import org.mdpnp.rtiapi.data.NumericInstanceModelListener;

import com.rti.dds.subscription.SampleInfo;

public class VitalModelNumericProvider implements NumericInstanceModelListener {
    private final VitalModel model;

    public VitalModelNumericProvider(final VitalModel model) {
        this.model = model;
    }
    
    @Override
    public void instanceAlive(InstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric data, SampleInfo sampleInfo) {
        
    }

    @Override
    public void instanceNotAlive(InstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric keyHolder, SampleInfo sampleInfo) {
        this.model.removeNumeric(keyHolder.unique_device_identifier, keyHolder.metric_id, keyHolder.instance_id);
    }

    @Override
    public void instanceSample(InstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric data, SampleInfo sampleInfo) {
        this.model.updateNumeric(data.unique_device_identifier, data.metric_id, data.instance_id, 
                sampleInfo.source_timestamp.sec * 1000L + sampleInfo.source_timestamp.nanosec / 1000000L, data.value);
    }
    
    
}
