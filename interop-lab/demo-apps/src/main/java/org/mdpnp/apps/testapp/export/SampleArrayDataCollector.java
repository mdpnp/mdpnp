package org.mdpnp.apps.testapp.export;

import ice.Patient;
import ice.SampleArray;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.util.Callback;
import org.mdpnp.apps.fxbeans.ElementObserver;
import org.mdpnp.apps.fxbeans.SampleArrayFx;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 *
 */
public class SampleArrayDataCollector extends DataCollector<SampleArrayFx> {

    private static final Logger log = LoggerFactory.getLogger(SampleArrayDataCollector.class);

    private final SampleArrayFxList sampleArrayList;
    private ElementObserver<SampleArrayFx> sampleArrayObserver;

    @Override
    public void add(SampleArrayFx fx) {

        try {
            if (log.isTraceEnabled())
                log.trace(dateFormats.get().format(fx.getPresentation_time()) + " " + fx.getMetric_id());

            Patient patient = resolvePatient(fx.getUnique_device_identifier());
            SampleArrayEvent ev = new SampleArrayEvent(patient, fx);
            fireDataSampleEvent(ev);
        } catch (Exception e) {
            log.error("firing data sample event", e);
        }

        /*
        Number[] values = fx.getValues();
        Date presentationTime = fx.getPresentation_time();
        long baseTime = presentationTime.getTime();

        final int sz = values.length;
        if (0 < fx.getFrequency()) {
            int msPerSample = (int) (1000 / fx.getFrequency());
            for (int j = 0; j < sz; j++) {
                long tm = baseTime - (sz - j) * msPerSample;
                float value = values[j].floatValue();

                if (log.isTraceEnabled())
                    log.trace(dateFormats.get().format(new Date(tm)) + " " + fx.getMetric_id() + "=" + value);

                Value v = toValue(fx.getUnique_device_identifier(), fx.getMetric_id(), fx.getInstance_id(), tm, value);
                Patient patient = resolvePatient(v.getUniqueDeviceIdentifier());
                NumericsDataCollector.NumericSampleEvent ev = new NumericsDataCollector.NumericSampleEvent(patient, v);
                try {
                    fireDataSampleEvent(ev);
                } catch (Exception e) {
                    log.error("firing data sample event", e);
                }

            }
        } else {
            log.warn("Invalid frequency " + fx.getFrequency() +
                    " for " + fx.getUnique_device_identifier() + " " +
                    fx.getMetric_id() + " " + fx.getInstance_id());
        }
        */
    }

    public SampleArrayDataCollector(SampleArrayFxList sampleArrayList) {

        this.sampleArrayList = sampleArrayList;
        this.sampleArrayObserver = new ElementObserver<>(sampleArrayExtractor, sampleArrayListenerGenerator, sampleArrayList);
        this.sampleArrayList.addListener(sampleArrayListener);
        this.sampleArrayList.forEach((fx)->sampleArrayObserver.attachListener(fx));
    }

    @Override
    public void destroy() {

        sampleArrayList.removeListener(sampleArrayListener);
        sampleArrayList.forEach((fx)->sampleArrayObserver.detachListener(fx));
    }

    private final ListChangeListener<SampleArrayFx> sampleArrayListener = new ListChangeListener<SampleArrayFx>() {
        @Override
        public void onChanged(javafx.collections.ListChangeListener.Change<? extends SampleArrayFx> c) {
            while(c.next()) {
                if(c.wasAdded()) c.getAddedSubList().forEach((fx) -> sampleArrayObserver.attachListener(fx));
                if(c.wasRemoved()) c.getRemoved().forEach((fx)->sampleArrayObserver.detachListener(fx));
            }
        }
    };

    private static final Callback<SampleArrayFx, Observable[]> sampleArrayExtractor = new Callback<SampleArrayFx, Observable[]>() {
        @Override
        public Observable[] call(SampleArrayFx param) {
            return new Observable[] {
                    param.presentation_timeProperty()
            };
        }
    };

    private final Callback<SampleArrayFx, InvalidationListener> sampleArrayListenerGenerator = new Callback<SampleArrayFx, InvalidationListener>() {

        @Override
        public InvalidationListener call(final SampleArrayFx param) {
            return new InvalidationListener() {

                @Override
                public void invalidated(Observable observable) {
                    add(param);
                }

            };
        }
    };

    @SuppressWarnings("serial")
    public static class SampleArrayEvent extends DataCollector.DataSampleEvent {

        private final long        time;
        private final Number[]    value;
        private final SampleArrayFx data;

        public SampleArrayEvent(SampleArrayFx data) {
            this(UNDEFINED, data);
        }

        public SampleArrayEvent(Patient p, SampleArrayFx v) {
            super(p);
            data = v;
            value = data.getValues();
            time = data.getDevice_time().getTime();
        }

        public String getUniqueDeviceIdentifier() {
            return data.getUnique_device_identifier();
        }
        public String getMetricId() {
            return data.getMetric_id();
        }
        public long getDevTime() {
            return time;
        }
        public int getInstanceId() {
            return data.getInstance_id();
        }
        public Number[] getValue() {
            return value;
        }
    }
}
