package org.mdpnp.guis.waveform;

public interface WaveformPanel {
	void setSource(WaveformSource source);
	java.awt.Component asComponent();
	CachingWaveformSource cachingSource();
	EvenTempoWaveformSource evenTempoSource();
	void setOutOfTrack(boolean outOfTrack);
	void start();
	void stop();
	void setEvenTempo(boolean evenTempo);
	void setCaching(boolean caching);
}
