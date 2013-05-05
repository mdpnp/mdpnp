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
