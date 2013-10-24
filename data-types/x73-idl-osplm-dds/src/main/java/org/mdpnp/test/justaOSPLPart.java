package org.mdpnp.test;

import ice.Numeric;

import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.concurrent.TimeUnit;

import org.omg.dds.core.Duration;
import org.omg.dds.core.ServiceEnvironment;
import org.omg.dds.core.policy.Durability;
import org.omg.dds.core.policy.History;
import org.omg.dds.core.policy.Liveliness;
import org.omg.dds.core.policy.Ownership;
import org.omg.dds.core.policy.PolicyFactory;
import org.omg.dds.core.policy.Reliability;
import org.omg.dds.domain.DomainParticipant;
import org.omg.dds.domain.DomainParticipantFactory;
import org.omg.dds.sub.DataReader;
import org.omg.dds.sub.Sample;
import org.omg.dds.sub.SampleState;
import org.omg.dds.sub.Subscriber;
import org.omg.dds.sub.DataReader.Selector;
import org.omg.dds.topic.ParticipantBuiltinTopicData;
import org.omg.dds.topic.Topic;
import org.omg.dds.type.TypeSupport;

public class justaOSPLPart {
    public static void main(String[] args) throws SocketException {
        Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
        StringBuilder sb = new StringBuilder();
        if(nis.hasMoreElements()) {
            NetworkInterface ni = nis.nextElement();
            sb.append(ni.getName());
        }
        while(nis.hasMoreElements()) {
            NetworkInterface ni = nis.nextElement();
            sb.append(",").append(ni.getName());
        }
        System.out.println("Interfaces:"+sb.toString());
        System.setProperty("java.net.preferIPv4Stack","true");
        System.setProperty("ddsi.participant.leaseDuration", "3.0");
        System.setProperty("ddsi.spdp.writer.resendPeriod", "0.250");
        System.setProperty("ddsi.networkInterfaces", sb.toString());
//        System.setProperty("ddsi.participant.discoveryWaitPeriod", "0.0");



        // Set "serviceClassName" property to OpenSplice Mobile implementation
        System.setProperty(ServiceEnvironment.IMPLEMENTATION_CLASS_NAME_PROPERTY,
            "org.opensplice.mobile.core.ServiceEnvironmentImpl");

        // Instantiate a DDS ServiceEnvironment
        ServiceEnvironment env = ServiceEnvironment.createInstance(
            Main.class.getClassLoader());

        // Get the DomainParticipantFactory
        DomainParticipantFactory dpf = DomainParticipantFactory.getInstance(env);

        dpf.setQos(dpf.getQos().withPolicy(env.getSPI().getPolicyFactory().EntityFactory().withAutoEnableCreatedEntities(false)));

        // Create a DomainParticipant with domainID=0
        DomainParticipant p = dpf.createParticipant(0);
        p.getDiscoveredParticipants();
        DataReader<ParticipantBuiltinTopicData> partReader = p.getBuiltinSubscriber().lookupDataReader("DCPSParticipant");
        if(null == partReader) {
            throw new IllegalArgumentException("NO READER");
        }

        TypeSupport<Numeric> ts = TypeSupport.newTypeSupport(Numeric.class, "ice::Numeric", env);

        Topic<Numeric> topic = p.createTopic(ice.NumericTopic.value, ts);

        // Create a Partition QoS with "HelloWorld example" as partition.
//        Partition partition = PolicyFactory.getPolicyFactory(env)
//            .Partition().withName("HelloWorld example");

        // Create a Subscriber using default QoS except partition
        Subscriber sub = p.createSubscriber(p.getDefaultSubscriberQos());
        // Create Reliability and Durability QoS
        Reliability r = PolicyFactory.getPolicyFactory(env).Reliability().withReliable();
        Durability  d = PolicyFactory.getPolicyFactory(env).Durability().withTransientLocal();
        Ownership o = PolicyFactory.getPolicyFactory(env).Ownership().withExclusive();
        Liveliness l = PolicyFactory.getPolicyFactory(env).Liveliness().withAutomatic().withLeaseDuration(2, TimeUnit.SECONDS);
        History h = PolicyFactory.getPolicyFactory(env).History().withKeepLast(10);
        // Create DataReader on our topic with default QoS except Reliability and Durability
        DataReader<Numeric> reader = sub.createDataReader(topic,
            sub.getDefaultDataReaderQos().withPolicies(r, d, o, l, h));

        // Prepare a List of Sample<Msg> for received samples
//        List<Sample<Numeric>> samples = new ArrayList<Sample<Numeric>>();

        // Try to take samples every seconds. We stop as soon as we get some.
        while(true)
        {
            Selector<Numeric> selector = reader.select();
            selector.dataState(sub.createDataState().withAnyInstanceState().withAnyViewState().with(SampleState.NOT_READ));
            Sample.Iterator<Numeric> itr = reader.read(selector);
            try {
                int cnt = 0;
                while(itr.hasNext()) {
                    Sample<Numeric> sample = itr.next();
                    if(sample != null && sample.getData() != null) {
                        System.out.println("Received:"+sample.getData().metric_id+" "+sample.getData().value);
                    }
                    cnt++;
                }
                System.out.println("Samples:"+cnt);
            } finally {
                try {
                    itr.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            try
            {
                Thread.sleep(1000);
            }
            catch (InterruptedException e)
            {
                // nothing
            }
        }

        // Close Participant (closing also chlidren entities: Topic, Subscriber, DataReader)
//        p.close();
    }
}
