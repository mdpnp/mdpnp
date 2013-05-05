package org.mdpnp.android.pulseox;

import org.mdpnp.guis.waveform.WaveformSource;

import android.graphics.Color;

public interface WaveformRepresentation {
	void setSource(WaveformSource source);
	void setBackground(int color);
	void setForeground(int color);
	void pause();
	void resume();
	void setOutOfTrack(boolean outOfTrack);
}
