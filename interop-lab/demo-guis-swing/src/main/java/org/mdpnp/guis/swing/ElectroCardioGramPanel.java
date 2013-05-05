/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.guis.swing;

import java.awt.GridLayout;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mdpnp.data.IdentifiableUpdate;
import org.mdpnp.data.Identifier;
import org.mdpnp.data.waveform.MutableWaveformUpdate;
import org.mdpnp.data.waveform.MutableWaveformUpdateImpl;
import org.mdpnp.data.waveform.Waveform;
import org.mdpnp.data.waveform.WaveformUpdate;
import org.mdpnp.guis.waveform.WaveformUpdateWaveformSource;
import org.mdpnp.guis.waveform.swing.SwingWaveformPanel;
import org.mdpnp.messaging.Gateway;
import org.mdpnp.nomenclature.ElectroCardioGram;

public class ElectroCardioGramPanel extends DevicePanel {

	private final SwingWaveformPanel[] panel;
	
	private final static Waveform[] ECG_WAVEFORMS = new Waveform[] {
		ElectroCardioGram.I,
		ElectroCardioGram.II,
		ElectroCardioGram.III,
		ElectroCardioGram.A_VF,
		ElectroCardioGram.A_VL,
		ElectroCardioGram.A_VR
	};
	private final MutableWaveformUpdate waveformUpdate[] = new MutableWaveformUpdate[ECG_WAVEFORMS.length];
	private final Map<Waveform, WaveformUpdateWaveformSource> panelMap = new HashMap<Waveform, WaveformUpdateWaveformSource>();
	public ElectroCardioGramPanel(Gateway gateway, String source) {
		super(gateway, source);
		setLayout(new GridLayout(ECG_WAVEFORMS.length, 1));
		panel = new SwingWaveformPanel[ECG_WAVEFORMS.length];
		for(int i = 0; i < panel.length; i++) {
			waveformUpdate[i] = new MutableWaveformUpdateImpl(ECG_WAVEFORMS[i]);
			add(panel[i] = new SwingWaveformPanel());
			WaveformUpdateWaveformSource wuws = new WaveformUpdateWaveformSource();
			panel[i].setSource(wuws);
			panelMap.put(ECG_WAVEFORMS[i], wuws);
			
		}
		
		registerAndRequestRequiredIdentifiedUpdates();
	}
	
	public static boolean supported(Set<Identifier> identifiers) {
		for(Waveform w : ECG_WAVEFORMS) {
			if(identifiers.contains(w)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public void setName(String name) {
		
	}

	@Override
	public void setGuid(String name) {
		
	}
	
	@Override
	protected void doUpdate(IdentifiableUpdate<?> update) {
		WaveformUpdateWaveformSource wuws = panelMap.get(update.getIdentifier());
		if(null != wuws) {
			wuws.applyUpdate((WaveformUpdate) update);
		}
	}

	@Override
	public void setIcon(Image image) {
		
	}
	
	@Override
	public Collection<Identifier> requiredIdentifiedUpdates() {
		List<Identifier> ids = new ArrayList<Identifier>(super.requiredIdentifiedUpdates());
		ids.addAll(Arrays.asList(ECG_WAVEFORMS));
		return ids;
	}

}
