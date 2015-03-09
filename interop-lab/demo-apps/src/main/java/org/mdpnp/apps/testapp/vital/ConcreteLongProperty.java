package org.mdpnp.apps.testapp.vital;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;

public class ConcreteLongProperty extends SimpleLongProperty implements InvalidationListener {
    private final ObjectProperty<Long> source;
    private final long reportIfNull;
    public ConcreteLongProperty(ObjectProperty<Long> source) {
        this(source, 0L);
    }
    
    public ConcreteLongProperty(ObjectProperty<Long> source, long reportIfNull) {
        this.source = source;
        this.reportIfNull = reportIfNull;
        // TODO register listener weakly
        source.addListener(this);
    }
    @Override
    public void set(long newValue) {
        super.set(newValue);
        source.set(newValue);
    }
    @Override
    public long get() {
        Long s = source.get();
        return null == s ? reportIfNull : s;
    }
    @Override
    public void invalidated(Observable observable) {
        fireValueChangedEvent();
    }
}