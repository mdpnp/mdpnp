package org.mdpnp.apps.testapp.hl7;

import ca.uhn.fhir.context.FhirContext;
import com.rti.dds.subscription.Subscriber;
import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.apps.fxbeans.*;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;
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

            HL7Emitter emitter = new HL7Emitter(subscriber, eventLoop, numericList, null, fhirContext);

            number.setPresentation_time(new Date());
            Set<NumericFx> updates = emitter.getRecentUpdates();

            Assert.assertNotEquals(0, updates.size());

            //Observation o = emitter.fhirObservation(number);
            //Assert.assertNotNull(o);

        }
        finally {

            context.destroy();
        }
    }

    @Test
    public void testPatientAssessmentObserver() throws Exception {

        AbstractApplicationContext context =
                new ClassPathXmlApplicationContext(new String[]{"IceAppContainerContext.xml"});

        try {

            PatientAssessmentFx assessment =new PatientAssessmentFx();
            assessment.setOperator_id("THIS IS A TEST");
            assessment.setDate_and_time(new Date(0));

            EventLoop eventLoop = context.getBean(EventLoop.class);
            Subscriber subscriber = (Subscriber)context.getBean("himssSubscriber");

            FhirContext fhirContext = ca.uhn.fhir.context.FhirContext.forDstu2();
            PatientAssessmentFxList assessmentList = new PatientAssessmentFxList("test");
            assessmentList.add(assessment);

            HL7Emitter emitter = new HL7Emitter(subscriber, eventLoop, null, assessmentList, fhirContext);

            assessment.setDate_and_time(new Date());
            Set<NumericFx> updates = emitter.getRecentUpdates();

            Assert.assertNotEquals(0, updates.size());

            //Set<Observation> o = emitter.fhirObservation(assessment);
            //Assert.assertNotNull(o);
        }
        finally {

            context.destroy();
        }
    }

    @Test
    public void testPatientAssessmentFxListFactory() throws Exception {

        AbstractApplicationContext context =
                new ClassPathXmlApplicationContext(new String[]{"IceAppContainerContext.xml"});

        try {
            PatientAssessmentFxListFactory f = new PatientAssessmentFxListFactory();
            f.setTopicName(himss.PatientAssessmentTopic.VALUE);

            f.setEventLoop(context.getBean(EventLoop.class));
            f.setSubscriber((Subscriber) context.getBean("himssSubscriber"));
            f.setQosLibrary("ice_library");
            f.setQosProfile("himss");

            PatientAssessmentFxList  l = f.getObject();

            Assert.assertNotNull(l);
        }
        finally {

            context.destroy();
        }
    }
}

