package org.mdpnp.devices;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.net.URL;

public class PartitionAssignmentControllerTest {

    @Test
    public void testCheckForNoPartitionFile() {

        PartitionAssignmentController controller = new PartitionAssignmentController() {
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

        PartitionAssignmentController controller = new PartitionAssignmentController() {
            public void setPartition(String[] partition) {
                Assert.assertEquals(2, partition.length);
                Assert.assertEquals("foo", partition[0]);
                Assert.assertEquals("bar", partition[1]);
            }
        };

        controller.checkForPartitionFile(new File(f));
    }
}


