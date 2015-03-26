package org.mdpnp.apps.testapp.validate;

import java.util.HashMap;
import java.util.Map;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart.Series;

import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.apps.testapp.vital.Vital;

public class VitalValidator implements ListChangeListener<Value> {
    private final Vital vital;
    
    private final Map<Value, ValueValidator> valueValidators = new HashMap<Value, ValueValidator>();
    private final ObservableList<Series<String, Number>> data;
    
    public VitalValidator(final Vital vital, final ObservableList<Series<String, Number>> data) {
        this.vital = vital;
        this.data = data;
        vital.addListener(this);

    }
    
    @Override
    public void onChanged(javafx.collections.ListChangeListener.Change<? extends Value> c) {
        while(c.next()) {
            if(c.wasAdded()) {
                for(final Value v : c.getAddedSubList()) {
                    ValueValidator vv = new ValueValidator(v);
                    data.add(vv.getSeries());
                    valueValidators.put(v, vv);
                }
            }
            if(c.wasRemoved()) {
                for(Value v : c.getRemoved()) {
                    ValueValidator vv = valueValidators.remove(v);
                    if(null != vv) {
                        data.remove(vv.getSeries());
                    }
                }
            }
        }
    }
}
