package org.mdpnp.guis.waveform;

import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.RETCODE_ALREADY_DELETED;
import com.rti.dds.infrastructure.RETCODE_NOT_ENABLED;
import com.rti.dds.infrastructure.RETCODE_TIMEOUT;
import com.rti.dds.subscription.DataReader;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.util.Sequence;

public abstract class AbstractDdsWaveformSource<R extends DataReader, D, S extends Sequence> implements WaveformSource {
    protected final R reader;
    protected final D keyHolder;
    protected final Class<S> sequenceClass;
    
    protected final ThreadLocal<SampleInfoSeq> sample_info_seq = new ThreadLocal<SampleInfoSeq>() {
        protected SampleInfoSeq initialValue() {
            return new SampleInfoSeq();
        }
    };
    protected final ThreadLocal<S> data_seq = new ThreadLocal<S>() {
        protected S initialValue() {
            try {
                return sequenceClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    };
    
    
    public AbstractDdsWaveformSource(final R reader, final D keyHolder, final Class<D> dataClass, final Class<S> sequenceClass) {
        this.reader = reader;
        this.sequenceClass = sequenceClass;
        try {
            this.keyHolder = (D) dataClass.getConstructor(dataClass).newInstance(keyHolder);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static final Duration_t waitDuration = new Duration_t(0,1000000);
    public boolean loadingHistoricalData() {
        try {
            reader.wait_for_historical_data(waitDuration);
            return false;
        } catch (RETCODE_TIMEOUT timeout) {
            return true;
        } catch(RETCODE_ALREADY_DELETED alreadydeleted) {
            // TODO investigate lifecycle issues
            return false;
        } catch(RETCODE_NOT_ENABLED notEnabled) {
            // TODO investigate lifecycle issues
            return false;
        }
    }

}
