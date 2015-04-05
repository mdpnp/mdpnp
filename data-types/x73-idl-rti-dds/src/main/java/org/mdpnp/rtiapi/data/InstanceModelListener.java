package org.mdpnp.rtiapi.data;

import com.rti.dds.infrastructure.Copyable;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.SampleInfo;

public interface InstanceModelListener<D extends Copyable, R extends DataReader> {
    void instanceAlive(ReaderInstanceModel<D,R> model, R reader, D data, SampleInfo sampleInfo);
    void instanceNotAlive(ReaderInstanceModel<D,R> model, R reader, D keyHolder, SampleInfo sampleInfo);
    void instanceSample(ReaderInstanceModel<D,R> model, R reader, D data, SampleInfo sampleInfo);
}
