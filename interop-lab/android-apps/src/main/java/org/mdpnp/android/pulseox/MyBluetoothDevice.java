/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.android.pulseox;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Jeff Plourde
 *
 */
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
