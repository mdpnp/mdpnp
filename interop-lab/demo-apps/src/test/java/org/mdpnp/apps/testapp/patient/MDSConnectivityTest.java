package org.mdpnp.apps.testapp.patient;

import ice.MDSConnectivity;
import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.apps.testapp.RtConfig;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MDSConnectivityTest {

    @Test
    public void testReadWrite() throws Exception
    {
        RtConfig.loadAndSetIceQos();

        RtConfig master = RtConfig.setupDDS(0);

        final MDSConnectivity sample = new MDSConnectivity();
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
                Assert.fail("Failed to close the dialog");

        }
        catch (Exception ex) {
            throw ex;
        }
        finally {
            master.stop();
        }
    }
}
