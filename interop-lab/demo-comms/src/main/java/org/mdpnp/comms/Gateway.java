/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.comms;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Gateway is a generic conduit for emitting and receiving messages
 * in the form of IdentifiableUpdate.
 * 
 * It is expected that messages might be relayed between this Gateway
 * and other message buses for communication outside of the process.
 * 
 * @author jplourde
 *
 */
public class Gateway {
	public Gateway() {
		
	}
	
	public void clearListeners() {
		listeners.clear();
	}
	
	public void addListener(GatewayListener listener) {
		listeners.add(listener);
	}
	public void removeListener(GatewayListener listener) {
		listeners.remove(listener);
	}
	private final java.util.Set<GatewayListener> listeners = new java.util.concurrent.CopyOnWriteArraySet<GatewayListener>();

	private static final Logger log = LoggerFactory.getLogger(Gateway.class);
	
	public void update(GatewayListener source, IdentifiableUpdate<?> update) {
		for(GatewayListener listener : listeners) {
			if(source == null || !listener.equals(source)) {
				try {
					listener.update(update);
				} catch (Throwable t) {
					log.error(t.getMessage(), t);
				}
			}
		}
	}
	
	public void update(GatewayListener source, Collection<? extends IdentifiableUpdate<?>> updates) {
		for(IdentifiableUpdate<?> update : updates) {
			update(source, update);
		}
	}

	public void update(GatewayListener source, IdentifiableUpdate<?>... updates) {
		for(IdentifiableUpdate<?> update : updates) {
			update(source, update);
		}
	}


	public void update(IdentifiableUpdate<?> update) {
		update(null, update);
	}
	
	public void update(Collection<? extends IdentifiableUpdate<?>> updates) {
		update(null, updates);
	}

	public void update(IdentifiableUpdate<?>... updates) {
		for(IdentifiableUpdate<?> update : updates) {
			update(null, update);
		}
	}
}
