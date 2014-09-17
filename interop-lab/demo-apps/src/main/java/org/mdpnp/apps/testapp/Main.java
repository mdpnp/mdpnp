/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.apps.testapp;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.mdpnp.rtiapi.data.QosProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantFactoryQos;
import com.rti.dds.domain.DomainParticipantQos;
import com.rti.dds.infrastructure.DurabilityQosPolicy;
import com.rti.dds.infrastructure.DurabilityQosPolicyKind;
import com.rti.dds.infrastructure.HistoryQosPolicy;
import com.rti.dds.infrastructure.HistoryQosPolicyKind;
import com.rti.dds.infrastructure.ReliabilityQosPolicy;
import com.rti.dds.infrastructure.ReliabilityQosPolicyKind;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.publication.DataWriterQos;
import com.rti.dds.publication.PublisherQos;
import com.rti.dds.subscription.DataReaderQos;
import com.rti.dds.subscription.SubscriberQos;
import com.rti.dds.topic.TopicQos;

/**
 * @author Jeff Plourde
 * 
 */
public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static final String verify(String header, DomainParticipantQos qos) {
        return header;
    }

    public static final String verify(String header, TopicQos qos) {
        return header;
    }

    public static final String verify(String header, SubscriberQos qos) {
        return header;
    }

    public static final String verify(String header, PublisherQos qos) {
        return header;
    }

    private static String logHeader(String header) {
        if(null != header) {
            log.info(header);
        }
        return null;
    }
    
    public static final String verify(String header, String name, HistoryQosPolicy history, ResourceLimitsQosPolicy resource_limits) {
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
    
    public static final String verify(String header, String name, DurabilityQosPolicy durability, ReliabilityQosPolicy reliability) {
        if(!durability.kind.equals(DurabilityQosPolicyKind.VOLATILE_DURABILITY_QOS)) {
            if(!reliability.kind.equals(ReliabilityQosPolicyKind.RELIABLE_RELIABILITY_QOS)) {
                header = logHeader(header);
                log.info("\t"+name+" has durability="+durability.kind+" which is ineffective with reliability="+reliability.kind);
            }
        }
        return header;
    }

    public static final String verify(String header, DataReaderQos qos) {
        header = verify(header, "DataReader", qos.history, qos.resource_limits);
        header = verify(header, "DataReader", qos.durability, qos.reliability);
        return header;
    }
    
    public static final String verify(String header, DataWriterQos qos) {
        header = verify(header, "DataWriter", qos.history, qos.resource_limits);
        header = verify(header, "DataWriter", qos.durability, qos.reliability);
        return header;
    }

    public static final String verify(String header, DataReaderQos rqos, DataWriterQos wqos) {
        // Should check for valid RxO for endpoints using the same profile
        return header;
    }

    public static final String verify(String header, SubscriberQos sqos, PublisherQos pqos) {
        // Should check for valid RxO for endpoints using the same profile
        return header;
    }

    public static final void verifyQosLibraries() {
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

    }

    public static final void loadAndSetIceQosLibrary() {
        DomainParticipantFactory dpf = DomainParticipantFactory.get_instance();
        DomainParticipantFactoryQos qos = new DomainParticipantFactoryQos();
        dpf.get_qos(qos);
        loadIceQosLibrary(qos);
        dpf.set_qos(qos);
        verifyQosLibraries();
    }
    
    public static final void loadIceQosLibrary(DomainParticipantFactoryQos qos) {
        InputStream is = Main.class.getResourceAsStream("/META-INF/ice_library.xml");
        if (is != null) {
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

    public static void main2(String[] args) throws Exception {
        DomainParticipantFactory dpf = DomainParticipantFactory.get_instance();
        DomainParticipantFactoryQos qos = new DomainParticipantFactoryQos();
        dpf.get_qos(qos);
        loadIceQosLibrary(qos);
        dpf.set_qos(qos);
        verifyQosLibraries();
    }

    public static void main(String[] args) throws Exception {
        // TODO this should be external
        System.setProperty("java.net.preferIPv4Stack", "true");
//        final boolean debug = false;

        Configuration runConf = null;

        File jumpStartSettings = new File(".JumpStartSettings");
        File jumpStartSettingsHome = new File(System.getProperty("user.home"), ".JumpStartSettings");

        boolean cmdline = false;

        if (args.length > 0) {
            runConf = Configuration.read(args);
            cmdline = true;
        } else if (jumpStartSettings.exists() && jumpStartSettings.canRead()) {
            FileInputStream fis = new FileInputStream(jumpStartSettings);
            runConf = Configuration.read(fis);
            fis.close();
        } else if (jumpStartSettingsHome.exists() && jumpStartSettingsHome.canRead()) {
            FileInputStream fis = new FileInputStream(jumpStartSettingsHome);
            runConf = Configuration.read(fis);
            fis.close();
        }

        Configuration writeConf = null;

        try {
            Class<?> cls = Class.forName("com.apple.eawt.Application");
            Method m1 = cls.getMethod("getApplication");
            Method m2 = cls.getMethod("setDockIconImage", Image.class);
            m2.invoke(m1.invoke(null), ImageIO.read(Main.class.getResource("icon.png")));
        } catch (Throwable t) {
            log.debug("Not able to set Mac OS X dock icon");
        }

        if (!cmdline) {
            ConfigurationDialog d = new ConfigurationDialog(runConf, null);

            d.setIconImage(ImageIO.read(Main.class.getResource("icon.png")));
            runConf = d.showDialog();
            // It's nice to be able to change settings even without running
            if (null == runConf) {
                writeConf = d.getLastConfiguration();
            }
        } else {
            // fall through to allow configuration via a file
        }

        if (null != runConf) {
            writeConf = runConf;
        }

        if (null != writeConf) {
            if (!jumpStartSettings.exists()) {
                jumpStartSettings.createNewFile();
            }

            if (jumpStartSettings.canWrite()) {
                FileOutputStream fos = new FileOutputStream(jumpStartSettings);
                writeConf.write(fos);
                fos.close();
            }

            if (!jumpStartSettingsHome.exists()) {
                jumpStartSettingsHome.createNewFile();
            }

            if (jumpStartSettingsHome.canWrite()) {
                FileOutputStream fos = new FileOutputStream(jumpStartSettingsHome);
                writeConf.write(fos);
                fos.close();
            }
        }

        if (null != runConf) {

            {
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

            switch (runConf.getApplication()) {
            case ICE_Device_Interface:
                if(null == runConf.getDeviceType()) {
                    log.error("Unknown device type was specified");
                    System.exit(-1);
                }
                new DeviceAdapter().start(runConf.getDeviceType(), runConf.getDomainId(), runConf.getAddress(), !cmdline);
                break;
            case ICE_Supervisor:
                DemoApp.start(runConf.getDomainId());
                break;
            case ICE_ParticipantOnly:
                ParticipantOnly.start(runConf.getDomainId(), cmdline);
                break;
            }
        } else if (cmdline) {
            Configuration.help(Main.class, System.out);
        } else {

        }
        log.trace("This is the end of Main");
    }
}
