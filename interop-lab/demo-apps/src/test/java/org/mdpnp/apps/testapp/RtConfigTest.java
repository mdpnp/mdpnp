package org.mdpnp.apps.testapp;

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

/**
 *
 */
public class RtConfigTest {

    @Test
    public void testVerifyQosLibraries() throws Exception
    {
        DomainParticipantFactory dpf = DomainParticipantFactory.get_instance();
        DomainParticipantFactoryQos qos = new DomainParticipantFactoryQos();
        dpf.get_qos(qos);
        RtConfig.loadIceQosLibrary(qos);
        dpf.set_qos(qos);
        boolean ok = RtConfig.verifyQosLibraries();
        Assert.assertTrue("RtConfig.verifyQosLibraries failed", ok);
    }
}
