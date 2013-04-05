package org.mdpnp.devices.philips.intellivue.dataexport.error;

import java.util.List;

import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;
import org.mdpnp.devices.philips.intellivue.data.ManagedObjectIdentifier;
import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.dataexport.ModifyOperator;

public interface ErrorDetailSetList extends ErrorDetail {
	interface SetError extends Parseable, Formatable {
		ErrorStatus getErrorStatus();
		ModifyOperator getModifyOperator();
		OIDType getOid();
	};
	ManagedObjectIdentifier getManagedObject();
	List<SetError> getList();
}
