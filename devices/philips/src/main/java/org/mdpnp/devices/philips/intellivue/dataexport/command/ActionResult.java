package org.mdpnp.devices.philips.intellivue.dataexport.command;

import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportAction;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportCommand;

public interface ActionResult extends DataExportCommand {

	OIDType getActionType();
	void setActionType(OIDType type);
	DataExportAction getAction();
	void setAction(DataExportAction action);
}
