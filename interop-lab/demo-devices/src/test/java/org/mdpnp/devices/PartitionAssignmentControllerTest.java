package org.mdpnp.devices;

import ice.MDSConnectivity;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class PartitionAssignmentControllerTest {

    ice.DeviceIdentity deviceIdentity = new ice.DeviceIdentity();

    @Test
    public void testPartitionNames() {

        String[] partitions = { "A", "B", "C"};
        String s = PartitionAssignmentController.toString(partitions);
        Assert.assertEquals("A,B,C", s);
    }

    @Test
    public void testSetPartition() throws Exception{

        RtConfig.loadAndSetIceQos();

        RtConfig master = RtConfig.setupDDS(0);

        try {
            final CountDownLatch stopOk = new CountDownLatch(1);

            PartitionAssignmentController controller =
                    new PartitionAssignmentController(deviceIdentity,
                                                      master.getEventLoop(),
                                                      master.getPublisher(),
                                                      master.getSubscriber());

            MDSHandler mdsHandler = controller.getConnectivityAdapter();
            mdsHandler.addConnectivityListener(new MDSHandler.Connectivity.MDSListener() {
                @Override
                public void handleDataSampleEvent(MDSHandler.Connectivity.MDSEvent evt) {
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
            master.stop();
        }
    }

    @Test
    public void testCheckForNoPartitionFile() {

        PartitionAssignmentController controller = new PartitionAssignmentController(deviceIdentity) {
            public void setPartition(String[] partition) {
                Assert.assertEquals(0, partition.length);
            }
        };

        controller.checkForPartitionFile(null);
    }

    @Test
    public void testCheckForPartitionFile() {

        URL u = getClass().getResource("device.partition.0.txt");
        String f = u.getFile();

        PartitionAssignmentController controller = new PartitionAssignmentController(deviceIdentity) {
            public void setPartition(String[] partition) {
                Assert.assertEquals(2, partition.length);
                Assert.assertEquals("foo", partition[0]);
                Assert.assertEquals("bar", partition[1]);
            }
        };

        controller.checkForPartitionFile(new File(f));
    }
}


