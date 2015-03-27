package org.mdpnp.devices;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.*;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.*;
import com.rti.dds.topic.Topic;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.TopicUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.event.EventListenerList;
import java.util.EventListener;
import java.util.EventObject;


public class MDSConnectivityAdapter {

    private static final Logger log = LoggerFactory.getLogger(MDSConnectivityAdapter.class);

    /*
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
*/

    private final Publisher  publisher;
    private final Subscriber subscriber;
    private final EventLoop  eventLoop;

    private final Topic msdoConnectivityTopic;
    private final ReadCondition mdsoReadCondition;
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
                            MDSConnectivityEvent ev = new MDSConnectivityEvent(dco);
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

        eventLoop.removeHandler(mdsoReadCondition);

        DomainParticipant participant = subscriber.get_participant();

        mdsoReader.delete_readcondition(mdsoReadCondition);
        subscriber.delete_datareader(mdsoReader);
        publisher.delete_datawriter(mdsoWriter);

        participant.delete_topic(msdoConnectivityTopic);
        ice.MDSConnectivityObjectiveTypeSupport.unregister_type(participant, ice.MDSConnectivityObjectiveTypeSupport.get_type_name());
        ice.MDSConnectivityTypeSupport.unregister_type(participant, ice.MDSConnectivityTypeSupport.get_type_name());
    }


    public MDSConnectivityAdapter(EventLoop eventLoop, Publisher publisher, Subscriber subscriber) {
        this.publisher = publisher;
        this.subscriber = subscriber;
        this.eventLoop = eventLoop;

        DomainParticipant participant = subscriber.get_participant();

        ice.MDSConnectivityObjectiveTypeSupport.register_type(participant, ice.MDSConnectivityObjectiveTypeSupport.get_type_name());
        ice.MDSConnectivityTypeSupport.register_type(participant, ice.MDSConnectivityTypeSupport.get_type_name());

        msdoConnectivityTopic = (Topic) TopicUtil.lookupOrCreateTopic(participant,
                                                                     ice.MDSConnectivityObjectiveTopic.VALUE,
                                                                     ice.MDSConnectivityObjectiveTypeSupport.class);

        mdsoReader =
                (ice.MDSConnectivityObjectiveDataReader) subscriber.create_datareader_with_profile(msdoConnectivityTopic,
                                                                                                   QosProfiles.ice_library,
                                                                                                   QosProfiles.device_identity,
                                                                                                   null,
                                                                                                   StatusKind.STATUS_MASK_NONE);

        mdsoReadCondition = mdsoReader.create_readcondition(SampleStateKind.NOT_READ_SAMPLE_STATE,
                                                       ViewStateKind.ANY_VIEW_STATE,
                                                       InstanceStateKind.ANY_INSTANCE_STATE);


        mdsoWriter =
                (ice.MDSConnectivityObjectiveDataWriter) publisher.create_datawriter_with_profile(msdoConnectivityTopic,
                                                                                                  QosProfiles.ice_library,
                                                                                                  QosProfiles.state,
                                                                                                  null,
                                                                                                  StatusKind.STATUS_MASK_NONE);

    }

    @SuppressWarnings("serial")
    public static class MDSConnectivityEvent extends EventObject {
        public MDSConnectivityEvent(Object source) {
            super(source);
        }
    }

    public interface MDSConnectivityListener extends EventListener {
        public void handleDataSampleEvent(MDSConnectivityEvent evt) ;
    }

    EventListenerList listenerList = new EventListenerList();

    public void addConnectivityListener(MDSConnectivityListener l) {
        listenerList.add(MDSConnectivityListener.class, l);
    }

    public void removeConnectivityListener(MDSConnectivityListener l) {
        listenerList.remove(MDSConnectivityListener.class, l);
    }

    void fireMDSConnectivityEvent(MDSConnectivityEvent data) {
        MDSConnectivityListener listeners[] = listenerList.getListeners(MDSConnectivityListener.class);
        for(MDSConnectivityListener l : listeners) {
            l.handleDataSampleEvent(data);
        }
    }
}
