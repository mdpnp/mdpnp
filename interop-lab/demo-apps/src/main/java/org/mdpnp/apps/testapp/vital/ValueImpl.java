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
        return isIgnore() ? false : Float.compare(numeric.value, parent.getWarningHigh()) >= 0;
    }
    @Override
    public boolean isAtOrBelowLow() {
        return isIgnore() ? false : Float.compare(parent.getWarningLow(), numeric.value) >= 0;
    }
    @Override
    public boolean isAtOrOutsideOfBounds() {
        return isAtOrAboveHigh() || isAtOrBelowLow();
    }
    
    @Override
    public boolean isAtOrAboveCriticalHigh() {
        return isIgnore() ? false : Float.compare(numeric.value,  parent.getCriticalHigh()) >= 0;
    }
    @Override
    public boolean isAtOrBelowCriticalLow() {
        return isIgnore() ? false : Float.compare(parent.getCriticalLow(), numeric.value) >= 0;
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
