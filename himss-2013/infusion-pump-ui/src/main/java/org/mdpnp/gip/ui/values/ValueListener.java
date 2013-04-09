package org.mdpnp.gip.ui.values;

import org.mdpnp.gip.ui.Listener;
import org.mdpnp.gip.ui.units.Units;

public interface ValueListener<U extends Units> extends Listener {
	void valueChanged(Value<U> v);
	void unitsChanged(Value<U> v);
	void boundsChanged(Value<U> v);
}
