package org.mdpnp.devices;

import java.util.Properties;

import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantFactoryQos;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.infrastructure.*;
import com.rti.dds.publication.DataWriterQos;
import com.rti.dds.publication.PublisherQos;
import com.rti.dds.subscription.DataReaderQos;
import com.rti.dds.subscription.SubscriberQos;
import com.rti.dds.topic.TopicQos;

import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.rtiapi.qos.IceQos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 *
 */
public class RtConfigTest {

    private static final Logger log = LoggerFactory.getLogger(RtConfigTest.class);

    private ConfigurableApplicationContext createContext() throws Exception
    {
        ClassPathXmlApplicationContext ctx =
                new ClassPathXmlApplicationContext(new String[] { "RtConfig.xml" }, false);
        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        Properties props = new Properties();
        props.load(getClass().getResourceAsStream("/RtConfig.properties"));
        ppc.setProperties(props);
        ppc.setOrder(0);

        ctx.addBeanFactoryPostProcessor(ppc);
        ctx.refresh();
        return ctx;
    }
    
    @Test
    public void testLifecycleReload() throws Exception
    {
        for(int i=0; i<5; i++) {
            try {
                ConfigurableApplicationContext ctx = createContext();
                ctx.close();
            }
            catch(Exception ex) {
                log.error("Failed to loadAndSetIceQos", ex);
                Assert.fail("Failed to loadAndSetIceQos on iteration #" + i);
            }
        }
    }

    @Test
    public void testVerifyQosLibraries() throws Exception
    {
        DomainParticipantFactory dpf = DomainParticipantFactory.get_instance();
        DomainParticipantFactoryQos qos = new DomainParticipantFactoryQos();
        dpf.get_qos(qos);
        IceQos.loadIceQosLibrary(qos);
        dpf.set_qos(qos);
        boolean ok = verifyQosLibraries();

        dpf.unload_profiles();
        DomainParticipantFactory.finalize_instance();

        Assert.assertTrue("RtConfig.verifyQosLibraries failed", ok);
    }


    /**
     * @return true if all OK.
     */
    private boolean verifyQosLibraries() {
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
