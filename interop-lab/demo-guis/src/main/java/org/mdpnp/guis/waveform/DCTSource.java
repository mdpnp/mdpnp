/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.guis.waveform;

import java.util.Set;

import org.mdpnp.devices.math.DCT;
import org.mdpnp.guis.waveform.WaveformSource;
import org.mdpnp.guis.waveform.WaveformSourceListener;

public class DCTSource implements WaveformSource, WaveformSourceListener {
	private double[] sourceData;
	private double[] data;
	private double[] coeffs;
	private final Set<WaveformSourceListener> listeners = new java.util.concurrent.CopyOnWriteArraySet<WaveformSourceListener>();
	private int count;
	private int maxCoeff;
	private double resolution = 1;
	
	private int checkMax(final int max) {
		if(data == null || max != data.length) {
			if(max == -1) {
				this.data = this.sourceData = this.coeffs = new double[0];
			} else {
				this.data = new double[max];
				this.sourceData = new double[max];
				this.coeffs = new double[max];
			}
		}
		return max;
	}
	
	public double[] getCoeffs() {
        return coeffs;
    }
	
	public double[] getData() {
        return data;
    }
	
	public double[] getSourceData() {
        return sourceData;
    }
	
	public DCTSource(WaveformSource source) {
		resolution = source.getMillisecondsPerSample();
		int max = source.getMax();
		checkMax(max);
		maxCoeff = this.coeffs.length;
		this.count = source.getCount();
		source.addListener(this);
	}
	
	@Override
	public float getValue(int x) {
		return (float)( x >= data.length ? 0 : data[x] );
	}

	@Override
	public int getMax() {
		return data.length;
	}
	
	@Override
	public double getMillisecondsPerSample() {
		return resolution;
	}

	@Override
	public int getCount() {
		return count;
	}

	@Override
	public void addListener(WaveformSourceListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(WaveformSourceListener listener) {
		listeners.remove(listener);
	}

	public void setMaxCoeff(int maxCoeff) {
		this.maxCoeff = maxCoeff;
	}
	
	public int getMaxCoeff() {
		return this.maxCoeff;
	}
	
	@Override
	public void waveform(WaveformSource source) {
		if(checkMax(source.getMax())<0) {
			return;
		}
		
		for(int i = 0; i < sourceData.length; i++) {
			sourceData[i] = source.getValue(i);
		}
		DCT.dct(sourceData, coeffs);
		DCT.idct(coeffs, 0, maxCoeff , data);
		count = source.getCount();
		for(WaveformSourceListener listener : listeners) {
			listener.waveform(this);
		}
	}

	@Override
	public void reset(WaveformSource source) {
		// TODO Auto-generated method stub
		
	}
	
}
