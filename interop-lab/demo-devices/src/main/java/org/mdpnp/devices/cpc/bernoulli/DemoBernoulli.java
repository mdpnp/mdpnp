package org.mdpnp.devices.cpc.bernoulli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.IdentifiableUpdate;
import org.mdpnp.comms.Identifier;
import org.mdpnp.comms.MutableIdentifiableUpdate;
import org.mdpnp.comms.connected.AbstractConnectedDevice;
import org.mdpnp.comms.connected.TimeAwareInputStream;
import org.mdpnp.comms.data.numeric.MutableNumericUpdate;
import org.mdpnp.comms.data.numeric.MutableNumericUpdateImpl;
import org.mdpnp.comms.data.numeric.Numeric;
import org.mdpnp.comms.data.text.MutableTextUpdate;
import org.mdpnp.comms.data.text.MutableTextUpdateImpl;
import org.mdpnp.comms.data.text.Text;
import org.mdpnp.comms.data.waveform.MutableWaveformUpdate;
import org.mdpnp.comms.data.waveform.MutableWaveformUpdateImpl;
import org.mdpnp.comms.data.waveform.Waveform;
import org.mdpnp.comms.nomenclature.NoninvasiveBloodPressure;
import org.mdpnp.comms.nomenclature.PulseOximeter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

public class DemoBernoulli extends AbstractConnectedDevice implements Runnable {
	protected final Map<String, MutableNumericUpdate> numerics = new HashMap<String, MutableNumericUpdate>();
	protected final Map<String, MutableTextUpdate> texts = new HashMap<String, MutableTextUpdate>();
	protected final Map<String, MutableWaveformUpdate> waveforms = new HashMap<String, MutableWaveformUpdate>();
	
	private class MyBernoulli extends Bernoulli {

		public MyBernoulli() {
			
		}
		@Override
		public void startDocument() throws SAXException {
			super.startDocument();
			inited = true;
		}
		protected void measurement(String name, String value) {
			MutableNumericUpdate numeric = numerics.get(name);
			MutableTextUpdate text = texts.get(name);
			
			if(null != numeric) {
				try {
					numeric.setValue(Double.parseDouble(value));
				} catch (NumberFormatException nfe) {
					numeric.setValue(null);
				}
				gateway.update(DemoBernoulli.this, numeric);
				log.debug(numeric.toString());
				return;
			} else if(null != text) {
				text.setValue(value);
				gateway.update(DemoBernoulli.this, text);
				log.debug(text.toString());
				return;
			} else {
			    log.warn("Orphaned Measure:"+name+"="+value);
			}
			
		}
		@Override
		protected void measurementGroup(String name, Number[] n, double msPerSample) {
			super.measurementGroup(name, n, msPerSample);
			MutableWaveformUpdate waveform = waveforms.get(name);
			if(null != waveform) {
				waveform.setMillisecondsPerSample(msPerSample);
				waveform.setValues(n);
				// TODO ugly and not correct
				waveform.setTimestamp(new Date());
				gateway.update(DemoBernoulli.this, waveform);
				log.debug(waveform.toString());
			} else {
			    log.warn("Orphaned Measure:"+name+"="+Arrays.toString(n));
			}
		}
		@Override
		protected void device(String bid, String make, String model) {
			super.device(bid, make, model);
			guidUpdate.setValue(bid);
			nameUpdate.setValue(null==model?null:model.replaceAll("\\_", " "));
			gateway.update(DemoBernoulli.this, guidUpdate, nameUpdate);
		}
	}
	
	protected void add(String name, Identifier i) {
		if(i instanceof Text) {
			add(name, new MutableTextUpdateImpl((Text) i));
		} else if(i instanceof Numeric) {
			add(name, new MutableNumericUpdateImpl((Numeric) i));
		} else if(i instanceof Waveform) {
			add(name, new MutableWaveformUpdateImpl((Waveform) i));
		}
	}
	
	protected void add(String name, MutableIdentifiableUpdate<?> update) {
		if(update instanceof MutableTextUpdate) {
			texts.put(name, (MutableTextUpdate) update);
			add(update);
		} else if(update instanceof MutableNumericUpdate) {
			numerics.put(name, (MutableNumericUpdate) update);
			add(update);
		} else if(update instanceof MutableWaveformUpdate) {
			waveforms.put(name, (MutableWaveformUpdate) update);
			add(update);
		}
	}
	
	public DemoBernoulli(Gateway gateway) {
		super(gateway);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(DemoBernoulli.class.getResourceAsStream("bernoulli.map")));
			String line = null;
			// TODO this is a kluge until nomenclature ideas are more mature
			String prefix = PulseOximeter.class.getPackage().getName()+".";
			while(null != (line = br.readLine())) {
				line = line.trim();
				if('#'!=line.charAt(0)) {
					String v[] = line.split("\t");
					String c[] = v[1].split("\\.");
					add(v[0], (Identifier) Class.forName(prefix+c[0]).getField(c[1]).get(null));
				}
			}
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		if(MAX_QUIET_TIME>0L) {
            executor.scheduleAtFixedRate(new Runnable() {
                public void run() {
                    watchdog();
                }
            }, 0L, MAX_QUIET_TIME, TimeUnit.MILLISECONDS);
        }
	}
	
	private final MyBernoulli myBernoulli = new MyBernoulli();
	
	@Override
	protected ConnectionType getConnectionType() {
		return ConnectionType.Network;
	}

	@Override
	protected String iconResourceName() {
		return "450c.png";
	}
	@Override
	protected void connect(String str) {
		int port = 17008;
		if(str.contains(":")) {
			int colon = str.lastIndexOf(':');
			port = Integer.parseInt(str.substring(colon+1, str.length()));
			str = str.substring(0, colon);
		}
		
		log.trace("connect requested to " + str);
		synchronized(this) {
			this.host = str;
			this.port = port;

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
			currentThread = new Thread(this, "BernoulliImpl Processing");
			currentThread.setDaemon(true);
			currentThread.start();
		}

	}
	private long previousAttempt = 0L;
	private TimeAwareInputStream tais;
	
	@Override
	public void run() {
		log.info(Thread.currentThread().getName() + " (" + Thread.currentThread().getId() + ") begins");
		
		Socket socket = null;
		
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
			log.trace("Invoking Socket.connect("+host+")");
			socket = new Socket(host, port);

			this.socket = socket;
			if(!stateMachine.transitionIfLegal(State.Negotiating)) {
				throw new IllegalStateException("Cannot begin negotiating from " + getState());
			}
			
			// This thread will drive the next state transition
			Thread t = new Thread(new Negotiation(socket.getOutputStream()), "Subscription");
			t.setDaemon(true);
			t.start();
			
			
			
			myBernoulli.process(tais = new TimeAwareInputStream(socket.getInputStream()));
		} catch (UnknownHostException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			log.info(Thread.currentThread().getName() + " (" + Thread.currentThread().getId() + ")  ends");
			State priorState = getState();
			close();
			stateMachine.transitionIfLegal(State.Disconnected);
			switch(priorState) {
			case Connected:
			case Connecting:
			case Negotiating:
				log.trace("process thread died unexpectedly, trying to reconnect");
				connect(host);
				break;
			default:
			}
		}
		
	}
	private Socket socket;
	
	protected String host;
	private int port;
	private Thread currentThread;
	
	private static final long MAX_QUIET_TIME = 4000L;
	
	protected void watchdog() {
        TimeAwareInputStream tais = this.tais;
        if(null != tais) {
            long quietTime = System.currentTimeMillis() - tais.getLastReadTime();
            if(quietTime > MAX_QUIET_TIME) {
                if(State.Connected.equals(getState())) {
                    log.warn("WATCHDOG - closing after " + quietTime + "ms quiet time (exceeds " + MAX_QUIET_TIME+")");
                    // close should cause the processing thread to end... which will spawn a connect on its exit
                    close();
                }
            }
        }
        
    }


	@Override
	protected void disconnect() {
		log.trace("disconnect requested");
		synchronized(this) {
			switch(getState()){
			case Disconnected:
			case Disconnecting:
				return;
			case Connecting:
			case Connected:
			case Negotiating:
				stateMachine.transitionWhenLegal(State.Disconnecting);

				try {
					socket.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
				break;
			}
		}
	}



	private static final Logger log = LoggerFactory.getLogger(DemoBernoulli.class);
	
	private void close() {
		log.trace("close");
		Socket socket = this.socket;
		this.socket = null;
		
		if(socket != null) {
			try {
				log.trace("attempting to close socket");
				socket.close();
				log.trace("close - socket closed without error");
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		} else {
			log.debug("close - socket was already null");
		}
	}	
	private volatile boolean inited;
	public class Negotiation implements Runnable {
		private final OutputStream outputStream;
		
		public Negotiation(OutputStream outputStream) {
			this.outputStream = outputStream;
		}
		@Override
		public void run() {
			log.trace(Thread.currentThread().getName() + " ("+ Thread.currentThread().getId() + ") begins");
		    inited = false;
			try {
				log.trace("invoking sendSubscription");
				
				long now = System.currentTimeMillis();
				long giveup = now + 2000L;
				
				Bernoulli.sendSubscription(outputStream);
				
				while(true) {
					
					synchronized(this) {
						if(inited || System.currentTimeMillis()>giveup) {
							break;
						}
						try {
							wait(500L);
						} catch (InterruptedException e) {
							log.error("", e);
						}
					}
					if(giveup > System.currentTimeMillis()) {
						Bernoulli.sendSubscription(outputStream);
					}
				}
				
				
				
//			} catch (JAXBException e) {
//				log.error(e.getMessage(), e);
			} catch (IOException e1) {
				log.error(e1.getMessage(), e1);
			} finally {
				log.trace(Thread.currentThread().getName() + " ("+ Thread.currentThread().getId() + ") ends");
				if(inited) {
					log.trace("sendSubscription returns true");
					stateMachine.transitionIfLegal(State.Connected);
				} else {
					log.trace("sendSubscription returns false");
					if(State.Negotiating.equals(getState())) {
						log.trace("canceling negotation via close()");
						close();
					}
				}
			}

			
		}
		
	}
	
	private final Runnable nibpRequest = new Runnable() {
	    public void run() {
	        int port = 2050;
	        try {
	            String host = DemoBernoulli.this.host;
	            String bid = guidUpdate.getValue();
	            if(null != host && bid != null && !"".equals(bid)) {
	                Bernoulli.sendCommand(DemoBernoulli.this.host, port, guidUpdate.getValue(), Bernoulli.CMD_REQUEST_NIBP);
	            }
            } catch (IOException e) {
                log.error("Error requesting NIBP", e);
            }
	    }
	};
	
	@Override
	public void update(IdentifiableUpdate<?> command) {
	    if(NoninvasiveBloodPressure.REQUEST_NIBP.equals(command.getIdentifier())) {
	        executor.schedule(nibpRequest, 0L, TimeUnit.MILLISECONDS);
	    }
	    super.update(command);
	}
}
