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

import ice.GlobalAlarmLimitObjective;

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
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ModifiableObservableListBase;
import javafx.util.Callback;

import org.mdpnp.apps.fxbeans.ElementObserver;
import org.mdpnp.devices.AbstractDevice.InstanceHolder;

class VitalImpl extends ModifiableObservableListBase<Value> implements Vital { 
    private final Callback<Value, Observable[]> extractor = new Callback<Value, Observable[]>() {

        @Override
        public Observable[] call(final Value param) {
            return new Observable[] {
//                param.ageInMillisecondsProperty(),
                param.atOrAboveCriticalHighProperty(),
                param.atOrBelowCriticalLowProperty(),
                param.atOrAboveHighProperty(),
                param.atOrBelowLowProperty(),
//                param.atOrAboveValueMsHighProperty(),
//                param.atOrAboveValueMsLowProperty(),
                param.valueProperty(),
                param.timestampProperty()
            };
        }
        
    };
    
    private final VitalModel parent;
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
            super.set(low);
            writeCriticalLimits();
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
            
            super.set(high);
            writeCriticalLimits();
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
    private final ElementObserver<Value> elementObserver;
    
    private final InstanceHolder<ice.GlobalAlarmLimitObjective>[] alarmObjectivesLow, alarmObjectivesHigh;

    @SuppressWarnings("unchecked")
    VitalImpl(VitalModel parent, String label, String units, String[] metricIds, Double low, Double high, Double criticalLow, Double criticalHigh,
            double minimum, double maximum, Long valueMsWarningLow, Long valueMsWarningHigh, Color color) {
        this.parent = parent;
        this.label.set(label);
        this.units.set(units);
        this.metricIds.set(metricIds);
        this.minimum.set(minimum);
        this.maximum.set(maximum);

        ice.GlobalAlarmLimitObjectiveDataWriter writer = getParent().getWriter();
        
        if(null != writer) {
	        alarmObjectivesLow = new InstanceHolder[metricIds.length];
	        alarmObjectivesHigh = new InstanceHolder[metricIds.length];
	        for (int i = 0; i < metricIds.length; i++) {
	            alarmObjectivesLow[i] = new InstanceHolder<ice.GlobalAlarmLimitObjective>();
	            alarmObjectivesLow[i].data = (GlobalAlarmLimitObjective) ice.GlobalAlarmLimitObjective.create();
	            alarmObjectivesLow[i].data.metric_id = metricIds[i];
	            alarmObjectivesLow[i].data.limit_type = ice.LimitType.low_limit;
	            alarmObjectivesLow[i].handle = getParent().getWriter().register_instance(alarmObjectivesLow[i].data);
	            
	            alarmObjectivesHigh[i] = new InstanceHolder<ice.GlobalAlarmLimitObjective>();
	            alarmObjectivesHigh[i].data = (GlobalAlarmLimitObjective) ice.GlobalAlarmLimitObjective.create();
	            alarmObjectivesHigh[i].data.metric_id = metricIds[i];
	            alarmObjectivesHigh[i].data.limit_type = ice.LimitType.high_limit;
	            alarmObjectivesHigh[i].handle = getParent().getWriter().register_instance(alarmObjectivesHigh[i].data);
	        }
        } else {
        	alarmObjectivesLow = null;
            alarmObjectivesHigh = null;
        }

        
        
        setCriticalLow(criticalLow);
        setCriticalHigh(criticalHigh);
        setWarningLow(low);
        setWarningHigh(high);
        setValueMsWarningLow(valueMsWarningLow);
        setValueMsWarningHigh(valueMsWarningHigh);

        setIgnoreZero(minimum > 0.0);

        computeDisplayMinMax.invalidated(null);
        
        warningHigh.addListener(computeDisplayMinMax);
        warningLow.addListener(computeDisplayMinMax);
        this.minimum.addListener(computeDisplayMinMax);
        this.maximum.addListener(computeDisplayMinMax);
        
        this.elementObserver = new ElementObserver<Value>(extractor, new Callback<Value, InvalidationListener>() {

            @Override
            public InvalidationListener call(final Value e) {
                return new InvalidationListener() {

                    @Override
                    public void invalidated(Observable observable) {
                        beginChange();
                        int i = 0;
                        final int size = size();
                        for (; i < size; ++i) {
                            if (get(i) == e) {
                                nextUpdate(i);
                            }
                        }
                        endChange();
                    }
                };
            }
        }, this);

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
        if(alarmObjectivesLow != null) {
            for (int i = 0; i < alarmObjectivesLow.length; i++) {
            	alarmObjectivesLow[i].data.value = null == criticalLow.get() ? Float.NEGATIVE_INFINITY : (float)(double)criticalLow.get();
            	alarmObjectivesLow[i].data.limit_type = ice.LimitType.low_limit;
            	getParent().getWriter().write(alarmObjectivesLow[i].data, alarmObjectivesLow[i].handle);    
            }
        }
        
        if(alarmObjectivesHigh != null) {
            for (int i = 0; i < alarmObjectivesHigh.length; i++) {
            	alarmObjectivesHigh[i].data.value = null == criticalHigh.get() ? Float.POSITIVE_INFINITY : (float)(double) criticalHigh.get();
        		alarmObjectivesHigh[i].data.limit_type = ice.LimitType.high_limit;
                getParent().getWriter().write(alarmObjectivesHigh[i].data, alarmObjectivesHigh[i].handle);
            }
        }

    }

    @Override
    public void setCriticalHigh(Double high) {
        criticalHigh.set(high);
    }

    public void destroy() {
        for (int i = 0; i < metricIds.getValue().length/*alarmObjectives.length*/; i++) {
            getParent().getWriter().unregister_instance(alarmObjectivesLow[i].data, alarmObjectivesLow[i].handle);
            getParent().getWriter().unregister_instance(alarmObjectivesHigh[i].data, alarmObjectivesHigh[i].handle);
        }
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
        return "[label=" + getLabel() + ",names=" + Arrays.toString(metricIds.get()) + ",minimum=" + getMinimum() + ",maximum=" + getMaximum() + ",low=" + getWarningLow()
                + ",high=" + getWarningHigh() + ",values=" + values.toString() + "]";
    }

    @Override
    public boolean isAnyOutOfBounds() {
        for (Value v : this) {
            if (v.isAtOrOutsideOfBounds()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int countOutOfBounds() {
        int cnt = 0;
        for (Value v : this) {
            cnt += v.isAtOrAboveHigh() ? 1 : 0;
            cnt += v.isAtOrBelowLow() ? 1 : 0;
        }
        return cnt;
    }

    private final BooleanProperty ignoreZero = new SimpleBooleanProperty(this, "ignoreZero", false);
    private final BooleanProperty required = new SimpleBooleanProperty(this, "required", false);
    private final SimpleObjectProperty<VitalModel.State> modelStateTransitionCondition = new SimpleObjectProperty<>(this, "modelStateTransitionCondition", VitalModel.State.Alarm);


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
        // TODO support it
        throw new UnsupportedOperationException();
    }

    @Override
    public ReadOnlyIntegerProperty countOutOfBoundsProperty() {
        // TODO support it
        throw new UnsupportedOperationException();
    }

    @Override
    public BooleanProperty ignoreZeroProperty() {
        return ignoreZero;
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    protected void doAdd(int index, Value element) {
        elementObserver.attachListener(element);
        values.add(index, element);
    }

    @Override
    protected Value doSet(int index, Value element) {
        Value removed =  values.set(index, element);
        elementObserver.detachListener(removed);
        elementObserver.attachListener(element);
        return removed;
    }

    @Override
    protected Value doRemove(int index) {
        Value v = values.remove(index);
        elementObserver.detachListener(v);
        return v;
    }


    @Override
    public void clear() {
        if (elementObserver != null) {
            final int sz = size();
            for (int i = 0; i < sz; ++i) {
                elementObserver.detachListener(get(i));
            }
        }
        if (hasListeners()) {
            beginChange();
            nextRemove(0, this);
        }
        values.clear();
        ++modCount;
        if (hasListeners()) {
            endChange();
        }
    }
    
    @Override
    public Value get(int index) {
        return values.get(index);
    }


    public final BooleanProperty requiredProperty() {
        return this.required;
    }


    public final boolean isRequired() {
        return this.requiredProperty().get();
    }


    public final void setRequired(final boolean required) {
        this.requiredProperty().set(required);
    }

    public final ReadOnlyObjectProperty<VitalModel.State> modelStateTransitionConditionProperty() {
        return this.modelStateTransitionCondition;
    }
    public final VitalModel.State getModelStateTransitionCondition() {
        return this.modelStateTransitionCondition.get();
    }
    public final void setModelStateTransitionCondition(VitalModel.State v){
        this.modelStateTransitionCondition.set(v);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        VitalImpl values1 = (VitalImpl) o;

        if (!label.equals(values1.label)) return false;
        if (!units.equals(values1.units)) return false;
        if (!metricIds.equals(values1.metricIds)) return false;
        if (!minimum.equals(values1.minimum)) return false;
        if (!maximum.equals(values1.maximum)) return false;
        if (!warningLow.equals(values1.warningLow)) return false;
        if (!warningHigh.equals(values1.warningHigh)) return false;
        if (!criticalLow.equals(values1.criticalLow)) return false;
        if (!criticalHigh.equals(values1.criticalHigh)) return false;
        if (!valueMsWarningLow.equals(values1.valueMsWarningLow)) return false;
        if (!valueMsWarningHigh.equals(values1.valueMsWarningHigh)) return false;
        return values.equals(values1.values);

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + label.hashCode();
        result = 31 * result + units.hashCode();
        result = 31 * result + metricIds.hashCode();
        result = 31 * result + minimum.hashCode();
        result = 31 * result + maximum.hashCode();
        result = 31 * result + warningLow.hashCode();
        result = 31 * result + warningHigh.hashCode();
        result = 31 * result + criticalLow.hashCode();
        result = 31 * result + criticalHigh.hashCode();
        result = 31 * result + valueMsWarningLow.hashCode();
        result = 31 * result + valueMsWarningHigh.hashCode();
        result = 31 * result + values.hashCode();
        return result;
    }
}
