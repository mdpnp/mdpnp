package org.mdpnp.apps.testapp.export;

import himss.PatientAssessment;
import ice.Patient;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.ListChangeListener;
import javafx.util.Callback;
import org.mdpnp.apps.fxbeans.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            Value v = null; //toValue(fx);
            Patient patient = resolvePatient(v.getUniqueDeviceIdentifier());
            NumericsDataCollector.NumericSampleEvent ev = new NumericsDataCollector.NumericSampleEvent(patient, v);
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
}
