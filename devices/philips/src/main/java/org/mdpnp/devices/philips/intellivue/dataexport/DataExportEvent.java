package org.mdpnp.devices.philips.intellivue.dataexport;

import java.nio.ByteBuffer;

import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;

public interface DataExportEvent extends Parseable, Formatable {
	void parseMore(ByteBuffer bb);
}
