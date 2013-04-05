package org.mdpnp.devices.philips.intellivue.dataexport.command;

import org.mdpnp.devices.philips.intellivue.data.AttributeValueList;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportCommand;

public interface SetResult extends DataExportCommand {
	AttributeValueList getAttributes();
}
