package org.mdpnp.devices.io.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StateMachine<T> {
	
	private final T[][] legalTransitions;
	private T state;
	
	private static final Logger log = LoggerFactory.getLogger(StateMachine.class);
	
	public StateMachine(T [][] legalTransitions, T initialState) {
		this.state = initialState;
		this.legalTransitions = legalTransitions;
	}
	
	public synchronized boolean wait(T state, long timeout) {
	    long giveup = System.currentTimeMillis() + timeout;
	    
	    if(timeout < 10L) {
	        log.warn("Blocking in 10ms increments so timeout of " + timeout + "ms is promoted");
	    }
	    if(0L != (timeout % 10L)) {
	        log.warn("Blocking in 10ms increments so timeout of " + timeout +"ms made coarser");
	    }
	    
	    while(!state.equals(this.state) && System.currentTimeMillis() <= giveup) {
	        try {
                wait(10L);
            } catch (InterruptedException e) {
                log.error("interrupted waiting for " + state, e);
            }
	    }
	    return state.equals(this.state);
	}
	
	public synchronized boolean legalTransition(T state) {
		// TODO technically transitions from State A to State A should be explicitly legalized
//		if(this.state.equals(state)) {
//			return true;
//		}
		for(int i = 0; i < legalTransitions.length; i++) {
			if(this.state.equals(legalTransitions[i][0]) && state.equals(legalTransitions[i][1])) {
				return true;
			}
		}
		return false;
	}
	
	public void emit(T newState, T oldState) {
		
	}
	
	public synchronized T getState() {
		return state;
	}
	
	public synchronized boolean transitionIfLegal(T state) {
		if(legalTransition(state)) {
			T oldState = this.state;
//			log.trace(this.state + " -----> " + state);
			this.state = state;
			this.notifyAll();
			emit(state, oldState);
			return true;
		} else {
			log.trace("NO " + this.state + " --/--> " + state);
			Thread.dumpStack();
			return false;
		}
	}
	
	public boolean transitionWhenLegal(T state)  {
		return transitionWhenLegal(state, getTransitionTimeout());
	}
	
	public boolean transitionWhenLegal(T state, long timeout) {
		return transitionWhenLegal(state, timeout, null);
	}
	
	public synchronized boolean transitionWhenLegal(T state, long timeout, T[] priorState) {
		T _priorState;
		long giveup = System.currentTimeMillis() + timeout;
		while(!legalTransition(state) && System.currentTimeMillis() < giveup) {
			try {
				this.wait(giveup - System.currentTimeMillis());
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		_priorState = this.state;
		if(!transitionIfLegal(state)) {
			if(isTimeoutFatal()) {
				throw new RuntimeException("Unable to transition from " + this.state + " to " + state + " after waiting " + timeout + "ms");
			} else {
				return false;
			}
		}
		if(null != priorState && priorState.length > 0) {
			priorState[0] = _priorState;
		}
		return true;
	}
	protected long getTransitionTimeout() {
		return 2000L;
	}
	protected boolean isTimeoutFatal() {
		return true;
	}
}
