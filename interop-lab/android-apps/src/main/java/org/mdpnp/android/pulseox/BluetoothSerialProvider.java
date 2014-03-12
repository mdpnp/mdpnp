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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

/**
 * @author Jeff Plourde
 *
 */
public class BluetoothSerialProvider implements SerialProvider {

private final static UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	
	private static class SocketImpl implements SerialSocket {
		private final BluetoothSocket socket;
		private final String portIdentifier;
		public SocketImpl(BluetoothSocket socket, String portIdentifier) {
			this.socket = socket;
			this.portIdentifier = portIdentifier;
		}
		@Override
		public void close() throws IOException {
			socket.close();
		}
		@Override
		public InputStream getInputStream() throws IOException {
			return socket.getInputStream();
		}
		@Override
		public OutputStream getOutputStream() throws IOException {
			return socket.getOutputStream();
		}
		@Override
		public String getPortIdentifier() {
			return portIdentifier;
		}
		@Override
		public void setSerialParams(int baud, DataBits dataBits, Parity parity,
				StopBits stopBits) {
		};
	}
	@Override
		public void setDefaultSerialSettings(int baudrate, SerialSocket.DataBits dataBits,
		        SerialSocket.Parity parity, SerialSocket.StopBits stopBits) {
		}
	@Override
	public List<String> getPortNames() {
		return new ArrayList<String>();
	}

	
	private volatile boolean[] cancel = new boolean[] {false};
	@Override
	public void cancelConnect() {
		cancel[0] = true;
	}
	
	@Override
	public SerialSocket connect(String portIdentifier) {
		
		BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(portIdentifier);
		cancel[0] = false;
		BluetoothSocket socket = BluetoothUtils.connect(device, SPP_UUID, cancel);
		
		return null==socket?null:new SocketImpl(socket, portIdentifier);
	}

}
