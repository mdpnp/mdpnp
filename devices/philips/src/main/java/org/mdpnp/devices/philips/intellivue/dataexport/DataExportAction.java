package org.mdpnp.devices.philips.intellivue.dataexport;

import java.nio.ByteBuffer;

import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;
import org.mdpnp.devices.philips.intellivue.dataexport.command.ActionResult;

public interface DataExportAction extends Parseable, Formatable {
	void parseMore(ByteBuffer bb);
	ActionResult getAction();
	void setAction(ActionResult action);
}
