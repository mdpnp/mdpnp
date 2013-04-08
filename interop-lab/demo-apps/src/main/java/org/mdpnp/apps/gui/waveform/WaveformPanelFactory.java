package org.mdpnp.apps.gui.waveform;

import org.mdpnp.apps.gui.waveform.opengl.jogl.GLWaveformPanel;
import org.mdpnp.apps.gui.waveform.swing.SwingWaveformPanel;

public class WaveformPanelFactory {
	public WaveformPanelFactory() {
	}
	public WaveformPanel createWaveformPanel() {
		try {
//			return new SwingWaveformPanel();
			return new GLWaveformPanel();
		} catch (java.lang.UnsatisfiedLinkError err) {
			System.err.println("Unable to load native libraries for Java OpenGL, using swing...");
			return new SwingWaveformPanel();
		} catch (java.lang.NoClassDefFoundError err) {
			System.err.println("Unable to load classes for OpenGL ("+err.getMessage()+"), using swing...");
			return new SwingWaveformPanel();
		}
		
	}
	
	
}
