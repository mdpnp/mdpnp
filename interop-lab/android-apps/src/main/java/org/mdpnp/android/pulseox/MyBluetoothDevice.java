package org.mdpnp.android.pulseox;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

public class MyBluetoothDevice implements Parcelable {
	private final BluetoothDevice device;
	
	public MyBluetoothDevice(BluetoothDevice device) {
		this.device = device;
	}
	
	public BluetoothDevice getDevice() {
		return device;
	}
	
	public String getName() {
		return null == device ? "Simulator":device.getName();
	}
	
	@Override
	public int hashCode() {
		return null == device ? 1 : device.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if( o instanceof MyBluetoothDevice) {
			MyBluetoothDevice mbd = (MyBluetoothDevice) o;
			if(null == device && null == mbd.device) {
				return true;
			} else if (null != device && null != mbd.device) {
				return device.equals(mbd.device);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public int describeContents() {
		return null == device ? 0 : device.describeContents();
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		if(null != device) {
			device.writeToParcel(out, flags);
		}
	}
	
}
