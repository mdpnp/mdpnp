package org.mdpnp.devices.philips.intellivue.dataexport;

public interface DataExportInvoke extends DataExportMessage {
	CommandType getCommandType();
	void setCommandType(CommandType commandType);
	DataExportCommand getCommand();
	void setCommand(DataExportCommand dec);
}
