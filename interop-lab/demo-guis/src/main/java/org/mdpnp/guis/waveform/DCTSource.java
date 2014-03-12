/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.guis.waveform;

import java.util.Set;

import org.mdpnp.devices.math.DCT;

/**
 * @author Jeff Plourde
 *
 */
public class DCTSource implements WaveformSource, WaveformSourceListener {
    private double[] sourceData;
    private double[] data;
    private double[] coeffs;
    private final Set<WaveformSourceListener> listeners = new java.util.concurrent.CopyOnWriteArraySet<WaveformSourceListener>();
    private int count;
    private int maxCoeff;
    private double resolution = 1;

    private int checkMax(final int max) {
        if (data == null || max != data.length) {
            if (max == -1) {
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
        return (float) (x >= data.length ? 0 : data[x]);
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
        if (checkMax(source.getMax()) < 0) {
            return;
        }

        for (int i = 0; i < sourceData.length; i++) {
            sourceData[i] = source.getValue(i);
        }
        DCT.dct(sourceData, coeffs);
        DCT.idct(coeffs, 0, maxCoeff, data);
        count = source.getCount();
        for (WaveformSourceListener listener : listeners) {
            listener.waveform(this);
        }
    }

    @Override
    public void reset(WaveformSource source) {
        // TODO Auto-generated method stub

    }

    @Override
    public long getStartTime() {
        return 0L;
    }

}
