package org.mdpnp.apps.testapp.vital;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class ConcreteDoubleProperty extends SimpleDoubleProperty implements InvalidationListener {
    private final ObjectProperty<Double> source;
    private final double reportIfNull;
    public ConcreteDoubleProperty(ObjectProperty<Double> source) {
        this(source, Double.NaN);
    }
    
    public ConcreteDoubleProperty(ObjectProperty<Double> source, double reportIfNull) {
        this.source = source;
        this.reportIfNull = reportIfNull;
        // TODO register listener weakly
        source.addListener(this);
    }
    @Override
    public void set(double newValue) {
        super.set(newValue);
        source.set(newValue);
    }
    @Override
    public double get() {
        Double s = source.get();
        return null == s ? reportIfNull : s;
    }
    @Override
    public void invalidated(Observable observable) {
        fireValueChangedEvent();
    }
}