package org.mdpnp.devices;

import static org.junit.Assert.assertEquals;
import ice.HeartBeat;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.qos.IceQos;

import com.rti.dds.infrastructure.Duration_t;
import com.rti.dds.publication.Publisher;
import com.rti.dds.publication.PublisherQos;
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
        IceQos.loadAndSetIceQos();
        
        eventLoop = new EventLoop();
        eventLoopHandler = new EventLoopHandler(eventLoop);
        executor = Executors.newSingleThreadScheduledExecutor();
        
        ff = new DomainParticipantFactoryFactory();
        f = ff.getObject();
        
        
        dpf1 = new DomainParticipantFactory(0);
        dpf2 = new DomainParticipantFactory(0);
        dpf3 = new DomainParticipantFactory(0);
        
        // Creates a heartbeating "Device" in partition A
        s1 = new SubscriberFactory(dpf1.getObject(), "A");
        p1 = new PublisherFactory(dpf1.getObject(), "A");
        t1 = new TimeManager(executor, eventLoop, p1.getObject(), s1.getObject(), "123", "Device");
        
        // Creates a TimeManager in "A" partition
        s2 = new SubscriberFactory(dpf2.getObject(), "A");
        p2 = new PublisherFactory(dpf2.getObject(), "A");
        t2 = new TimeManager(executor, eventLoop, p2.getObject(), s2.getObject(), "456", "Supervisor");

        liveUdis1 = new HashSet<String>();
        t2.addListener(listener);

        
        // in wildcard partition for listen only (No Type)
        s3 = new SubscriberFactory(dpf3.getObject(), "*");
        p3 = new PublisherFactory(dpf3.getObject(), "*");
        t3 = new TimeManager(executor, eventLoop, p3.getObject(), s3.getObject(), "456", null);

        t1.start();
        t2.start();
        t3.start();
    }

    @After
    public void tearDown() throws Exception {
        
        
        t3.stop();
        t2.stop();
        t1.stop();
        
        t2.removeListener(listener);
        
        p3.destroy();
        s3.destroy();
        p2.destroy();
        s2.destroy();
        p1.destroy();
        s1.destroy();
        
        dpf1.destroy();
        dpf2.destroy();
        dpf3.destroy();
        
        executor.shutdownNow();
        eventLoopHandler.shutdown();
        

        ff.destroy();
    }

    
    private DomainParticipantFactory dpf1, dpf2, dpf3;
    private SubscriberFactory s1, s2, s3;
    private PublisherFactory  p1, p2, p3;
    private TimeManager t1, t2, t3;
    private Set<String> liveUdis1;
    
    private EventLoop eventLoop;
    private EventLoopHandler eventLoopHandler;
    private ScheduledExecutorService executor;
    
    private TimeManagerListener listener = new TimeManagerListener() {

        @Override
        public void aliveHeartbeat(final String unique_device_identifier, final String type, String host_name) {
            synchronized(liveUdis1) {
                liveUdis1.add(unique_device_identifier);
                liveUdis1.notifyAll();
            }
            System.err.println(new Date() + " Live UDI " + unique_device_identifier);
        }

        @Override
        public void notAliveHeartbeat(final String unique_device_identifier, final String type) {
            synchronized(liveUdis1) {
                liveUdis1.remove(unique_device_identifier);
                liveUdis1.notifyAll();
            }
            System.err.println(new Date() + " Unalive UDI " + unique_device_identifier);
        }

        @Override
        public void synchronization(String remote_udi, Duration_t latency, Duration_t clockDifference) {
        }
        
    };
    
    @Test
    public void testMultipleTimeManager() throws Exception {
        System.err.println("testMultipleTimeManager:");
        waitForSizeOrTimeout(1, DEFAULT_TIMEOUT, () -> assertEquals("see device in partition A", 1, liveUdis1.size()));
    }
    
    private void waitForSizeOrTimeout(int size, long timeout, Runnable r) throws InterruptedException {
        long giveUp = System.nanoTime() + timeout;
        synchronized(liveUdis1) {
            while(liveUdis1.size() != size) {
                long now = System.nanoTime();
                long remaining = giveUp - now;
                if(remaining <= 0) {
                    break;
                } else {
                    liveUdis1.wait(remaining / 1000000L, (int)(remaining % 1000000L));
                }
            }
            r.run();
        }
    }
    private static final long DEFAULT_TIMEOUT = 10000000000L; 
    
    @Test
    public void testDetectNoDevice() throws Exception {
        waitForSizeOrTimeout(1, DEFAULT_TIMEOUT, () -> assertEquals("device never found", 1, liveUdis1.size()));
        t1.stop();
        waitForSizeOrTimeout(0, DEFAULT_TIMEOUT, () -> assertEquals("device didn't go away", 0, liveUdis1.size()));
    }
    
    @Test
    public void testMultipleTimeManagerChangePartition() throws Exception {
        System.err.println("testMultipleTimeMAnagerChangePartition:");
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

        synchronized(liveUdis1) {
            if (!liveUdis1.isEmpty()) {
                System.err.println(liveUdis1);
            }
        }
        waitForSizeOrTimeout(0, DEFAULT_TIMEOUT, () -> assertEquals("TimeManager not unregistered on partition switch", 0, liveUdis1.size()));
        

    }

}
