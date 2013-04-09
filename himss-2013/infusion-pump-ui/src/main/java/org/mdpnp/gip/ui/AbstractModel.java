package org.mdpnp.gip.ui;

import java.util.Set;

public abstract class AbstractModel<L extends Listener> {
	private final Set<L> listeners = new java.util.concurrent.CopyOnWriteArraySet<L>();
	
	public void addListener(L listener) {
		listeners.add(listener);
	}
	public void removeListener(L listener) {
		listeners.remove(listener);
	}
	protected abstract void doFireEvent(Object event, L listener);
	
	protected void fireEvent() {
		fireEvent(null);
	}
	
	protected void fireEvent(Object event) {
		for(L listener : listeners) {
			doFireEvent(event, listener);
		}
	}
}
