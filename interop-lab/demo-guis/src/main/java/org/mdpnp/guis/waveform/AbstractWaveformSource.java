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
package org.mdpnp.guis.waveform;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractWaveformSource implements WaveformSource {
	private final List<WaveformSourceListener> listeners = new CopyOnWriteArrayList<WaveformSourceListener>();
	
	protected void fireWaveform() {
		for(WaveformSourceListener listener : listeners) {
			listener.waveform(this);
		}
	}

	protected void fireReset() {
		for(WaveformSourceListener listener : listeners) {
			listener.reset(this);
		}
	}
	
	@Override
	public void addListener(WaveformSourceListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(WaveformSourceListener listener) {
		listeners.remove(listener);
	}
	
	protected List<WaveformSourceListener> getListeners() {
		return listeners;
	}
}
