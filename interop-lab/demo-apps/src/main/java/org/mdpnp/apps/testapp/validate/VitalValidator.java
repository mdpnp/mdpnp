package org.mdpnp.apps.testapp.validate;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.property.IntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Series;

import org.mdpnp.apps.device.OnListChange;
import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.apps.testapp.vital.Vital;

public class VitalValidator {
    private final Vital vital;
    
    private final Map<Value, ValueValidator> valueValidators = new HashMap<Value, ValueValidator>();
    private final ObservableList<Series<String, Number>> data;
    private final IntegerProperty maxDataPoints;
    
    public VitalValidator(final IntegerProperty maxDataPoints, final Vital vital, final ObservableList<Series<String, Number>> data) {
        this.maxDataPoints = maxDataPoints;
        this.vital = vital;
        this.data = data;
        vital.addListener(valueListener);
        vital.forEach((t)->add(t));

    }
    
    private OnListChange<Value> valueListener = new OnListChange<Value>(
            (t)->add(t), null, (t)->remove(t));
    
    private void add(Value v) {
        ValueValidator vv = new ValueValidator(maxDataPoints, v);
        data.add(vv.getSeries());
        valueValidators.put(v, vv);
    }
    private void remove(Value v) {
        ValueValidator vv = valueValidators.remove(v);
        if(null != vv) {
            data.remove(vv.getSeries());
        }
    }

}
