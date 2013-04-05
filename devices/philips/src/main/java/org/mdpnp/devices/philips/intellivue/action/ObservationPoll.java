package org.mdpnp.devices.philips.intellivue.action;

import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;
import org.mdpnp.devices.philips.intellivue.data.AttributeValueList;
import org.mdpnp.devices.philips.intellivue.data.Handle;

public interface ObservationPoll extends Parseable, Formatable {
	Handle getHandle();
	AttributeValueList getAttributes();
}