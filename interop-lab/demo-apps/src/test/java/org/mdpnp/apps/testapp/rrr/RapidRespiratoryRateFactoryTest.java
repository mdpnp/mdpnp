package org.mdpnp.apps.testapp.rrr;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class RapidRespiratoryRateFactoryTest {

    private static final Logger log = LoggerFactory.getLogger(RapidRespiratoryRateFactoryTest.class);

    @Test
    public void testDeviceSetupViaSpring() throws Exception {
        // TODO Jeff Plourde apologizes for this and needs to update these tests.
//        RtConfig.loadAndSetIceQos();
//
//        final AbstractApplicationContext context =
//                new ClassPathXmlApplicationContext(new String[]{"IceAppContainerContext.xml"});
//
//        RtConfig rtConfig = (RtConfig)context.getBean("rtConfig");
//
//        SimMultiparameter device = new SimMultiparameter(rtConfig.getDomainId(), rtConfig.getEventLoop());
//        device.connect(null);
//
//        RapidRespiratoryRateFactory rrrf = new RapidRespiratoryRateFactory();
//        IceApplicationProvider.IceApp app = rrrf.create(context);
//
//        InstanceModel capnoModel =  (InstanceModel)  context.getBean("capnoModel");
//
//        int nDev = 0;
//        for(int i=0; i<10; i++) {
//            Thread.sleep(2000);
//            nDev = capnoModel.getSize();
//            if(nDev!=0)
//                break;
//            log.info("Wait for slow hardware to update...." + i);
//        }
//
//        device.disconnect();
//        context.destroy();
//
//        Assert.assertEquals("CapnoModel did not locate the device", 1, nDev);
    }

}
