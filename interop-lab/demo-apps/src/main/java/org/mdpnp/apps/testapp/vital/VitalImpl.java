package org.mdpnp.apps.testapp.vital;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class VitalImpl implements Vital {

    private final VitalModelImpl parent;
    private final String label, units;
    private final int[] names;
    private final float minimum, maximum;
    private float low, high;
    private final List<Value> values = new ArrayList<Value>();
    
    VitalImpl(VitalModelImpl parent, String label, String units, int[] names, float low, float high, float minimum, float maximum) {
        this.parent = parent;
        this.label = label;
        this.units = units;
        this.names = names;
        this.minimum = minimum;
        this.maximum = maximum;
        setLow(low);
        setHigh(high);
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
    public float getLow() {
        return low;
    }

    @Override
    public float getHigh() {
        return high;
    }

    @Override
    public float getDisplayMaximum() {
        return 1.5f*high-0.5f*low;
    }
    
    @Override
    public float getDisplayMinimum() {
        return 1.5f * low - 0.5f*high;
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
    public void setLow(float low) {
        this.low = low;
        parent.fireVitalChanged(this);
    }

    @Override
    public void setHigh(float high) {
        this.high = high;
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
        return "[label="+label+",names="+Arrays.toString(names)+",minimum="+minimum+",maximum="+maximum+",low="+low+",high="+high+",values="+values.toString()+"]";
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
}
