package org.mdpnp.transport;

import org.mdpnp.comms.nomenclature.ConnectedDevice;


public interface Device {
	String getSource();
	
	 String getGuid();
	 String getName();
	 String getDeviceModel();
	 DeviceIcon getIcon();
	 
	 long getLastUpdate();
	 ConnectedDevice.State getConnectedState();
	
	
}
