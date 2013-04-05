package org.mdpnp.devices.philips.intellivue.dataexport;

import java.nio.ByteBuffer;

import org.mdpnp.devices.philips.intellivue.Formatable;
import org.mdpnp.devices.philips.intellivue.Parseable;
import org.mdpnp.devices.philips.intellivue.data.ManagedObjectIdentifier;

public interface DataExportCommand extends Parseable, Formatable {
	void parseMore(ByteBuffer bb);
	void setMessage(DataExportMessage message);
	DataExportMessage getMessage();
	ManagedObjectIdentifier getManagedObject();
}
