package org.mdpnp.devices.philips.intellivue.dataexport.error;

import org.mdpnp.devices.philips.intellivue.data.ManagedObjectIdentifier;

public interface ErrorDetailInvalidObjectInstance extends ErrorDetail {
	ManagedObjectIdentifier getManagedObject();
}
