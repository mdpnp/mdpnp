package org.mdpnp.devices.simulation;

/**
 * @author mfeinberg
 */
public class NumberWithJitter<T> extends Number {

    private final Number increment;
    private final Number maxDelta;
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
        if (Math.abs(initialValue.doubleValue() - nextValue) > maxDelta.doubleValue()) {
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
                ", maxDelta=" + maxDelta +
                ", initialValue=" + initialValue +
                ", currentValue=" + currentValue +
                '}';
    }

    public T getIncrement() {
        return (T)increment;
    }
    public T getDelta() {
        return (T)maxDelta;
    }
    public T getAverageValue() {
        return (T)initialValue;
    }

    /**
     *
     * @param initialValue
     * @param increment
     * @param maxDelta
     */
    public NumberWithJitter(Number initialValue, Number increment, Number maxDelta) {
        this.increment = increment;
        this.maxDelta = maxDelta;
        this.initialValue = initialValue;
        this.currentValue = initialValue;
        if(increment.doubleValue()>maxDelta.doubleValue())
            throw new IllegalArgumentException("Increment step value "
                                                       + increment.doubleValue()
                                                       + " must be less than total max delta "
                                                       + maxDelta.doubleValue());
    }

    /**
     * default drift to 10% with 2% delta on each step
    */
    public static NumberWithJitter<Double> makeDouble(double v) {
        double increment = 0.02*v;
        double delta     = 0.1*v;
        return new NumberWithJitter<Double>(v, increment, delta);
    }

    public static NumberWithJitter<Double> makeDouble(double v, double increment, double delta) {
        return new NumberWithJitter<Double>(v, increment, delta);
    }

    public static NumberWithJitter<Integer> makeInteger(int v) {
        int increment = (int)(0.02*v); if(increment==0) increment= 1;
        int delta     = (int)(0.1*v);  if(delta==0) delta=2;
        return new NumberWithJitter<Integer>(v, increment, delta);
    }
}
