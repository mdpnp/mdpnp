package org.mdpnp.android.pulseox;

import org.mdpnp.guis.waveform.WaveformSource;

public interface WaveformRepresentation {
	void setSource(WaveformSource source);
	WaveformSource getSource();
	void setBackground(int color);
	void setForeground(int color);
	int getForeground();
	void pause();
	void resume();
	void setOutOfTrack(boolean outOfTrack);
	void onPause();
	void onResume();
}
