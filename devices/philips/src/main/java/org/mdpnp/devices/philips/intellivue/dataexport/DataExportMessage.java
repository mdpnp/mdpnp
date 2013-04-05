package org.mdpnp.devices.philips.intellivue.dataexport;

import org.mdpnp.devices.philips.intellivue.Message;

public interface DataExportMessage extends Message {
	int getInvoke();
	void setInvoke(int i);
	RemoteOperation getRemoteOperation();
}
