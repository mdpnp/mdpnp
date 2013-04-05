package org.mdpnp.devices.philips.intellivue.dataexport.command;

import org.mdpnp.devices.philips.intellivue.data.ManagedObjectIdentifier;
import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportCommand;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportEvent;

public interface EventReport extends DataExportCommand {
	ManagedObjectIdentifier getManagedObject();
	OIDType getEventType();
	void setEventType(OIDType oid);
	EventReport createConfirm();
	DataExportEvent getEvent();
	void setEvent(DataExportEvent event);
}
