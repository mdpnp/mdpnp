/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.connected.AbstractConnectedDevice;
import org.mdpnp.devices.connected.TimeAwareInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractSerialDevice extends AbstractConnectedDevice implements Runnable {
	protected abstract boolean doInitCommands(OutputStream outputStream) throws IOException;
	protected abstract void process(InputStream inputStream) throws IOException;

	protected SerialSocket socket;
	protected TimeAwareInputStream timeAwareInputStream;
	protected Throwable lastError;
	
	protected SerialProvider serialProvider;
	
	private static final Logger log = LoggerFactory.getLogger(AbstractSerialDevice.class);

	private class Watchdog implements Runnable {

		@Override
		public void run() {
			watchdog();
		}
		
	}
	
	public AbstractSerialDevice(int domainId) {
		super(domainId);
		long maxQuietTime = getMaximumQuietTime();
		if(maxQuietTime>0L) {
			executor.scheduleAtFixedRate(new Watchdog(), 0L, getMaximumQuietTime(), TimeUnit.MILLISECONDS);
		}
		deviceConnectivity.valid_targets.addAll(getSerialProvider().getPortNames());
	}
	
	public AbstractSerialDevice(int domainId, SerialSocket sock) {
		super(domainId);
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
		ice.ConnectionState state = getState();
		if(ice.ConnectionState.Disconnected.equals(state) ||
		   ice.ConnectionState.Disconnecting.equals(state)) {
		    log.trace("nothing to do getState()="+state);
		} else if(ice.ConnectionState.Connecting.equals(state)) {
			canceledConnect = true;
			serialProvider.cancelConnect();
			log.trace("canceled connecting");
		} else if(ice.ConnectionState.Connected.equals(state) ||
		          ice.ConnectionState.Negotiating.equals(state)) {
		    log.trace("getState()="+getState()+" entering Disconnecting");
			stateMachine.transitionWhenLegal(ice.ConnectionState.Disconnecting);
			log.trace("closing the AbstractSerialDevice");
			close();
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
			ice.ConnectionState state = getState();
			if(ice.ConnectionState.Connected.equals(state) ||
			   ice.ConnectionState.Negotiating.equals(state) ||
			   ice.ConnectionState.Connecting.equals(state)) {
			} else if(ice.ConnectionState.Disconnected.equals(state) ||
			          ice.ConnectionState.Disconnecting.equals(state)) {
				stateMachine.transitionWhenLegal(ice.ConnectionState.Connecting);
				
	            currentThread = new Thread(this, "AbstractSerialDevice Processing");
	            currentThread.setDaemon(true);
	            currentThread.start();
			}

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
				if(!stateMachine.transitionIfLegal(ice.ConnectionState.Negotiating)) {
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
			ice.ConnectionState priorState = getState();
			close();
			stateMachine.transitionIfLegal(ice.ConnectionState.Disconnected);
			if(ice.ConnectionState.Connecting.equals(priorState) && canceledConnect) {
			    return;
			}
			if(ice.ConnectionState.Connecting.equals(priorState) ||
			   ice.ConnectionState.Connected.equals(priorState) ||
			   ice.ConnectionState.Negotiating.equals(priorState)) {
				log.trace("process thread died unexpectedly, trying to reconnect");
				connect(portIdentifier);
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
					stateMachine.transitionIfLegal(ice.ConnectionState.Connected);
				} else {
					log.trace("doInitCommands returns false");
					if(ice.ConnectionState.Negotiating.equals(getState())) {
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
				if(ice.ConnectionState.Connected.equals(getState())) {
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
	protected ice.ConnectionType getConnectionType() {
		return ice.ConnectionType.Serial;
	}
}
