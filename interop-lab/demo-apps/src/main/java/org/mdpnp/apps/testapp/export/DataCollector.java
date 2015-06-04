package org.mdpnp.apps.testapp.export;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventListener;
import java.util.EventObject;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.util.Callback;

import javax.swing.event.EventListenerList;

import org.mdpnp.apps.fxbeans.ElementObserver;
import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.SampleArrayFx;
import org.mdpnp.apps.fxbeans.SampleArrayFxList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataCollector {

    private static final Logger log = LoggerFactory.getLogger(DataCollector.class);

    static ThreadLocal<SimpleDateFormat> dateFormats = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd.HHmmss.SSSZ");
        }
    };

    @SuppressWarnings("serial")
    public static class DataSampleEvent extends EventObject {
        public DataSampleEvent(Object source) {
            super(source);
        }
    }

    public interface DataSampleEventListener extends EventListener {
        public void handleDataSampleEvent(DataSampleEvent evt) throws Exception;
    }

    EventListenerList listenerList = new EventListenerList();

    public void addDataSampleListener(DataSampleEventListener l) {
        listenerList.add(DataSampleEventListener.class, l);
    }

    public void removeDataSampleListener(DataSampleEventListener l) {
        listenerList.remove(DataSampleEventListener.class, l);
    }

    void fireDataSampleEvent(DataSampleEvent data) throws Exception{
        DataSampleEventListener listeners[] = listenerList.getListeners(DataSampleEventListener.class);
        for(DataSampleEventListener l : listeners) {
            l.handleDataSampleEvent(data);
        }
    }

    private final NumericFxList numericList;
    private final SampleArrayFxList sampleArrayList;
    
    public void add(NumericFx fx) {
        try {
            if (log.isTraceEnabled())
                log.trace(dateFormats.get().format(fx.getPresentation_time()) + " " + fx.getMetric_id() + "=" + fx.getValue());
            Value v = toValue(fx);
            DataSampleEvent ev = new DataSampleEvent(v);
            fireDataSampleEvent(ev);
        } catch (Exception e) {
            log.error("firing data sample event", e);
        }
    }
    
    private ElementObserver<SampleArrayFx> sampleArrayObserver;
    private ElementObserver<NumericFx> numericObserver;
    public void add(SampleArrayFx fx) {
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
                DataSampleEvent ev = new DataSampleEvent(v);
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
    
    public DataCollector(SampleArrayFxList sampleArrayList, NumericFxList numericList) {
        this.numericList = numericList;
        this.sampleArrayList = sampleArrayList;
        sampleArrayObserver = new ElementObserver<>(sampleArrayExtractor, sampleArrayListenerGenerator, sampleArrayList);
        numericObserver = new ElementObserver<>(numericExtractor, numericListenerGenerator, numericList);
        
        
        this.numericList.addListener(numericListener);
        numericList.forEach((fx)->numericObserver.attachListener(fx));
        
        this.sampleArrayList.addListener(sampleArrayListener);
        sampleArrayList.forEach((fx)->sampleArrayObserver.attachListener(fx));
    }
    
    public void destroy() {
        
        numericList.removeListener(numericListener);
        numericList.forEach((fx)->numericObserver.detachListener(fx));
        
        sampleArrayList.removeListener(sampleArrayListener);
        sampleArrayList.forEach((fx)->sampleArrayObserver.detachListener(fx));
    }

    static Value toValue(NumericFx fx) {
        Value v = new Value(fx.getUnique_device_identifier(), fx.getMetric_id(), fx.getInstance_id());
        v.updateFrom(fx.getPresentation_time().getTime(), fx.getValue());
        return v;
    }
    
    static Value toValue(String dev, String metric, int instance_id, long tMs, double val) {
        Value v = new Value(dev, metric, instance_id);
        v.updateFrom(tMs, (float) val);
        return v;
    }
}
