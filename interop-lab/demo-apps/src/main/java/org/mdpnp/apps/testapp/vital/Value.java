package org.mdpnp.apps.testapp.vital;

import com.rti.dds.subscription.SampleInfo;

import ice.Numeric;

public interface Value {
    String getUniqueDeviceIdentifier();
    String getMetricId();
    int getInstanceId();
    Numeric getNumeric();
    SampleInfo getSampleInfo();
    Vital getParent();

    boolean isAtOrAboveHigh();
    boolean isAtOrBelowLow();
    boolean isAtOrOutsideOfBounds();

    boolean isAtOrAboveCriticalHigh();
    boolean isAtOrBelowCriticalLow();
    boolean isAtOrOutsideOfCriticalBounds();

    boolean isAtOrAboveValueMsHigh();
    boolean isAtOrAboveValueMsLow();

    boolean isIgnore();

    long getAgeInMilliseconds();

    void updateFrom(Numeric numeric, SampleInfo sampleInfo);
    void writeCriticalLimitsToDevice(ice.AlarmSettingsObjectiveDataWriter writer);
    void unregisterCriticalLimits(ice.AlarmSettingsObjectiveDataWriter writer);

    long getValueMsBelowLow();
    long getValueMsAboveHigh();

    int getHistoryCount();
    float getHistoryValue(int x);
    long getHistoryTime(int x);
    int getHistoryCurrent();

//    long getRateOfChange();
}
