package org.mdpnp.apps.testapp.vital;

import com.rti.dds.subscription.SampleInfo;

import ice.Numeric;

public class ValueImpl implements Value {

    private final String universalDeviceIdentifier;
    private final int name;
    private final Numeric numeric = (Numeric) Numeric.create();
    private final SampleInfo sampleInfo = new SampleInfo();
    private final Vital parent;
    
    private long valueMsBelowLow;
    private long valueMsAboveHigh;
    
    private static final int HISTORY_SAMPLES = 100;
    private int historyCount = 0;
    private boolean historyWrapped = false;
    
    private long[] historyTime = new long[HISTORY_SAMPLES];
    private float[] historyValue = new float[HISTORY_SAMPLES];
    
    public ValueImpl(String universalDeviceIdentifier, int name, Vital parent) {
        this.name = name;
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
        return parent.isIgnoreZero() && (0 == Float.compare(0f, numeric.value) || Float.isNaN(numeric.value));
    }
    
    @Override
    public boolean isAtOrAboveHigh() {
        Float warningHigh = parent.getWarningHigh();
        return (isIgnore() || null == warningHigh) ? false : (Float.compare(numeric.value, warningHigh) >= 0);
    }
    @Override
    public boolean isAtOrBelowLow() {
        Float warningLow = parent.getWarningLow();
        return (isIgnore() || null == warningLow) ? false : (Float.compare(warningLow, numeric.value) >= 0);
    }
    @Override
    public boolean isAtOrOutsideOfBounds() {
        return isAtOrAboveHigh() || isAtOrBelowLow();
    }
    
    @Override
    public boolean isAtOrAboveCriticalHigh() {
        Float criticalHigh = parent.getCriticalHigh();
        return (isIgnore() || null == criticalHigh) ? false : (Float.compare(numeric.value,  criticalHigh) >= 0);
    }
    @Override
    public boolean isAtOrBelowCriticalLow() {
        Float criticalLow = parent.getCriticalLow();
        return (isIgnore() || null == criticalLow) ? false : (Float.compare(criticalLow, numeric.value) >= 0);
    }
    @Override
    public boolean isAtOrOutsideOfCriticalBounds() {
        return isAtOrAboveCriticalHigh() || isAtOrBelowCriticalLow();
    }
    
    @Override
    public long getAgeInMilliseconds() {
        return System.currentTimeMillis() - (sampleInfo.source_timestamp.sec * 1000L + sampleInfo.source_timestamp.nanosec / 1000000L);
    }
    
    @Override
    public long getValueMsBelowLow() {
        return valueMsBelowLow;
    }
    
    public long getValueMsAboveHigh() {
        return valueMsAboveHigh;
    }
    
    @Override
    public int getHistoryCount() {
        return historyWrapped ? HISTORY_SAMPLES : historyCount;
    }
    
    @Override
    public long getHistoryTime(int x) {
        return historyTime[x];
    }
    
    @Override
    public float getHistoryValue(int x) {
        return historyValue[x];
    }
    
    @Override
    public void updateFrom(Numeric numeric, SampleInfo sampleInfo) {
        // characterize the previous sample
        boolean wasBelow = isAtOrBelowLow();
        boolean wasAbove = isAtOrAboveHigh();
        float wasValue = this.numeric.value;
        long wasTime = this.sampleInfo.source_timestamp.sec * 1000L + this.sampleInfo.source_timestamp.nanosec / 1000000L;

        // update the sample info
        this.numeric.copy_from(numeric);
        this.sampleInfo.copy_from(sampleInfo);

        // characterize the new sample
        boolean isAbove = isAtOrAboveHigh();
        boolean isBelow =  isAtOrBelowLow();
        float isValue = this.numeric.value;
        long isTime = this.sampleInfo.source_timestamp.sec * 1000L + this.sampleInfo.source_timestamp.nanosec / 1000000L;
        
        // store for history
        historyTime[historyCount] = isTime;
        historyValue[historyCount] = isValue;
        
        if(++historyCount>=HISTORY_SAMPLES) {
            historyWrapped = true;
            historyCount = 0;
        }
        
        // Integrate
        if(isAbove) {
            if(wasAbove) {
                // persisting above the bound ... 
                valueMsAboveHigh += (long) ((isTime - wasTime) * (wasValue - parent.getWarningHigh()));
            } else {
                // above the bound but it wasn't previously ... so restart at zero
                valueMsAboveHigh = 0L;
            }
        } else {
            valueMsAboveHigh = 0L;
        }
        
        if(isBelow) {
            if(wasBelow) {
                // persisting below the bound ...
                valueMsBelowLow += (long)((isTime - wasTime) * (parent.getWarningLow() - wasValue));
            } else {
                valueMsBelowLow = 0L;
            }
        } else {
            valueMsBelowLow = 0L;
        }
        

    }
    @Override
    public int getName() {
        return name;
    }

    @Override
    public boolean isAtOrAboveValueMsHigh() {
        Long warningHigh = parent.getValueMsWarningHigh();
        return (isIgnore() || null == warningHigh) ? false : Long.compare(valueMsAboveHigh, warningHigh) >= 0;
    }

    @Override
    public boolean isAtOrAboveValueMsLow() {
        Long warningLow = parent.getValueMsWarningLow();
        return (isIgnore() || null == warningLow) ? false : (Long.compare(warningLow, valueMsBelowLow) >= 0);
    }
}
