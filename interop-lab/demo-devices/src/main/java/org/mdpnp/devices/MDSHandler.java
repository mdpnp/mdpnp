package org.mdpnp.devices;

import com.rti.dds.publication.DataWriterImpl;
import com.rti.dds.subscription.*;
import ice.MDSConnectivity;
import ice.MDSConnectivityObjective;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.LogEntityStatus;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.TopicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.WriteParams_t;
import com.rti.dds.publication.Publisher;
import com.rti.dds.publication.PublisherQos;
import com.rti.dds.topic.Topic;

/**
 * @author mfeinberg
 *
 * Container for both sides. Each side can be instaciated separately if needs to
 */
public class MDSHandler {

    private static final String QOS_PARTITION = "MDSHandler";

    final MDSHandler.Connectivity mdsConnectivityAdapter;
    final MDSHandler.Objective mdsConnectivityObjectiveAdapter;
    final MDSHandler.Patient mdsPatientAdapter;

    final DomainParticipant domainParticipant;
    final Publisher  mdsPublisher;
    final Subscriber mdsSubscriber;
    final Publisher  wildcardPublisher;
    final Subscriber wildcardSubscriber;

    protected MDSHandler()
    {
        domainParticipant = null;
        mdsPublisher = null;
        mdsSubscriber = null;
        wildcardPublisher = null;
        wildcardSubscriber = null;
        mdsConnectivityAdapter = null;
        mdsConnectivityObjectiveAdapter = null;
        mdsPatientAdapter = null;
    }


    public MDSHandler(EventLoop eventLoop, DomainParticipant dp) {
        domainParticipant = dp;
        mdsPublisher  = dp.create_publisher(DomainParticipant.PUBLISHER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        mdsSubscriber = dp.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        setCommunicationChannel(mdsPublisher, mdsSubscriber);

        wildcardPublisher  = dp.create_publisher(DomainParticipant.PUBLISHER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        wildcardSubscriber = dp.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

        mdsConnectivityAdapter = new MDSHandler.Connectivity(eventLoop, mdsPublisher, mdsSubscriber);
        mdsConnectivityObjectiveAdapter = new MDSHandler.Objective(eventLoop, mdsPublisher, mdsSubscriber);

        mdsPatientAdapter = new MDSHandler.Patient(eventLoop, wildcardPublisher, wildcardSubscriber);
    }

    public void start() {
        mdsConnectivityAdapter.start();
        mdsConnectivityObjectiveAdapter.start();
        mdsPatientAdapter.start();
    }

    public void shutdown() {
        mdsConnectivityAdapter.shutdown();
        mdsConnectivityObjectiveAdapter.shutdown();
        mdsPatientAdapter.shutdown();

        domainParticipant.delete_publisher(mdsPublisher);
        domainParticipant.delete_subscriber(mdsSubscriber);

        domainParticipant.delete_publisher(wildcardPublisher);
        domainParticipant.delete_subscriber(wildcardSubscriber);
    }

    public void addConnectivityListener(Connectivity.MDSListener l) {
        mdsConnectivityAdapter.addConnectivityListener(l);
    }

    public void removeConnectivityListener(Connectivity.MDSListener l) {
        mdsConnectivityAdapter.removeConnectivityListener(l);
    }

    public void addConnectivityListener(Objective.MDSListener l) {
        mdsConnectivityObjectiveAdapter.addConnectivityListener(l);
    }

    public void removeConnectivityListener(Objective.MDSListener l) {
        mdsConnectivityObjectiveAdapter.removeConnectivityListener(l);
    }

    public void addPatientListener(Patient.PatientListener l) {
        mdsPatientAdapter.addPatientListener(l);
    }

    public void removePatientListener(Patient.PatientListener l) {
        mdsPatientAdapter.removePatientListener(l);
    }

    public void publish(ice.MDSConnectivity val) {
        mdsConnectivityAdapter.publish(val);
    }

    public void publish(ice.MDSConnectivityObjective val) {
        mdsConnectivityObjectiveAdapter.publish(val);
    }

    public void publish(ice.Patient val) {
        mdsPatientAdapter.publish(val);
    }

    void setCommunicationChannel(final Publisher publisher, final Subscriber subscriber) {

        PublisherQos pQos = new PublisherQos();
        SubscriberQos sQos = new SubscriberQos();
        publisher.get_qos(pQos);
        subscriber.get_qos(sQos);

        List<String> asList   = new ArrayList<>();
        asList.add(QOS_PARTITION);

        pQos.partition.name.clear();
        sQos.partition.name.clear();
        pQos.partition.name.addAll(asList);
        sQos.partition.name.addAll(asList);
        publisher.set_qos(pQos);
        subscriber.set_qos(sQos);
    }


    static void shutdown(EventLoop eventLoop,
                                 Subscriber subscriber, Publisher publisher,
                                 DataReaderImpl reader, DataWriterImpl writer,
                                 Topic topic, ReadCondition readCondition) {

        eventLoop.removeHandler(readCondition);

        DomainParticipant participant = subscriber.get_participant();

        reader.delete_readcondition(readCondition);
        subscriber.delete_datareader(reader);
        publisher.delete_datawriter(writer);

        participant.delete_topic(topic);

    }

    /**
     * Decorator for MDSConnectivity messages
     */
    public static class Connectivity {

        private static final Logger log = LoggerFactory.getLogger(Connectivity.class);

        private final Publisher publisher;
        private final Subscriber subscriber;
        private final EventLoop eventLoop;

        private final Topic msdoConnectivityTopic;
        private final ReadCondition mdsoReadCondition;
        private final ice.MDSConnectivityDataReader mdsoReader;
        private final ice.MDSConnectivityDataWriter mdsoWriter;

        public void publish(ice.MDSConnectivity val) {
            mdsoWriter.write_w_params(val, new WriteParams_t());
        }

        public void start() {

            final ice.MDSConnectivitySeq data_seq = new ice.MDSConnectivitySeq();
            final SampleInfoSeq info_seq = new SampleInfoSeq();

            eventLoop.addHandler(mdsoReadCondition, new EventLoop.ConditionHandler() {

                @Override
                public void conditionChanged(Condition condition) {
                    try {
                        mdsoReader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, mdsoReadCondition);
                        for (int i = 0; i < info_seq.size(); i++) {
                            SampleInfo si = (SampleInfo) info_seq.get(i);
                            if (si.valid_data) {
                                ice.MDSConnectivity dco = (ice.MDSConnectivity) data_seq.get(i);
                                log.info("got " + dco);
                                MDSEvent ev = new MDSEvent(dco);
                                try {
                                    fireMDSConnectivityEvent(ev);
                                }
                                catch (Exception ex) {
                                    log.error("Failed to propagate MDSConnectivityEvent", ex);
                                }
                            }
                        }

                    } catch (RETCODE_NO_DATA noData) {

                    } finally {
                        mdsoReader.return_loan(data_seq, info_seq);
                    }
                }
            });
        }

        public void shutdown() {

            MDSHandler.shutdown(eventLoop, subscriber, publisher,
                                mdsoReader, mdsoWriter,
                                msdoConnectivityTopic, mdsoReadCondition);
        }

        public Connectivity(EventLoop eventLoop, Publisher publisher, Subscriber subscriber) {
            this.publisher = publisher;
            this.subscriber = subscriber;
            this.eventLoop = eventLoop;

            DomainParticipant participant = subscriber.get_participant();

            ice.MDSConnectivityTypeSupport.register_type(participant, ice.MDSConnectivityTypeSupport.get_type_name());

            msdoConnectivityTopic = TopicUtil.findOrCreateTopic(participant,
                                                                          ice.MDSConnectivityTopic.VALUE,
                                                                          ice.MDSConnectivityTypeSupport.class);
            
            mdsoReader =
                    (ice.MDSConnectivityDataReader) subscriber.create_datareader_with_profile(msdoConnectivityTopic,
                                                                                                       QosProfiles.ice_library,
                                                                                                       QosProfiles.state,
                                                                                                       null,
                                                                                                       StatusKind.STATUS_MASK_NONE);

            mdsoReadCondition = mdsoReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE,
                                                           ViewStateKind.ANY_VIEW_STATE,
                                                           InstanceStateKind.ANY_INSTANCE_STATE);


            mdsoWriter =
                    (ice.MDSConnectivityDataWriter) publisher.create_datawriter_with_profile(msdoConnectivityTopic,
                                                                                                      QosProfiles.ice_library,
                                                                                                      QosProfiles.state,
                                                                                                      null,
                                                                                                      StatusKind.STATUS_MASK_NONE);

        }

        @SuppressWarnings("serial")
        public static class MDSEvent extends EventObject {
            public MDSEvent(MDSConnectivity source) {
                super(source);
            }
        }

        public interface MDSListener extends EventListener {
            void handleConnectivityChange(MDSEvent evt) ;
        }

        EventListenerList listenerList = new EventListenerList();

        public void addConnectivityListener(MDSListener l) {
            listenerList.add(MDSListener.class, l);
        }

        public void removeConnectivityListener(MDSListener l) {
            listenerList.remove(MDSListener.class, l);
        }

        void fireMDSConnectivityEvent(MDSEvent data) {
            MDSListener listeners[] = listenerList.getListeners(MDSListener.class);
            for(MDSListener l : listeners) {
                l.handleConnectivityChange(data);
            }
        }
    }

    /**
     * Decorator for MDSConnectivityObjective messages
     */
    public static class Objective {

        private static final Logger log = LoggerFactory.getLogger(Objective.class);

        private final Publisher  publisher;
        private final Subscriber subscriber;
        private final EventLoop  eventLoop;

        private final Topic                                  msdoConnectivityTopic;
        private final ReadCondition                          mdsoReadCondition;
        private final ice.MDSConnectivityObjectiveDataReader mdsoReader;
        private final ice.MDSConnectivityObjectiveDataWriter mdsoWriter;

        public void publish(ice.MDSConnectivityObjective val) {
            mdsoWriter.write_w_params(val, new WriteParams_t());
        }

        public void start() {

            final ice.MDSConnectivityObjectiveSeq data_seq = new ice.MDSConnectivityObjectiveSeq();
            final SampleInfoSeq info_seq = new SampleInfoSeq();

            eventLoop.addHandler(mdsoReadCondition, new EventLoop.ConditionHandler() {

                @Override
                public void conditionChanged(Condition condition) {
                    try {
                        mdsoReader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, mdsoReadCondition);
                        for (int i = 0; i < info_seq.size(); i++) {
                            SampleInfo si = (SampleInfo) info_seq.get(i);
                            if (si.valid_data) {
                                ice.MDSConnectivityObjective dco = (ice.MDSConnectivityObjective) data_seq.get(i);
                                log.info("got " + dco);
                                MDSEvent ev = new MDSEvent(dco);
                                try {
                                    fireMDSConnectivityObjectiveEvent(ev);
                                }
                                catch (Exception ex) {
                                    log.error("Failed to propagate MDSConnectivityEvent", ex);
                                }
                            }
                        }

                    } catch (RETCODE_NO_DATA noData) {

                    } finally {
                        mdsoReader.return_loan(data_seq, info_seq);
                    }
                }
            });
        }

        public void shutdown() {

            MDSHandler.shutdown(eventLoop, subscriber, publisher,
                                mdsoReader, mdsoWriter,
                                msdoConnectivityTopic, mdsoReadCondition);
        }


        public Objective(EventLoop eventLoop, Publisher publisher, Subscriber subscriber) {
            this.publisher = publisher;
            this.subscriber = subscriber;
            this.eventLoop = eventLoop;

            DomainParticipant participant = subscriber.get_participant();

            ice.MDSConnectivityObjectiveTypeSupport.register_type(participant, ice.MDSConnectivityObjectiveTypeSupport.get_type_name());

            msdoConnectivityTopic = (Topic) TopicUtil.findOrCreateTopic(participant,
                                                                         ice.MDSConnectivityObjectiveTopic.VALUE,
                                                                         ice.MDSConnectivityObjectiveTypeSupport.class);

            mdsoReader =
                    (ice.MDSConnectivityObjectiveDataReader) subscriber.create_datareader_with_profile(msdoConnectivityTopic,
                                                                                                       QosProfiles.ice_library,
                                                                                                       QosProfiles.state,
                                                                                                       null,
                                                                                                       StatusKind.STATUS_MASK_NONE);

            mdsoReadCondition = mdsoReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE,
                                                           ViewStateKind.ANY_VIEW_STATE,
                                                           InstanceStateKind.ANY_INSTANCE_STATE);


            mdsoWriter =
                    (ice.MDSConnectivityObjectiveDataWriter) publisher.create_datawriter_with_profile(msdoConnectivityTopic,
                                                                                                      QosProfiles.ice_library,
                                                                                                      QosProfiles.state,
                                                                                                      new LogEntityStatus(log, "MDSConnectivity"),
                                                                                                      StatusKind.STATUS_MASK_ALL);

        }

        @SuppressWarnings("serial")
        public static class MDSEvent extends EventObject {
            public MDSEvent(MDSConnectivityObjective source) {
                super(source);
            }
        }

        public interface MDSListener extends EventListener {
            public void handleConnectivityObjective(MDSEvent evt) ;
        }

        EventListenerList listenerList = new EventListenerList();

        public void addConnectivityListener(MDSListener l) {
            listenerList.add(MDSListener.class, l);
        }

        public void removeConnectivityListener(MDSListener l) {
            listenerList.remove(MDSListener.class, l);
        }

        void fireMDSConnectivityObjectiveEvent(MDSEvent data) {
            MDSListener listeners[] = listenerList.getListeners(MDSListener.class);
            for(MDSListener l : listeners) {
                l.handleConnectivityObjective(data);
            }
        }
    }


    /**
     * Decorator for MDSConnectivity messages
     */
    public static class Patient {

        private static final Logger log = LoggerFactory.getLogger(Patient.class);

        private final Publisher  publisher;
        private final Subscriber subscriber;
        private final EventLoop  eventLoop;

        private final Topic                 patientConnectivityTopic;
        private final ReadCondition         patientReadCondition;
        private final ice.PatientDataReader patientReader;
        private final ice.PatientDataWriter patientWriter;

        public void publish(ice.Patient val) {
            patientWriter.write_w_params(val, new WriteParams_t());
        }

        public void start() {

            final ice.PatientSeq data_seq = new ice.PatientSeq();
            final SampleInfoSeq info_seq = new SampleInfoSeq();

            eventLoop.addHandler(patientReadCondition, new EventLoop.ConditionHandler() {

                @Override
                public void conditionChanged(Condition condition) {
                    try {
                        patientReader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED, patientReadCondition);
                        for (int i = 0; i < info_seq.size(); i++) {
                            SampleInfo si = (SampleInfo) info_seq.get(i);
                            if (si.valid_data) {
                                ice.Patient dco = (ice.Patient) data_seq.get(i);
                                log.info("got " + dco);
                                PatientEvent ev = new PatientEvent(dco);
                                try {
                                    firePatientEvent(ev);
                                }
                                catch (Exception ex) {
                                    log.error("Failed to propagate PatientEvent", ex);
                                }
                            }
                        }

                    } catch (RETCODE_NO_DATA noData) {

                    } finally {
                        patientReader.return_loan(data_seq, info_seq);
                    }
                }
            });
        }

        public void shutdown() {

            MDSHandler.shutdown(eventLoop, subscriber, publisher,
                                patientReader, patientWriter,
                                patientConnectivityTopic, patientReadCondition);
        }


        public Patient(EventLoop eventLoop, Publisher publisher, Subscriber subscriber) {
            this.publisher = publisher;
            this.subscriber = subscriber;
            this.eventLoop = eventLoop;

            DomainParticipant participant = subscriber.get_participant();

            ice.MDSConnectivityTypeSupport.register_type(participant, ice.MDSConnectivityTypeSupport.get_type_name());

            patientConnectivityTopic = TopicUtil.findOrCreateTopic(participant,
                    ice.PatientTopic.VALUE,
                    ice.PatientTypeSupport.class);

            patientReader =
                    (ice.PatientDataReader) subscriber.create_datareader_with_profile(patientConnectivityTopic,
                            QosProfiles.ice_library,
                            QosProfiles.state,
                            null,
                            StatusKind.STATUS_MASK_NONE);

            patientReadCondition = patientReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE,
                    ViewStateKind.ANY_VIEW_STATE,
                    InstanceStateKind.ANY_INSTANCE_STATE);


            patientWriter =
                    (ice.PatientDataWriter) publisher.create_datawriter_with_profile(patientConnectivityTopic,
                            QosProfiles.ice_library,
                            QosProfiles.state,
                            null,
                            StatusKind.STATUS_MASK_NONE);

        }

        @SuppressWarnings("serial")
        public static class PatientEvent extends EventObject {
            public PatientEvent(ice.Patient source) {
                super(source);
            }
        }

        public interface PatientListener extends EventListener {
            void handlePatientChange(PatientEvent evt) ;
        }

        EventListenerList listenerList = new EventListenerList();

        public void addPatientListener(PatientListener l) {
            listenerList.add(PatientListener.class, l);
        }

        public void removePatientListener(PatientListener l) {
            listenerList.remove(PatientListener.class, l);
        }

        void firePatientEvent(PatientEvent data) {
            PatientListener listeners[] = listenerList.getListeners(PatientListener.class);
            for(PatientListener l : listeners) {
                l.handlePatientChange(data);
            }
        }
    }

    //
    // for disconnected functionality - mostly for tests
    //
    public static class NoOp extends MDSHandler {
        public NoOp() {
            super();
        }

        @Override
        public void start() {
            // do nothing
        }

        @Override
        public void shutdown() {
            // do nothing
        }

        @Override
        public void addConnectivityListener(Connectivity.MDSListener l) {
            // do nothing
        }

        @Override
        public void removeConnectivityListener(Connectivity.MDSListener l) {
            // do nothing
        }

        @Override
        public void removeConnectivityListener(Objective.MDSListener l) {
            // do nothing
        }

        @Override
        public void addConnectivityListener(Objective.MDSListener l) {
            // do nothing
        }

        @Override
        public void removePatientListener(Patient.PatientListener l) {
            // do nothing
        }

        @Override
        public void addPatientListener(Patient.PatientListener l) {
            // do nothing
        }

        @Override
        public void publish(ice.MDSConnectivity val) {
            // do nothing
        }

        @Override
        public void publish(ice.MDSConnectivityObjective val) {
            // do nothing
        }

        @Override
        public void publish(ice.Patient val) {
            // do nothing
        }
    }

}
