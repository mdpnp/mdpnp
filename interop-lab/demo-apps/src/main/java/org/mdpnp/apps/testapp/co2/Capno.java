package org.mdpnp.apps.testapp.co2;

import com.rti.dds.subscription.SampleInfo;

public interface Capno {
    ice.SampleArray getSampleArray();
    SampleInfo getSampleInfo();
    CapnoModel getParent();
}
