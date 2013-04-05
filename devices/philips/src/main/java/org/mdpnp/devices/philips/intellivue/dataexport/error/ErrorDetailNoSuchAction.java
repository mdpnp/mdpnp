package org.mdpnp.devices.philips.intellivue.dataexport.error;

import org.mdpnp.devices.philips.intellivue.data.OIDType;

public interface ErrorDetailNoSuchAction extends ErrorDetail {
	OIDType getObjectClass();
	OIDType getActionType();
}
