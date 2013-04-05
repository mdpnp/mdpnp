package org.mdpnp.devices.philips.intellivue.dataexport.error;

import org.mdpnp.devices.philips.intellivue.data.OIDType;

public interface ErrorDetailProcessingFailure extends ErrorDetail {
	OIDType getErrorId();
	int getLength();
}
