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

public abstract class AbstractNestedWaveformSource extends AbstractWaveformSource implements NestedWaveformSource, WaveformSourceListener {
    private final WaveformSource source;

    public AbstractNestedWaveformSource(WaveformSource source) {
        this.source = source;
    }

    @Override
    public void addListener(WaveformSourceListener listener) {
        if (getListeners().isEmpty()) {
            source.addListener(this);
        }
        super.addListener(listener);
    }

    @Override
    public void removeListener(WaveformSourceListener listener) {
        super.removeListener(listener);
        if (getListeners().isEmpty()) {
            source.removeListener(this);
        }
    }

    @Override
    public WaveformSource getTarget() {
        return source;
    }

    public int hashCode() {
        return source.hashCode();
    };

    public boolean equals(Object obj) {
        if (obj instanceof AbstractNestedWaveformSource) {
            return source.equals(((AbstractNestedWaveformSource) obj).source);
        } else {
            return super.equals(obj);
        }
    };

    @Override
    public float getValue(int x) {
        return null == source ? 0 : source.getValue(x);
    }

    @Override
    public int getMax() {
        return null == source ? -1 : source.getMax();
    }

    @Override
    public double getMillisecondsPerSample() {
        return null == source ? 0.0 : source.getMillisecondsPerSample();
    }

    @Override
    public long getStartTime() {
        return null == source ? 0L : source.getStartTime();
    }

    public <T extends NestedWaveformSource> T source(Class<T> cls) {
        return source(cls, this.source);
    }

    @SuppressWarnings("unchecked")
    public static final <T extends NestedWaveformSource> T source(Class<T> cls, WaveformSource source) {
        while (source instanceof NestedWaveformSource) {
            if (cls.isInstance(source)) {
                return (T) source;
            }
            source = ((NestedWaveformSource) source).getTarget();
        }
        return null;
    }
}
