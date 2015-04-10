package org.mdpnp.apps.testapp.hl7;

import ca.uhn.fhir.context.FhirContext;
import com.rti.dds.subscription.Subscriber;
import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.PatientAssessmentFx;
import org.mdpnp.apps.fxbeans.PatientAssessmentFxList;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.Set;

/**
 * @author mfeinberg
 */
public class HL7EmitterTest {

    @Test
    public void testNumericObserver() throws Exception {

        AbstractApplicationContext context =
                new ClassPathXmlApplicationContext(new String[]{"IceAppContainerContext.xml"});

        try {

            NumericFx number =new NumericFx();
            number.setPresentation_time(new Date(0));
            number.setMetric_id(HL7Emitter.METRIC_PREFIX+"TEST");

            EventLoop eventLoop = context.getBean(EventLoop.class);
            Subscriber subscriber = (Subscriber)context.getBean("himssSubscriber");

            FhirContext fhirContext = ca.uhn.fhir.context.FhirContext.forDstu2();
            NumericFxList numericList = new NumericFxList("test");
            numericList.add(number);

            HL7Emitter emitter = new HL7Emitter(subscriber, eventLoop, numericList, fhirContext);

            number.setPresentation_time(new Date());
            Set<NumericFx> updates = emitter.getRecentUpdates();

            Assert.assertNotEquals(0, updates.size());

            }
        finally {

            context.destroy();
        }
    }

    @Test
    public void testPatientAssessmentObserver() throws Exception {

        /*
        AbstractApplicationContext context =
                new ClassPathXmlApplicationContext(new String[]{"IceAppContainerContext.xml"});

        try {

            PatientAssessmentFx number =new PatientAssessmentFx();
            number.setPresentation_time(new Date(0));
            number.setMetric_id(HL7Emitter.METRIC_PREFIX+"TEST");

            EventLoop eventLoop = context.getBean(EventLoop.class);
            Subscriber subscriber = (Subscriber)context.getBean("himssSubscriber");

            FhirContext fhirContext = ca.uhn.fhir.context.FhirContext.forDstu2();
            PatientAssessmentFxList numericList = new PatientAssessmentFxList("test");
            numericList.add(number);

            HL7Emitter emitter = new HL7Emitter(subscriber, eventLoop, numericList, fhirContext);

            number.setPresentation_time(new Date());
            Set<NumericFx> updates = emitter.getRecentUpdates();

            Assert.assertNotEquals(0, updates.size());

        }
        finally {

            context.destroy();
        }
        */
    }

    // PatientAssessmentFxListFactory
}

