/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.comms.serial;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.connected.AbstractConnectedDevice;
import org.mdpnp.comms.data.textarray.MutableTextArrayUpdate;
import org.mdpnp.comms.data.textarray.MutableTextArrayUpdateImpl;
import org.mdpnp.comms.nomenclature.SerialDevice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractSerialDevice extends AbstractConnectedDevice implements SerialDevice, Runnable {

	protected final MutableTextArrayUpdate serialPortsUpdate = new MutableTextArrayUpdateImpl(SerialDevice.SERIAL_PORTS);
	
	protected abstract boolean doInitCommands(OutputStream outputStream) throws IOException;
	protected abstract void process(InputStream inputStream) throws IOException;

	protected SerialSocket socket;
	protected TimeAwareInputStream timeAwareInputStream;
	protected Throwable lastError;
	
	protected SerialProvider serialProvider;
	
	private static final Logger log = LoggerFactory.getLogger(AbstractSerialDevice.class);

	private static class TimeAwareInputStream extends FilterInputStream {
		private long lastRead = 0L;
		
		protected TimeAwareInputStream(InputStream in) {
			super(in);
		}
		
		@Override
		public int read() throws IOException {
			int r = super.read();
			if(r >= 0) {
				lastRead = System.currentTimeMillis();
			}
			return r;
		}
		
		@Override
		public int read(byte[] b) throws IOException {
			int n = in.read(b);
			if(n >= 0) {
				lastRead = System.currentTimeMillis();
			}
			return n;
		}
		public int read(byte[] b, int off, int len) throws IOException {
			int n = in.read(b, off, len);
			if(n >= 0) {
				lastRead = System.currentTimeMillis();
			}
			return n;
		};
		public long getLastReadTime() {
			return lastRead;
		}
		
	}
	
	private class Watchdog implements Runnable {

		@Override
		public void run() {
			watchdog();
		}
		
	}
	
	public AbstractSerialDevice(Gateway gateway) {
		super(gateway);
		long maxQuietTime = getMaximumQuietTime();
		if(maxQuietTime>0L) {
			executor.scheduleAtFixedRate(new Watchdog(), 0L, getMaximumQuietTime(), TimeUnit.MILLISECONDS);
		}
		serialPortsUpdate.setValue(getSerialProvider().getPortNames().toArray(new String[0]));
		add(serialPortsUpdate);
		
		
	}
	
	public AbstractSerialDevice(Gateway gateway, SerialSocket sock) {
		super(gateway);
		long maxQuietTime = getMaximumQuietTime();
		if(maxQuietTime>0L) {
			executor.scheduleAtFixedRate(new Watchdog(), 0L, getMaximumQuietTime(), TimeUnit.MILLISECONDS);
		}
		this.socket = sock;
		this.portIdentifier = sock.getPortIdentifier();
	}
	
	public void setSerialProvider(SerialProvider serialProvider) {
		this.serialProvider = serialProvider;
	}
	
	public SerialProvider getSerialProvider() {
		if(null == serialProvider) {
			this.serialProvider = SerialProviderFactory.getDefaultProvider();
		}
		return serialProvider;
	}
	
	protected void setLastError(Throwable lastError) {
		log.error("setLastError", lastError);
		this.lastError = lastError;
	}
	
	public Throwable getLastError() {
		return lastError;
	}
	

	
	public SerialSocket getSocket() {
		return this.socket;
	}
	
	@Override
	public void disconnect() {
		log.trace("disconnect requested");
		synchronized(stateMachine) {
			switch(getState()){
			case Disconnected:
			case Disconnecting:
			    log.trace("nothing to do getState()="+getState());
				return;
			case Connecting:
				canceledConnect = true;
				serialProvider.cancelConnect();
				log.trace("canceled connecting");
				return;
			case Connected:
			case Negotiating:
			    log.trace("getState()="+getState()+" entering Disconnecting");
				stateMachine.transitionWhenLegal(State.Disconnecting);
				log.trace("closing the AbstractSerialDevice");
				close();
				break;
			}
		}
	}
	private void close() {
		log.trace("close");
		SerialSocket socket = this.socket;
		this.socket = null;
		this.timeAwareInputStream = null;
		
		if(socket != null) {
			try {
				log.trace("attempting to close socket");
				socket.close();
				log.trace("close - socket closed without error");
			} catch (IOException e) {
				setLastError(e);
			}
		} else {
			log.debug("close - socket was already null");
		}
	}
	private Thread currentThread;
	
	private String portIdentifier;
	
	@Override
	public void connect(String portIdentifier) {
		log.trace("connect requested to " + portIdentifier);
		synchronized(this) {
			this.portIdentifier = portIdentifier;

			switch(getState()) {
			case Connected:
			case Negotiating:
			case Connecting:
				return;
			case Disconnected:
			case Disconnecting:
				stateMachine.transitionWhenLegal(State.Connecting);
				break;
			}
			currentThread = new Thread(this, "AbstractSerialDevice Processing");
			currentThread.setDaemon(true);
			currentThread.start();
		}
	}
	
	private long previousAttempt = 0L;
	private boolean canceledConnect = false;
	
	public void run() {
		log.info(Thread.currentThread().getName() + " (" + Thread.currentThread().getId() + ") begins");
		
		SerialSocket socket = null;
		
		long now = System.currentTimeMillis();
		
		// Staying in the Connecting state while awaiting another time interval
		while(now < (previousAttempt+getConnectInterval())) {
			setConnectionInfo("Waiting to reconnect... " + ((previousAttempt+getConnectInterval()) - now) + "ms");
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				log.error("", e);
			}
			now = System.currentTimeMillis();
		}
		setConnectionInfo("");
		previousAttempt = now;
		try {
			log.trace("Invoking SerialProvider.connect("+portIdentifier+")");
			socket = getSerialProvider().connect(portIdentifier);
		
			if(null == socket) {
				log.trace("socket is null after connect");
				return;
			} else {
				this.socket = socket;
				if(!stateMachine.transitionIfLegal(State.Negotiating)) {
					throw new IllegalStateException("Cannot begin negotiating from " + getState());
				}
			}
			
			
			// This thread will drive the next state transition
			Thread t = new Thread(new Negotiation(socket.getOutputStream()), "Connection Parameters");
			t.setDaemon(true);
			t.start();
			
			
			
			process(timeAwareInputStream = new TimeAwareInputStream(socket.getInputStream()));
		} catch (IOException e) {
			// Let this thread die, it will be replaced
			log.error("processing thread ends with IOException", e);
		} finally {
			log.info(Thread.currentThread().getName() + " (" + Thread.currentThread().getId() + ")  ends");
			State priorState = getState();
			close();
			stateMachine.transitionIfLegal(State.Disconnected);
			switch(priorState) {
			case Connecting:
				if(canceledConnect) {
					return;
				}
			case Connected:
			case Negotiating:
				log.trace("process thread died unexpectedly, trying to reconnect");
				connect(portIdentifier);
				break;
			default:
			}
		}


	}
	private class Negotiation implements Runnable {
		private OutputStream outputStream;
		Negotiation(OutputStream outputStream) {
			this.outputStream = outputStream;
		}
		public void run() {
			log.trace(Thread.currentThread().getName() + " ("+ Thread.currentThread().getId() + ") begins");
			boolean inited = false;
			try {
				log.trace("invoking doInitCommands");
				inited = doInitCommands(outputStream);
				
			} catch (IOException e) {
				setLastError(e);
				inited = false;
			} finally {
				log.trace(Thread.currentThread().getName() + " ("+ Thread.currentThread().getId() + ") ends");
				if(inited) {
					log.trace("doInitCommands returns true");
					stateMachine.transitionIfLegal(State.Connected);
				} else {
					log.trace("doInitCommands returns false");
					if(State.Negotiating.equals(getState())) {
						log.trace("canceling negotation via close()");
						close();
					}
				}
			}

			
		}
	}
	
	protected void watchdog() {
		TimeAwareInputStream tais = this.timeAwareInputStream;
		if(null != tais) {
			long quietTime = System.currentTimeMillis() - timeAwareInputStream.getLastReadTime();
			if(quietTime > getMaximumQuietTime()) {
				if(State.Connected.equals(getState())) {
					log.warn("WATCHDOG - disconnecting after " + quietTime + "ms quiet time (exceeds " + getMaximumQuietTime()+")");
					disconnect();
					connect(portIdentifier);
				}
			}
		}
		
	}
	protected long getMaximumQuietTime() {
		return -1L;
	}
	

	
	@Override
	protected ConnectionType getConnectionType() {
		return ConnectionType.Serial;
	}
}
