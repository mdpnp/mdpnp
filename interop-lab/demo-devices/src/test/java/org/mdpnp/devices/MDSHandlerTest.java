package org.mdpnp.devices;

import ice.MDSConnectivity;
import ice.MDSConnectivityObjective;
import org.junit.Assert;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MDSHandlerTest {

    @Test
    public void testMDSConnectivity() throws Exception
    {
        RtConfig.loadAndSetIceQos();

        RtConfig master = RtConfig.setupDDS(0);

        final MDSConnectivity sample = new MDSConnectivity();
        sample.partition="p1";
        sample.unique_device_identifier=Long.toBinaryString(System.currentTimeMillis());

        try {
            final CountDownLatch stopOk = new CountDownLatch(1);


            MDSHandler.Connectivity c = new MDSHandler.Connectivity(master.getEventLoop(),
                                                                  master.getPublisher(),
                                                                  master.getSubscriber());
            c.start();

            c.addConnectivityListener(new MDSHandler.Connectivity.MDSListener() {
                @Override
                public void handleDataSampleEvent(MDSHandler.Connectivity.MDSEvent evt) {
                    MDSConnectivity v = (MDSConnectivity)evt.getSource();
                    if(sample.unique_device_identifier.equals(v.unique_device_identifier))
                        stopOk.countDown();
                }
            });

            c.publish(sample);

            boolean isOk = stopOk.await(5000, TimeUnit.MILLISECONDS);
            c.shutdown();
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
    public void testMDSConnectivityObjective() throws Exception
    {
        RtConfig.loadAndSetIceQos();

        RtConfig master = RtConfig.setupDDS(0);

        final MDSConnectivityObjective sample = new MDSConnectivityObjective();
        sample.partition="p1";
        sample.unique_device_identifier=Long.toBinaryString(System.currentTimeMillis());

        try {
            final CountDownLatch stopOk = new CountDownLatch(1);


            MDSHandler.Objective c = new MDSHandler.Objective(master.getEventLoop(),
                                                                                                          master.getPublisher(),
                                                                                                          master.getSubscriber());
            c.start();

            c.addConnectivityListener(new MDSHandler.Objective.MDSListener() {
                @Override
                public void handleDataSampleEvent(MDSHandler.Objective.MDSEvent evt) {
                    MDSConnectivityObjective v = (MDSConnectivityObjective) evt.getSource();
                    if (sample.unique_device_identifier.equals(v.unique_device_identifier))
                        stopOk.countDown();
                }
            });

            c.publish(sample);

            boolean isOk = stopOk.await(5000, TimeUnit.MILLISECONDS);
            c.shutdown();
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
