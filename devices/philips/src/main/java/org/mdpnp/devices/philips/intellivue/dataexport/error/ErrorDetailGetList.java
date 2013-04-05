package org.mdpnp.devices.philips.intellivue.dataexport.error;

import java.util.List;

import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;
import org.mdpnp.devices.philips.intellivue.data.ManagedObjectIdentifier;
import org.mdpnp.devices.philips.intellivue.data.OIDType;

public interface ErrorDetailGetList extends ErrorDetail {
	interface GetError extends Parseable, Formatable {
		ErrorStatus getErrorStatus();
		OIDType getOid();
	};
	ManagedObjectIdentifier getManagedObject();
	List<GetError> getList();
}
