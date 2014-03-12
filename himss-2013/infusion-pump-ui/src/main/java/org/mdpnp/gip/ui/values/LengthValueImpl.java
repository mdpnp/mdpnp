package org.mdpnp.gip.ui.values;

import org.mdpnp.gip.ui.units.LengthUnits;
import org.mdpnp.gip.ui.units.LengthUnitsFactory;

/**
 * @author Jeff Plourde
 *
 */
public class LengthValueImpl extends AbstractValueImpl<LengthUnits> implements LengthValue {
	public LengthValueImpl() {
		super(LengthUnitsFactory.centimeters);
	}
	public LengthValueImpl(LengthUnits... u) {
		super(u);
	}
	public LengthValueImpl(Double minimum, Double maximum, LengthUnits... u) {
		super(minimum, maximum, u);
	}
	@Override
	protected void doFireEvent(Object event, ValueListener<LengthUnits> listener) {
		if(UNITS_CHANGED.equals(event)) {
			listener.unitsChanged(this);
		} else if(VALUE_CHANGED.equals(event)) {
			listener.valueChanged(this);
		} else if(BOUNDS_CHANGED.equals(event)) {
			listener.boundsChanged(this);
		}
	}
}
