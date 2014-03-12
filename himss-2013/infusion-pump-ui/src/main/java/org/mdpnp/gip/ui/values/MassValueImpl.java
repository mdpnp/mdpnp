package org.mdpnp.gip.ui.values;

import org.mdpnp.gip.ui.units.MassUnits;
import org.mdpnp.gip.ui.units.MassUnitsFactory;

/**
 * @author Jeff Plourde
 *
 */
public class MassValueImpl extends AbstractValueImpl<MassUnits> implements MassValue {
	public MassValueImpl() {
		super(MassUnitsFactory.micrograms);
	}
	public MassValueImpl(MassUnits... u) {
		super(u);
	}
	public MassValueImpl(Double minimum, Double maximum, MassUnits... u) {
		super(minimum, maximum, u);
	}
	public MassValueImpl(Double minimum, Double maximum, int fractionDigits, Double stepSize, MassUnits... u) {
		super(minimum, maximum, fractionDigits, stepSize, u);
	}
	@Override
	protected void doFireEvent(Object event, ValueListener<MassUnits> listener) {
		if(UNITS_CHANGED.equals(event)) {
			listener.unitsChanged(this);
		} else if(VALUE_CHANGED.equals(event)) {
			listener.valueChanged(this);
		} else if(BOUNDS_CHANGED.equals(event)) {
			listener.boundsChanged(this);
		}
	}
}
