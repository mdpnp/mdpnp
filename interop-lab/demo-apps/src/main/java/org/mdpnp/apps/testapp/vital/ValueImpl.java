package org.mdpnp.apps.testapp.vital;

import com.rti.dds.subscription.SampleInfo;

import ice.Numeric;

public class ValueImpl implements Value {

    private final String universalDeviceIdentifier;
    private final Numeric numeric = (Numeric) Numeric.create();
    private final SampleInfo sampleInfo = new SampleInfo();
    private final Vital parent;
    
    public ValueImpl(String universalDeviceIdentifier, Vital parent) {
        this.universalDeviceIdentifier = universalDeviceIdentifier;
        this.parent = parent;
    }
    
    @Override
    public String getUniversalDeviceIdentifier() {
        return universalDeviceIdentifier;
    }

    @Override
    public Numeric getNumeric() {
        return numeric;
    }
    
    @Override
    public SampleInfo getSampleInfo() {
        return sampleInfo;
    }
    
    @Override
    public Vital getParent() {
        return parent;
    }
    @Override
    public String toString() {
        return "[udi="+universalDeviceIdentifier+",numeric="+numeric+",sampleInfo="+sampleInfo+"]";
    }
    @Override
    public boolean isIgnore() {
        return parent.isIgnoreZero() && 0 == Float.compare(0f, numeric.value);
    }
    
    @Override
    public boolean isAtOrAboveHigh() {
        Float warningHigh = parent.getWarningHigh();
        return isIgnore() || null == warningHigh ? false : Float.compare(numeric.value, warningHigh) >= 0;
    }
    @Override
    public boolean isAtOrBelowLow() {
        Float warningLow = parent.getWarningLow();
        return isIgnore() || null == warningLow ? false : Float.compare(warningLow, numeric.value) >= 0;
    }
    @Override
    public boolean isAtOrOutsideOfBounds() {
        return isAtOrAboveHigh() || isAtOrBelowLow();
    }
    
    @Override
    public boolean isAtOrAboveCriticalHigh() {
        Float criticalHigh = parent.getCriticalHigh();
        return isIgnore() || null == criticalHigh ? false : Float.compare(numeric.value,  criticalHigh) >= 0;
    }
    @Override
    public boolean isAtOrBelowCriticalLow() {
        Float criticalLow = parent.getCriticalLow();
        return isIgnore() || null == criticalLow ? false : Float.compare(criticalLow, numeric.value) >= 0;
    }
    @Override
    public boolean isAtOrOutsideOfCriticalBounds() {
        return isAtOrAboveCriticalHigh() || isAtOrBelowCriticalLow();
    }
    
    @Override
    public long getAgeInMilliseconds() {
        return System.currentTimeMillis() - (sampleInfo.source_timestamp.sec * 1000L + sampleInfo.source_timestamp.nanosec / 1000000L);
    }
}
