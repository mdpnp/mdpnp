package org.mdpnp.devices.philips.intellivue.dataexport.error;

import org.mdpnp.devices.philips.intellivue.data.OIDType;

public interface ErrorDetailNoSuchObjectClass extends ErrorDetail {
	OIDType getObjectClass();
}
