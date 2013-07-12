/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.simulation;

import org.mdpnp.devices.connected.AbstractConnectedDevice;

public abstract class AbstractSimulatedConnectedDevice extends AbstractConnectedDevice {
	protected Throwable t;
	
	public AbstractSimulatedConnectedDevice(int domainId) {
		super(domainId);
		AbstractSimulatedDevice.randomUDI(deviceIdentity);
		deviceConnectivity.universal_device_identifier = deviceIdentity.universal_device_identifier;
	    deviceConnectivityHandle = deviceConnectivityWriter.register_instance(deviceConnectivity);
	    deviceConnectivityWriter.write(deviceConnectivity, deviceConnectivityHandle);
	}
	
	public Throwable getLastError() {
		return t;
	}

	@Override
	public void connect(String str) {
	    ice.ConnectionState state = getState();
	    if(ice.ConnectionState.Connected.equals(state) ||
	       ice.ConnectionState.Connecting.equals(state) ||
	       ice.ConnectionState.Negotiating.equals(state)) {
	    } else {
    		if(!stateMachine.transitionWhenLegal(ice.ConnectionState.Connecting, 1000L)) {
    			throw new RuntimeException("Unable to enter Connecting State");
    		}
    		if(!stateMachine.transitionWhenLegal(ice.ConnectionState.Negotiating, 1000L)) {
    			throw new RuntimeException("Unable to enter Negotiating State");
    		}
    		if(!stateMachine.transitionWhenLegal(ice.ConnectionState.Connected, 1000L)) {
    			throw new RuntimeException("Unable to enter Connected State");
    		}
	    }
	}

	@Override
	public void disconnect() {
	    ice.ConnectionState state = getState();
	    if(ice.ConnectionState.Disconnected.equals(state) ||
	       ice.ConnectionState.Disconnecting.equals(state)) {
	    } else {
    		if(!stateMachine.transitionWhenLegal(ice.ConnectionState.Disconnecting, 1000L)) {
    			throw new RuntimeException("Unable to enter Disconnecting State");
    		}
    		if(!stateMachine.transitionWhenLegal(ice.ConnectionState.Disconnected, 1000L)) {
    			throw new RuntimeException("Unable to enter Disconnected State");
    		}
	    }
	}
	
	@Override
	protected ice.ConnectionType getConnectionType() {
		return ice.ConnectionType.Simulated;
	}
	
	public String getConnectionInfo() {
		return null;
	}
}
