package org.mdpnp.devices.simulation.multi;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantFactoryQos;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.subscription.Subscriber;
import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.devices.EventLoopHandler;
import org.mdpnp.devices.IceQos;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.SampleArrayInstanceModel;
import org.mdpnp.rtiapi.data.SampleArrayInstanceModelImpl;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 */
public class SimMultiparameterTest {

    private static final int domainId=0;


    @Test
    public void testDeviceSetup() throws Exception {

        IceQos.loadAndSetIceQos();

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
        capnoModel.startReader(subscriber, eventLoop, "metric_id = %0 or metric_id = %1 ", params, QosProfiles.ice_library, QosProfiles.waveform_data);

        Thread.sleep(2000);

        int nDev = capnoModel.size();

        device.disconnect();
        capnoModel.stopReader();
        handler.shutdown();

        Assert.assertEquals("CapnoModel did not locate the device", 1, nDev);
    }

}
