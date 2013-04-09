package org.mdpnp.gip.ui.values;

import java.util.Arrays;

import org.mdpnp.gip.ui.AbstractModel;
import org.mdpnp.gip.ui.units.Units;

public abstract class AbstractValueImpl<U extends Units> extends AbstractModel<ValueListener<U>> implements Value<U> {
	private Double value = null;
	private U units;
	private U[] acceptableUnits;
	private Double minimum = null;
	private Double maximum = null;
	private Double softMinimum = null;
	private Double softMaximum = null;
	private Double starting = null;
	private int fractionDigits = 0;
	private Double stepSize = 1.0;
	
	protected static final Integer VALUE_CHANGED = new Integer(1);
	protected static final Integer UNITS_CHANGED = new Integer(2);
	protected static final Integer BOUNDS_CHANGED = new Integer(3);
	
	protected AbstractValueImpl(U u) {
		units = u;
	}
	
	protected AbstractValueImpl(U... u) {
		units = u[0];
		acceptableUnits = u;
	}
	
	protected AbstractValueImpl(Double minimum, Double maximum, U... u) {
		this(u);
		this.minimum = units.to(minimum);
		this.maximum = units.to(maximum);
	}
	protected AbstractValueImpl(Double minimum, Double maximum, int fractionDigits, Double stepSize, U... u) {
		this(minimum, maximum, u);
		this.fractionDigits = fractionDigits;
		this.stepSize = stepSize;
		
	}
	
	@Override
	public void setValue(U u, Double v) {
		if(this.value == null && v == null) {
			// No change
			return;
		}
		
		if(v != null) {
			Double _v = u.to(v);
			if(null != _v) {
				if(_v.equals(v)) {
					// no change
					return;
				}
			
				if(minimum != null && _v < minimum) {
					throw new IllegalArgumentException(""+v+" "+u+" is below minimum of "+u.from(minimum) +" "+u);
				}
				if(maximum != null && _v > maximum) {
					throw new IllegalArgumentException(""+v+" "+u+" is above maximum of "+u.from(maximum)+" "+u);
				}
			}
			value = _v;
		} else {
			value = null;
		}
		fireEvent(VALUE_CHANGED);
	}
	@Override
	public Double getValue(U u) {
		return u.from(value);
	}
	@Override
	public void setUnits(U u) {
		if(null == this.units) {
			if(null == u) {
				// no change
				return;			
			}
		} else {
			if(null != u && this.units.equals(u)) {
				// no change
				return;
			}
		}
		
		
		if(null != acceptableUnits) {
			for(U acceptable : acceptableUnits) {
				if(acceptable.equals(u)) {
					this.units = u;
					fireEvent(UNITS_CHANGED);
					return;
				}
			}
			throw new IllegalArgumentException(u + " is not an acceptable unit; choose one of " + Arrays.toString(acceptableUnits));
		} else {
			this.units = u;
			fireEvent(UNITS_CHANGED);
		}
		
	}
	@Override
	public U getUnits() {
		return units;
	}
	@Override
	public void setValue(Double value) {
		setValue(units, value);
	}
	@Override
	public Double getValue() {
		return getValue(units);
	}
	@Override
	public U[] getAcceptableUnits() {
		return acceptableUnits;
	}
	@Override
	public Double getMaximum() {
		return units.from(maximum);
	}
	@Override
	public Double getMinimum() {
		return units.from(minimum);
	}
	
	@Override
	public void setMaximum(Double d) {
		this.maximum = units.to(d);
		fireEvent(BOUNDS_CHANGED);
	};
	
	@Override
	public void setMinimum(Double minimum) {
		this.minimum = units.to(minimum);
		fireEvent(BOUNDS_CHANGED);
	}
	
	@Override
	public int getFractionDigits() {
		return fractionDigits;
	}
	@Override
	public Double getStepSize() {
		return stepSize;
	}
	@Override
	public Double getSoftMaximum() {
		return softMaximum;
	}
	@Override
	public Double getSoftMinimum() {
		return softMinimum;
	}
	@Override
	public Double getStarting() {
		return starting;
	}
	@Override
	public void setSoftMaximum(Double d) {
		this.softMaximum = d;
		fireEvent(BOUNDS_CHANGED);
	}
	@Override
	public void setSoftMinimum(Double d) {
		this.softMinimum = d;
		fireEvent(BOUNDS_CHANGED);
	}
	@Override
	public void setStarting(Double d) {
		this.starting = d;
		fireEvent(BOUNDS_CHANGED);
	}
	
}
