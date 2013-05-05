/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.guis.waveform;

import org.mdpnp.data.waveform.WaveformUpdate;

public class WaveformUpdateWaveformSource extends AbstractWaveformSource {
	private WaveformUpdate lastUpdate;
	
	public void applyUpdate(WaveformUpdate update) {
		this.lastUpdate = update;
		fireWaveform();
	}
	
	public void reset() {
		fireReset();
	}
	
	@Override
	public int getValue(int x) {
		if(null == lastUpdate) {
			return 0; 
		} else {
			Number[] values = lastUpdate.getValues();
			if(null == values) {
				return 0;
			} else {
				Number value = values[x];
				if(null == value) {
					return 0;
				} else {
					return value.intValue();
				}
			}
		}
	}

	@Override
	public int getMax() {
		return null == lastUpdate.getValues() ? 0 : lastUpdate.getValues().length;
	}
	
	@Override
	public int getCount() {
		return -1;
	}

	@Override
	public double getMillisecondsPerSample() {
		return lastUpdate.getMillisecondsPerSample();
	}
}
