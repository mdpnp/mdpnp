/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.data.serial;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket;


import purejavacomm.CommPortIdentifier;
import purejavacomm.SerialPort;
import purejavacomm.UnsupportedCommOperationException;

public class PureJavaCommSerialProvider implements SerialProvider {
	private static class DefaultSerialSettings {
		private final int baud;
		private final SerialSocket.DataBits dataBits;
		private final SerialSocket.Parity parity;
		private final SerialSocket.StopBits stopBits;
		
		public DefaultSerialSettings(int baud, SerialSocket.DataBits dataBits, SerialSocket.Parity parity, SerialSocket.StopBits stopBits) {
			this.baud = baud;
			this.dataBits = dataBits;
			this.parity = parity;
			this.stopBits = stopBits;
		}
		public void configurePort(SerialSocket socket) {
			socket.setSerialParams(baud, dataBits, parity, stopBits);
		}
	}
	
	/**
	 * This is adapting some semantics into what a java caller expects.
	 * By default an InputStream acquired from a PureJavaCommSerialPort
	 * will block in a system "read" call indefinitely and will not be
	 * interrupted by a call to PureJavaCommSerialPort.close()
	 * 
	 * To allow interruption this Provider calls enableReceiveTimeout on
	 * the PureJavaCommSerialPort.  This converts the InputStream.read into
	 * essentially a poll (it will return 0 at every timeout when no bytes
	 * have been received).
	 * 
	 * This Interruptible stream will hide those 0 byte returns from its
	 * clients and use the timeouts as an opportunity to determine if the
	 * port has been closed; in which case it will return EOF (-1)
	 * @author jplourde
	 *
	 */
	private static class InterruptibleInputStream extends FilterInputStream {
		private volatile boolean closed = false;
		
		protected InterruptibleInputStream(InputStream in) {
			super(in);
		}

		private final byte[] onebyte = new byte[1];
		
		@Override
		public int read() throws IOException {
			int b = read(this.onebyte);
			if(b < 0) {
				return b;
			} else {
				return this.onebyte[0];
			}
			
		}
		
		@Override
		public int read(byte[] b) throws IOException {
			int n = 0;
			while(!closed && n >= 0) {
				n = in.read(b);
				if(n > 0) {
					return n;
				}
			}
			return -1;
		}
		public int read(byte[] b, int off, int len) throws IOException {
			int n = 0;
			while(!closed && n >= 0) {
				n = in.read(b, off, len);
				if(n > 0) {
					return n;
				}
			}
			return -1;
		};

		@Override
		public void close() throws IOException {
			closed = true;
			super.close();
		}
	}
	
	
	private static class SocketImpl implements SerialSocket {
		private final SerialPort serialPort;
		private final String portIdentifier;
		private InputStream interruptibleInputStream;
		
		public SocketImpl(SerialPort serialPort, String portIdentifier) {
			this.portIdentifier = portIdentifier;
			this.serialPort = serialPort;
			
		}
		@Override
		public synchronized InputStream getInputStream() throws IOException {
			if(null == interruptibleInputStream) {
				this.interruptibleInputStream = new InterruptibleInputStream(serialPort.getInputStream());
				
			}
			return interruptibleInputStream;
		}
		@Override
		public OutputStream getOutputStream() throws IOException {
			return serialPort.getOutputStream();
		}
		@Override
		public synchronized void close() throws IOException {
			if(null != interruptibleInputStream) {
				interruptibleInputStream.close();
			}
			serialPort.close();
		}
		
		@Override
		public String getPortIdentifier() {
			return portIdentifier;
		}
		@Override
		public void setSerialParams(int baud, DataBits dataBits, Parity parity, StopBits stopBits) {
			int db = 0;
			switch(dataBits) {
			case Eight:
				db = SerialPort.DATABITS_8;
				break;
			case Seven:
				db = SerialPort.DATABITS_7;
				break;
			}
			int p = 0;
			switch(parity) {
			case None:
				p = SerialPort.PARITY_NONE;
				break;
			case Even:
				p = SerialPort.PARITY_EVEN;
				break;
			case Odd:
				p = SerialPort.PARITY_ODD;
				break;
			}
			int sb = 0;
			switch(stopBits) {
			case One:
				sb = SerialPort.STOPBITS_1;
				break;
			case OneAndOneHalf:
				sb = SerialPort.STOPBITS_1_5;
				break;
			case Two:
				sb = SerialPort.STOPBITS_2;
				break;
			}
			try {
				
				serialPort.setSerialPortParams(baud, db, sb, p);
			} catch (UnsupportedCommOperationException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public List<String> getPortNames() {
		List<String> list = new ArrayList<String>();
		Enumeration<?> e = purejavacomm.CommPortIdentifier.getPortIdentifiers();
		while(e.hasMoreElements()) {
			Object o = e.nextElement();
			if(o instanceof purejavacomm.CommPortIdentifier) {
				list.add( ((purejavacomm.CommPortIdentifier)o).getName() );
			}
		}
		Collections.sort(list);
		return list;
	}
	
	private DefaultSerialSettings defaultSettings = new DefaultSerialSettings(9600, SerialSocket.DataBits.Eight, SerialSocket.Parity.None, SerialSocket.StopBits.One);
	
	public void setDefaultSerialSettings(int baud, SerialSocket.DataBits dataBits, SerialSocket.Parity parity, SerialSocket.StopBits stopBits) {
		defaultSettings = new DefaultSerialSettings(baud, dataBits, parity, stopBits);
	}
	
	protected void doConfigurePort(SerialSocket serialPort) throws UnsupportedCommOperationException {
		defaultSettings.configurePort(serialPort);
	}
	
	@Override
	public void cancelConnect() {
		// Not so terribly much we can do here
		// added for the sake of the android impl which
		// is trying several connect methods
	}
	
	protected int getConnectTimeout() {
		return 10000;
	}
	
	public SerialSocket connect(String portIdentifier) {
		try {
//			CommPortIdentifier cpi = CommPortIdentifier.getPortIdentifier(portIdentifier);
//			if(null == cpi) {
//				throw new IllegalArgumentException("Unknown portIdentifier:" + portIdentifier);
//			}
			// getPortIdentifiers does some initialization that we desire
			Enumeration<?> e = CommPortIdentifier.getPortIdentifiers();
			while(e.hasMoreElements()) {
				CommPortIdentifier cpi = (CommPortIdentifier) e.nextElement();
				if(cpi.getName().equals(portIdentifier)) {
					SerialPort serialPort = (SerialPort) cpi.open("", getConnectTimeout());
					// TODO canonical 0.0.16 purejavacomm does not contain flush (tcflush alias)
					// perhaps it should?
//					((PureJavaSerialPort)serialPort).flush();
					// I'm not sure this is strictly necessary
					serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
					// Ensures no returns of 0 bytes
//					serialPort.enableReceiveThreshold(1);
					// Allows close to unblock read
					serialPort.enableReceiveTimeout(500);
					
					SerialSocket socket = new SocketImpl(serialPort, portIdentifier); 
					doConfigurePort(socket);

					return socket;
				}
			}
			throw new IllegalArgumentException("Unknown portIdentifier:" + portIdentifier);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}		
	}
}
