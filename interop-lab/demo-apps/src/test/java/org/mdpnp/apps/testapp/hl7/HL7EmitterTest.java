package org.mdpnp.apps.testapp.hl7;

import java.util.Date;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mdpnp.apps.fxbeans.NumericFx;
import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.fxbeans.NumericFxListFactory;
import org.mdpnp.apps.fxbeans.PatientAssessmentFx;
import org.mdpnp.apps.testapp.validate.Validation;
import org.mdpnp.apps.testapp.validate.ValidationOracle;
import org.mdpnp.rtiapi.data.EventLoop;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ca.uhn.fhir.context.FhirContext;

import com.rti.dds.subscription.Subscriber;

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
            Subscriber subscriber = (Subscriber)context.getBean("subscriber");

            FhirContext fhirContext = ca.uhn.fhir.context.FhirContext.forDstu2();
            ValidationOracle validationOracle = new ValidationOracle();
            validationOracle.add(new Validation(number));

            HL7Emitter emitter = new HL7Emitter(subscriber, eventLoop, validationOracle, fhirContext);

            number.setPresentation_time(new Date());
            Set<Validation> updates = emitter.getRecentUpdates();

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
            Subscriber subscriber = (Subscriber)context.getBean("subscriber");

            FhirContext fhirContext = ca.uhn.fhir.context.FhirContext.forDstu2();

            HL7Emitter emitter = new HL7Emitter(subscriber, eventLoop, null, fhirContext);

            assessment.setDate_and_time(new Date());
            Set<Validation> updates = emitter.getRecentUpdates();

//            Assert.assertNotEquals(0, updates.size());

            //Set<Observation> o = emitter.fhirObservation(assessment);
            //Assert.assertNotNull(o);
        }
        finally {

            context.destroy();
        }
    }

    @Test
    public void testNumericFxListFactory() throws Exception {

        AbstractApplicationContext context =
                new ClassPathXmlApplicationContext(new String[]{"IceAppContainerContext.xml"});

        try {
            NumericFxListFactory f = new NumericFxListFactory();
            f.setTopicName(ice.NumericTopic.VALUE);

            f.setEventLoop(context.getBean(EventLoop.class));
            f.setSubscriber((Subscriber) context.getBean("subscriber"));
            f.setQosLibrary("ice_library");
            f.setQosProfile("numeric_data");

            NumericFxList  l = f.getObject();

            Assert.assertNotNull(l);
        }
        finally {

            context.destroy();
        }
    }
}

