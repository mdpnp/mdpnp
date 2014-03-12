package org.mdpnp.gip.ui.values;

import org.mdpnp.gip.ui.units.TimeUnits;
import org.mdpnp.gip.ui.units.TimeUnitsFactory;

/**
 * @author Jeff Plourde
 *
 */
public class TimeValueImpl extends AbstractValueImpl<TimeUnits> implements TimeValue {
	public TimeValueImpl() {
		super(TimeUnitsFactory.hours);
	}
	public TimeValueImpl(TimeUnits... u) {
		super(u);
	}
	@Override
	protected void doFireEvent(Object event, ValueListener<TimeUnits> listener) {
		if(UNITS_CHANGED.equals(event)) {
			listener.unitsChanged(this);
		} else if(VALUE_CHANGED.equals(event)) {
			listener.valueChanged(this);
		} else if(BOUNDS_CHANGED.equals(event)) {
			listener.boundsChanged(this);
		}
	}
}
