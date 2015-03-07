package org.mdpnp.apps.testapp.export;

import ice.Numeric;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.EventListener;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.ListChangeListener;

import javax.swing.event.EventListenerList;

import org.mdpnp.apps.testapp.vital.Value;
import org.mdpnp.apps.testapp.vital.ValueImpl;
import org.mdpnp.apps.testapp.vital.Vital;
import org.mdpnp.apps.testapp.vital.VitalModel;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.TopicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.ConditionSeq;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.RETCODE_TIMEOUT;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.Time_t;
import com.rti.dds.infrastructure.WaitSet;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.TopicDescription;

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

    private final ice.SampleArrayDataReader saReader;
    private final ice.NumericDataReader     nReader;

    private DataHandler worker = null;

    public DataCollector(Subscriber subscriber) {

        DomainParticipant participant = subscriber.get_participant();
        
        // Inform the participant about the sample array data type we would like to use in our endpoints
        ice.SampleArrayTypeSupport.register_type(participant, ice.SampleArrayTypeSupport.get_type_name());

        // Inform the participant about the numeric data type we would like to use in our endpoints
        ice.NumericTypeSupport.register_type(participant, ice.NumericTypeSupport.get_type_name());

        // A topic the mechanism by which reader and writer endpoints are matched.
        TopicDescription sampleArrayTopic = TopicUtil.lookupOrCreateTopic(participant,
                                                                          ice.SampleArrayTopic.VALUE,
                                                                          ice.SampleArrayTypeSupport.class);
        /*
        Topic sampleArrayTopic = participant.create_topic(ice.SampleArrayTopic.VALUE,
                                                            ice.SampleArrayTypeSupport.get_type_name(),
                                                            DomainParticipant.TOPIC_QOS_DEFAULT, null,
                                                            StatusKind.STATUS_MASK_NONE);
        */

        // A second topic if for Numeric data
        TopicDescription numericTopic = TopicUtil.lookupOrCreateTopic(participant,
                                                                      ice.NumericTopic.VALUE,
                                                                      ice.NumericTypeSupport.class);
        /*
        Topic numericTopic = participant.create_topic(ice.NumericTopic.VALUE,
                ice.NumericTypeSupport.get_type_name(),
                DomainParticipant.TOPIC_QOS_DEFAULT, null,
                StatusKind.STATUS_MASK_NONE);
        */

        // Create a reader endpoint for waveform data
        saReader =
                (ice.SampleArrayDataReader) subscriber.create_datareader_with_profile(sampleArrayTopic,
                        QosProfiles.ice_library, QosProfiles.waveform_data, null, StatusKind.STATUS_MASK_NONE);

        nReader =
                (ice.NumericDataReader) subscriber.create_datareader_with_profile(numericTopic,
                        QosProfiles.ice_library, QosProfiles.numeric_data, null, StatusKind.STATUS_MASK_NONE);

        // Here we configure the status condition to trigger when new data becomes available to the reader
        saReader.get_statuscondition().set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);

        nReader.get_statuscondition().set_enabled_statuses(StatusKind.DATA_AVAILABLE_STATUS);

    }

    public synchronized void start() {
        worker = new DataHandler();
        (new Thread(worker, "DataCollector")).start();
        System.out.println("Start your data collectors");
    }

    public synchronized void stop() throws Exception {
        if(worker != null) {
            worker.interrupt();
            worker = null;
        }
    }

    private class DataHandler implements Runnable {

        private final CountDownLatch stopOk = new CountDownLatch(1);
        private boolean keepRunning = true;

        @Override
        public void run() {
            try {
                captureData();
            } catch (Exception ex) {
                log.error("Failed to run data capture loop.", ex);
            }
            finally {
                stopOk.countDown();
            }
        }

        void interrupt() throws Exception {

            // This will force 'captureData' to break out or the loop.
            keepRunning = false;
            // we want to hang out here a little longer than the actual loop that could
            // be waiting for data.
            boolean isOK = stopOk.await(5 * WAIT_FOR_DATA.sec, TimeUnit.SECONDS);
            if (!isOK)
                throw new IllegalStateException("Failed to stop data collector");
        }

        private void captureData() throws Exception {

            // A waitset allows us to wait for various status changes in various entities
            WaitSet ws = new WaitSet();

            // And register that status condition with the waitset so we can monitor its triggering
            ws.attach_condition(saReader.get_statuscondition());

            ws.attach_condition(nReader.get_statuscondition());

            // will contain triggered conditions
            ConditionSeq cond_seq = new ConditionSeq();

            // Will contain the data samples we read from the reader
            ice.SampleArraySeq sa_data_seq = new ice.SampleArraySeq();

            // Will contain the SampleInfo information about those data
            SampleInfoSeq info_seq = new SampleInfoSeq();

            ice.NumericSeq n_data_seq = new ice.NumericSeq();

            // This loop will repeat until the process is terminated
            while (keepRunning && !Thread.currentThread().isInterrupted()) {

                try {
                    // Wait for a condition to be triggered
                    ws.wait(cond_seq, WAIT_FOR_DATA);
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

                                            if (log.isDebugEnabled())
                                                log.debug(dateFormats.get().format(new Date(tm)) + " " + data.metric_id + "=" + value);

                                            Value v = toValue(si, data.unique_device_identifier, data.metric_id, data.instance_id, tm, value);
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

                                    if (log.isDebugEnabled())
                                        log.debug(dateFormats.get().format(new Date(baseTime)) + " " + data.metric_id + "=" + data.value);

                                    Value v = toValue(si, data.unique_device_identifier, data.metric_id, data.instance_id, baseTime, data.value);
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
        }
    }

    private static final Duration_t WAIT_1SEC = Duration_t.from_millis(1000);
    @SuppressWarnings("unused")
    private static final Duration_t WAIT_5SEC = Duration_t.from_millis(5000);

    // how long to wait for data to become available before we timeout and check interrupted status.
    private static final Duration_t WAIT_FOR_DATA = WAIT_1SEC;

    static long toMilliseconds(ice.Time_t t) {
        long ms = t.sec*1000L + t.nanosec/1000000L;
        return ms;
    }

    static Value toValue(SampleInfo si, String dev, String metric, int instance_id, long tMs, double val)
    {
        Value v = new ValueImpl(dev, metric, instance_id, noopVital);
        Numeric numeric = new Numeric();
        numeric.value = (float) val;
        numeric.device_time = new ice.Time_t();
        numeric.device_time.sec = (int)(tMs/1000L);
        numeric.device_time.nanosec = (int)(tMs%1000L)*1000000;

        v.updateFrom(numeric, si);
        return v;
    }

    static Value toValue(String dev, String metric, int instance_id, long tMs, double val)
    {
        return toValue(noopInfo, dev, metric, instance_id, tMs, val);
    }

    private static final SampleInfo noopInfo = new SampleInfo();

    private static final Vital noopVital = new Vital() {

        @Override
        public ReadOnlyStringProperty labelProperty() {

            return null;
        }

        @Override
        public String getLabel() {
            return null;
        }

        @Override
        public ReadOnlyStringProperty unitsProperty() {
            return null;
        }

        @Override
        public String getUnits() {
            return null;
        }

        @Override
        public ReadOnlyObjectProperty<String[]> metricIdsProperty() {
            return null;
        }

        @Override
        public String[] getMetricIds() {
            return null;
        }

        @Override
        public ReadOnlyDoubleProperty minimumProperty() {
            return null;
        }

        @Override
        public double getMinimum() {
            return 0;
        }

        @Override
        public ReadOnlyDoubleProperty maximumProperty() {
            return null;
        }

        @Override
        public double getMaximum() {
            return 0;
        }

        @Override
        public ObjectProperty<Long> valueMsWarningLowProperty() {
            return null;
        }

        @Override
        public Long getValueMsWarningLow() {
            return null;
        }

        @Override
        public ObjectProperty<Long> valueMsWarningHighProperty() {
            return null;
        }

        @Override
        public Long getValueMsWarningHigh() {
            return null;
        }

        @Override
        public ObjectProperty<Double> warningLowProperty() {
            return null;
        }

        @Override
        public Double getWarningLow() {
            return null;
        }

        @Override
        public ObjectProperty<Double> warningHighProperty() {
            return null;
        }

        @Override
        public Double getWarningHigh() {
            return null;
        }

        @Override
        public ObjectProperty<Double> criticalLowProperty() {
            return null;
        }

        @Override
        public Double getCriticalLow() {
            return null;
        }

        @Override
        public ObjectProperty<Double> criticalHighProperty() {
            return null;
        }

        @Override
        public Double getCriticalHigh() {
            return null;
        }

        @Override
        public ReadOnlyDoubleProperty displayMaximumProperty() {
            return null;
        }

        @Override
        public double getDisplayMaximum() {
            return 0;
        }

        @Override
        public ReadOnlyDoubleProperty displayMinimumProperty() {
            return null;
        }

        @Override
        public double getDisplayMinimum() {
            return 0;
        }

        @Override
        public ReadOnlyStringProperty labelMinimumProperty() {
            return null;
        }

        @Override
        public String getLabelMinimum() {
            return null;
        }

        @Override
        public ReadOnlyStringProperty labelMaximumProperty() {
            return null;
        }

        @Override
        public String getLabelMaximum() {
            return null;
        }

        @Override
        public BooleanProperty noValueWarningProperty() {
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
        public LongProperty warningAgeBecomesAlarmProperty() {
            return null;
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
        public void setWarningLow(Double low) {
            
        }

        @Override
        public void setWarningHigh(Double high) {
            
        }

        @Override
        public void setCriticalLow(Double low) {
            
        }

        @Override
        public void setCriticalHigh(Double high) {
            
        }

        @Override
        public void setValueMsWarningLow(Long low) {
            
        }

        @Override
        public void setValueMsWarningHigh(Long high) {
            
        }

        @Override
        public VitalModel getParent() {
            return null;
        }

        @Override
        public ReadOnlyBooleanProperty anyOutOfBoundsProperty() {
            return null;
        }

        @Override
        public boolean isAnyOutOfBounds() {
            return false;
        }

        @Override
        public ReadOnlyIntegerProperty countOutOfBoundsProperty() {
            return null;
        }

        @Override
        public int countOutOfBounds() {
            return 0;
        }

        @Override
        public BooleanProperty ignoreZeroProperty() {
            return null;
        }

        @Override
        public boolean isIgnoreZero() {
            return false;
        }

        @Override
        public void setIgnoreZero(boolean ignoreZero) {
            
        }

        @Override
        public void addListener(ListChangeListener<? super Value> listener) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void removeListener(ListChangeListener<? super Value> listener) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public boolean addAll(Value... elements) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean setAll(Value... elements) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean setAll(Collection<? extends Value> col) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean removeAll(Value... elements) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean retainAll(Value... elements) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void remove(int from, int to) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public int size() {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public boolean isEmpty() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean contains(Object o) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public Iterator<Value> iterator() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Object[] toArray() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public <T> T[] toArray(T[] a) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public boolean add(Value e) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean remove(Object o) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean addAll(Collection<? extends Value> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean addAll(int index, Collection<? extends Value> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void clear() {
            // TODO Auto-generated method stub
            
        }

        @Override
        public Value get(int index) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Value set(int index, Value element) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void add(int index, Value element) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public Value remove(int index) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public int indexOf(Object o) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int lastIndexOf(Object o) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public ListIterator<Value> listIterator() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public ListIterator<Value> listIterator(int index) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public List<Value> subList(int fromIndex, int toIndex) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void addListener(InvalidationListener listener) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void removeListener(InvalidationListener listener) {
            // TODO Auto-generated method stub
            
        }

    };
}
