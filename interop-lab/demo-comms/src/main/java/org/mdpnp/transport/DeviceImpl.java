package org.mdpnp.transport;

import org.mdpnp.comms.nomenclature.ConnectedDevice;
import org.mdpnp.comms.nomenclature.ConnectedDevice.State;

public class DeviceImpl implements MutableDevice {
	private final String source;
	private String guid;
	private String name = "<no name>";
	private String deviceModel;
	private DeviceIcon icon = new DeviceIcon();
	private long lastUpdate = System.currentTimeMillis();
	private ConnectedDevice.State connectedState;
	
	public DeviceImpl(String source, String deviceModel) {
		this.source = source;
		this.deviceModel = deviceModel;
	}
	public String getSource() {
		return source;
	}
	
	public String getGuid() {
		return guid;
	}
	public String getName() {
		return name;
	}
	@Override
	public DeviceIcon getIcon() {
		return icon;
	}

	public String getDeviceModel() {
		return deviceModel;
	}
	
	@Override
	public int hashCode() {
		return source.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Device) {
			return source.equals( ((Device)obj).getSource());
		} else {
			return false;
		}
		
	}
	
	@Override
	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}
	
	@Override
	public void setGuid(String guid) {
		this.guid = guid;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return null == name || "".equals(name) ? "<no name>" : name;
	}
	@Override
	public State getConnectedState() {
		return connectedState;
	}
	@Override
	public long getLastUpdate() {
		return lastUpdate;
	}
	@Override
	public void setConnectedState(State state) {
		this.connectedState = state;
		if(state == null) {
			icon.setConnected(false);
		} else {
			switch(state) {
			case Connected:
				icon.setConnected(true);
				break;
			default:
				icon.setConnected(false);
				break;
			}
		}
	}
	@Override
	public void setLastUpdate(long time) {
		this.lastUpdate = time;
	}
	public void setIcon(DeviceIcon icon) {
		this.icon = icon;
		setConnectedState(getConnectedState());
	}
}
