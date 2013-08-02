package org.mdpnp.apps.testapp.vital;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

class VitalImpl implements Vital {

    private final VitalModelImpl parent;
    private final String label, units;
    private final int[] names;
    private final float minimum, maximum;
    private float low, high;
    private final List<Value> values = new ArrayList<Value>();
    
    VitalImpl(VitalModelImpl parent, String label, String units, int[] names, float minimum, float maximum) {
        this.parent = parent;
        this.label = label;
        this.units = units;
        this.names = names;
        this.minimum = minimum;
        this.maximum = maximum;
        this.low = minimum;
        this.high = maximum;
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
}
