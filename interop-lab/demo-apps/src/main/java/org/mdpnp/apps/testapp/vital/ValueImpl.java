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

import java.util.Date;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.testapp.Device;
import org.mdpnp.apps.testapp.DeviceListModel;

/**
 * @author Jeff Plourde
 *
 */
public class ValueImpl implements Value {
    private final FloatProperty value = new SimpleFloatProperty(this, "value", 0f);
    private final Device device;
    private final StringProperty uniqueDeviceIdentifier = new SimpleStringProperty(this, "uniqueDeviceIdentifier", "");
    private final StringProperty metricId = new SimpleStringProperty(this, "metricId", "");
    private final IntegerProperty instanceId = new SimpleIntegerProperty(this, "instanceId", 0);
    private final ObjectProperty<Date> timestamp = new SimpleObjectProperty<>(this, "timestamp", new Date(0L));
    private final Vital parent;
    private final NumericFx numeric;

//    private final LongProperty valueMsBelowLow = new SimpleLongProperty(this, "valueMsBelowLow", 0L);
//    private final LongProperty valueMsAboveHigh = new SimpleLongProperty(this, "valueMsAboveHigh", 0L);
    
    private final BooleanProperty ignore = new SimpleBooleanProperty(this, "ignore", false); 
    private final BooleanProperty atOrAboveHigh = new SimpleBooleanProperty(this, "atOrAboveHigh", false);
    private final BooleanProperty atOrBelowLow = new SimpleBooleanProperty(this, "atOrBelowLow", false);
    private final BooleanProperty atOrOutsideBounds = new SimpleBooleanProperty(this, "atOrOutsideBounds", false);
    private final BooleanProperty atOrAboveCriticalHigh = new SimpleBooleanProperty(this, "atOrAboveCriticalHigh", false);
    private final BooleanProperty atOrBelowCriticalLow  = new SimpleBooleanProperty(this, "atOrBelowCriticalLow", false);
    private final BooleanProperty atOrOutsideCriticalBounds = new SimpleBooleanProperty(this, "atOrOutsideCriticalBounds", false);
//    private final BooleanProperty atOrAboveValueMsLow = new SimpleBooleanProperty(this, "atOrAboveValueMsLow", false);
//    private final BooleanProperty atOrAboveValueMsHigh = new SimpleBooleanProperty(this, "atOrAboveValueMsHigh", false);

    public ValueImpl(final NumericFx numeric, Vital parent) {
        this.uniqueDeviceIdentifier.bind(numeric.unique_device_identifierProperty());
        this.metricId.bind(numeric.metric_idProperty());
        this.instanceId.bind(numeric.instance_idProperty());
        this.value.bind(numeric.valueProperty());
        this.timestamp.bind(numeric.source_timestampProperty());
        this.parent = parent;
        this.numeric = numeric;
        if(null != parent) {
            VitalModel model = parent.getParent();
            if(null != model) {
                DeviceListModel deviceListModel = model.getDeviceListModel();
                this.device = deviceListModel.getByUniqueDeviceIdentifier(numeric.getUnique_device_identifier());
            } else {
                this.device = null;
            }
        } else {
            this.device = null;
        }
        ignore.bind(parent.ignoreZeroProperty().and(value.isEqualTo(0.0, 0.00001).or(value.isEqualTo(Double.NaN, 0.0))));
        atOrAboveHigh.bind(ignore.not().and(parent.warningHighProperty().isNotNull()).and(value.greaterThanOrEqualTo(new ConcreteDoubleProperty(parent.warningHighProperty(), Double.POSITIVE_INFINITY))));
        atOrBelowLow.bind(ignore.not().and(parent.warningLowProperty().isNotNull()).and(value.lessThanOrEqualTo(new ConcreteDoubleProperty(parent.warningLowProperty(), Double.NEGATIVE_INFINITY))));
        atOrOutsideBounds.bind(atOrBelowLow.or(atOrAboveHigh));
        atOrAboveCriticalHigh.bind(ignore.not().and(parent.criticalHighProperty().isNotNull()).and(value.greaterThanOrEqualTo(new ConcreteDoubleProperty(parent.criticalHighProperty(), Double.POSITIVE_INFINITY))));
        atOrBelowCriticalLow.bind(ignore.not().and(parent.criticalLowProperty().isNotNull()).and(value.lessThanOrEqualTo(new ConcreteDoubleProperty(parent.criticalLowProperty(), Double.NEGATIVE_INFINITY) {
            public double get() {
                double d = super.get();
                return d;
            }
        })));
        atOrOutsideBounds.bind(atOrBelowCriticalLow.or(atOrAboveCriticalHigh));
//        atOrAboveValueMsHigh.bind(ignore.not().and(parent.valueMsWarningHighProperty().isNotNull()).and(valueMsAboveHigh.greaterThan(new ConcreteLongProperty(parent.valueMsWarningHighProperty(), Long.MAX_VALUE))));
//        atOrAboveValueMsLow.bind(ignore.not().and(parent.valueMsWarningLowProperty().isNotNull()).and(valueMsBelowLow.greaterThan(new ConcreteLongProperty(parent.valueMsWarningLowProperty(), Long.MIN_VALUE))));
    }
    
    @Override
    public Device getDevice() {
        return device;
    }

    @Override
    public String getUniqueDeviceIdentifier() {
        return this.uniqueDeviceIdentifier.get();
    }

    @Override
    public Vital getParent() {
        return parent;
    }
    
    @Override
    public NumericFx getNumeric() {
        return numeric;
    }

    @Override
    public String toString() {
        return "[udi=" + getUniqueDeviceIdentifier() + ",value=" + getValue() + "]";
    }

    @Override
    public boolean isIgnore() {
        return ignore.get();
    }

    @Override
    public boolean isAtOrAboveHigh() {
        return atOrAboveHigh.get();
    }

    @Override
    public boolean isAtOrBelowLow() {
        return atOrBelowLow.get();
    }

    @Override
    public boolean isAtOrOutsideOfBounds() {
        return atOrOutsideBounds.get();
    }

    @Override
    public boolean isAtOrAboveCriticalHigh() {
        return atOrAboveCriticalHigh.get();
    }

    @Override
    public boolean isAtOrBelowCriticalLow() {
        return atOrBelowCriticalLow.get();
    }

    @Override
    public boolean isAtOrOutsideOfCriticalBounds() {
        return atOrOutsideCriticalBounds.get();
    }

    @Override
    public long getAgeInMilliseconds() {
        return System.currentTimeMillis() - timestamp.get().getTime();
    }

//    @Override
//    public long getValueMsBelowLow() {
//        return valueMsBelowLow.get();
//    }
//
//    public long getValueMsAboveHigh() {
//        return valueMsAboveHigh.get();
//    }

//    @Override
//    public void updateFrom(final long timestamp, float value) {
//        if(!Platform.isFxApplicationThread()) {
//            throw new IllegalThreadStateException("ValueImpl must be updated on the FX Application thread");
//        }
//        
//        // characterize the previous sample
//        boolean wasBelow = isAtOrBelowLow();
//        boolean wasAbove = isAtOrAboveHigh();
//        float wasValue = this.value.get();
//        long wasTime = this.timestamp.get();
//
//        this.value.set(value);
//        this.timestamp.set(timestamp);
//
//        // characterize the new sample
//        boolean isAbove = isAtOrAboveHigh();
//        boolean isBelow = isAtOrBelowLow();
//
//        // Integrate
//        if (isAbove) {
//            if (wasAbove) {
//                // persisting above the bound ...
//                valueMsAboveHigh.add((long) ((this.timestamp.get() - wasTime) * (wasValue - parent.getWarningHigh())));
//            } else {
//                // above the bound but it wasn't previously ... so restart at
//                // zero
//                valueMsAboveHigh.set(0L);
//            }
//        } else {
//            valueMsAboveHigh.set(0L);
//        }
//
//        if (isBelow) {
//            if (wasBelow) {
//                // persisting below the bound ...
//                valueMsBelowLow.add((long) ((this.timestamp.get() - wasTime) * (parent.getWarningLow() - wasValue)));
//            } else {
//                valueMsBelowLow.set(0L);
//            }
//        } else {
//            valueMsBelowLow.set(0L);
//        }
//
//    }

    @Override
    public String getMetricId() {
        return metricId.get();
    }

    @Override
    public int getInstanceId() {
        return instanceId.get();
    }

//    @Override
//    public boolean isAtOrAboveValueMsHigh() {
//        return atOrAboveValueMsHigh.get();
//    }
//
//    @Override
//    public boolean isAtOrAboveValueMsLow() {
//        return atOrAboveValueMsLow.get();
//    }

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
        return atOrAboveHigh;
    }

    @Override
    public ReadOnlyBooleanProperty atOrBelowLowProperty() {
        return atOrBelowLow;
    }

    @Override
    public ReadOnlyBooleanProperty atOrOutsideOfBoundsProperty() {
        return atOrOutsideBounds;
    }

    @Override
    public ReadOnlyBooleanProperty atOrAboveCriticalHighProperty() {
        return atOrAboveCriticalHigh;
    }

    @Override
    public ReadOnlyBooleanProperty atOrBelowCriticalLowProperty() {
        return atOrBelowCriticalLow;
    }

    @Override
    public ReadOnlyBooleanProperty atOrOutsideOfCriticalBoundsProperty() {
        return atOrOutsideCriticalBounds;
    }

//    @Override
//    public ReadOnlyBooleanProperty atOrAboveValueMsHighProperty() {
//        return atOrAboveValueMsHigh;
//    }
//
//    @Override
//    public ReadOnlyBooleanProperty atOrAboveValueMsLowProperty() {
//        return atOrAboveValueMsLow;
//    }

    @Override
    public ReadOnlyBooleanProperty ignoreProperty() {
        return ignore;
    }

    @Override
    public ReadOnlyLongProperty ageInMillisecondsProperty() {
        // TODO fixme
        throw new UnsupportedOperationException();
    }

//    @Override
//    public ReadOnlyLongProperty valueMsBelowLowProperty() {
//        return valueMsBelowLow;
//    }
//
//    @Override
//    public ReadOnlyLongProperty valueMsAboveHighProperty() {
//        return valueMsAboveHigh;
//    }
    public FloatProperty valueProperty() {
        return value;
    }
    public float getValue() {
        return this.value.get();
    }
    public void setValue(float value) {
        this.value.set(value);
    }
    
    @Override
    public ReadOnlyObjectProperty<Date> timestampProperty() {
        return timestamp;
    }
    @Override
    public Date getTimestamp() {
        return timestamp.get();
    }
}
