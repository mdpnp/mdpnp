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

import com.rti.dds.subscription.SampleInfo;

public class NumericUpdateWaveformSource extends AbstractWaveformSource {
    private final ice.Numeric lastUpdate = new ice.Numeric();
    private final SampleInfo lastSampleInfo = new SampleInfo();
    private final double millisecondsPerSample;

    public NumericUpdateWaveformSource(double millisecondsPerSample) {
        this.millisecondsPerSample = millisecondsPerSample;
    }

    public void applyUpdate(ice.Numeric update, SampleInfo sampleInfo) {
        this.lastUpdate.copy_from(update);
        this.lastSampleInfo.copy_from(sampleInfo);
        fireWaveform();
    }

    @Override
    public float getValue(int x) {
        return lastUpdate.value;
        // return null == lastUpdate.getValue() ? 0 :
        // lastUpdate.getValue().intValue();
    }

    @Override
    public int getMax() {
        return 1;
    }

    @Override
    public int getCount() {
        return -1;
    }

    @Override
    public double getMillisecondsPerSample() {
        return millisecondsPerSample;
    }

    public void reset() {
        fireReset();
    }

    @Override
    public long getStartTime() {
        return lastSampleInfo.source_timestamp.sec * 1000L + lastSampleInfo.source_timestamp.nanosec / 1000000L;
    }
}
