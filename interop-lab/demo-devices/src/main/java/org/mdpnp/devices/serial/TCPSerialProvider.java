package org.mdpnp.devices.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.mdpnp.devices.serial.SerialSocket.DataBits;
import org.mdpnp.devices.serial.SerialSocket.Parity;
import org.mdpnp.devices.serial.SerialSocket.StopBits;


public class TCPSerialProvider implements SerialProvider {

	private static class TCPSerialSocket implements SerialSocket {

		private final Socket tcpSocket;
		
		public TCPSerialSocket(Socket tcpSocket) {
			this.tcpSocket = tcpSocket;
		}
		
		@Override
		public String getPortIdentifier() {
			return tcpSocket.getRemoteSocketAddress().toString();
		}

		@Override
		public void close() throws IOException {
			tcpSocket.close();
		}

		@Override
		public InputStream getInputStream() throws IOException {
			return tcpSocket.getInputStream();
		}

		@Override
		public OutputStream getOutputStream() throws IOException {
			return tcpSocket.getOutputStream();
		}

		@Override
		public void setSerialParams(int baud, DataBits dataBits, Parity parity,
				StopBits stopBits) {
			// all this needs a refactoring for another day
		}
		
	}
	
	@Override
	public List<String> getPortNames() {
		return new ArrayList<String>();
	}

	@Override
	public SerialSocket connect(String portIdentifier, long timeout) {
		String[] parts = portIdentifier.split("\\:");
		String host = parts[0];
		int port = Integer.parseInt(parts[1]);
		
		try {
			return new TCPSerialSocket(new Socket(host, port));
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void cancelConnect() {
		// we'll see if there is something to do about his maybe but TCP timeouts are pretty short
	}

	@Override
	public void setDefaultSerialSettings(int baudrate, DataBits dataBits,
			Parity parity, StopBits stopBits) {
		
	}
	
}
