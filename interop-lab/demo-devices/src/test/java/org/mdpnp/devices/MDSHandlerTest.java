package org.mdpnp.devices;

import ice.MDSConnectivity;
import ice.MDSConnectivityObjective;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.rti.dds.domain.DomainParticipant;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

public class MDSHandlerTest {

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
    public void testMDSLifecycle() throws Exception
    {
        ConfigurableApplicationContext ctx = createContext();
        
        DomainParticipant participant = ctx.getBean("domainParticipant", DomainParticipant.class);
        EventLoop eventLoop   = ctx.getBean("eventLoop", EventLoop.class);

        MDSHandler handler = new MDSHandler(eventLoop, participant);

        final MDSConnectivity sample = new MDSConnectivity();
        sample.partition="p1";
        sample.unique_device_identifier=Long.toBinaryString(System.currentTimeMillis());

        try {
            final CountDownLatch stopOk = new CountDownLatch(1);

            handler.start();

            handler.addConnectivityListener(new MDSHandler.Connectivity.MDSListener() {
                @Override
                public void handleConnectivityChange(MDSHandler.Connectivity.MDSEvent evt) {
                    MDSConnectivity v = (MDSConnectivity)evt.getSource();
                    if(sample.unique_device_identifier.equals(v.unique_device_identifier))
                        stopOk.countDown();
                }
            });

            handler.publish(sample);

            boolean isOk = stopOk.await(5000, TimeUnit.MILLISECONDS);
            handler.shutdown();
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
    public void testMDSConnectivity() throws Exception
    {
        ConfigurableApplicationContext ctx = createContext();
        
        Subscriber subscriber = ctx.getBean("subscriber", Subscriber.class);
        Publisher  publisher  = ctx.getBean("publisher", Publisher.class);
        EventLoop eventLoop   = ctx.getBean("eventLoop", EventLoop.class);
        
        final MDSConnectivity sample = new MDSConnectivity();
        sample.partition="p1";
        sample.unique_device_identifier=Long.toBinaryString(System.currentTimeMillis());

        try {
            final CountDownLatch stopOk = new CountDownLatch(1);


            MDSHandler.Connectivity c = new MDSHandler.Connectivity(eventLoop,
                                                                    publisher,
                                                                    subscriber);
            c.start();

            c.addConnectivityListener(new MDSHandler.Connectivity.MDSListener() {
                @Override
                public void handleConnectivityChange(MDSHandler.Connectivity.MDSEvent evt) {
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
            ctx.close();
        }
    }

    @Test
    public void testMDSConnectivityObjective() throws Exception
    {
        ConfigurableApplicationContext ctx = createContext();
        
        Subscriber subscriber = ctx.getBean("subscriber", Subscriber.class);
        Publisher  publisher  = ctx.getBean("publisher", Publisher.class);
        EventLoop eventLoop   = ctx.getBean("eventLoop", EventLoop.class);

        final MDSConnectivityObjective sample = new MDSConnectivityObjective();
        sample.partition="p1";
        sample.unique_device_identifier=Long.toBinaryString(System.currentTimeMillis());

        try {
            final CountDownLatch stopOk = new CountDownLatch(1);


            MDSHandler.Objective c = new MDSHandler.Objective(eventLoop,
                                                              publisher,
                                                              subscriber);
            c.start();

            c.addConnectivityListener(new MDSHandler.Objective.MDSListener() {
                @Override
                public void handleConnectivityObjective(MDSHandler.Objective.MDSEvent evt) {
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
            ctx.close();
        }
    }
}
