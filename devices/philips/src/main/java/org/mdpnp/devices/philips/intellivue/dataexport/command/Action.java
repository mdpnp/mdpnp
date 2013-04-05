package org.mdpnp.devices.philips.intellivue.dataexport.command;

import org.mdpnp.devices.philips.intellivue.dataexport.DataExportAction;

public interface Action extends ActionResult {
	long getScope();
	void setScope(long x);
	void setAction(DataExportAction action);
	DataExportAction getAction();
}
