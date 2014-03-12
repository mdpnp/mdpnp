package org.mdpnp.gip.ui.values;

import org.mdpnp.gip.ui.units.VolumeUnits;
import org.mdpnp.gip.ui.units.VolumeUnitsFactory;

/**
 * @author Jeff Plourde
 *
 */
public class VolumeValueImpl extends AbstractValueImpl<VolumeUnits> implements VolumeValue {
	public VolumeValueImpl() {
		super(VolumeUnitsFactory.milliliters);
	}
	public VolumeValueImpl(VolumeUnits... u) {
		super(u);
	}
	@Override
	protected void doFireEvent(Object event, ValueListener<VolumeUnits> listener) {
		if(UNITS_CHANGED.equals(event)) {
			listener.unitsChanged(this);
		} else if(VALUE_CHANGED.equals(event)) {
			listener.valueChanged(this);
		} else if(BOUNDS_CHANGED.equals(event)) {
			listener.boundsChanged(this);
		}
	}
}
