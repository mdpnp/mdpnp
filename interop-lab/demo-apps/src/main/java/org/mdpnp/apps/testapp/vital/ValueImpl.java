package org.mdpnp.apps.testapp.vital;

import org.mdpnp.devices.AbstractDevice.InstanceHolder;

import com.rti.dds.subscription.SampleInfo;

import ice.AlarmSettingsObjective;
import ice.Numeric;

public class ValueImpl implements Value {

    private final String uniqueDeviceIdentifier;
    private final String metric_id;
    private final int instance_id;
    private final Numeric numeric = (Numeric) Numeric.create();
    private final SampleInfo sampleInfo = new SampleInfo();
    private final Vital parent;

    private long valueMsBelowLow;
    private long valueMsAboveHigh;

    private static final int HISTORY_SAMPLES = 1500;
    private int historyCount = 0;
    private boolean historyWrapped = false;

    private long[] historyTime = new long[HISTORY_SAMPLES];
    private float[] historyValue = new float[HISTORY_SAMPLES];


    private final InstanceHolder<ice.AlarmSettingsObjective> objective = new InstanceHolder<ice.AlarmSettingsObjective>();

    public ValueImpl(String uniqueDeviceIdentifier, String metric_id, int instance_id, Vital parent) {
        objective.data = (AlarmSettingsObjective) ice.AlarmSettingsObjective.create();
        objective.data.metric_id = metric_id;
        objective.data.unique_device_identifier = uniqueDeviceIdentifier;
        this.metric_id = metric_id;
        this.instance_id = instance_id;
        this.uniqueDeviceIdentifier = uniqueDeviceIdentifier;
        this.parent = parent;

    }

    @Override
    public String getUniqueDeviceIdentifier() {
        return uniqueDeviceIdentifier;
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
        return "[udi="+uniqueDeviceIdentifier+",numeric="+numeric+",sampleInfo="+sampleInfo+"]";
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
    public int getHistoryCurrent() {
        return historyCount;
    }

    public void writeCriticalLimitsToDevice(ice.AlarmSettingsObjectiveDataWriter writer) {
//        boolean newObj = false;
        if(null == objective.handle) {
            objective.handle = writer.register_instance(objective.data);
//            newObj = true;
        }
//        if(newObj || getParent().getCriticalLow() != objective.data.lower || getParent().getCriticalHigh() != objective.data.upper) {
            objective.data.lower = getParent().getCriticalLow() == null ? Float.MIN_VALUE : getParent().getCriticalLow();
            objective.data.upper = getParent().getCriticalHigh() == null ? Float.MAX_VALUE : getParent().getCriticalHigh();
            writer.write(objective.data, objective.handle);
//        }
    }

    @Override
    public void unregisterCriticalLimits(ice.AlarmSettingsObjectiveDataWriter writer) {
         if(null != objective.handle) {
             writer.unregister_instance(objective.data, objective.handle);
             objective.handle = null;
         }
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
    public String getMetricId() {
        return metric_id;
    }

    @Override
    public int getInstanceId() {
        return instance_id;
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
