package org.mdpnp.messaging;

import org.mdpnp.apps.testapp.DeviceIcon;
import org.mdpnp.nomenclature.ConnectedDevice;


public interface Device {
	String getSource();
	
	 String getGuid();
	 String getName();
	 String getDeviceModel();
	 DeviceIcon getIcon();
	 
	 long getLastUpdate();
	 ConnectedDevice.State getConnectedState();
	
	
}
