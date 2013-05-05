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
