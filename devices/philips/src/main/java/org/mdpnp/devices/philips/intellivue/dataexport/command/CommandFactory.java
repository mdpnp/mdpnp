package org.mdpnp.devices.philips.intellivue.dataexport.command;

import org.mdpnp.devices.philips.intellivue.dataexport.CommandType;
import org.mdpnp.devices.philips.intellivue.dataexport.DataExportCommand;
import org.mdpnp.devices.philips.intellivue.dataexport.command.impl.ActionImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.command.impl.ActionResultImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.command.impl.EventReportImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.command.impl.GetImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.command.impl.GetResultImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.command.impl.SetImpl;
import org.mdpnp.devices.philips.intellivue.dataexport.command.impl.SetResultImpl;

public class CommandFactory {
	public static final DataExportCommand buildCommand(CommandType commandType, boolean result) {
		switch(commandType) {
		case EventReport:
		case ConfirmedEventReport:
			return new EventReportImpl();
		case ConfirmedAction:
			return result ? new ActionResultImpl() : new ActionImpl();
		case Get:
			return result ? new GetResultImpl() : new GetImpl();
		case Set:
		case ConfirmedSet:
			return result ? new SetResultImpl() : new SetImpl();
		default:
			throw new IllegalArgumentException("Unknown command type:"+commandType);
		}
	}
}
