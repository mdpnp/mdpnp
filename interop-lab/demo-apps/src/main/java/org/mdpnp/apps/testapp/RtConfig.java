package org.mdpnp.apps.testapp;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantFactoryQos;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.infrastructure.*;
import com.rti.dds.publication.DataWriterQos;
import com.rti.dds.publication.Publisher;
import com.rti.dds.publication.PublisherQos;
import com.rti.dds.subscription.DataReaderQos;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;
import com.rti.dds.topic.TopicQos;
import org.mdpnp.devices.EventLoopHandler;
import org.mdpnp.devices.TimeManager;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class RtConfig {

    private static final Logger log = LoggerFactory.getLogger(RtConfig.class);

    public int domainId;
    public String udi;
    public EventLoop eventLoop;
    public Publisher publisher;
    public Subscriber subscriber;
    public DomainParticipant participant;
    public DeviceListModel deviceListModel;
    public EventLoopHandler handler;

    public static RtConfig setupDDS(final int domainId) {
        final EventLoop eventLoop = new EventLoop();
        final EventLoopHandler handler = new EventLoopHandler(eventLoop);

        // UIManager.put("List.focusSelectedCellHighlightBorder", null);
        // UIManager.put("List.focusCellHighlightBorder", null);

        // This could prove confusing
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));
        final String udi = AbstractSimulatedDevice.randomUDI();

        final DomainParticipantFactoryQos qos = new DomainParticipantFactoryQos();
        DomainParticipantFactory.get_instance().get_qos(qos);
        qos.entity_factory.autoenable_created_entities = false;
        DomainParticipantFactory.get_instance().set_qos(qos);
        final DomainParticipant participant = DomainParticipantFactory.get_instance().create_participant(domainId, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null,
                StatusKind.STATUS_MASK_NONE);

        /**
         * This is a workaround.  Publisher.set_qos (potentially called later to
         * change partitions) expects thread priorities be set in the java range
         * Thread.MIN_PRIORITY to Thread.MAX_PRIORITY but Publisher.get_qos DOES NOT
         * populate thread priority.  So we set NORM_PRIORITY here and later
         * to avoid changing an immutable QoS.
         */
        PublisherQos pubQos = new PublisherQos();
        participant.get_default_publisher_qos(pubQos);
        pubQos.asynchronous_publisher.asynchronous_batch_thread.priority = Thread.NORM_PRIORITY;
        pubQos.asynchronous_publisher.thread.priority = Thread.NORM_PRIORITY;

        final Subscriber subscriber = participant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);
        final Publisher publisher = participant.create_publisher(pubQos, null, StatusKind.STATUS_MASK_NONE);
        final TimeManager timeManager = new TimeManager(publisher, subscriber, udi, "Supervisor");

        final DeviceListModel deviceListModel = new DeviceListModel(subscriber, eventLoop, timeManager);

        final CountDownLatch startSignal = new CountDownLatch(1);

        Runnable enable = new Runnable() {
            public void run() {
                deviceListModel.start();
                participant.enable();
                timeManager.start();
                qos.entity_factory.autoenable_created_entities = true;
                DomainParticipantFactory.get_instance().set_qos(qos);

                startSignal.countDown();
            }
        };

        eventLoop.doLater(enable);

        try {
            boolean isOk = startSignal.await(20, TimeUnit.SECONDS);
            if(!isOk)
                throw new IllegalStateException("Failed to start DDS");

        }
        catch(InterruptedException ex) {
            throw new IllegalStateException("Failed to start DDS", ex);
        }

        RtConfig conf = new RtConfig();
        conf.domainId = domainId;
        conf.eventLoop = eventLoop;
        conf.publisher = publisher;
        conf.subscriber = subscriber;
        conf.participant = participant;
        conf.deviceListModel = deviceListModel;
        conf.handler = handler;
        conf.udi = udi;

        return conf;
    }

    public int getDomainId() {
        return domainId;
    }

    public String getUdi() {
        return udi;
    }

    public EventLoop getEventLoop() {
        return eventLoop;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public DomainParticipant getParticipant() {
        return participant;
    }

    public DeviceListModel getDeviceListModel() {
        return deviceListModel;
    }

    public EventLoopHandler getHandler() {
        return handler;
    }

    public static final void loadAndSetIceQos() {

        // Unfortunately this throws an Exception if there are errors in
        // XML profiles
        // which Exception prevents a more useful Exception throwing
        // later
        try {
            boolean userIceLibrary = false;

            File userProfiles = new File("USER_QOS_PROFILES.xml");
            if (userProfiles.exists() && userProfiles.isFile() && userProfiles.canRead()) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(userProfiles);
                NodeList libraryNodes = doc.getElementsByTagName("qos_library");
                for (int i = 0; i < libraryNodes.getLength(); i++) {
                    Node libraryNode = libraryNodes.item(i);
                    if (libraryNode.hasAttributes()) {
                        Node nameNode = libraryNode.getAttributes().getNamedItem("name");
                        if (QosProfiles.ice_library.equals(nameNode.getTextContent())) {
                            log.debug(QosProfiles.ice_library + " specified in USER_QOS_PROFILES.xml");
                            userIceLibrary = true;
                        }
                    }
                }
            }

            DomainParticipantFactory factory = DomainParticipantFactory.get_instance();
            DomainParticipantFactoryQos qos = new DomainParticipantFactoryQos();
            factory.get_qos(qos);

            if (!userIceLibrary) {
                loadIceQosLibrary(qos);
                log.debug("Loaded default ice_library QoS");
            }

            qos.resource_limits.max_objects_per_thread = 8192;
            factory.set_qos(qos);
            verifyQosLibraries();
        } catch (Exception e) {
            log.error("Unable to set factory qos", e);
            throw new RuntimeException("Unable to set factory qos", e);
        }
    }

    //MIKEFIX should be removed.
    public static void loadAndSetIceQosLibrary() throws IOException  {
        DomainParticipantFactory dpf = DomainParticipantFactory.get_instance();
        DomainParticipantFactoryQos qos = new DomainParticipantFactoryQos();
        dpf.get_qos(qos);
        loadIceQosLibrary(qos);
        dpf.set_qos(qos);
        verifyQosLibraries();
    }

    static void loadIceQosLibrary(DomainParticipantFactoryQos qos) throws IOException {

        URL url =  RtConfig.class.getResource("/META-INF/ice_library.xml");
        if (url != null) {
            log.info("Loading ice_library.xml from " + url.toExternalForm());

            InputStream is = url.openStream();
            java.util.Scanner scanner = new java.util.Scanner(is);
            try {
                qos.profile.url_profile.clear();
                qos.profile.string_profile.clear();
                qos.profile.string_profile.add(scanner.useDelimiter("\\A").next());
            } finally {
                scanner.close();
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
    }

    /**
     * @return true if all OK.
     */
    static boolean verifyQosLibraries() {
        DomainParticipantQos part_qos = new DomainParticipantQos();
        SubscriberQos sub_qos = new SubscriberQos();
        PublisherQos pub_qos = new PublisherQos();
        DataReaderQos r_qos = new DataReaderQos();
        DataWriterQos w_qos = new DataWriterQos();
        TopicQos t_qos = new TopicQos();

        DomainParticipantFactory dpf = DomainParticipantFactory.get_instance();
        StringSeq libraries = new StringSeq();
        StringSeq profiles = new StringSeq();
        dpf.get_qos_profile_libraries(libraries);
        for (int i = 0; i < libraries.size(); i++) {
            String library = (String) libraries.get(i);
            dpf.get_qos_profiles(profiles, library);
            for (int j = 0; j < profiles.size(); j++) {
                String profile = (String) profiles.get(j);
                dpf.get_participant_qos_from_profile(part_qos, library, profile);
                dpf.get_publisher_qos_from_profile(pub_qos, library, profile);
                dpf.get_subscriber_qos_from_profile(sub_qos, library, profile);
                dpf.get_datawriter_qos_from_profile(w_qos, library, profile);
                dpf.get_datareader_qos_from_profile(r_qos, library, profile);
                dpf.get_topic_qos_from_profile(t_qos, library, profile);

                String header = "Examining QoS profile: " + library + "::" + profile;
                header = verify(header, part_qos);
                header = verify(header, pub_qos);
                header = verify(header, sub_qos);
                header = verify(header, sub_qos, pub_qos);
                header = verify(header, r_qos);
                header = verify(header, w_qos);
                header = verify(header, r_qos, w_qos);
                header = verify(header, t_qos);
            }
            profiles.clear();
        }

        return true;
    }

    private static String verify(String header, DomainParticipantQos qos) {
        return header;
    }

    private static String verify(String header, TopicQos qos) {
        return header;
    }

    private static String verify(String header, SubscriberQos qos) {
        return header;
    }

    private static String verify(String header, PublisherQos qos) {
        return header;
    }

    private static String logHeader(String header) {
        if(null != header) {
            log.info(header);
        }
        return null;
    }

    private static String verify(String header, String name, HistoryQosPolicy history, ResourceLimitsQosPolicy resource_limits) {
        if (history.kind.equals(HistoryQosPolicyKind.KEEP_ALL_HISTORY_QOS)) {
            if (resource_limits.max_samples != ResourceLimitsQosPolicy.LENGTH_UNLIMITED) {
                header = logHeader(header);
                log.info("\t"+name+" KEEP_ALL history with max_samples=" + resource_limits.max_samples
                        + " will exclude newer samples when max_samples has been reached");
            }
            if (resource_limits.max_samples_per_instance != ResourceLimitsQosPolicy.LENGTH_UNLIMITED) {
                header = logHeader(header);
                log.info("\t"+name+" KEEP_ALL history with max_samples_per_instance=" + resource_limits.max_samples_per_instance
                        + " will exclude newer samples when max_samples_per_instance has been reached");
            }
        } else if (history.kind.equals(HistoryQosPolicyKind.KEEP_LAST_HISTORY_QOS)) {
            int depth = history.depth;
            if (resource_limits.max_samples!=ResourceLimitsQosPolicy.LENGTH_UNLIMITED && resource_limits.max_samples < depth) {
                header = logHeader(header);
                log.info("\t"+name+" KEEP_LAST depth=" + depth + " history with max_samples=" + resource_limits.max_samples
                        + " will exclude newer samples when max_samples has been reached");
            }
            if (resource_limits.max_samples_per_instance!=ResourceLimitsQosPolicy.LENGTH_UNLIMITED && resource_limits.max_samples_per_instance < depth) {
                header = logHeader(header);
                log.info("\t"+name+" KEEP_LAST depth=" + depth + " history with max_samples_per_instance="
                        + resource_limits.max_samples_per_instance + " will exclude newer samples when max_samples_per_instance has been reached");
            }
        }
        return header;
    }

    private static String verify(String header, String name, DurabilityQosPolicy durability, ReliabilityQosPolicy reliability) {
        if(!durability.kind.equals(DurabilityQosPolicyKind.VOLATILE_DURABILITY_QOS)) {
            if(!reliability.kind.equals(ReliabilityQosPolicyKind.RELIABLE_RELIABILITY_QOS)) {
                header = logHeader(header);
                log.info("\t"+name+" has durability="+durability.kind+" which is ineffective with reliability="+reliability.kind);
            }
        }
        return header;
    }

    private static String verify(String header, DataReaderQos qos) {
        header = verify(header, "DataReader", qos.history, qos.resource_limits);
        header = verify(header, "DataReader", qos.durability, qos.reliability);
        return header;
    }

    private static String verify(String header, DataWriterQos qos) {
        header = verify(header, "DataWriter", qos.history, qos.resource_limits);
        header = verify(header, "DataWriter", qos.durability, qos.reliability);
        return header;
    }

    private static String verify(String header, DataReaderQos rqos, DataWriterQos wqos) {
        // Should check for valid RxO for endpoints using the same profile
        return header;
    }

    private static String verify(String header, SubscriberQos sqos, PublisherQos pqos) {
        // Should check for valid RxO for endpoints using the same profile
        return header;
    }

}
