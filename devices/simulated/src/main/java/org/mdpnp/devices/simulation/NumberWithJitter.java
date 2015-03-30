package org.mdpnp.devices.simulation;

/**
 * @author mfeinberg
 */
public abstract class NumberWithJitter extends Number {

    /**
     * default drift to 10% with 2% delta on each step
     */
    public static NumberWithJitter makeDouble(double v) {
        double increment = 0.02*v;
        double delta     = 0.1*v;
        return new DoubleWithJitter(v, increment, delta);
    }

    public static NumberWithJitter makeDouble(double v, double increment, double delta) {
        return new DoubleWithJitter(v, increment, delta);
    }

    public static NumberWithJitter makeInteger(int v) {
        int increment = (int)(0.02*v); if(increment==0) increment= 1;
        int delta     = (int)(0.1*v);  if(delta==0) delta=2;
        return new DoubleWithJitter(v, increment, delta);
    }

    public abstract double getAverageValue();

    static class DoubleWithJitter extends NumberWithJitter {

        private final double increment;
        private final double delta;
        private final double initialValue;
        private double currentValue;

        public DoubleWithJitter(double initialValue, double increment, double delta) {
            this.increment = increment;
            this.delta = delta;
            this.initialValue = initialValue;
            currentValue = initialValue;
        }

        @Override
        public int intValue() {
            return (int) getValue();
        }

        @Override
        public long longValue() {
            return (long) getValue();
        }

        @Override
        public float floatValue() {
            return (float) getValue();
        }

        @Override
        public double doubleValue() {
            return getValue();
        }

        double getValue() {
            double diff = (increment - (2 * increment * Math.random()));
            double nextValue = currentValue + diff;
            if (Math.abs(initialValue - nextValue) > delta) {
                nextValue = currentValue - diff;
            }
            currentValue = nextValue;
            return currentValue;
        }

        @Override
        public double getAverageValue() {
            return initialValue;
        }
    }
}
