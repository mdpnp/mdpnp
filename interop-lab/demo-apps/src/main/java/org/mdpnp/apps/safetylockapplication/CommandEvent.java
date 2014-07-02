package org.mdpnp.apps.safetylockapplication;

import org.mdpnp.apps.safetylockapplication.Resources.Command;

public class CommandEvent {

	public Command command;
	
	public CommandEvent(Command command)
	{
		this.command = command;
	}
}
