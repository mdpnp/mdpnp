package org.mdpnp.devices;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import ice.HeartBeat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.publication.Publisher;
import com.rti.dds.publication.PublisherQos;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.SubscriberQos;

public class TimeManagerTest {
    DomainParticipantFactoryFactory ff;
    com.rti.dds.domain.DomainParticipantFactory f;
    
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {

    }

    @Before
    public void setUp() throws Exception {
        ff = new DomainParticipantFactoryFactory();
        f = ff.getObject();
        IceQos.loadAndSetIceQos();
        
        dpf = new DomainParticipantFactory(0);
        
        // Creates a heartbeating "Device" in partition A
        s1 = new SubscriberFactory(dpf.getObject(), "A");
        p1 = new PublisherFactory(dpf.getObject(), "A");
        t1 = new TimeManager(p1.getObject(), s1.getObject(), "123", "Device");
        liveUdis1 = new HashSet<String>();
        t1.addListener(new TimeManagerListener() {

            @Override
            public void aliveHeartbeat(SampleInfo sampleInfo, HeartBeat heartbeat, String host_name) {
                synchronized(liveUdis1) {
                    liveUdis1.add(heartbeat.unique_device_identifier);
                    liveUdis1.notifyAll();
                }
                System.err.println("Live UDI " + heartbeat.unique_device_identifier);
            }

            @Override
            public void notAliveHeartbeat(SampleInfo sampleInfo, HeartBeat heartbeat) {
                synchronized(liveUdis1) {
                    liveUdis1.add(heartbeat.unique_device_identifier);
                    liveUdis1.notifyAll();
                }
                System.err.println("Unalive UDI " + heartbeat.unique_device_identifier);
            }

            @Override
            public void synchronization(String remote_udi, Duration_t latency, Duration_t clockDifference) {
            }
            
        });
        
     // Creates a TimeManager in "A" partition
        s2 = new SubscriberFactory(dpf.getObject(), "A");
        p2 = new PublisherFactory(dpf.getObject(), "A");
        t2 = new TimeManager(p2.getObject(), s2.getObject(), "456", "Supervisor");
        
        // in wildcard partition for listen only (No Type)
        s3 = new SubscriberFactory(dpf.getObject(), "*");
        p3 = new PublisherFactory(dpf.getObject(), "*");
        t3 = new TimeManager(p3.getObject(), s3.getObject(), "456");
        
        t1.start();
        t2.start();
        t3.start();
    }

    @After
    public void tearDown() throws Exception {
        t3.stop();
        t2.stop();
        t1.stop();
        
        
        p3.destroy();
        s3.destroy();
        p2.destroy();
        s2.destroy();
        p1.destroy();
        s1.destroy();
        dpf.destroy();
        
        
        ff.destroy();
    }

    
    private DomainParticipantFactory dpf;
    private SubscriberFactory s1, s2, s3;
    private PublisherFactory  p1, p2, p3;
    private TimeManager t1, t2, t3;
    private Set<String> liveUdis1;
    
    @Test
    public void testMultipleTimeManager() throws Exception {
        long giveUp = System.nanoTime() + 5000000000L;
        synchronized(liveUdis1) {
            while(liveUdis1.size() != 1) {
                long now = System.nanoTime();
                if(now >= giveUp) {
                    break;
                } else {
                    liveUdis1.wait(0L, (int)(giveUp-System.nanoTime()));
                }
            }
            assertEquals("see device in partition A", 1, liveUdis1.size());
        }
    }
    
    private void waitForSizeOrTimeout(int size, long timeout, Runnable r) throws InterruptedException {
        long giveUp = System.nanoTime() + timeout;
        synchronized(liveUdis1) {
            while(liveUdis1.size() != size) {
                long now = System.nanoTime();
                long remaining = giveUp - now;
                if(remaining > 0) {
                    break;
                } else {
                    liveUdis1.wait(remaining / 1000000L, (int)(remaining % 1000000L));
                }
            }
            r.run();
        }
    }
    private static final long DEFAULT_TIMEOUT = 5000000000L; 
    @Test
    public void testMultipleTimeManagerChangePartition() throws Exception {
        // In "A" partition
        Subscriber subscriberTarget = s2.getObject();
        Publisher  publisherTarget  = p2.getObject();
        
        waitForSizeOrTimeout(1, DEFAULT_TIMEOUT, () -> assertEquals("see device in partition A", 1, liveUdis1.size()));
        
        // Change the partition of the target TimeManager and ensure that the source disappears
        SubscriberQos sQos = new SubscriberQos();
        PublisherQos  pQos = new PublisherQos();
        subscriberTarget.get_qos(sQos);
        publisherTarget.get_qos(pQos);
        sQos.partition.name.clear();
        pQos.partition.name.clear();
        sQos.partition.name.add("B");
        pQos.partition.name.add("B");
        subscriberTarget.set_qos(sQos);
        publisherTarget.set_qos(pQos);
        
        waitForSizeOrTimeout(0, DEFAULT_TIMEOUT, () -> assertTrue("TimeManager not unregistered on partition switch", liveUdis1.isEmpty()));

    }

}
