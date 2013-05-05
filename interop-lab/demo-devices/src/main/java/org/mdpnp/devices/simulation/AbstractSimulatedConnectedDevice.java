/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.simulation;

import java.util.UUID;

import org.mdpnp.devices.connected.AbstractConnectedDevice;
import org.mdpnp.messaging.Gateway;

public abstract class AbstractSimulatedConnectedDevice extends AbstractConnectedDevice implements SimulatedConnectedDevice {
	protected Throwable t;
	
	public AbstractSimulatedConnectedDevice(Gateway gateway) {
		super(gateway);
		guidUpdate.setValue(UUID.randomUUID().toString());
	}
	
	public Throwable getLastError() {
		return t;
	}

	@Override
	public void connect(String str) {
		switch(getState()) {
		case Connected:
		case Connecting:
		case Negotiating:
			return;
		default:
		}
		if(!stateMachine.transitionWhenLegal(State.Connecting, 1000L)) {
			throw new RuntimeException("Unable to enter Connecting State");
		}
		if(!stateMachine.transitionWhenLegal(State.Negotiating, 1000L)) {
			throw new RuntimeException("Unable to enter Negotiating State");
		}
		if(!stateMachine.transitionWhenLegal(State.Connected, 1000L)) {
			throw new RuntimeException("Unable to enter Connected State");
		}		
	}

	@Override
	public void disconnect() {
		switch(getState()) {
		case Disconnected:
		case Disconnecting:
			return;
		default:
			break;
		}
		if(!stateMachine.transitionWhenLegal(State.Disconnecting, 1000L)) {
			throw new RuntimeException("Unable to enter Disconnecting State");
		}
		if(!stateMachine.transitionWhenLegal(State.Disconnected, 1000L)) {
			throw new RuntimeException("Unable to enter Disconnected State");
		}

	}
	
	@Override
	protected ConnectionType getConnectionType() {
		return ConnectionType.Simulated;
	}
	
	public String getConnectionInfo() {
		return null;
	}
}
