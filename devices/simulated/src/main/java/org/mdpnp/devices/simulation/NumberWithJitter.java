package org.mdpnp.devices.simulation;

/**
 * @author mfeinberg
 *
 * A mutable number with value that fluctuates within a range with steps in small
 * random increments. It is intended to be used tio simulate a source of physical
 * signal that would change in small deltas, but could drift over a broad spectrum
 * over time.
 */
public class NumberWithJitter<T> extends Number {

    private final Number increment;
    private final Number floor;
    private final Number ceil;
    private final Number initialValue;
    private Number currentValue;

    @Override
    public int intValue() {
        return next().intValue();
    }

    @Override
    public long longValue() {
        return next().longValue();
    }

    @Override
    public float floatValue() {
        return next().floatValue();
    }

    @Override
    public double doubleValue() {
        return next().doubleValue();
    }

    synchronized Number next() {
        double diff = (increment.doubleValue() - (2 * increment.doubleValue() * Math.random()));
        double nextValue = currentValue.doubleValue() + diff;
        if (nextValue < floor.doubleValue() || nextValue > ceil.doubleValue()) {
            nextValue = currentValue.doubleValue() - diff;
        }
        Number toRet = currentValue;
        currentValue = nextValue;
        return toRet;
    }

    @Override
    public String toString() {
        return "NumberWithJitter{" +
                "increment=" + increment +
                ", floor=" + floor +
                ", ceil=" + ceil +
                ", initialValue=" + initialValue +
                ", currentValue=" + currentValue +
                '}';
    }

    T getIncrement() {
        return (T)increment;
    }

    T getFloor() {
        return (T)floor;
    }

    T getCeil() {
        return (T)ceil;
    }

    T getInitialValue() {
        return (T)initialValue;
    }

    T getCurrentValue() {
        return (T)currentValue;
    }

    /**
     * Create a number with initialValue being in the middle between upper/lower bounds
     */
    public NumberWithJitter(Number initialValue, Number increment, Number maxDelta) {
        this(initialValue,
             increment,
             initialValue.doubleValue()-maxDelta.doubleValue(),
             initialValue.doubleValue()+maxDelta.doubleValue());
    }

    public NumberWithJitter(Number initialValue, Number increment, Number floor, Number ceil) {
        this.increment    = increment;
        this.ceil         = ceil;
        this.floor        = floor;
        this.initialValue = initialValue;
        this.currentValue = initialValue;
    }
}
