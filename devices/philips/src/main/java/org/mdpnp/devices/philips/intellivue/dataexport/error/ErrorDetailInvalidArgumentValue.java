package org.mdpnp.devices.philips.intellivue.dataexport.error;

import org.mdpnp.devices.philips.intellivue.data.ManagedObjectIdentifier;
import org.mdpnp.devices.philips.intellivue.data.OIDType;

public interface ErrorDetailInvalidArgumentValue extends ErrorDetail {
	ManagedObjectIdentifier getManagedObject();
	OIDType getActionType();
	int getLength();
}
