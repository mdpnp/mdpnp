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

import org.mdpnp.devices.AbstractDevice.InstanceHolder;

class VitalImpl implements Vital {

    private final VitalModelImpl parent;
    private final String label, units;
    private final String[] metric_ids;
    private final float minimum, maximum;
    private Float warningLow, warningHigh;
    private Float criticalLow, criticalHigh;
    private Long valueMsWarningLow, valueMsWarningHigh;
    private final List<Value> values = new ArrayList<Value>();
    private boolean noValueWarning = false;
    private long warningAgeBecomesAlarm = Long.MAX_VALUE;

    private final InstanceHolder<ice.GlobalAlarmSettingsObjective>[] alarmObjectives;

    @SuppressWarnings("unchecked")
    VitalImpl(VitalModelImpl parent, String label, String units, String[] metric_ids, Float low, Float high, Float criticalLow, Float criticalHigh,
            float minimum, float maximum, Long valueMsWarningLow, Long valueMsWarningHigh, Color color) {
        this.parent = parent;
        this.label = label;
        this.units = units;
        this.metric_ids = metric_ids;
        this.minimum = minimum;
        this.maximum = maximum;

        alarmObjectives = new InstanceHolder[metric_ids.length];
        for (int i = 0; i < metric_ids.length; i++) {
            alarmObjectives[i] = new InstanceHolder<ice.GlobalAlarmSettingsObjective>();
            alarmObjectives[i].data = (GlobalAlarmSettingsObjective) ice.GlobalAlarmSettingsObjective.create();
            alarmObjectives[i].data.metric_id = metric_ids[i];
            alarmObjectives[i].handle = getParent().getWriter().register_instance(alarmObjectives[i].data);
        }

        setCriticalLow(criticalLow);
        setCriticalHigh(criticalHigh);
        setWarningLow(low);
        setWarningHigh(high);
        setValueMsWarningLow(valueMsWarningLow);
        setValueMsWarningHigh(valueMsWarningHigh);

    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String[] getMetricIds() {
        return metric_ids;
    }

    @Override
    public float getMinimum() {
        return minimum;
    }

    @Override
    public float getMaximum() {
        return maximum;
    }

    @Override
    public Float getWarningLow() {
        return warningLow;
    }

    @Override
    public Float getWarningHigh() {
        return warningHigh;
    }

    @Override
    public Float getCriticalHigh() {
        return criticalHigh;
    }

    @Override
    public Float getCriticalLow() {
        return criticalLow;
    }

    @Override
    public float getDisplayMaximum() {
        if (null == this.warningHigh) {
            if (null == this.warningLow) {
                return maximum + maximum - minimum;
            } else {
                return maximum + 2 * (maximum - warningLow);
            }
        } else {
            if (null == this.warningLow) {
                return warningHigh + (warningHigh - minimum);
            } else {
                return 1.5f * warningHigh - 0.5f * warningLow;
            }
        }

    }

    @Override
    public float getDisplayMinimum() {
        if (null == this.warningHigh) {
            if (null == this.warningLow) {
                return minimum - minimum + maximum;
            } else {
                return warningLow - (maximum - warningLow);
            }
        } else {
            if (null == this.warningLow) {
                return warningHigh - 3 * (warningHigh - minimum);
            } else {
                return 1.5f * warningLow - 0.5f * warningHigh;
            }
        }
    }

    @Override
    public String getLabelMaximum() {
        if (Float.compare(getDisplayMaximum(), maximum) > 0) {
            return "";
        } else {
            return Integer.toString((int) getDisplayMaximum());
        }
    }

    @Override
    public String getLabelMinimum() {
        if (Float.compare(minimum, getDisplayMinimum()) > 0) {
            return "";
        } else {
            return Integer.toString((int) getDisplayMinimum());
        }
    }

    @Override
    public void setWarningLow(Float low) {
        if (null != low) {
            if (criticalLow != null && low < criticalLow) {
                low = criticalLow;
            } else if (warningHigh != null && low > warningHigh) {
                low = warningHigh;
            }
        }
        this.warningLow = low;
        parent.fireVitalChanged(this);
    }

    @Override
    public void setWarningHigh(Float high) {
        if (null != high) {
            if (criticalHigh != null && high > criticalHigh) {
                high = criticalHigh;
            } else if (warningLow != null && high < warningLow) {
                high = warningLow;
            }
        }
        this.warningHigh = high;
        parent.fireVitalChanged(this);
    }

    @Override
    public void setCriticalLow(Float low) {
        if (null != low) {
            if (low < minimum) {
                low = minimum;
            } else if (warningLow != null && low > warningLow) {
                low = warningLow;
            }
        }
        this.criticalLow = low;
        writeCriticalLimits();

        parent.fireVitalChanged(this);
    }

    private void writeCriticalLimits() {
        for (int i = 0; i < alarmObjectives.length; i++) {
            alarmObjectives[i].data.lower = null == criticalLow ? Float.MIN_VALUE : criticalLow;
            alarmObjectives[i].data.upper = null == criticalHigh ? Float.MAX_VALUE : criticalHigh;
            getParent().getWriter().write(alarmObjectives[i].data, alarmObjectives[i].handle);
        }
    }

    @Override
    public void setCriticalHigh(Float high) {
        if (null != high) {
            if (high > maximum) {
                high = maximum;
            } else if (warningHigh != null && high < warningHigh) {
                high = warningHigh;
            }
        }
        this.criticalHigh = high;
        writeCriticalLimits();
        parent.fireVitalChanged(this);
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
        return units;
    }

    @Override
    public String toString() {
        return "[label=" + label + ",names=" + Arrays.toString(metric_ids) + ",minimum=" + minimum + ",maximum=" + maximum + ",low=" + warningLow
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

    private boolean ignoreZero = true;

    @Override
    public boolean isIgnoreZero() {
        return ignoreZero;
    }

    @Override
    public void setIgnoreZero(boolean ignoreZero) {
        if (this.ignoreZero ^ ignoreZero) {
            this.ignoreZero = ignoreZero;
            parent.fireVitalChanged(this);
        }
    }

    @Override
    public boolean isNoValueWarning() {
        return noValueWarning;
    }

    @Override
    public void setNoValueWarning(boolean noValueWarning) {
        if (this.noValueWarning ^ noValueWarning) {
            this.noValueWarning = noValueWarning;
            parent.fireVitalChanged(this);
        }
    }

    @Override
    public long getWarningAgeBecomesAlarm() {
        return warningAgeBecomesAlarm;
    }

    @Override
    public void setWarningAgeBecomesAlarm(long warningAgeBecomesAlarm) {
        this.warningAgeBecomesAlarm = warningAgeBecomesAlarm;
        parent.fireVitalChanged(this);
    }

    @Override
    public Long getValueMsWarningHigh() {
        return valueMsWarningHigh;
    }

    @Override
    public Long getValueMsWarningLow() {
        return valueMsWarningLow;
    }

    @Override
    public void setValueMsWarningHigh(Long high) {
        this.valueMsWarningHigh = high;
        parent.fireVitalChanged(this);
    }

    @Override
    public void setValueMsWarningLow(Long low) {
        this.valueMsWarningLow = low;
        parent.fireVitalChanged(this);
    }

}
