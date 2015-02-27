package org.mdpnp.apps.testapp;

import java.io.IOException;
import java.util.Date;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.app.Initiator;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v26.datatype.NM;
import ca.uhn.hl7v2.model.v26.group.ORU_R01_OBSERVATION;
import ca.uhn.hl7v2.model.v26.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v26.group.ORU_R01_PATIENT;
import ca.uhn.hl7v2.model.v26.message.ADT_A01;
import ca.uhn.hl7v2.model.v26.message.ORU_R01;
import ca.uhn.hl7v2.model.v26.segment.MSH;
import ca.uhn.hl7v2.model.v26.segment.OBX;
import ca.uhn.hl7v2.model.v26.segment.PID;
import ca.uhn.hl7v2.parser.Parser;

public class SendAnR01 {
    public static void main(String[] args) throws HL7Exception, IOException, LLPException {
        ORU_R01 r01 = new ORU_R01();
        // ORU is an observation
        // Event R01 is an unsolicited observation message
        // "T" for Test, "P" for Production, etc.
        r01.initQuickstart("ORU", "R01", "T");

        // Populate the MSH Segment
        MSH mshSegment = r01.getMSH();
        mshSegment.getSendingApplication().getNamespaceID().setValue("TestSendingSystem");
        mshSegment.getSequenceNumber().setValue("123");

        // Populate the PID Segment
        ORU_R01_PATIENT patient = r01.getPATIENT_RESULT().getPATIENT();
        PID pid = patient.getPID();
        pid.getPatientName(0).getFamilyName().getSurname().setValue("Doe");
        pid.getPatientName(0).getGivenName().setValue("John");
        pid.getPatientIdentifierList(0).getIDNumber().setValue("123456");

        ORU_R01_ORDER_OBSERVATION orderObservation = r01.getPATIENT_RESULT().getORDER_OBSERVATION();

        orderObservation.getOBR().getObr7_ObservationDateTime().setValueToSecond(new Date());
        
        ORU_R01_OBSERVATION observation = orderObservation.getOBSERVATION(0);
        
        
        // Populate the first OBX
        OBX obx = observation.getOBX();
        //obx.getSetIDOBX().setValue("1");
        obx.getObservationIdentifier().getIdentifier().setValue("0002-4182");
        obx.getObservationIdentifier().getText().setValue("HR");
        obx.getObservationIdentifier().getCwe3_NameOfCodingSystem().setValue("MDIL");
        obx.getObservationSubID().setValue("0");
        obx.getUnits().getIdentifier().setValue("0004-0aa0");
        obx.getUnits().getText().setValue("bpm");
        obx.getUnits().getCwe3_NameOfCodingSystem().setValue("MDIL");
        obx.getObservationResultStatus().setValue("F");

        // The first OBX has a value type of CE. So first, we populate OBX-2 with "CE"...
        obx.getValueType().setValue("NM");

        // "NM" is Numeric
        NM nm = new NM(r01);
        nm.setValue("60");

        obx.getObservationValue(0).setData(nm);

        // Now, let's encode the message and look at the output
        HapiContext context = new DefaultHapiContext();
        Parser parser = context.getPipeParser();
        String encodedMessage = parser.encode(r01);
        System.out.println("Printing ER7 Encoded Message:");
        System.out.println(encodedMessage);
        //context.close();
        /*
         * Prints:
         * 
         * MSH|^~\&|TestSendingSystem||||200701011539||ADT^A01^ADT A01||||123
         * PID|||123456||Doe^John
         */

        // Next, let's use the XML parser to encode as XML
//         parser = context.getXMLParser();
//         encodedMessage = parser.encode(r01);
//         System.out.println("Printing XML Encoded Message:");
//         System.out.println(encodedMessage);
         
         Connection conn = context.newClient("192.168.7.208", 8001, false);
         Initiator initiator = conn.getInitiator();
         Message response = initiator.sendAndReceive(r01);
         String responseString = parser.encode(response);
         System.out.println("Received response:\n" + responseString);
         conn.close();
    }
}
