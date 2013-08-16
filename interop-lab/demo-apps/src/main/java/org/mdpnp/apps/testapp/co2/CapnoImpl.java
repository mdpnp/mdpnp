package org.mdpnp.apps.testapp.co2;

import com.rti.dds.subscription.SampleInfo;

import ice.SampleArray;

public class CapnoImpl implements Capno {
    private final CapnoModel parent;
    private final ice.SampleArray sampleArray = new ice.SampleArray();
    private final SampleInfo sampleInfo = new SampleInfo();
    
    public CapnoImpl(CapnoModel parent, ice.SampleArray sampleArray, SampleInfo sampleInfo) {
        this.parent = parent;
        this.sampleArray.copy_from(sampleArray);
        this.sampleInfo.copy_from(sampleInfo);
    }
    
    @Override
    public SampleInfo getSampleInfo() {
        return sampleInfo;
    }
    
    @Override
    public SampleArray getSampleArray() {
        return sampleArray;
    }
    
    @Override
    public CapnoModel getParent() {
        return parent;
    }
    
    @Override
    public String toString() {
        return sampleArray.universal_device_identifier;
    }
}
