package org.mdpnp.messaging;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.mdpnp.data.IdentifiableUpdate;
import org.mdpnp.data.MutableIdentifiableUpdate;
import org.mdpnp.data.text.MutableTextUpdate;
import org.mdpnp.data.text.MutableTextUpdateImpl;
import org.mdpnp.messaging.BindingFactory.BindingType;
import org.mdpnp.nomenclature.Association;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Adapter implements ThreadFactory {
	private final MutableTextUpdate announceArriveUpdate = new MutableTextUpdateImpl(Association.ANNOUNCE_ARRIVE);
	private final MutableTextUpdate announceDepartUpdate = new MutableTextUpdateImpl(Association.ANNOUNCE_DEPART);
	
	private static final Logger log = LoggerFactory.getLogger(Adapter.class);
	
	public void announceDepart() {
		announceDepartUpdate.setSource(source);
		announceDepartUpdate.setTarget("*");
		announceDepartUpdate.setValue("");
		externalGateway.update(externalListener, announceDepartUpdate);
		log.trace("ANNOUNCE_DEPART " + source);
	}
	
	public void announceArrive() {
		announceArriveUpdate.setSource(source);
		announceArriveUpdate.setTarget("*");
		announceArriveUpdate.setValue("<xml>Smart stuff goes in here</xml>");

		externalGateway.update(externalListener, announceArriveUpdate);
		log.trace("ANNOUNCE_ARRIVE " + source);
	}
	
	public String getSource() {
		return source;
	}
	
	private final String source = UUID.randomUUID().toString();
	private final Gateway deviceGateway, externalGateway;
	private Binding binding;
	private final GatewayListener deviceListener, externalListener;
//	private final GetConnected getConnected;
	
	private final MutableTextUpdate lastUpdate = new MutableTextUpdateImpl(Association.HEARTBEAT);
	
	private final ScheduledFuture<?> heartbeat;
	
	public Adapter(Gateway deviceGateway, Gateway externalGateway, BindingType type, String settings) throws Exception {
		this.deviceGateway = deviceGateway;
		this.externalGateway = externalGateway;
		this.deviceListener = new GatewayListener() {
			@Override
			public void update(IdentifiableUpdate<?> update) {
				deviceUpdate(update);
			}
		};
		this.externalListener = new GatewayListener() {
			@Override
			public void update(IdentifiableUpdate<?> update) {
				externalUpdate(update);
			}
		};
		externalGateway.addListener(externalListener);
		deviceGateway.addListener(deviceListener);
		this.binding = BindingFactory.createBinding(type, externalGateway, Binding.Role.Device, settings);
//		this.getConnected = new GetConnected(null, deviceGateway);

		announceArrive();
		
		lastUpdate.setSource(source);
		lastUpdate.setTarget("*");
		heartbeat = executor.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				Adapter.this.externalGateway.update(externalListener, lastUpdate);
			}
			
		}, 1000L, 1000L, TimeUnit.MILLISECONDS);
	}
	
	private boolean departed = false;
	private boolean arrived = false;
	public void depart() {
		heartbeat.cancel(false);
		announceDepart();
		synchronized(this) {
			long start = System.currentTimeMillis();
			
			while(arrived && !departed) {
				try {
					this.wait(2000L);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if( (System.currentTimeMillis()-start) >= 5000L) {
					log.warn("Unable to depart the system cleanly");
					break;
				}
			}
		}
	}
	
	public void tearDown() {
//		getConnected.disconnect();
		deviceGateway.removeListener(deviceListener);
		externalGateway.removeListener(externalListener);
		Binding  binding = this.binding;
		if(null != binding) {
			binding.tearDown();
		}
		

	}
	private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor(this);

	public void deviceUpdate(final IdentifiableUpdate<?> update) {
		((MutableIdentifiableUpdate<?>)update).setSource(source);
		externalGateway.update(externalListener, update);
		
	}
	
	public void externalUpdate(final IdentifiableUpdate<?> update) {
		if(!"*".equals(update.getTarget()) && !source.equals(update.getTarget())) {
			return;
		}
		if(Association.ACKNOWLEDGE_ARRIVE.equals(update.getIdentifier())) {
			log.trace("ACKNOWLEDGE_ARRIVE received from " + update.getSource());
			
			synchronized(this) {
				this.arrived = true;
				this.notifyAll();
			}
//			executor.execute(new Runnable() {
//
//				@Override
//				public void run() {
//					getConnected.connect();
//				}
//				
//			});
		} else if(Association.ACKNOWLEDGE_DEPART.equals(update.getIdentifier())) {
			log.trace("ACKNOWLEDGE_DEPART received from " + update.getSource());
			synchronized(this) {
				this.departed = true;
				this.notifyAll();
			}
		} else if(Association.SOLICIT.equals(update.getIdentifier())) {
			log.trace("SOLICIT received from " + update.getSource());
		// TODO do I care which NC solicited the request for announcement?
//			executor.execute(new Runnable() {
//				public void run() {
					announceArrive();
//				}
//			});
		} else if(Association.class.equals(update.getIdentifier().getField().getDeclaringClass())) {
			log.trace("Not forwarding Assocation message " + update.getIdentifier() + " to device");
		} else {
			log.trace("Relay " + update.getIdentifier() + " to device ");
			deviceGateway.update(deviceListener, update);
		}
	}

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r, Adapter.class.getName());
        t.setDaemon(true);
        return t;
    }
	
}
