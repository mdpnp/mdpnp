package org.mdpnp.devices;

import ice.MDSConnectivity;
import ice.MDSConnectivityObjective;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MDSConnectivityAdapterTest {

    @Test
    public void testReadWrite() throws Exception
    {
        RtConfig.loadAndSetIceQos();

        RtConfig master = RtConfig.setupDDS(0);

        final MDSConnectivityObjective sample = new MDSConnectivityObjective();
        sample.partition="p1";
        sample.unique_device_identifier=Long.toBinaryString(System.currentTimeMillis());

        try {
            final CountDownLatch stopOk = new CountDownLatch(1);


            MDSConnectivityAdapter c = new MDSConnectivityAdapter();
            c.createReader(master.getSubscriber());
            c.createWriter(master.getPublisher());
            c.start();

            c.addConnectivityListener(new MDSConnectivityAdapter.MDSConnectivityListener() {
                @Override
                public void handleDataSampleEvent(MDSConnectivityAdapter.MDSConnectivityEvent evt) throws Exception {
                    MDSConnectivity v = (MDSConnectivity)evt.getSource();
                    if(sample.unique_device_identifier.equals(v.unique_device_identifier))
                        stopOk.countDown();
                }
            });

            c.publish(sample);

            boolean isOk = stopOk.await(5000, TimeUnit.MILLISECONDS);
            c.stop();
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
}
