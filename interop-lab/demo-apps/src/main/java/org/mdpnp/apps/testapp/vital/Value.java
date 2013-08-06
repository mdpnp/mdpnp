package org.mdpnp.apps.testapp.vital;

import com.rti.dds.subscription.SampleInfo;

import ice.Numeric;

public interface Value {
    String getUniversalDeviceIdentifier();
    Numeric getNumeric();
    SampleInfo getSampleInfo();
    Vital getParent();
    boolean isAtOrAboveHigh();
    boolean isAtOrBelowLow();
    boolean isAtOrOutsideOfBounds();
    
    boolean isAtOrAboveCriticalHigh();
    boolean isAtOrBelowCriticalLow();
    boolean isAtOrOutsideOfCriticalBounds();
    
    boolean isIgnore();
    
    long getAgeInMilliseconds();
}
