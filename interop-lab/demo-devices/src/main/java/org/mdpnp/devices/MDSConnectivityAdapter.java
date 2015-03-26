package org.mdpnp.devices;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.*;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.*;
import com.rti.dds.topic.Topic;
import com.rti.dds.topic.TopicDescription;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.TopicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.EventListenerList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MDSConnectivityAdapter {

    private static final Logger log = LoggerFactory.getLogger(MDSConnectivityAdapter.class);

    void createReader(Subscriber subscriber) {

        DomainParticipant participant = subscriber.get_participant();

        ice.MDSConnectivityTypeSupport.register_type(participant, ice.MDSConnectivityTypeSupport.get_type_name());

        TopicDescription msdConnectivityTopic = TopicUtil.lookupOrCreateTopic(participant,
                                                                          ice.MDSConnectivityTopic.VALUE,
                                                                          ice.MDSConnectivityTypeSupport.class);
        mdsReader =
                (ice.MDSConnectivityDataReader) subscriber.create_datareader_with_profile(msdConnectivityTopic,
                                                                                  QosProfiles.ice_library,
                                                                                  QosProfiles.device_identity,
                                                                                  null,
                                                                                  StatusKind.STATUS_MASK_NONE);

    }

    void createWriter(Publisher publisher) {

        DomainParticipant participant = publisher.get_participant();

        ice.MDSConnectivityObjectiveTypeSupport.register_type(participant, ice.MDSConnectivityObjectiveTypeSupport.get_type_name());

        Topic msdConnectivityTopic = (Topic)TopicUtil.lookupOrCreateTopic(participant,
                                                                              ice.MDSConnectivityObjectiveTopic.VALUE,
                                                                              ice.MDSConnectivityObjectiveTypeSupport.class);
        mdsWriter =
                (ice.MDSConnectivityObjectiveDataWriter) publisher.create_datawriter_with_profile(msdConnectivityTopic,
                                                                                         QosProfiles.ice_library,
                                                                                         QosProfiles.state,
                                                                                         null,
                                                                                         StatusKind.STATUS_MASK_NONE);
    }

    ice.MDSConnectivityDataReader mdsReader;
    ice.MDSConnectivityObjectiveDataWriter mdsWriter;

    private DataHandler worker = null;

    public synchronized void start() {
        worker = new DataHandler();
        (new Thread(worker, "DataCollector")).start();
    }

    public synchronized void stop() throws Exception {
        if(worker != null) {
            worker.interrupt();
            worker = null;
        }
    }

    public void publish(ice.MDSConnectivityObjective val) {
        mdsWriter.write_w_params(val, new WriteParams_t());
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
            ws.attach_condition(mdsReader.get_statuscondition());

            // will contain triggered conditions
            ConditionSeq cond_seq = new ConditionSeq();

            // Will contain the data samples we read from the reader
            SampleInfoSeq info_seq = new SampleInfoSeq();

            ice.MDSConnectivitySeq n_data_seq = new ice.MDSConnectivitySeq();

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
                if (cond_seq.contains(mdsReader.get_statuscondition())) {
                    // read the actual status changes
                    int status_changes = mdsReader.get_status_changes();
                    // Ensure that DATA_AVAILABLE is one of the statuses that changed in the DataReader.
                    // Since this is the only enabled status (see above) this is here mainly for completeness
                    if (0 != (status_changes & StatusKind.DATA_AVAILABLE_STATUS)) {
                        try {
                            // Read samples from the reader
                            mdsReader.read(n_data_seq, info_seq,
                                         ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                                         SampleStateKind.NOT_READ_SAMPLE_STATE,
                                         ViewStateKind.ANY_VIEW_STATE,
                                         InstanceStateKind.ALIVE_INSTANCE_STATE);

                            // Iterator over the samples
                            for (int i = 0; i < info_seq.size(); i++) {
                                SampleInfo si = (SampleInfo) info_seq.get(i);
                                ice.MDSConnectivity data = (ice.MDSConnectivity) n_data_seq.get(i);

                                // If the updated sample status contains fresh data that we can evaluate
                                if (si.valid_data) {

                                    if (log.isDebugEnabled())
                                        log.debug(data.unique_device_identifier + " " + data.partition);
                                    MDSConnectivityEvent ev = new MDSConnectivityEvent(data);
                                    fireMDSConnectivityEvent(ev);
                                }
                            }
                        } catch (RETCODE_NO_DATA noData) {
                            // No Data was available to the read call
                        } finally {
                            // the objects provided by "read" are owned by the reader and we must return them
                            // so the reader can control their lifecycle
                            mdsReader.return_loan(n_data_seq, info_seq);
                        }
                    }
                }
            }
        }
    }

    @SuppressWarnings("serial")
    public static class MDSConnectivityEvent extends EventObject {
        public MDSConnectivityEvent(Object source) {
            super(source);
        }
    }

    public interface MDSConnectivityListener extends EventListener {
        public void handleDataSampleEvent(MDSConnectivityEvent evt) throws Exception;
    }

    EventListenerList listenerList = new EventListenerList();

    public void addConnectivityListener(MDSConnectivityListener l) {
        listenerList.add(MDSConnectivityListener.class, l);
    }

    public void removeConnectivityListener(MDSConnectivityListener l) {
        listenerList.remove(MDSConnectivityListener.class, l);
    }

    void fireMDSConnectivityEvent(MDSConnectivityEvent data) throws Exception{
        MDSConnectivityListener listeners[] = listenerList.getListeners(MDSConnectivityListener.class);
        for(MDSConnectivityListener l : listeners) {
            l.handleDataSampleEvent(data);
        }
    }

    private static final Duration_t WAIT_FOR_DATA = Duration_t.from_millis(1000);

}
