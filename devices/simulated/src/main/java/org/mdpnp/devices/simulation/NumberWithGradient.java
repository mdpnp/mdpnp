package org.mdpnp.devices.simulation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mfeinberg
 */
public abstract class NumberWithGradient extends Number {

    private static final Logger log = LoggerFactory.getLogger(NumberWithGradient.class);

    final Number delegate;
    final Number gradient;
    final Number targetValue;
    final Number startValue;
    Number currentValue=null;

    protected NumberWithGradient(Number startValue, Number targetValue, Number gradient) {

        if(startValue instanceof NumberWithGradient) {
            NumberWithGradient other = (NumberWithGradient) startValue;
            this.startValue = other.currentValue == null ? other.startValue : other.currentValue;
        }
        else
            this.startValue = startValue;

        this.delegate     = targetValue;
        this.targetValue  = makeTarget(targetValue);
        this.gradient     = gradient;

        if(this.startValue == null)
            throw new IllegalArgumentException("start value cannot be null");
        if(this.gradient == null)
            throw new IllegalArgumentException("gradient value cannot be null");
        if(this.delegate == null)
            throw new IllegalArgumentException("delegate value cannot be null");
    }

    @Override
    public String toString() {
        return "NumberWithGradient{" +
                "delegate=" + delegate +
                ", gradient=" + gradient +
                ", targetValue=" + targetValue +
                ", startValue=" + startValue +
                ", currentValue=" + currentValue +
                '}';
    }

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

    private final Number next() {
        if (currentValue == null) {
            currentValue = startValue;
            return currentValue;
        }
        return makeNext();
    }

    protected abstract Number makeNext();
    protected abstract Number makeTarget(Number target);

    public static class Integer extends NumberWithGradient {

        public Integer(Number currentValue, Number target, Number gradient) {
            super(currentValue, target, gradient);
        }

        protected Number makeTarget(Number target) {
            return target.intValue();
        }


        protected Number makeNext() {
            int toRet = currentValue.intValue();
            int delta = targetValue.intValue() - toRet;
            // we are at the target level ?
            if (Math.abs(delta) < gradient.intValue()) {
                currentValue = delegate.intValue();
            } else {
                delta = Math.min(gradient.intValue(), delta);
                delta = Math.max(-gradient.intValue(), delta);
                currentValue = toRet + delta;
            }
            return currentValue;
        }
    }

    public static class Double extends NumberWithGradient {

        public Double(Double currentValue, Number target, Double gradient) {
            super(currentValue, target, gradient);
        }

        protected Number makeTarget(Number target) {
            return target.doubleValue();
        }

        protected Number makeNext() {
            double toRet = currentValue.doubleValue();
            double delta = targetValue.doubleValue() - toRet;
            // we are at the target level ?
            if (Math.abs(delta) <= gradient.doubleValue()) {
                currentValue = delegate.doubleValue();
            } else {
                delta = Math.min(gradient.doubleValue(), delta);
                delta = Math.max(-gradient.doubleValue(), delta);
                currentValue = toRet + delta;
            }
            return currentValue;
        }
    }
}

