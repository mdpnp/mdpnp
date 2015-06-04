package org.mdpnp.apps.fxbeans;

import com.rti.dds.infrastructure.Copyable;
import com.rti.dds.infrastructure.InstanceHandle_t;
import com.rti.dds.subscription.SampleInfo;

public interface Updatable<D extends Copyable> {
    void update(D data, SampleInfo sampleInfo);
    InstanceHandle_t getHandle();
}
