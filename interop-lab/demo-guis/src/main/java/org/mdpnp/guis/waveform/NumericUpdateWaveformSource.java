package org.mdpnp.guis.waveform;

import org.mdpnp.data.numeric.NumericUpdate;

public class NumericUpdateWaveformSource extends AbstractWaveformSource {
	private NumericUpdate lastUpdate;
	private final double millisecondsPerSample;
	
	public NumericUpdateWaveformSource(double millisecondsPerSample) {
		this.millisecondsPerSample = millisecondsPerSample;
	}
		
	public void applyUpdate(NumericUpdate update) {
		this.lastUpdate = update;
		fireWaveform();
	}
	
	@Override
	public int getValue(int x) {
		return null == lastUpdate.getValue() ? 0 : lastUpdate.getValue().intValue();
	}

	@Override
	public int getMax() {
		return 1;
	}
	
	@Override
	public int getCount() {
		return -1;
	}

	@Override
	public double getMillisecondsPerSample() {
		return millisecondsPerSample;
	}
	public void reset() {
		fireReset();
	}
}
