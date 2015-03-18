/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.devices.io.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class StateMachine<T> {

    private final T[][] legalTransitions;
    private T state;
    private String transitionNote;

    private static final Logger log = LoggerFactory.getLogger(StateMachine.class);

    public StateMachine(T[][] legalTransitions, T initialState, String transitionNote) {
        this.state = initialState;
        this.legalTransitions = legalTransitions;
        this.transitionNote = transitionNote;
    }

    public synchronized boolean wait(T state, long timeout) {
        long giveup = System.currentTimeMillis() + timeout;

        if (timeout < 10L) {
            log.warn("Blocking in 10ms increments so timeout of " + timeout + "ms is promoted");
        }
        if (0L != (timeout % 10L)) {
            log.warn("Blocking in 10ms increments so timeout of " + timeout + "ms made coarser");
        }

        while (!state.equals(this.state) && System.currentTimeMillis() <= giveup) {
            try {
                wait(10L);
            } catch (InterruptedException e) {
                log.error("interrupted waiting for " + state, e);
            }
        }
        return state.equals(this.state);
    }

    public synchronized boolean legalTransition(T state) {
        // TODO technically transitions from State A to State A should be
        // explicitly legalized
        // if(this.state.equals(state)) {
        // return true;
        // }
        for (int i = 0; i < legalTransitions.length; i++) {
            if (this.state.equals(legalTransitions[i][0]) && state.equals(legalTransitions[i][1])) {
                return true;
            }
        }
        return false;
    }

    public void emit(T newState, T oldState, String transitionNote) {

    }

    public synchronized T getState() {
        return state;
    }
    
    public synchronized String getTransitionNote() {
        return transitionNote;
    }

    public synchronized boolean transitionIfLegal(T state, String transitionNote) {
        if (legalTransition(state)) {
            T oldState = this.state;
            // log.trace(this.state + " -----> " + state);
            this.state = state;
            this.transitionNote = transitionNote;
            this.notifyAll();
            emit(state, oldState, transitionNote);
            return true;
        } else {
            log.trace("NO " + this.state + " --/--> " + state, new Exception().fillInStackTrace());
            return false;
        }
    }

    public boolean transitionWhenLegal(T state, String transitionNote) {
        return transitionWhenLegal(state, getTransitionTimeout(), transitionNote);
    }

    public boolean transitionWhenLegal(T state, long timeout, String transitionNote) {
        return transitionWhenLegal(state, timeout, null, transitionNote);
    }

    public synchronized boolean transitionWhenLegal(T state, long timeout, T[] priorState, String transitionNote) {
        T _priorState;
        long giveup = System.currentTimeMillis() + timeout;
        while (!legalTransition(state) && System.currentTimeMillis() < giveup) {
            try {
                this.wait(giveup - System.currentTimeMillis());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        _priorState = this.state;
        if (!transitionIfLegal(state, transitionNote)) {
            if (isTimeoutFatal()) {
                throw new RuntimeException("Unable to transition from " + this.state + " to " + state + " after waiting " + timeout + "ms");
            } else {
                return false;
            }
        }
        if (null != priorState && priorState.length > 0) {
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
