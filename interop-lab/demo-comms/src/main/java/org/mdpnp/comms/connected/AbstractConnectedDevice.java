/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.comms.connected;

import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.IdentifiableUpdate;
import org.mdpnp.comms.data.enumeration.MutableEnumerationUpdate;
import org.mdpnp.comms.data.enumeration.MutableEnumerationUpdateImpl;
import org.mdpnp.comms.data.text.MutableTextUpdate;
import org.mdpnp.comms.data.text.MutableTextUpdateImpl;
import org.mdpnp.comms.data.text.TextUpdate;
import org.mdpnp.comms.device.AbstractDevice;
import org.mdpnp.comms.nomenclature.ConnectedDevice;
import org.mdpnp.devices.io.util.StateMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConnectedDevice extends AbstractDevice implements ConnectedDevice {
	protected MutableTextUpdate connectionInfoUpdate = new MutableTextUpdateImpl(CONNECTION_INFO);
	protected MutableEnumerationUpdate stateUpdate = new MutableEnumerationUpdateImpl(STATE);
	protected MutableEnumerationUpdate connectionTypeUpdate = new MutableEnumerationUpdateImpl(CONNECTION_TYPE);
	
	protected final StateMachine<State> stateMachine = new StateMachine<State>(legalTransitions, State.Disconnected) {
		@Override
		public void emit(org.mdpnp.comms.nomenclature.ConnectedDevice.State newState, org.mdpnp.comms.nomenclature.ConnectedDevice.State oldState) {
			log.debug(oldState + "==>"+newState);
			stateUpdate.setValue(newState);
			gateway.update(AbstractConnectedDevice.this, stateUpdate);
		};
	};
	
	private static final Logger log = LoggerFactory.getLogger(AbstractConnectedDevice.class);
	
	public AbstractConnectedDevice(Gateway gateway) {
		super(gateway);
		add(connectionInfoUpdate);
		add(stateUpdate);
		add(connectionTypeUpdate);
		connectionTypeUpdate.setValue(getConnectionType());
		stateUpdate.setValue(State.Disconnected);
	}
	
	protected abstract void connect(String str);
	protected abstract void disconnect();
	protected abstract ConnectionType getConnectionType();
	
	@Override
	public void update(IdentifiableUpdate<?> command) {	
		if(ConnectedDevice.CONNECT_TO.equals(command.getIdentifier())) {
			connect(( (TextUpdate)command).getValue());
		} else if(ConnectedDevice.DISCONNECT.equals(command.getIdentifier())) {
			disconnect();
		} else {
			super.update(command);
		}
	}
	
	public State getState() {
		return stateMachine.getState();
	};
	
	private static final State[][] legalTransitions = new State[][] {
		// Normal "flow"
		{State.Disconnected, State.Connecting},
		{State.Connected, State.Disconnecting},
		{State.Connecting, State.Negotiating},
		{State.Negotiating, State.Connected},
		{State.Disconnecting, State.Disconnected},
		// Exception pathways
		{State.Negotiating, State.Disconnected},
		{State.Connecting, State.Disconnected},
		{State.Connected, State.Disconnected}
	};
	
	//Disconnected -> Connecting -> Negotiating -> Connected -> Disconnecting -> Disconnected
	
	
	protected void setConnectionInfo(String connectionInfo) {
		connectionInfoUpdate.setValue(connectionInfo);
		gateway.update(this, connectionInfoUpdate);
	}

	protected long getConnectInterval() {
		return 20000L;
	}
}
