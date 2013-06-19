/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.guis.swing;

import ice.Numeric;
import ice.SampleArray;

import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.mdpnp.guis.waveform.WaveformUpdateWaveformSource;
import org.mdpnp.guis.waveform.swing.SwingWaveformPanel;

import com.rti.dds.subscription.SampleInfo;

public class ElectroCardioGramPanel extends DevicePanel {

	private final SwingWaveformPanel[] panel;
	
	private final static int[] ECG_WAVEFORMS = new int[] {
	    ice.MDC_ECG_LEAD_I.VALUE,
	    ice.MDC_ECG_LEAD_II.VALUE,
	    ice.MDC_ECG_LEAD_III.VALUE,
//	    ice.MDC_ECG_LEAD_A_VF.VALUE,
//	    ice.MDC_ECG_LEAD_A_VL.VALUE,
//	    ice.MDC_ECG_LEAD_A_VR.VALUE,
	};
	
	private final Map<Integer, WaveformUpdateWaveformSource> panelMap = new HashMap<Integer, WaveformUpdateWaveformSource>();
	public ElectroCardioGramPanel() {
		super();
		setLayout(new GridLayout(ECG_WAVEFORMS.length, 1));
		panel = new SwingWaveformPanel[ECG_WAVEFORMS.length];
		for(int i = 0; i < panel.length; i++) {
			add(panel[i] = new SwingWaveformPanel());
			WaveformUpdateWaveformSource wuws = new WaveformUpdateWaveformSource();
			panel[i].setSource(wuws);
			panelMap.put(ECG_WAVEFORMS[i], wuws);
			
		}
	}
	
	
	public static boolean supported(Set<Integer> identifiers) {
		for(int w : ECG_WAVEFORMS) {
			if(identifiers.contains(w)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void numeric(Numeric numeric, SampleInfo sampleInfo) {
	    
	}
	@Override
	public void sampleArray(SampleArray sampleArray, SampleInfo sampleInfo) {
	    WaveformUpdateWaveformSource wuws = panelMap.get(sampleArray.name);
        if(null != wuws) {
            wuws.applyUpdate(sampleArray);
        }
	}

}
