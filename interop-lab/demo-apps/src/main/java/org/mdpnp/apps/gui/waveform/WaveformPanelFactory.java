package org.mdpnp.apps.gui.waveform;

import org.mdpnp.apps.gui.waveform.opengl.jogl.GLWaveformPanel;
import org.mdpnp.apps.gui.waveform.swing.SwingWaveformPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WaveformPanelFactory {
    private final Logger log = LoggerFactory.getLogger(WaveformPanelFactory.class);
    
	public WaveformPanelFactory() {
	}
	public WaveformPanel createWaveformPanel() {
		try {
//			return new SwingWaveformPanel();
			return new GLWaveformPanel();
		} catch (java.lang.UnsatisfiedLinkError err) {
			log.warn("Unable to load native libraries for Java OpenGL, using swing...");
			return new SwingWaveformPanel();
		} catch (java.lang.NoClassDefFoundError err) {
			log.warn("Unable to load classes for OpenGL ("+err.getMessage()+"), using swing...");
			return new SwingWaveformPanel();
		}
		
	}
	
	
}