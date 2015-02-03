package org.mdpnp.apps.testapp.export;


import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.*;
import com.rti.dds.subscription.*;
import com.rti.dds.topic.TopicDescription;
import ice.Numeric;
import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.apps.testapp.vital.ValueImpl;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.TopicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.EventListenerList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventListener;
import java.util.EventObject;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class DataCollector {

    private static final Logger log = LoggerFactory.getLogger(DataCollector.class);

    static ThreadLocal<SimpleDateFormat> dateFormats = new ThreadLocal<SimpleDateFormat>() {
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMddHHmmss.SSSZ");
        }
    };

    static class DataSampleEvent extends EventObject {
        public DataSampleEvent(Object source) {
            super(source);
        }
    }

    interface DataSampleEventListener extends EventListener {
        public void handleDataSampleEvent(DataSampleEvent evt) throws Exception;
    }

    EventListenerList listenerList = new EventListenerList();

    public void addDataSampleListener(DataSampleEventListener l) {
        listenerList.add(DataSampleEventListener.class, l);
    }

    public void removeDataSampleListener(DataSampleEventListener l) {
        listenerList.remove(DataSampleEventListener.class, l);
    }

    protected void fireDataSampleEvent(DataSampleEvent data) throws Exception{
        DataSampleEventListener listeners[] = listenerList.getListeners(DataSampleEventListener.class);
        for(DataSampleEventListener l : listeners) {
            l.handleDataSampleEvent(data);
        }
    }

    private final ice.SampleArrayDataReader saReader;
    private final ice.NumericDataReader     nReader;

    private DataHandler worker = null;

    public DataCollector(DomainParticipant participant) {

        // Inform the participant about the sample array data type we would like to use in our endpoints
        ice.SampleArrayTypeSupport.register_type(participant, ice.SampleArrayTypeSupport.get_type_name());

        // Inform the participant about the numeric data type we would like to use in our endpoints
        ice.NumericTypeSupport.register_type(participant, ice.NumericTypeSupport.get_type_name());

        // A topic the mechanism by which reader and writer endpoints are matched.
        TopicDescription sampleArrayTopic = TopicUtil.lookupOrCreateTopic(participant, ice.SampleArrayTopic.VALUE, ice.SampleArrayTypeSupport.class);
        /*
        Topic sampleArrayTopic = participant.create_topic(ice.SampleArrayTopic.VALUE,
                                                            ice.SampleArrayTypeSupport.get_type_name(),
                                                            DomainParticipant.TOPIC_QOS_DEFAULT, null,
                                                            StatusKind.STATUS_MASK_NONE);
        */

        // A second topic if for Numeric data
        TopicDescription numericTopic = TopicUtil.lookupOrCreateTopic(participant, ice.NumericTopic.VALUE, ice.NumericTypeSupport.class);
        /*
        Topic numericTopic = participant.create_topic(ice.NumericTopic.VALUE,
                ice.NumericTypeSupport.get_type_name(),
                DomainParticipant.TOPIC_QOS_DEFAULT, null,
                StatusKind.STATUS_MASK_NONE);
        */

        // Create a reader endpoint for waveform data
        saReader =
                (ice.SampleArrayDataReader) participant.create_datareader_with_profile(sampleArrayTopic,
                        QosProfiles.ice_library, QosProfiles.waveform_data, null, StatusKind.STATUS_MASK_NONE);

        nReader =
                (ice.NumericDataReader) participant.create_datareader_with_profile(numericTopic,
                        QosProfiles.ice_library, QosProfiles.numeric_data, null, StatusKind.STATUS_MASK_NONE);

        // Here we configure the status condition to trigger when new data becomes available to the reader
        saReader.get_statuscondition().set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);

        nReader.get_statuscondition().set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);

    }

    public synchronized void start() {
        worker = new DataHandler();
        (new Thread(worker, "DataCollector")).start();
    }

    public synchronized void stop() throws Exception {
        if(worker != null) {
            worker.interrupt();
        }
    }

    private class DataHandler implements Runnable {

        private final Semaphore stopOk = new Semaphore(0);
        private boolean keepRunning = true;

        @Override
        public void run() {
            try {
                captureData();
            } catch (Exception ex) {
                log.error("Failed to run data capture loop.", ex);
                stopOk.release();
            }
        }

        void interrupt() throws Exception {

            keepRunning = false;
            boolean isOK = stopOk.tryAcquire(5, TimeUnit.SECONDS);
            if (!isOK)
                throw new IllegalStateException("Failed to stop data collector");
        }

        void captureData() throws Exception {

            // A waitset allows us to wait for various status changes in various entities
            WaitSet ws = new WaitSet();

            // And register that status condition with the waitset so we can monitor its triggering
            ws.attach_condition(saReader.get_statuscondition());

            ws.attach_condition(nReader.get_statuscondition());

            // will contain triggered conditions
            ConditionSeq cond_seq = new ConditionSeq();

            // how long to wait for data to become available before we timeout and check interrupted status.
            Duration_t timeout = WAIT_1SEC;

            // Will contain the data samples we read from the reader
            ice.SampleArraySeq sa_data_seq = new ice.SampleArraySeq();

            // Will contain the SampleInfo information about those data
            SampleInfoSeq info_seq = new SampleInfoSeq();

            ice.NumericSeq n_data_seq = new ice.NumericSeq();

            // This loop will repeat until the process is terminated
            while (keepRunning && !Thread.currentThread().isInterrupted()) {

                try {
                    // Wait for a condition to be triggered
                    ws.wait(cond_seq, timeout);
                } catch (RETCODE_TIMEOUT ex) {
                    // no data, check 'isInterrupted' and go back to wait
                    continue;
                }

                // Check that our status condition was indeed triggered
                if (cond_seq.contains(saReader.get_statuscondition())) {
                    // read the actual status changes
                    int status_changes = saReader.get_status_changes();
                    // Ensure that DATA_AVAILABLE is one of the statuses that changed in the DataReader.
                    // Since this is the only enabled status (see above) this is here mainly for completeness
                    if (0 != (status_changes & StatusKind.DATA_AVAILABLE_STATUS)) {
                        try {
                            // Read samples from the reader
                            saReader.read(sa_data_seq, info_seq,
                                    ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                                    SampleStateKind.NOT_READ_SAMPLE_STATE,
                                    ViewStateKind.ANY_VIEW_STATE,
                                    InstanceStateKind.ALIVE_INSTANCE_STATE);

                            // Iterator over the samples
                            for (int i = 0; i < info_seq.size(); i++) {
                                SampleInfo si = (SampleInfo) info_seq.get(i);
                                ice.SampleArray data = (ice.SampleArray) sa_data_seq.get(i);

                                // If the updated sample status contains fresh data that we can evaluate
                                if (si.valid_data) {
                                    Time_t t = si.source_timestamp;
                                    long baseTime = t.sec * 1000L + t.nanosec / 1000000L;

                                    final int sz = data.values.userData.size();
                                    if (0 < data.frequency) {
                                        int msPerSample = 1000 / data.frequency;
                                        for (int j = 0; j < sz; j++) {
                                            long tm = baseTime - (sz - j) * msPerSample;
                                            float value = data.values.userData.getFloat(j);

                                            if (log.isInfoEnabled())
                                                log.info(dateFormats.get().format(new Date(tm)) + " " + data.metric_id + "=" + value);

                                            Value v = toValue(si, data.unique_device_identifier, data.metric_id, tm, value);
                                            DataSampleEvent ev = new DataSampleEvent(v);
                                            fireDataSampleEvent(ev);

                                        }
                                    } else {
                                        log.warn("Invalid frequency " + data.frequency +
                                                " for " + data.unique_device_identifier + " " +
                                                data.metric_id + " " + data.instance_id);
                                    }
                                }
                            }
                        } catch (RETCODE_NO_DATA noData) {
                            // No Data was available to the read call
                        } finally {
                            // the objects provided by "read" are owned by the reader and we must return them
                            // so the reader can control their lifecycle
                            saReader.return_loan(sa_data_seq, info_seq);
                        }
                    }
                }

                if (cond_seq.contains(nReader.get_statuscondition())) {
                    // read the actual status changes
                    int status_changes = nReader.get_status_changes();
                    // Ensure that DATA_AVAILABLE is one of the statuses that changed in the DataReader.
                    // Since this is the only enabled status (see above) this is here mainly for completeness
                    if (0 != (status_changes & StatusKind.DATA_AVAILABLE_STATUS)) {
                        try {
                            // Read samples from the reader
                            nReader.read(n_data_seq, info_seq,
                                    ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                                    SampleStateKind.NOT_READ_SAMPLE_STATE,
                                    ViewStateKind.ANY_VIEW_STATE,
                                    InstanceStateKind.ALIVE_INSTANCE_STATE);

                            // Iterator over the samples
                            for (int i = 0; i < info_seq.size(); i++) {
                                SampleInfo si = (SampleInfo) info_seq.get(i);
                                ice.Numeric data = (ice.Numeric) n_data_seq.get(i);

                                // If the updated sample status contains fresh data that we can evaluate
                                if (si.valid_data) {

                                    Time_t t = si.source_timestamp;
                                    long baseTime = t.sec * 1000L + t.nanosec / 1000000L;

                                    if (log.isInfoEnabled())
                                        log.info(dateFormats.get().format(new Date(baseTime)) + " " + data.metric_id + "=" + data.value);

                                    Value v = toValue(si, data.unique_device_identifier, data.metric_id, baseTime, data.value);
                                    DataSampleEvent ev = new DataSampleEvent(v);
                                    fireDataSampleEvent(ev);
                                }
                            }
                        } catch (RETCODE_NO_DATA noData) {
                            // No Data was available to the read call
                        } finally {
                            // the objects provided by "read" are owned by the reader and we must return them
                            // so the reader can control their lifecycle
                            nReader.return_loan(n_data_seq, info_seq);
                        }
                    }
                }
            }

            // clear the latch so that stop can complete.
            stopOk.release();
        }
    }

    private static Duration_t WAIT_1SEC = Duration_t.from_millis(5000);

    static Value toValue(SampleInfo si, String dev, String metric, long tMs, double val)
    {
        Value v = new ValueImpl(dev, metric, 0, noopVital);
        Numeric numeric = new Numeric();
        numeric.value = (float) val;
        numeric.device_time = new ice.Time_t();
        numeric.device_time.sec = (int)(tMs/1000L);
        numeric.device_time.nanosec = (int)(tMs%1000L)*1000;

        v.updateFrom(numeric, si);
        return v;
    }

    static final Vital noopVital = new Vital() {

        @Override
        public String getLabel() {
            return null;
        }

        @Override
        public String getUnits() {
            return null;
        }

        @Override
        public String[] getMetricIds() {
            return new String[0];
        }

        @Override
        public float getMinimum() {
            return 0;
        }

        @Override
        public float getMaximum() {
            return 0;
        }

        @Override
        public Long getValueMsWarningLow() {
            return null;
        }

        @Override
        public Long getValueMsWarningHigh() {
            return null;
        }

        @Override
        public Float getWarningLow() {
            return (float)0.0;
        }

        @Override
        public Float getWarningHigh() {
            return (float)0.0;
        }

        @Override
        public Float getCriticalLow() {
            return null;
        }

        @Override
        public Float getCriticalHigh() {
            return null;
        }

        @Override
        public float getDisplayMaximum() {
            return 0;
        }

        @Override
        public float getDisplayMinimum() {
            return 0;
        }

        @Override
        public String getLabelMinimum() {
            return null;
        }

        @Override
        public String getLabelMaximum() {
            return null;
        }

        @Override
        public boolean isNoValueWarning() {
            return false;
        }

        @Override
        public void setNoValueWarning(boolean noValueWarning) {

        }

        @Override
        public long getWarningAgeBecomesAlarm() {
            return 0;
        }

        @Override
        public void setWarningAgeBecomesAlarm(long warningAgeBecomesAlarm) {

        }

        @Override
        public void destroy() {

        }

        @Override
        public void setWarningLow(Float low) {

        }

        @Override
        public void setWarningHigh(Float high) {

        }

        @Override
        public void setCriticalLow(Float low) {

        }

        @Override
        public void setCriticalHigh(Float high) {

        }

        @Override
        public void setValueMsWarningLow(Long low) {

        }

        @Override
        public void setValueMsWarningHigh(Long high) {

        }

        @Override
        public java.util.List<Value> getValues() {
            return null;
        }

        @Override
        public VitalModel getParent() {
            return null;
        }

        @Override
        public boolean isAnyOutOfBounds() {
            return false;
        }

        @Override
        public int countOutOfBounds() {
            return 0;
        }

        @Override
        public boolean isIgnoreZero() {
            return false;
        }

        @Override
        public void setIgnoreZero(boolean ignoreZero) {

        }
    };
}
