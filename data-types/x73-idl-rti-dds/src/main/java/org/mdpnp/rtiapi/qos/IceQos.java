package org.mdpnp.rtiapi.qos;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mdpnp.rtiapi.data.QosProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantFactoryQos;

//
// Combined with DriverContext.xml, RtConfig::setupDDS is like @Configuration annotation for spring
//
public class IceQos {

    private static final Logger log = LoggerFactory.getLogger(IceQos.class);

    private IceQos() {}

    public enum LoadStatus { NONE, USER, SYSTEM }

    public static LoadStatus loadAndSetIceQos() {

        // Unfortunately this throws an Exception if there are errors in
        // XML profiles
        // which Exception prevents a more useful Exception throwing
        // later
        try {

            File userProfiles = new File("USER_QOS_PROFILES.xml");
            LoadStatus statusCode = hasUserDefinedQoS(userProfiles)?LoadStatus.USER:LoadStatus.NONE;

            DomainParticipantFactory factory = DomainParticipantFactory.get_instance();
            DomainParticipantFactoryQos qos = new DomainParticipantFactoryQos();
            factory.get_qos(qos);

            if (statusCode==LoadStatus.NONE) {
                statusCode = loadIceQosLibrary(qos)?LoadStatus.SYSTEM:LoadStatus.NONE;
                log.info((statusCode==LoadStatus.SYSTEM?"Loaded":"Failed to load") + " default ice_library QoS from classpath");
            }

            qos.resource_limits.max_objects_per_thread = 8192;
            factory.set_qos(qos);

            return statusCode;

        } catch (Exception e) {
            log.error("Unable to set factory qos", e);
            throw new RuntimeException("Unable to set factory qos", e);
        }
    }

    public static boolean hasUserDefinedQoS(File userProfiles) throws ParserConfigurationException, SAXException, IOException {

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


    public static boolean loadIceQosLibrary(DomainParticipantFactoryQos qos) throws IOException {

		URL url;
        //
        // handle the -Dmdpnp.dds.qos=file:///some/path/to/ice_library.xml
        //
		if(QOS_DEFINITION.indexOf(":") > 0) {
			try {
				url = new URL(QOS_DEFINITION);
			}
			catch(MalformedURLException ex) {
				url = null;
			}
		}
		else
			url = IceQos.class.getResource(QOS_DEFINITION);

		if (url != null) {
			log.info("Loading ice_library.xml from " + url.toExternalForm());
			InputStream is = url.openStream();

            java.util.Scanner scanner = new java.util.Scanner(is);
            try {
                qos.profile.url_profile.clear();
                qos.profile.string_profile.clear();
                qos.profile.string_profile.add(scanner.useDelimiter("\\A").next());
                return true;
            } finally {
                scanner.close();
                try {
                    is.close();
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
        else {
			log.error("Could not locate '" + QOS_DEFINITION +"'");
            return false;
        }
    }

	private static final String QOS_DEFINITION = System.getProperty("mdpnp.dds.qos", "/META-INF/ice_library.xml");
}
