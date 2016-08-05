package org.mdpnp.apps.testapp.export;

import ice.Patient;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.util.Callback;
import org.mdpnp.apps.fxbeans.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class PatientAssessmentDataCollector extends DataCollector<PatientAssessmentFx> {

    private static final Logger log = LoggerFactory.getLogger(PatientAssessmentDataCollector.class);

    private final PatientAssessmentFxList paList;
    private ElementObserver<PatientAssessmentFx> paObserver;

    @Override
    public void add(PatientAssessmentFx fx) {
        try {
            if (log.isInfoEnabled())
                log.info(dateFormats.get().format(fx.getDate_and_time()) + " " + fx.getOperator_id() + "=" + fx.getAssessments());

            Patient patient = resolvePatient(fx.getOperator_id());
            PatientAssessmentEvent ev = new PatientAssessmentEvent(patient, fx);
            fireDataSampleEvent(ev);
        } catch (Exception e) {
            log.error("firing data sample event", e);
        }
    }

    public PatientAssessmentDataCollector(PatientAssessmentFxList paList) {

        this.paList = paList;
        this.paObserver = new ElementObserver<>(paExtractor, paListenerGenerator, paList);
        this.paList.addListener(paListener);
        this.paList.forEach((fx)-> paObserver.attachListener(fx));
    }

    @Override
    public void destroy() {

        paList.removeListener(paListener);
        paList.forEach((fx)-> paObserver.detachListener(fx));
    }

    private final ListChangeListener<PatientAssessmentFx> paListener = new ListChangeListener<PatientAssessmentFx>() {
        @Override
        public void onChanged(Change<? extends PatientAssessmentFx> c) {
            while(c.next()) {
                if(c.wasAdded()) c.getAddedSubList().forEach((fx) -> paObserver.attachListener(fx));
                if(c.wasRemoved()) c.getRemoved().forEach((fx) -> paObserver.detachListener(fx));
            }
        }
    };

    private static final Callback<PatientAssessmentFx, Observable[]> paExtractor = new Callback<PatientAssessmentFx, Observable[]>() {

        @Override
        public Observable[] call(PatientAssessmentFx param) {
            return new Observable[] {
                    param.date_and_timeProperty()
            };
        }
    };

    private final Callback<PatientAssessmentFx, InvalidationListener> paListenerGenerator = new Callback<PatientAssessmentFx, InvalidationListener>() {

        @Override
        public InvalidationListener call(final PatientAssessmentFx param) {
            return new InvalidationListener() {

                @Override
                public void invalidated(Observable observable) {
                    add(param);
                }

            };
        }
    };

    @SuppressWarnings("serial")
    public static class PatientAssessmentEvent extends DataCollector.DataSampleEvent {

        private final long      time;
        private final Map.Entry<String,String> value;
        private final PatientAssessmentFx   data;

        public PatientAssessmentEvent(PatientAssessmentFx data) {
            this(UNDEFINED, data);
        }

        public PatientAssessmentEvent(Patient p, PatientAssessmentFx v) {
            super(p);
            data = v;
            value = data.getAssessments().get(0);
            time = data.getDate_and_time().getTime();
        }

        public String getUniqueDeviceIdentifier() {
            return data.getOperator_id();
        }
        public String getMetricId() {
            return "";
        }
        public long getDevTime() {
            return time;
        }
        public int getInstanceId() {
            return 0;
        }
        public Map.Entry<String,String> getValue() {
            return value;
        }
    }

    static PatientAssessmentEvent toEvent(String operatorId, long tMs, final String metric, final String txt) {
        PatientAssessmentFx v  = toValue(operatorId, new Date(tMs), metric, txt);
        PatientAssessmentEvent evt = new PatientAssessmentEvent(v);
        return evt;
    }

    static PatientAssessmentFx toValue(String operatorId, Date tMs, final String metric, final String txt) {

        PatientAssessmentFx v = new PatientAssessmentFx();
        v.setDate_and_time(tMs);
        v.setOperator_id(operatorId);

        List<Map.Entry<String,String>> l = new ArrayList<>();
        Map.Entry<String,String> ae = new Map.Entry<String,String>()
        {
            @Override
            public String getKey() {
                return metric;
            }
            @Override
            public String getValue() {
                return txt;
            }
            @Override
            public String setValue(String value) {
                return txt;
            }
        };

        l.add(ae);
        v.setAssessments(l);
        return v;
    }
}
