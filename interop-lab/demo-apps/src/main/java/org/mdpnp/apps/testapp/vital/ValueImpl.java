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
}
