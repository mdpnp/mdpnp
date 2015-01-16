package org.mdpnp.apps.testapp.rrr;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantFactoryQos;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.subscription.Subscriber;
import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.apps.testapp.IceAppsContainer;
import org.mdpnp.apps.testapp.RtConfig;
import org.mdpnp.devices.EventLoopHandler;
import org.mdpnp.devices.simulation.multi.SimMultiparameter;
import org.mdpnp.rtiapi.data.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 *
 */
public class RapidRespiratoryRateFactoryTest {

    private static final Logger log = LoggerFactory.getLogger(RapidRespiratoryRateFactoryTest.class);

    @Test
    public void testDeviceSetupViaSpring() throws Exception {

        RtConfig.loadAndSetIceQos();

        final AbstractApplicationContext context =
                new ClassPathXmlApplicationContext(new String[]{"IceAppContainerContext.xml"});

        RtConfig rtConfig = (RtConfig)context.getBean("rtConfig");

        SimMultiparameter device = new SimMultiparameter(rtConfig.domainId, rtConfig.eventLoop);
        device.connect(null);

        RapidRespiratoryRateFactory rrrf = new RapidRespiratoryRateFactory();
        IceAppsContainer.IceApp app = rrrf.create(context);

        InstanceModel capnoModel =  (InstanceModel)  context.getBean("capnoModel");

        int nDev = 0;
        for(int i=0; i<10; i++) {
            Thread.sleep(2000);
            nDev = capnoModel.getSize();
            if(nDev!=0)
                break;
            log.info("Wait for slow hardware to update...." + i);
        }

        device.disconnect();
        context.destroy();

        Assert.assertEquals("CapnoModel did not locate the device", 1, nDev);
    }

    /*
    @Test
    public void testDeviceSetupViaRtConfig() throws Exception {

        RtConfig.loadAndSetIceQos();

        final int domainId = 0;

        RtConfig rtConfig = RtConfig.setupDDS(domainId);

        SimMultiparameter device = new SimMultiparameter(domainId, rtConfig.eventLoop);
        device.connect(null);

        final SampleArrayInstanceModel capnoModel =
                new SampleArrayInstanceModelImpl(ice.SampleArrayTopic.VALUE);

        final Subscriber subscriber = rtConfig.getSubscriber();


        StringSeq params = new StringSeq();
        params.add("'"+rosetta.MDC_AWAY_CO2.VALUE+"'");
        params.add("'"+rosetta.MDC_IMPED_TTHOR.VALUE+"'");
        capnoModel.start(subscriber, rtConfig.eventLoop, "metric_id = %0 or metric_id = %1 ", params, QosProfiles.ice_library, QosProfiles.waveform_data);

        int nDev = 0;
        for(int i=0; i<10; i++) {
            Thread.sleep(2000);
            nDev = capnoModel.getSize();
            if(nDev!=0)
                break;
            log.info("Wait for slow hardware to update...." + i);
        }

        device.disconnect();
        capnoModel.stop();
        rtConfig.handler.shutdown();

        Assert.assertEquals("CapnoModel did not locate the device", 1, nDev);
    }

    @Test
    public void testDeviceSetupByHand1() throws Exception {

        RtConfig.loadAndSetIceQos();
        testDeviceSetupByHand();
    }

    @Test
    public void testDeviceSetupByHand0() throws Exception {

        loadIceQosLibrary();
        testDeviceSetupByHand();
    }

    public void testDeviceSetupByHand() throws Exception {

        int domainId=0;

        EventLoop eventLoop = new EventLoop();
        EventLoopHandler handler = new EventLoopHandler(eventLoop);

        SimMultiparameter device = new SimMultiparameter(domainId, eventLoop);
        device.connect(null);

        final SampleArrayInstanceModel capnoModel =
                new SampleArrayInstanceModelImpl(ice.SampleArrayTopic.VALUE);

        final DomainParticipantFactoryQos qos = new DomainParticipantFactoryQos();
        DomainParticipantFactory.get_instance().get_qos(qos);
        qos.entity_factory.autoenable_created_entities = false;
        DomainParticipantFactory.get_instance().set_qos(qos);

        final DomainParticipant participant =
                DomainParticipantFactory.get_instance().create_participant(domainId, DomainParticipantFactory.PARTICIPANT_QOS_DEFAULT, null,
                        StatusKind.STATUS_MASK_NONE);

        final Subscriber subscriber = participant.create_subscriber(DomainParticipant.SUBSCRIBER_QOS_DEFAULT, null, StatusKind.STATUS_MASK_NONE);

        participant.enable();

        StringSeq params = new StringSeq();
        params.add("'"+rosetta.MDC_AWAY_CO2.VALUE+"'");
        params.add("'"+rosetta.MDC_IMPED_TTHOR.VALUE+"'");
        capnoModel.start(subscriber, eventLoop, "metric_id = %0 or metric_id = %1 ", params, QosProfiles.ice_library, QosProfiles.waveform_data);

        int nDev = 0;
        for(int i=0; i<10; i++) {
            Thread.sleep(2000);
            nDev = capnoModel.getSize();
            if(nDev!=0)
                break;
            log.info("Wait for slow hardware to update...." + i);
        }

        device.disconnect();
        capnoModel.stop();
        handler.shutdown();

        Assert.assertEquals("CapnoModel did not locate the device", 1, nDev);
    }

    public void loadIceQosLibrary() throws Exception {

        DomainParticipantFactory factory = DomainParticipantFactory.get_instance();
        DomainParticipantFactoryQos qos = new DomainParticipantFactoryQos();
        factory.get_qos(qos);

        URL url = getClass().getResource("/ice_library.xml");
        if (url == null)
            throw new IOException("Cannot load '/ice_library.xml' from classpath");
        log.info("Loading ice_library.xml from " + url.toExternalForm());

        InputStream is = url.openStream();

        java.util.Scanner scanner = new java.util.Scanner(is);
        try {
            qos.profile.url_profile.clear();
            qos.profile.string_profile.clear();
            qos.profile.string_profile.add(scanner.useDelimiter("\\A").next());
        } finally {
            scanner.close();
            is.close();
        }

        qos.resource_limits.max_objects_per_thread = 8192;
        factory.set_qos(qos);
    }
    */

}
