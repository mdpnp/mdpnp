/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.comms.connected;

import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.GatewayListener;
import org.mdpnp.comms.IdentifiableUpdate;
import org.mdpnp.comms.Identifier;
import org.mdpnp.comms.data.enumeration.EnumerationUpdate;
import org.mdpnp.comms.data.identifierarray.MutableIdentifierArrayUpdate;
import org.mdpnp.comms.data.identifierarray.MutableIdentifierArrayUpdateImpl;
import org.mdpnp.comms.data.text.MutableTextUpdate;
import org.mdpnp.comms.data.text.MutableTextUpdateImpl;
import org.mdpnp.comms.data.textarray.TextArrayUpdate;
import org.mdpnp.comms.nomenclature.ConnectedDevice;
import org.mdpnp.comms.nomenclature.Device;
import org.mdpnp.comms.nomenclature.SerialDevice;
import org.mdpnp.comms.nomenclature.ConnectedDevice.ConnectionType;
import org.mdpnp.comms.nomenclature.ConnectedDevice.State;

public abstract class AbstractGetConnected implements GatewayListener {
	private boolean closing = false;
	private ConnectedDevice.State deviceState;
	private ConnectedDevice.ConnectionType deviceType;
	private String[] serialPorts;
	private String connectTo = null;
	private final Gateway gateway;
	
	public AbstractGetConnected(Gateway gateway) {
		this.gateway = gateway;
		gateway.addListener(this);
		
	}

	@Override
	public void update(IdentifiableUpdate<?> update) {
		if(ConnectedDevice.STATE.equals(update.getIdentifier())) {
			synchronized(this) {
				deviceState = (State) ((EnumerationUpdate)update).getValue();
				this.notifyAll();
			}
			issueConnect();
		} else if(SerialDevice.SERIAL_PORTS.equals(update.getIdentifier())) {
			serialPorts = ((TextArrayUpdate)update).getValue();
			issueConnect();
		} else if(ConnectedDevice.CONNECTION_TYPE.equals(update.getIdentifier())) {
			deviceType = (ConnectionType) ((EnumerationUpdate)update).getValue();
			issueConnect();
		}	
	}
	
	public void connect() {
		MutableIdentifierArrayUpdate miau = new MutableIdentifierArrayUpdateImpl(Device.REQUEST_IDENTIFIED_UPDATES);
		miau.setValue(new Identifier[] { Device.NAME, Device.GUID, SerialDevice.SERIAL_PORTS, ConnectedDevice.CONNECTION_TYPE, ConnectedDevice.STATE, Device.GET_AVAILABLE_IDENTIFIERS });
		gateway.update(miau);
	}
	
	public void disconnect() {
		long start = System.currentTimeMillis();
		closing = true;
		MutableTextUpdate disconnect = new MutableTextUpdateImpl(ConnectedDevice.DISCONNECT);
		disconnect.setValue("APP IS CLOSING");
		
		boolean disconnected = false;
		
		while(!disconnected) {
			gateway.update(this, disconnect);
			synchronized(this) {
				disconnected = ConnectedDevice.State.Disconnected.equals(deviceState);
				if(!disconnected) {
					if( (System.currentTimeMillis()-start) >= 10000L) {
						return;
					}
					try {
						this.wait(1000L);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}

		}
	}
	
	protected abstract void abortConnect();
	protected abstract String addressFromUser();
	protected abstract String addressFromUserList(String[] list);
	protected abstract boolean isFixedAddress();
	
	private boolean issuingConnect;
	
	private void issueConnect() {
		synchronized(this) {
			if(issuingConnect) {
				return;
			} else {
				issuingConnect = true;
				notifyAll();
			}
		}
		try {
	//		System.err.println("connectTo="+connectTo+" deviceType="+deviceType+" serialPorts="+serialPorts+" deviceState="+deviceState);
			if(null == connectTo && !closing && deviceType != null && (isFixedAddress() || serialPorts != null || !ConnectedDevice.ConnectionType.Serial.equals(deviceType)) && ConnectedDevice.State.Disconnected.equals(deviceState)) {
				switch(deviceType) {
				case Network:
					connectTo = addressFromUser();
					if(null == connectTo) {
						abortConnect();
						return;
					}
					break;
				case Serial:
					connectTo = addressFromUserList(serialPorts);
					if(null == connectTo) {
						abortConnect();
						return;
					}
					break;
				default:
					connectTo = "";
				}
				MutableTextUpdate mtu = new MutableTextUpdateImpl(ConnectedDevice.CONNECT_TO);
				mtu.setValue(connectTo);
				gateway.update(mtu);
			}
		} finally {
			synchronized(this) {
				issuingConnect = false;
				notifyAll();
			}
		}
		
	}

}
