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

import java.lang.reflect.Method;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

/**
 * @author Jeff Plourde
 *
 */
public class BluetoothUtils {
	public static final BluetoothSocket connectRfcommSocket(BluetoothDevice device, int channel) {
		try {
			Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
			BluetoothSocket socket = (BluetoothSocket) m.invoke(device, channel);
			socket.connect();
			return socket;
		} catch (Throwable t) {
			Log.v(BluetoothUtils.class.getName(), "cannot use connectRfcommSocket", t);
			return null;
		}
	}
	
	public static final BluetoothSocket connectRfcommSocketToServiceRecord(BluetoothDevice device, UUID uuid) {
		try {
			BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuid);
			socket.connect();
			return socket;
		} catch(Throwable t) {
			Log.v(BluetoothUtils.class.getName(), "cannot use connectRfcommSocketToServiceRecord", t);
			return null;
		}
			
	}
	
	public static final BluetoothSocket connectInsecureRfcommSocketToServiceRecord(BluetoothDevice device, UUID uuid) {
		try {
			Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] {UUID.class});
			BluetoothSocket socket = (BluetoothSocket) m.invoke(device, uuid);
			socket.connect();
			return socket;
		} catch (Throwable t) {
			Log.v(BluetoothUtils.class.getName(), "cannot use connectInsecureRfcommSocketToServiceRecord", t);
			return null;
		}
	}
	
	public static final BluetoothSocket connect(BluetoothDevice device, UUID uuid, boolean[] canceled) {
		BluetoothSocket socket = null;
		BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
		switch(device.getBondState()) {
		case BluetoothDevice.BOND_NONE:
			Log.d(BluetoothUtils.class.getName(), "createBond:"+createBond(device));
			break;
		case BluetoothDevice.BOND_BONDED:
		case BluetoothDevice.BOND_BONDING:
		default:
		}
		for(SocketConnector conn : connectors) {
			if(canceled[0]) {
				return null;
			}
			socket = conn.connectSocket(device, uuid);
			if(canceled[0]) {
				return null;
			}
			if(socket != null) {
				Log.d(BluetoothUtils.class.getName(), conn + " success");
				return socket;
			} else {
				Log.d(BluetoothUtils.class.getName(), conn + " failed");
			}
		}
		return null;
	}
	
	interface SocketConnector {
		BluetoothSocket connectSocket(BluetoothDevice device, UUID uuid);
	}
	
	public static final Boolean createBond(BluetoothDevice device) {
		
		try {
			Method m1 = device.getClass().getMethod("createBond", new Class[] {});
			return (Boolean) m1.invoke(device);		
		} catch(Exception e) {
			return null;
		}
	}
	
	public static final BluetoothSocket connectInsecureRfcommSocket(BluetoothDevice device, int channel) {
		try {
			Method m = device.getClass().getMethod("createInsecureRfcommSocket", new Class[] {int.class});
			BluetoothSocket socket = (BluetoothSocket) m.invoke(device, channel);
			socket.connect();
			return socket;
		} catch (Throwable t) {
			Log.v(BluetoothUtils.class.getName(), "cannot use connectInsecureRfcommSocket", t);
			return null;
		}
	}
	
	private static final SocketConnector[] connectors = new SocketConnector[] {
		new SocketConnector() { public String toString() { return "connectRfcommSocketToServiceRecord"; } 
								public BluetoothSocket connectSocket(BluetoothDevice device, UUID uuid) { return connectRfcommSocketToServiceRecord(device, uuid); } },
								
//		new SocketConnector() { public String toString() { return "connectInsecureRfcommSocketToServiceRecord"; }
//								public BluetoothSocket connectSocket(BluetoothDevice device, UUID uuid) { return connectInsecureRfcommSocketToServiceRecord(device, uuid); } },
//
//		new SocketConnector() { public String toString() { return "connectRfcommSocket"; }
//							    public BluetoothSocket connectSocket(BluetoothDevice device, UUID uuid) { return connectRfcommSocket(device, 1); } },
//		
//		new SocketConnector() { public String toString() { return "connectInsecureRfcommSocket"; }
//						        public BluetoothSocket connectSocket(BluetoothDevice device, UUID uuid) { return connectInsecureRfcommSocket(device, 1); } },
	};

}
