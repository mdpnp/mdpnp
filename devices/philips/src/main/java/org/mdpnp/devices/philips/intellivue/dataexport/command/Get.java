package org.mdpnp.devices.philips.intellivue.dataexport.command;

import java.util.List;

import org.mdpnp.devices.philips.intellivue.data.OIDType;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportCommand;

public interface Get extends DataExportCommand {
	List<OIDType> getAttributeId();
}
