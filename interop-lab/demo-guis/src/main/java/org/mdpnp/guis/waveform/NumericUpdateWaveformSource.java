package org.mdpnp.guis.waveform;

import ice.Numeric;

public class NumericUpdateWaveformSource extends AbstractWaveformSource {
    private final ice.Numeric lastUpdate = new ice.Numeric();
	private final double millisecondsPerSample;
	
	public NumericUpdateWaveformSource(double millisecondsPerSample) {
		this.millisecondsPerSample = millisecondsPerSample;
	}
		
	public void applyUpdate(ice.Numeric update) {
		this.lastUpdate.copy_from(update);
		fireWaveform();
	}
	
	@Override
	public float getValue(int x) {
	    return lastUpdate.value;
//		return null == lastUpdate.getValue() ? 0 : lastUpdate.getValue().intValue();
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
