package org.mdpnp.guis.waveform;

import com.rti.dds.subscription.SampleInfo;

import ice.Numeric;

public class NumericUpdateWaveformSource extends AbstractWaveformSource {
    private final ice.Numeric lastUpdate = new ice.Numeric();
    private final SampleInfo lastSampleInfo = new SampleInfo();
    private final double millisecondsPerSample;

    public NumericUpdateWaveformSource(double millisecondsPerSample) {
        this.millisecondsPerSample = millisecondsPerSample;
    }

    public void applyUpdate(ice.Numeric update, SampleInfo sampleInfo) {
        this.lastUpdate.copy_from(update);
        this.lastSampleInfo.copy_from(sampleInfo);
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
    @Override
    public long getStartTime() {
        return lastSampleInfo.source_timestamp.sec * 1000L + lastSampleInfo.source_timestamp.nanosec / 1000000L;
    }
}
