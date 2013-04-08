package org.mdpnp.transport;

import org.mdpnp.comms.nomenclature.ConnectedDevice;

public interface MutableDevice extends Device {
	void setGuid(String guid);
	void setName(String name);
	void setDeviceModel(String deviceModel);
	void setIcon(DeviceIcon icon);
	
	 
	 void setLastUpdate(long time);
	 void setConnectedState(ConnectedDevice.State state);
}
