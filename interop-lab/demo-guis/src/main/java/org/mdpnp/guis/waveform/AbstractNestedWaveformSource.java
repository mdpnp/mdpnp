package org.mdpnp.guis.waveform;

public abstract class AbstractNestedWaveformSource extends AbstractWaveformSource implements NestedWaveformSource, WaveformSourceListener {
    private final WaveformSource source;

    public AbstractNestedWaveformSource(WaveformSource source) {
        this.source = source;
    }

    @Override
    public void addListener(WaveformSourceListener listener) {
        if(getListeners().isEmpty()) {
            source.addListener(this);
        }
        super.addListener(listener);
    }

    @Override
    public void removeListener(WaveformSourceListener listener) {
        super.removeListener(listener);
        if(getListeners().isEmpty()) {
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
        if(obj instanceof AbstractNestedWaveformSource) {
            return source.equals(((AbstractNestedWaveformSource)obj).source);
        } else {
            return super.equals(obj);
        }
    };
    @Override
    public float getValue(int x) {
        return null  == source ? 0 : source.getValue(x);
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
        while( source instanceof NestedWaveformSource ) {
            if(cls.isInstance(source)) {
                return (T) source;
            }
            source = ((NestedWaveformSource)source).getTarget();
        }
        return null;
    }
}
