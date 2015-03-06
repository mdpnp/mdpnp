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

import ice.GlobalAlarmSettingsObjective;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ModifiableObservableListBase;

import org.mdpnp.devices.AbstractDevice.InstanceHolder;

class VitalImpl extends ModifiableObservableListBase<Value> implements Vital {

    private final VitalModelImpl parent;
    private final StringProperty label = new SimpleStringProperty(this, "label", null);
    private final StringProperty units = new SimpleStringProperty(this, "units", null);
    private final ObjectProperty<String[]> metricIds = new SimpleObjectProperty<String[]>(this, "metricIds", null);
    private final DoubleProperty minimum = new SimpleDoubleProperty(this, "minimum", 0f);
    private final DoubleProperty maximum = new SimpleDoubleProperty(this, "maximum", 0f);
    private final ObjectProperty<Double> warningLow = new SimpleObjectProperty<Double>(this, "warningLow", null) {
        @Override
        public void set(Double newValue) {
            Double low = newValue;
            if (null != low) {
                if (criticalLow.get() != null && low < criticalLow.get()) {
                    low = criticalLow.get();
                } else if (warningHigh.get() != null && low > warningHigh.get()) {
                    low = warningHigh.get();
                }
            }
            super.set(low);
        };
    };
    private final ObjectProperty<Double> warningHigh = new SimpleObjectProperty<Double>(this, "warningHigh", null) {
        @Override
        public void set(Double high) {
            if (null != high) {
                if (criticalHigh.get() != null && high > criticalHigh.get()) {
                    high = criticalHigh.get();
                } else if (warningLow.get() != null && high < warningLow.get()) {
                    high = warningLow.get();
                }
            }
            super.set(high);
        }
    };
    private final ObjectProperty<Double> criticalLow = new SimpleObjectProperty<Double>(this, "criticalLow", null) {
        @Override
        public void set(Double low) {
            if (null != low) {
                if (low < minimum.get()) {
                    low = minimum.get();
                } else if (warningLow.get() != null && low > warningLow.get()) {
                    low = warningLow.get();
                }
            }
            writeCriticalLimits();
            super.set(low);
        };
    };
    private final ObjectProperty<Double> criticalHigh = new SimpleObjectProperty<Double>(this, "criticalHigh", null) {
        @Override
        public void set(Double high) {
            if (null != high) {
                if (high > maximum.get()) {
                    high = maximum.get();
                } else if (warningHigh.get() != null && high < warningHigh.get()) {
                    high = warningHigh.get();
                }
            }
            writeCriticalLimits();
            super.set(high);
        };
    };
    private final ObjectProperty<Long> valueMsWarningLow = new SimpleObjectProperty<Long>(this, "valueMsWarningLow", null);
    private final ObjectProperty<Long> valueMsWarningHigh = new SimpleObjectProperty<Long>(this, "valueMsWarningHigh", null);
    private final List<Value> values = new ArrayList<Value>();
    private final BooleanProperty noValueWarning = new SimpleBooleanProperty(this, "noValueWarning", false);
    private final LongProperty warningAgeBecomesAlarm = new SimpleLongProperty(this, "warningAgeBecomesAlarm", Long.MAX_VALUE);
    private final DoubleProperty displayMinimum = new SimpleDoubleProperty(this, "displayMinimum", 0f);
    private final DoubleProperty displayMaximum = new SimpleDoubleProperty(this, "displayMaximum", 0f);
    private final StringProperty labelMinimum = new SimpleStringProperty(this, "labelMinimum", "");
    private final StringProperty labelMaximum = new SimpleStringProperty(this, "labelMaximum", "");
    
    
    private final InstanceHolder<ice.GlobalAlarmSettingsObjective>[] alarmObjectives;

    @SuppressWarnings("unchecked")
    VitalImpl(VitalModelImpl parent, String label, String units, String[] metricIds, Double low, Double high, Double criticalLow, Double criticalHigh,
            double minimum, double maximum, Long valueMsWarningLow, Long valueMsWarningHigh, Color color) {
        this.parent = parent;
        this.label.set(label);
        this.units.set(units);
        this.metricIds.set(metricIds);
        this.minimum.set(minimum);
        this.maximum.set(maximum);

        alarmObjectives = new InstanceHolder[metricIds.length];
        for (int i = 0; i < metricIds.length; i++) {
            alarmObjectives[i] = new InstanceHolder<ice.GlobalAlarmSettingsObjective>();
            alarmObjectives[i].data = (GlobalAlarmSettingsObjective) ice.GlobalAlarmSettingsObjective.create();
            alarmObjectives[i].data.metric_id = metricIds[i];
            alarmObjectives[i].handle = getParent().getWriter().register_instance(alarmObjectives[i].data);
        }

        
        
        setCriticalLow(criticalLow);
        setCriticalHigh(criticalHigh);
        setWarningLow(low);
        setWarningHigh(high);
        setValueMsWarningLow(valueMsWarningLow);
        setValueMsWarningHigh(valueMsWarningHigh);
        
        computeDisplayMinMax.invalidated(null);
        
        warningHigh.addListener(computeDisplayMinMax);
        warningLow.addListener(computeDisplayMinMax);
        this.minimum.addListener(computeDisplayMinMax);
        this.maximum.addListener(computeDisplayMinMax);
        
        
    }

    
    @Override
    public String getLabel() {
        return label.get();
    }

    @Override
    public String[] getMetricIds() {
        return metricIds.get();
    }

    @Override
    public double getMinimum() {
        return minimum.get();
    }

    @Override
    public double getMaximum() {
        return maximum.get();
    }

    @Override
    public Double getWarningLow() {
        return warningLow.get();
    }

    @Override
    public Double getWarningHigh() {
        return warningHigh.get();
    }

    @Override
    public Double getCriticalHigh() {
        return criticalHigh.get();
    }

    @Override
    public Double getCriticalLow() {
        return criticalLow.get();
    }
    
    private InvalidationListener computeDisplayMinMax = new InvalidationListener() {
        public void invalidated(javafx.beans.Observable observable) {
            if (null == warningHigh.get()) {
                if (null == warningLow.get()) {
                    displayMaximum.set(maximum.get() + maximum.get() - minimum.get());
                } else {
                    displayMaximum.set(maximum.get() + 2 * (maximum.get() - warningLow.get()));
                }
            } else {
                if (null == warningLow.get()) {
                    displayMaximum.set(warningHigh.get() + (warningHigh.get() - minimum.get()));
                } else {
                    displayMaximum.set(1.5f * warningHigh.get() - 0.5f * warningLow.get());
                }
            }
            if (null == warningHigh.get()) {
                if (null == warningLow.get()) {
                    displayMinimum.set(minimum.get() - minimum.get() + maximum.get());
                } else {
                    displayMinimum.set(warningLow.get() - (maximum.get() - warningLow.get()));
                }
            } else {
                if (null == warningLow.get()) {
                    displayMinimum.set(warningHigh.get() - 3 * (warningHigh.get() - minimum.get()));
                } else {
                    displayMinimum.set(1.5f * warningLow.get() - 0.5f * warningHigh.get());
                }
            }
            if (Double.compare(getDisplayMaximum(), maximum.get()) > 0) {
                labelMaximum.set("");
            } else {
                labelMaximum.set(Integer.toString((int) getDisplayMaximum()));
            }
            if (Double.compare(minimum.get(), getDisplayMinimum()) > 0) {
                labelMinimum.set("");
            } else {
                labelMinimum.set(Integer.toString((int) getDisplayMinimum()));
            }            
        };
    };

    @Override
    public double getDisplayMaximum() {
        return displayMaximum.get();
    }

    @Override
    public double getDisplayMinimum() {
        return displayMinimum.get();
    }

    @Override
    public String getLabelMaximum() {
        return labelMaximum.get();
    }

    @Override
    public String getLabelMinimum() {
        return labelMinimum.get();
    }

    @Override
    public void setWarningLow(Double low) {
        warningLow.set(low);
    }

    @Override
    public void setWarningHigh(Double high) {
        warningHigh.set(high);
    }

    @Override
    public void setCriticalLow(Double low) {
        this.criticalLow.set(low);
    }

    private void writeCriticalLimits() {
        for (int i = 0; i < alarmObjectives.length; i++) {
            alarmObjectives[i].data.lower = null == criticalLow.get() ? Float.NEGATIVE_INFINITY : (float)(double)criticalLow.get();
            alarmObjectives[i].data.upper = null == criticalHigh.get() ? Float.POSITIVE_INFINITY : (float)(double) criticalHigh.get();
            getParent().getWriter().write(alarmObjectives[i].data, alarmObjectives[i].handle);
        }
    }

    @Override
    public void setCriticalHigh(Double high) {
        criticalHigh.set(high);
    }

    public void destroy() {
        for (int i = 0; i < alarmObjectives.length; i++) {
            getParent().getWriter().unregister_instance(alarmObjectives[i].data, alarmObjectives[i].handle);
        }
    }

    @Override
    public List<Value> getValues() {
        return values;
    }

    @Override
    public VitalModel getParent() {
        return parent;
    }

    @Override
    public String getUnits() {
        return units.get();
    }

    @Override
    public String toString() {
        return "[label=" + label + ",names=" + Arrays.toString(metricIds.get()) + ",minimum=" + minimum + ",maximum=" + maximum + ",low=" + warningLow
                + ",high=" + warningHigh + ",values=" + values.toString() + "]";
    }

    @Override
    public boolean isAnyOutOfBounds() {
        for (Value v : values) {
            if (v.isAtOrOutsideOfBounds()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int countOutOfBounds() {
        int cnt = 0;
        for (Value v : values) {
            cnt += v.isAtOrAboveHigh() ? 1 : 0;
            cnt += v.isAtOrBelowLow() ? 1 : 0;
        }
        return cnt;
    }

    private final BooleanProperty ignoreZero = new SimpleBooleanProperty(this, "ignoreZero", true);

    @Override
    public boolean isIgnoreZero() {
        return ignoreZero.get();
    }

    @Override
    public void setIgnoreZero(boolean ignoreZero) {
        this.ignoreZero.set(ignoreZero);
    }

    @Override
    public boolean isNoValueWarning() {
        return noValueWarning.get();
    }

    @Override
    public void setNoValueWarning(boolean noValueWarning) {
        this.noValueWarning.set(noValueWarning);
    }

    @Override
    public long getWarningAgeBecomesAlarm() {
        return warningAgeBecomesAlarm.get();
    }

    @Override
    public void setWarningAgeBecomesAlarm(long warningAgeBecomesAlarm) {
        this.warningAgeBecomesAlarm.set(warningAgeBecomesAlarm);
    }

    @Override
    public Long getValueMsWarningHigh() {
        return valueMsWarningHigh.get();
    }

    @Override
    public Long getValueMsWarningLow() {
        return valueMsWarningLow.get();
    }

    @Override
    public void setValueMsWarningHigh(Long high) {
        this.valueMsWarningHigh.set(high);
    }

    @Override
    public void setValueMsWarningLow(Long low) {
        this.valueMsWarningLow.set(low);
    }

    @Override
    public ReadOnlyStringProperty labelProperty() {
        return label;
    }

    @Override
    public ReadOnlyStringProperty unitsProperty() {
        return units;
    }

    @Override
    public ReadOnlyObjectProperty<String[]> metricIdsProperty() {
        return metricIds;
    }

    @Override
    public ReadOnlyDoubleProperty minimumProperty() {
        return minimum;
    }

    @Override
    public ReadOnlyDoubleProperty maximumProperty() {
        return maximum;
    }

    @Override
    public ObjectProperty<Long> valueMsWarningLowProperty() {
        return valueMsWarningLow;
    }

    @Override
    public ObjectProperty<Long> valueMsWarningHighProperty() {
        return valueMsWarningHigh;
    }

    @Override
    public ObjectProperty<Double> warningLowProperty() {
        return warningLow;
    }

    @Override
    public ObjectProperty<Double> warningHighProperty() {
        return warningHigh;
    }

    @Override
    public ObjectProperty<Double> criticalLowProperty() {
        return criticalLow;
    }

    @Override
    public ObjectProperty<Double> criticalHighProperty() {
        return criticalHigh;
    }

    @Override
    public ReadOnlyDoubleProperty displayMaximumProperty() {
        return displayMaximum;
    }

    @Override
    public ReadOnlyDoubleProperty displayMinimumProperty() {
        return displayMinimum;
    }

    @Override
    public ReadOnlyStringProperty labelMinimumProperty() {
        return labelMinimum;
    }

    @Override
    public ReadOnlyStringProperty labelMaximumProperty() {
        return labelMaximum;
    }

    @Override
    public BooleanProperty noValueWarningProperty() {
        return noValueWarning;
    }

    @Override
    public LongProperty warningAgeBecomesAlarmProperty() {
        return warningAgeBecomesAlarm;
    }

    @Override
    public ReadOnlyBooleanProperty anyOutOfBoundsProperty() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ReadOnlyIntegerProperty countOutOfBoundsProperty() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BooleanProperty ignoreZeroProperty() {
        return ignoreZero;
    }

    @Override
    public Value get(int index) {
        return values.get(index);
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    protected void doAdd(int index, Value element) {
        values.add(index, element);
    }

    @Override
    protected Value doSet(int index, Value element) {
        return values.set(index, element);
    }

    @Override
    protected Value doRemove(int index) {
        return values.remove(index);
    }

}
