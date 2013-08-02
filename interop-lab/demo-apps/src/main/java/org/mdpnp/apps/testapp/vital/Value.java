package org.mdpnp.apps.testapp.vital;

import com.rti.dds.subscription.SampleInfo;

import ice.Numeric;

public interface Value {
    String getUniversalDeviceIdentifier();
    Numeric getNumeric();
    SampleInfo getSampleInfo();
    Vital getParent();
}
