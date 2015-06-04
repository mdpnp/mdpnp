package org.mdpnp.devices.simulation.multi;

import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.devices.EventLoopHandler;
import org.mdpnp.devices.PublisherFactory;
import org.mdpnp.devices.SubscriberFactory;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.SampleArrayInstanceModel;
import org.mdpnp.rtiapi.data.SampleArrayInstanceModelImpl;
import org.mdpnp.rtiapi.qos.IceQos;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantFactoryQos;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.subscription.Subscriber;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 *
 */
public class SimMultiparameterTest {

    private static final int domainId=0;


    @Test
    public void testDeviceSetup() throws Exception {

        IceQos.LoadStatus qosStatus = IceQos.loadAndSetIceQos();
        if(qosStatus==IceQos.LoadStatus.NONE)
          Assert.fail("Failed to load QOS settings");

        EventLoop eventLoop = new EventLoop();
        EventLoopHandler handler = new EventLoopHandler(eventLoop);
        org.mdpnp.devices.DomainParticipantFactory dpf = new org.mdpnp.devices.DomainParticipantFactory(domainId);
        SubscriberFactory sf = new SubscriberFactory(dpf.getObject());
        PublisherFactory pf = new PublisherFactory(dpf.getObject());

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        SimMultiparameter device = new SimMultiparameter(sf.getObject(), pf.getObject(), eventLoop);
        device.setExecutor(scheduler);
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
        params.add("'" + rosetta.MDC_AWAY_CO2.VALUE+"'");
        params.add("'" + rosetta.MDC_IMPED_TTHOR.VALUE + "'");
        capnoModel.startReader(subscriber, eventLoop, "metric_id = %0 or metric_id = %1 ", params, QosProfiles.ice_library, QosProfiles.waveform_data);

        Thread.sleep(5000);

        int nDev = capnoModel.size();

        device.disconnect();
        capnoModel.stopReader();
        handler.shutdown();

        scheduler.shutdown();
        
        Assert.assertEquals("CapnoModel did not locate the device", 1, nDev);
    }

}
