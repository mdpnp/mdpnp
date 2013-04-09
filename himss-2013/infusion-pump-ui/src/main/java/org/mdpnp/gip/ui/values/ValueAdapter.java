package org.mdpnp.gip.ui.values;

import org.mdpnp.gip.ui.units.Units;

public abstract class ValueAdapter<U extends Units> implements ValueListener<U> {

	protected abstract void anythingChanged(Value<U> v);
	
	@Override
	public void valueChanged(Value<U> v) {
		anythingChanged(v);
	}

	@Override
	public void unitsChanged(Value<U> v) {
		anythingChanged(v);
	}

	@Override
	public void boundsChanged(Value<U> v) {
		anythingChanged(v);
	}

}
