package org.mdpnp.apps.testapp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mdpnp.devices.EventLoopHandler;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantFactoryQos;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.publication.Publisher;
import com.rti.dds.publication.PublisherQos;
import com.rti.dds.subscription.Subscriber;

//
// Combined with DriverContext.xml, RtConfig::setupDDS is like @Configuration annotation for spring
//
public class RtConfig {

    private static final Logger log = LoggerFactory.getLogger(RtConfig.class);

    private int domainId;
    private EventLoop eventLoop;
    private Publisher publisher;
    private Subscriber subscriber;
    private DomainParticipant participant;
    private EventLoopHandler handler;

    public void stop() {
        publisher.delete_contained_entities();
        participant.delete_publisher(publisher);
        subscriber.delete_contained_entities();
        participant.delete_subscriber(subscriber);
        participant.delete_contained_entities();
        DomainParticipantFactory.get_instance().delete_participant(participant);
        DomainParticipantFactory.finalize_instance();
    }
    
    
    public static RtConfig setupDDS(final int domainId) {
        final EventLoop eventLoop = new EventLoop();
        final EventLoopHandler handler = new EventLoopHandler(eventLoop);

        // This could prove confusing
        TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));

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

        final CountDownLatch startSignal = new CountDownLatch(1);

        Runnable enable = new Runnable() {
            public void run() {
                participant.enable();
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
        conf.handler = handler;
        
        return conf;
    }

    //
    // ctor is private to ensure that only way to make one is via setupDDS factory method.
    //
    private RtConfig() {}

    public int getDomainId() {
        return domainId;
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

    public EventLoopHandler getHandler() {
        return handler;
    }

    public static boolean loadAndSetIceQos() {

        // Unfortunately this throws an Exception if there are errors in
        // XML profiles
        // which Exception prevents a more useful Exception throwing
        // later
        try {

            File userProfiles = new File("USER_QOS_PROFILES.xml");
            boolean userIceLibrary = hasUserDefinedQoS(userProfiles);

            DomainParticipantFactory factory = DomainParticipantFactory.get_instance();
            DomainParticipantFactoryQos qos = new DomainParticipantFactoryQos();
            factory.get_qos(qos);

            if (!userIceLibrary) {
                loadIceQosLibrary(qos);
                log.debug("Loaded default ice_library QoS");
            }

            qos.resource_limits.max_objects_per_thread = 8192;
            factory.set_qos(qos);

            return userIceLibrary;

        } catch (Exception e) {
            log.error("Unable to set factory qos", e);
            throw new RuntimeException("Unable to set factory qos", e);
        }
    }

    static boolean hasUserDefinedQoS(File userProfiles) throws ParserConfigurationException, SAXException, IOException {

        boolean userIceLibrary = false;

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
        return userIceLibrary;
    }


    static void loadIceQosLibrary(DomainParticipantFactoryQos qos) throws IOException {

        URL url = RtConfig.class.getResource("/META-INF/ice_library.xml");
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
}
