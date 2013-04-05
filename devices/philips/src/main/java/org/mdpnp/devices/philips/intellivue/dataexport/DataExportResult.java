package org.mdpnp.devices.philips.intellivue.dataexport;

public interface DataExportResult extends DataExportMessage {
	CommandType getCommandType();
	void setCommandType(CommandType commandType);
	DataExportCommand getCommand();
	void setCommand(DataExportCommand dec);
	void parseMore(java.nio.ByteBuffer bb);
}
