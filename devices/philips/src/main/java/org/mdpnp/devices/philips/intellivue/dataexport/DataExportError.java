package org.mdpnp.devices.philips.intellivue.dataexport;

import org.mdpnp.devices.philips.intellivue.dataexport.error.ErrorDetail;
import org.mdpnp.devices.philips.intellivue.dataexport.error.RemoteError;

public interface DataExportError extends DataExportMessage {
	int getInvoke();
	RemoteError getError();
	ErrorDetail getErrorDetail();
}
