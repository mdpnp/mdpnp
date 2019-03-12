package org.mdpnp.apps.testapp.export;

import ice.Patient;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.util.Callback;
import org.mdpnp.apps.fxbeans.ElementObserver;
import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 *
 */
public class NumericsDataCollector extends DataCollector<NumericFx> {

    private static final Logger log = LoggerFactory.getLogger(NumericsDataCollector.class);

    private final NumericFxList numericList;
    private ElementObserver<NumericFx> numericObserver;

    @Override
    public void add(NumericFx fx) {
        try {
            if (log.isTraceEnabled())
                log.trace(dateFormats.get().format(fx.getPresentation_time()) + " " + fx.getMetric_id() + "=" + fx.getValue());

            Patient patient = resolvePatient(fx.getUnique_device_identifier());
            NumericSampleEvent ev = new NumericSampleEvent(patient, fx);
            fireDataSampleEvent(ev);
        } catch (Exception e) {
            log.error("firing data sample event", e);
        }
    }

    public NumericsDataCollector(NumericFxList numericList) {

        this.numericList = numericList;
        this.numericObserver = new ElementObserver<>(numericExtractor, numericListenerGenerator, numericList);
        this.numericList.addListener(numericListener);
        this.numericList.forEach((fx)->numericObserver.attachListener(fx));
    }

    @Override
    public void destroy() {

        numericList.removeListener(numericListener);
        numericList.forEach((fx)->numericObserver.detachListener(fx));
    }

    private final ListChangeListener<NumericFx> numericListener = new ListChangeListener<NumericFx>() {
        @Override
        public void onChanged(javafx.collections.ListChangeListener.Change<? extends NumericFx> c) {
            while(c.next()) {
                if(c.wasAdded()) c.getAddedSubList().forEach((fx) -> numericObserver.attachListener(fx));
                if(c.wasRemoved()) c.getRemoved().forEach((fx) -> numericObserver.detachListener(fx));
            }
        }
    };

    private static final Callback<NumericFx, Observable[]> numericExtractor = new Callback<NumericFx, Observable[]>() {

        @Override
        public Observable[] call(NumericFx param) {
            return new Observable[] {
                    param.presentation_timeProperty()
            };
        }
    };


    private final Callback<NumericFx, InvalidationListener> numericListenerGenerator = new Callback<NumericFx, InvalidationListener>() {

        @Override
        public InvalidationListener call(final NumericFx param) {
            return new InvalidationListener() {

                @Override
                public void invalidated(Observable observable) {
                    add(param);
                }

            };
        }
    };

    @SuppressWarnings("serial")
    public static class NumericSampleEvent extends DataCollector.DataSampleEvent {

        private final long      time;
        private final double    value;
        private final NumericFx data;

        public NumericSampleEvent(NumericFx data) {
            this(UNDEFINED, data);
        }

        public NumericSampleEvent(Patient p, NumericFx v) {
            super(p);
            data = v;
            value = data.getValue();
            time = data.getDevice_time().getTime();
            //we MUST read the presentation_time property, in order to change the validity
            data.getPresentation_time();
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
        public double getValue() {
            return value;
        }
    }

    static NumericSampleEvent toEvent(String dev, String metric, int instance_id, long tMs, double val) {
        NumericFx v  = toValue(dev, metric, instance_id, new Date(tMs), val);
        NumericSampleEvent evt = new NumericSampleEvent(v);
        return evt;
    }

    static NumericFx toValue(String dev, String metric, int instance_id, Date tMs, double val) {
        NumericFx v = new NumericFx();
        v.setUnique_device_identifier(dev);
        v.setMetric_id(metric);
        v.setInstance_id(instance_id);
        v.setSource_timestamp(tMs);
        v.setDevice_time(tMs);
        v.setPresentation_time(tMs);
        v.setValue((float)val);
        return v;
    }

}
