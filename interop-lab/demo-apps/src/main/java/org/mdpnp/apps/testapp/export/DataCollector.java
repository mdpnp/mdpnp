package org.mdpnp.apps.testapp.export;

import java.text.SimpleDateFormat;
import java.util.*;

import com.google.common.eventbus.EventBus;
import ice.MDSConnectivity;
import ice.Patient;

import javax.swing.event.EventListenerList;

import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.devices.MDSHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class DataCollector<T> implements MDSHandler.Connectivity.MDSListener {

    private static final Logger log = LoggerFactory.getLogger(DataCollector.class);

    static ThreadLocal<SimpleDateFormat> dateFormats = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd.HHmmss.SSSZ");
        }
    };

    public abstract void add(T data);
    public abstract void destroy();

    @SuppressWarnings("serial")
    public abstract static class DataSampleEvent extends EventObject {


        public DataSampleEvent(Patient p) {
            super(p);
        }
        public Patient getPatient() {
            return (Patient)getSource();
        }

        protected static Patient UNDEFINED = new Patient();
        static {
            UNDEFINED.mrn="UNDEFIEND";
        }
    }


    private final Map<String, Patient> deviceUdiToPatientMRN = Collections.synchronizedMap(new HashMap<String, Patient>());

    private final EventBus eventBus = new EventBus();

    void addDataSampleListener(Object l) {
        eventBus.register(l);
    }

    public void removeDataSampleListener(Object l) {
        eventBus.unregister(l);
    }

    void fireDataSampleEvent(NumericsDataCollector.NumericSampleEvent data) throws Exception {
        eventBus.post(data);
    }


    public DataCollector() {
    }

    @Override
    public void handleDataSampleEvent(MDSHandler.Connectivity.MDSEvent evt) {
        ice.MDSConnectivity c = (MDSConnectivity) evt.getSource();

        if(c.partition.startsWith("MRN=")) {
            log.info("udi " + c.unique_device_identifier + " is " + c.partition.substring(4, c.partition.length()));

            Patient p = new Patient();
            p.mrn = c.partition.substring(4, c.partition.length());
            deviceUdiToPatientMRN.put(c.unique_device_identifier, p);
        }
    }

    Patient resolvePatient(String deviceUID) {
        Patient p=deviceUdiToPatientMRN.get(deviceUID);
        return p == null ? DataSampleEvent.UNDEFINED : p;
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
