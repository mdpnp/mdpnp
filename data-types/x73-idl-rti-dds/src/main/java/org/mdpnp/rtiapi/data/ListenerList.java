package org.mdpnp.rtiapi.data;

import java.lang.reflect.Array;

public class ListenerList<L> {
    protected final Class<?> listenerClass;
    protected L[] listeners;
    
    @SuppressWarnings("unchecked")
    public ListenerList(Class<?> listenerClass) {
        this.listenerClass = listenerClass;
        this.listeners = (L[]) Array.newInstance(listenerClass, 2);
    }
    

    public interface Dispatcher<L> {
        void dispatch(L l);
    }
    
    @SuppressWarnings("unchecked")
    public synchronized void addListener(L l) {
        int i = 0;
        for(i = 0; i < listeners.length; i++) {
            if(null == listeners[i]) {
                listeners[i] = l;
                return;
            }
        }

        L[] newListeners = (L[]) Array.newInstance(listenerClass, 2 * listeners.length + 1);
        System.arraycopy(listeners, 0, newListeners, 0, i);
        newListeners[i] = l;
        this.listeners = newListeners;
    }
    public synchronized void removeListener(L l) {
        int i = 0;
        for(i = 0; i < listeners.length; i++) {
            if(listeners[i] != null && l.equals(listeners[i])) {
                System.arraycopy(this.listeners, i+1, this.listeners, i, listeners.length - i - 1);
                this.listeners[listeners.length-1] = null;
                return;
            }
        }
    }
    public void fire(Dispatcher<L> dispatcher) {
        L[] listeners = this.listeners;
        for(L l : listeners) {
            if(l != null) {
                dispatcher.dispatch(l);
            }
        }
    }
}
