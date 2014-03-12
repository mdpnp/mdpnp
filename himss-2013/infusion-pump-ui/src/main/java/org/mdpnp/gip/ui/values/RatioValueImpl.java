package org.mdpnp.gip.ui.values;

import org.mdpnp.gip.ui.units.RatioUnits;
import org.mdpnp.gip.ui.units.RatioUnitsFactory;

/**
 * @author Jeff Plourde
 *
 */
public class RatioValueImpl extends AbstractValueImpl<RatioUnits> implements RatioValue {
	public RatioValueImpl() {
		super(RatioUnitsFactory.mLPerHour);
	}
	public RatioValueImpl(RatioUnits... u) {
		super(u);
	}
	public RatioValueImpl(Double minimum, Double maximum, RatioUnits... u) {
		super(minimum, maximum, u);
	}
	public RatioValueImpl(Double minimum, Double maximum, int fractionDigits, Double stepSize, RatioUnits... u) {
		super(minimum, maximum, fractionDigits, stepSize, u);
	}
	@Override
	protected void doFireEvent(Object event, ValueListener<RatioUnits> listener) {
		if(UNITS_CHANGED.equals(event)) {
			listener.unitsChanged(this);
		} else if(VALUE_CHANGED.equals(event)) {
			listener.valueChanged(this);
		} else if(BOUNDS_CHANGED.equals(event)) {
			listener.boundsChanged(this);
		}
	}

}
