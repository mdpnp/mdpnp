package org.mdpnp.rtiapi.data;

import com.rti.dds.infrastructure.Copyable;
import com.rti.dds.subscription.DataReaderImpl;
import com.rti.dds.subscription.SampleInfo;

public interface InstanceModelListener<D extends Copyable, R extends DataReaderImpl> {
    void instanceAlive(InstanceModel<D,R> model, R reader, D data, SampleInfo sampleInfo);
    void instanceNotAlive(InstanceModel<D,R> model, R reader, D keyHolder, SampleInfo sampleInfo);
    void instanceSample(InstanceModel<D,R> model, R reader, D data, SampleInfo sampleInfo);
}
