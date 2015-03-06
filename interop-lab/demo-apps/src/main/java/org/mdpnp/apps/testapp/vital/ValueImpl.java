/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.apps.testapp.vital;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import ice.Numeric;

import com.rti.dds.subscription.SampleInfo;

/**
 * @author Jeff Plourde
 *
 */
public class ValueImpl implements Value {

    private final StringProperty uniqueDeviceIdentifier = new SimpleStringProperty(this, "uniqueDeviceIdentifier", "");
    private final StringProperty metricId = new SimpleStringProperty(this, "metricId", "");
    private final IntegerProperty instanceId = new SimpleIntegerProperty(this, "instanceId", 0);
    private final Numeric numeric = (Numeric) Numeric.create();
    private final SampleInfo sampleInfo = new SampleInfo();
    private final Vital parent;

    private LongProperty valueMsBelowLow = new SimpleLongProperty(this, "valueMsBelowLow", 0L);
    private LongProperty valueMsAboveHigh = new SimpleLongProperty(this, "valueMsAboveHigh", 0L);

    private static final int HISTORY_SAMPLES = 1500;
    private int historyCount = 0;
    private boolean historyWrapped = false;

    private long[] historyTime = new long[HISTORY_SAMPLES];
    private float[] historyValue = new float[HISTORY_SAMPLES];

    public ValueImpl(String uniqueDeviceIdentifier, String metricId, int instanceId, Vital parent) {

        this.metricId.set(metricId);
        this.instanceId.set(instanceId);
        this.uniqueDeviceIdentifier.set(uniqueDeviceIdentifier);
        this.parent = parent;

    }

    @Override
    public String getUniqueDeviceIdentifier() {
        return uniqueDeviceIdentifier.get();
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
        return "[udi=" + uniqueDeviceIdentifier + ",numeric=" + numeric + ",sampleInfo=" + sampleInfo + "]";
    }

    @Override
    public boolean isIgnore() {
        return parent.isIgnoreZero() && (0 == Float.compare(0f, numeric.value) || Float.isNaN(numeric.value));
    }

    @Override
    public boolean isAtOrAboveHigh() {
        Double warningHigh = parent.getWarningHigh();
        return (isIgnore() || null == warningHigh) ? false : (Double.compare(numeric.value, warningHigh) >= 0);
    }

    @Override
    public boolean isAtOrBelowLow() {
        Double warningLow = parent.getWarningLow();
        return (isIgnore() || null == warningLow) ? false : (Double.compare(warningLow, numeric.value) >= 0);
    }

    @Override
    public boolean isAtOrOutsideOfBounds() {
        return isAtOrAboveHigh() || isAtOrBelowLow();
    }

    @Override
    public boolean isAtOrAboveCriticalHigh() {
        Double criticalHigh = parent.getCriticalHigh();
        return (isIgnore() || null == criticalHigh) ? false : (Double.compare(numeric.value, criticalHigh) >= 0);
    }

    @Override
    public boolean isAtOrBelowCriticalLow() {
        Double criticalLow = parent.getCriticalLow();
        return (isIgnore() || null == criticalLow) ? false : (Double.compare(criticalLow, numeric.value) >= 0);
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
        return valueMsBelowLow.get();
    }

    public long getValueMsAboveHigh() {
        return valueMsAboveHigh.get();
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
        boolean isBelow = isAtOrBelowLow();
        float isValue = this.numeric.value;
        long isTime = this.sampleInfo.source_timestamp.sec * 1000L + this.sampleInfo.source_timestamp.nanosec / 1000000L;

        // store for history
        historyTime[historyCount] = isTime;
        historyValue[historyCount] = isValue;

        if (++historyCount >= HISTORY_SAMPLES) {
            historyWrapped = true;
            historyCount = 0;
        }

        // Integrate
        if (isAbove) {
            if (wasAbove) {
                // persisting above the bound ...
                valueMsAboveHigh.add((long) ((isTime - wasTime) * (wasValue - parent.getWarningHigh())));
            } else {
                // above the bound but it wasn't previously ... so restart at
                // zero
                valueMsAboveHigh.set(0L);
            }
        } else {
            valueMsAboveHigh.set(0L);
        }

        if (isBelow) {
            if (wasBelow) {
                // persisting below the bound ...
                valueMsBelowLow.add((long) ((isTime - wasTime) * (parent.getWarningLow() - wasValue)));
            } else {
                valueMsBelowLow.set(0L);
            }
        } else {
            valueMsBelowLow.set(0L);
        }

    }

    @Override
    public String getMetricId() {
        return metricId.get();
    }

    @Override
    public int getInstanceId() {
        return instanceId.get();
    }

    @Override
    public boolean isAtOrAboveValueMsHigh() {
        Long warningHigh = parent.getValueMsWarningHigh();
        return (isIgnore() || null == warningHigh) ? false : Long.compare(valueMsAboveHigh.get(), warningHigh) >= 0;
    }

    @Override
    public boolean isAtOrAboveValueMsLow() {
        Long warningLow = parent.getValueMsWarningLow();
        return (isIgnore() || null == warningLow) ? false : (Long.compare(warningLow, valueMsBelowLow.get()) >= 0);
    }

    @Override
    public ReadOnlyStringProperty uniqueDeviceIdentifierProperty() {
        return uniqueDeviceIdentifier;
    }

    @Override
    public ReadOnlyStringProperty metricIdProperty() {
        return metricId;
    }

    @Override
    public ReadOnlyIntegerProperty instanceIdProperty() {
        return instanceId;
    }

    @Override
    public ReadOnlyBooleanProperty atOrAboveHighProperty() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReadOnlyBooleanProperty atOrBelowLowProperty() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReadOnlyBooleanProperty atOrOutsideOfBoundsProperty() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReadOnlyBooleanProperty atOrAboveCriticalHighProperty() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReadOnlyBooleanProperty atOrBelowCriticalLowProperty() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReadOnlyBooleanProperty atOrOutsideOfCriticalBoundsProperty() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReadOnlyBooleanProperty atOrAboveValueMsHighProperty() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReadOnlyBooleanProperty atOrAboveValueMsLowProperty() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReadOnlyBooleanProperty ignoreProperty() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReadOnlyLongProperty ageInMillisecondsProperty() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReadOnlyLongProperty valueMsBelowLowProperty() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReadOnlyLongProperty valueMsAboveHighProperty() {
        // TODO Auto-generated method stub
        return null;
    }
}
