package org.mdpnp.devices.philips.intellivue.attribute;


import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;
import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.data.Value;

public interface Attribute<T extends Value> extends Parseable, Formatable {
	OIDType getOid();
	T getValue();
}
