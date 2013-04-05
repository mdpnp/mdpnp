package org.mdpnp.devices.philips.intellivue.dataexport;

public interface DataExportLinkedResult extends DataExportResult {
	RemoteOperationLinkedState getLinkedState();
	short getLinkedCount();
}
