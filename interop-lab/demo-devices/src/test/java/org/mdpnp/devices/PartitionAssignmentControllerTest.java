package org.mdpnp.devices;

import ice.MDSConnectivity;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

public class PartitionAssignmentControllerTest {

    ice.DeviceIdentity deviceIdentity = new ice.DeviceIdentity();

    private ConfigurableApplicationContext createContext() throws Exception {
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
    public void testPartitionNames() {

        String[] partitions = { "A", "B", "C"};
        String s = PartitionAssignmentController.toString(partitions);
        Assert.assertEquals("A,B,C", s);
    }

    @Test
    public void testParsePartitions() throws Exception {

        String input =
                "# A comment \n" +
                "   device-group  \n" +
                "MRN=joe\n" +
                "MRN=should be ignored\n";

        InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));

        PartitionAssignmentController.PersistentPartitionAssignment c =
                new PartitionAssignmentController.PersistentPartitionAssignment(null);

        List<String> l = c.readPartitionFile(br);

        Assert.assertEquals(2, l.size());
        Assert.assertEquals("MRN=joe", PartitionAssignmentController.findMRNPartition(l));

    }

    @Test
    public void testSetPartition() throws Exception {
        ConfigurableApplicationContext ctx = createContext();
        
        final DomainParticipant participant = ctx.getBean("domainParticipant", DomainParticipant.class);
        final Subscriber subscriber = ctx.getBean("subscriber", Subscriber.class);
        final Publisher  publisher  = ctx.getBean("publisher", Publisher.class);
        final EventLoop eventLoop   = ctx.getBean("eventLoop", EventLoop.class);
        
        try {
            final CountDownLatch stopOk = new CountDownLatch(1);

            PartitionAssignmentController controller =
                    new PartitionAssignmentController(deviceIdentity,
                                                      participant,
                                                      eventLoop,
                                                      publisher,
                                                      subscriber);

            MDSHandler mdsHandler = controller.getConnectivityAdapter();
            mdsHandler.addConnectivityListener(new MDSHandler.Connectivity.MDSListener() {
                @Override
                public void handleConnectivityChange(MDSHandler.Connectivity.MDSEvent evt) {
                    MDSConnectivity v = (MDSConnectivity) evt.getSource();
                    if (deviceIdentity.unique_device_identifier.equals(v.unique_device_identifier))
                        stopOk.countDown();
                }
            });
            controller.start();

            String[] partitions = { "testPartition1", "testPartition2", "testPartition3"};
            controller.setPartition(partitions);

            boolean isOk = stopOk.await(5000, TimeUnit.MILLISECONDS);
            controller.shutdown();
            if (!isOk)
                Assert.fail("Did not get publication method");
        }
        catch (Exception ex) {
            throw ex;
        }
        finally {
            ctx.close();
        }
    }

    @Test
    public void testCheckForNoPartitionFile() {

        PartitionAssignmentController.PersistentPartitionAssignment controller = new PartitionAssignmentController.PersistentPartitionAssignment(deviceIdentity) {
            @Override
            public void setPartition(String[] partition) {
                Assert.assertEquals(0, partition.length);
            }
            @Override
            void configureQosForPartition(String[] partition) {}

        };

        controller.checkForPartitionFile(null);
    }

    @Test
    public void testCheckForPartitionFile() {

        URL u = getClass().getResource("device.partition.0.txt");
        String f = u.getFile();

        PartitionAssignmentController.PersistentPartitionAssignment controller = new PartitionAssignmentController.PersistentPartitionAssignment(deviceIdentity) {
            @Override
            public void setPartition(String[] partition) {
                Assert.assertEquals(2, partition.length);
                Assert.assertEquals("foo", partition[0]);
                Assert.assertEquals("bar", partition[1]);
            }
            @Override
            void configureQosForPartition(String[] partition) {}
        };

        controller.checkForPartitionFile(new File(f));
    }
}


