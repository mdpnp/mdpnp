package org.mdpnp.devices.philips.intellivue.dataexport.event;

import org.mdpnp.devices.philips.intellivue.data.AttributeValueList;
import org.mdpnp.devices.philips.intellivue.data.ManagedObjectIdentifier;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportEvent;

public interface MdsCreateEvent extends DataExportEvent {
	AttributeValueList getAttributes();
	ManagedObjectIdentifier getManagedObject();
}
