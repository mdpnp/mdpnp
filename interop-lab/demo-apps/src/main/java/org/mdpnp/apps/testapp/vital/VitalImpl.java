package org.mdpnp.apps.testapp.vital;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class VitalImpl implements Vital {

    private final VitalModelImpl parent;
    private final String label, units;
    private final int[] names;
    private final float minimum, maximum;
    private float warningLow, warningHigh;
    private float criticalLow, criticalHigh;
    private final List<Value> values = new ArrayList<Value>();
    private boolean noValueWarning = false;
    private long warningAgeBecomesAlarm = Long.MAX_VALUE;
    
    
    VitalImpl(VitalModelImpl parent, String label, String units, int[] names, float low, float high, float minimum, float maximum) {
        this.parent = parent;
        this.label = label;
        this.units = units;
        this.names = names;
        this.minimum = this.warningLow = this.criticalLow = minimum;
        this.maximum = this.warningHigh = this.criticalHigh = maximum;
        setWarningLow(low);
        setWarningHigh(high);
        setCriticalLow(minimum);
        setCriticalHigh(maximum);
    }
    
    
    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public int[] getNames() {
        return names;
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
    public float getWarningLow() {
        return warningLow;
    }

    @Override
    public float getWarningHigh() {
        return warningHigh;
    }
    
    @Override
    public float getCriticalHigh() {
        return criticalHigh;
    }
    
    @Override
    public float getCriticalLow() {
        return criticalLow;
    }

    @Override
    public float getDisplayMaximum() {
        return 1.5f*warningHigh-0.5f*warningLow;
    }
    
    @Override
    public float getDisplayMinimum() {
        return 1.5f * warningLow - 0.5f*warningHigh;
    }
    
    @Override
    public String getLabelMaximum() {
        if(Float.compare(getDisplayMaximum(), maximum)>0) {
            return "";
        } else {
            return Integer.toString((int) getDisplayMaximum());
        }
    }
    
    @Override
    public String getLabelMinimum() {
        if(Float.compare(minimum, getDisplayMinimum())>0) {
            return "";            
        } else {
            return Integer.toString((int)getDisplayMinimum());
        }
    }
    
    @Override
    public void setWarningLow(float low) {
        if(low < criticalLow) {
            low = criticalLow;
        } else if(low > warningHigh) {
            low = warningHigh;
        }
        this.warningLow = low;
        parent.fireVitalChanged(this);
    }

    @Override
    public void setWarningHigh(float high) {
        if(high > criticalHigh) {
            high = criticalHigh;
        } else if(high < warningLow) {
            high = warningLow;
        }
        this.warningHigh = high;
        parent.fireVitalChanged(this);
    }
    
    @Override
    public void setCriticalLow(float low) {
        if(low < minimum) {
            low = minimum;
        } else if(low > warningLow) {
            low = warningLow;
        }
        this.criticalLow = low;
        parent.fireVitalChanged(this);
    }
    
    @Override
    public void setCriticalHigh(float high) {
        if(high > maximum) {
            high = maximum;
        } else if(high < warningHigh) {
            high = warningHigh;
        }
        this.criticalHigh = high;
        parent.fireVitalChanged(this);
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
        return "[label="+label+",names="+Arrays.toString(names)+",minimum="+minimum+",maximum="+maximum+",low="+warningLow+",high="+warningHigh+",values="+values.toString()+"]";
    }
    @Override
    public boolean isAnyOutOfBounds() {
        for(Value v : values) {
            if(v.isAtOrOutsideOfBounds()) {
                return true;
            }
        }
        return false;
    }
    @Override
    public int countOutOfBounds() {
        int cnt = 0;
        for(Value v : values) {
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
        if(this.ignoreZero ^ ignoreZero) {
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
        if(this.noValueWarning ^ noValueWarning) {
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
}
