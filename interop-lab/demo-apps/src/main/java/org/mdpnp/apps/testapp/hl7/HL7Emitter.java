package org.mdpnp.apps.testapp.hl7;

import ice.MDSConnectivity;
import ice.Numeric;
import ice.NumericDataReader;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import org.mdpnp.apps.fxbeans.NumericFxList;
import org.mdpnp.apps.testapp.DeviceListModel;
import org.mdpnp.devices.MDSHandler;
import org.mdpnp.devices.MDSHandler.Connectivity.MDSEvent;
import org.mdpnp.devices.MDSHandler.Connectivity.MDSListener;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.ListenerList;
import org.mdpnp.rtiapi.data.NumericInstanceModel;
import org.mdpnp.rtiapi.data.NumericInstanceModelListener;
import org.mdpnp.rtiapi.data.ReaderInstanceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Device;
import ca.uhn.fhir.model.dstu2.resource.DeviceMetric;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.model.dstu2.valueset.ObservationStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.client.IGenericClient;
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
import ca.uhn.hl7v2.model.v26.message.ORU_R01;
import ca.uhn.hl7v2.model.v26.segment.MSH;
import ca.uhn.hl7v2.model.v26.segment.OBX;
import ca.uhn.hl7v2.model.v26.segment.PID;
import ca.uhn.hl7v2.parser.Parser;

import com.rti.dds.infrastructure.SequenceNumber_t;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.Subscriber;

public class HL7Emitter implements MDSListener {
    public enum Type {
        FHIR_DSTU2, V26,
    }

    // TODO externalize or add UI
    private static final String[] metricIdsForExportArray = new String[] {
        rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE,
        rosetta.MDC_ECG_HEART_RATE.VALUE,
        rosetta.MDC_PULS_OXIM_SAT_O2.VALUE,
        rosetta.MDC_PRESS_BLD_SYS.VALUE,
        rosetta.MDC_PRESS_BLD_DIA.VALUE,
        rosetta.MDC_PRESS_BLD_MEAN.VALUE,
        rosetta.MDC_PRESS_BLD_NONINV_SYS.VALUE,
        rosetta.MDC_PRESS_BLD_NONINV_DIA.VALUE,
        rosetta.MDC_PRESS_BLD_NONINV_MEAN.VALUE,
        rosetta.MDC_CO2_RESP_RATE.VALUE,
        rosetta.MDC_RESP_RATE.VALUE
    };
    private static final Set<String> metricIdsForExport = new HashSet<String>(Arrays.asList(metricIdsForExportArray));
    
    protected static final Logger log = LoggerFactory.getLogger(HL7Emitter.class);
    
    private final HapiContext hl7Context;
    protected final FhirContext fhirContext;
    protected final MDSHandler mdsHandler;

    protected Connection hl7Connection;
    protected IGenericClient fhirClient;
    
    private final Map<String, String> deviceUdiToPatientMRN = Collections.synchronizedMap(new HashMap<String, String>());
    private final Map<String, IdDt> patientMRNtoResourceId = new HashMap<String, IdDt>();

    private final ListenerList<LineEmitterListener> listeners = new ListenerList<LineEmitterListener>(LineEmitterListener.class);
    private final ListenerList<StartStopListener> ssListeners = new ListenerList<StartStopListener>(StartStopListener.class);

    public HL7Emitter(final Subscriber subscriber, final EventLoop eventLoop, final NumericFxList numericList,
            final FhirContext fhirContext) {
        hl7Context = new DefaultHapiContext();
        this.fhirContext = fhirContext;
        this.numericList = numericList;
        this.mdsHandler = new MDSHandler(eventLoop, subscriber.get_participant());
        mdsHandler.addConnectivityListener(this);
        mdsHandler.start();

    }

    private final NumericFxList numericList;
    private Type type;

    public void start(final String host, final int port, final Type type) {
        this.type = type;
        // TODO this again
//        numericInstanceModel.iterateAndAddListener(numericListener);
        
        log.debug("Started NumericInstanceModel");
        if (host != null && !host.isEmpty()) {
            if (Type.V26.equals(type)) {
                try {

                    hl7Connection = hl7Context.newClient(host, port, false);
                    ssListeners.fire(started);

                } catch (HL7Exception e) {
                    log.error("", e);
                    stop();
                } catch (RuntimeException re) {
                    log.error("", re);
                    stop();
                }
            } else if (Type.FHIR_DSTU2.equals(type)) {
                fhirClient = fhirContext.newRestfulGenericClient(host);
                ssListeners.fire(started);
            }
        } else {
            // We'll make it ok to start with no external connection
            // just to demo the ability to compose HL7 messages
            ssListeners.fire(started);
        }
    }

    public void stop() {
        // TODO this again
//        numericInstanceModel.removeListener(numericListener);
        ssListeners.fire(stopped);
        if (hl7Connection != null) {
            hl7Connection.close();
            hl7Connection = null;
        }
        if (fhirClient != null) {
            // TODO is there an active connection to disconnect?
            fhirClient = null;
        }
    }
    
    public void shutdown() {
        mdsHandler.shutdown();
    }

    static class DispatchStartStop implements ListenerList.Dispatcher<StartStopListener> {
        final boolean started;

        DispatchStartStop(final boolean started) {
            this.started = started;
        }

        @Override
        public void dispatch(StartStopListener l) {
            if (started) {
                l.started();
            } else {
                l.stopped();
            }
        }

    }

    private final static DispatchStartStop started = new DispatchStartStop(true);
    private final static DispatchStartStop stopped = new DispatchStartStop(false);

    static class DispatchLine implements ListenerList.Dispatcher<LineEmitterListener> {
        private final String line;

        public DispatchLine(final String line) {
            this.line = line;
        }

        @Override
        public void dispatch(LineEmitterListener l) {
            l.newLine(line);
        }

    }

    public NumericFxList getNumericList() {
        return numericList;
    }

    public void addLineEmitterListener(LineEmitterListener listener) {
        listeners.addListener(listener);
    }

    public void removeLineEmitterListener(LineEmitterListener listener) {
        listeners.removeListener(listener);
    }

    public void addStartStopListener(StartStopListener listener) {
        ssListeners.addListener(listener);
    }

    public void removeStartStopListener(StartStopListener listener) {
        ssListeners.removeListener(listener);
    }

    protected void sendHL7v26(Numeric data, SampleInfo sampleInfo) {
        try {

            ORU_R01 r01 = new ORU_R01();
            // ORU is an observation
            // Event R01 is an unsolicited observation message
            // "T" for Test, "P" for Production, etc.
            r01.initQuickstart("ORU", "R01", "T");

            // Populate the MSH Segment
            MSH mshSegment = r01.getMSH();
            mshSegment.getSendingApplication().getNamespaceID().setValue("ICE");
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
            // obx.getSetIDOBX().setValue("1");
            obx.getObservationIdentifier().getIdentifier().setValue("0002-4182");
            obx.getObservationIdentifier().getText().setValue("HR");
            obx.getObservationIdentifier().getCwe3_NameOfCodingSystem().setValue("MDIL");
            obx.getObservationSubID().setValue("0");
            obx.getUnits().getIdentifier().setValue("0004-0aa0");
            obx.getUnits().getText().setValue("bpm");
            obx.getUnits().getCwe3_NameOfCodingSystem().setValue("MDIL");
            obx.getObservationResultStatus().setValue("F");

            // The first OBX has a value type of CE. So first, we populate OBX-2
            // with "CE"...
            obx.getValueType().setValue("NM");

            // "NM" is Numeric
            NM nm = new NM(r01);
            nm.setValue(Float.toString(data.value));

            obx.getObservationValue(0).setData(nm);

            Parser parser = hl7Context.getPipeParser();

            String encodedMessage = parser.encode(r01);
            listeners.fire(new DispatchLine(encodedMessage));

            // Now, let's encode the message and look at the output
            Connection hapiConnection = HL7Emitter.this.hl7Connection;
            if (null != hapiConnection) {

                Initiator initiator = hapiConnection.getInitiator();
                Message response = initiator.sendAndReceive(r01);
                String responseString = parser.encode(response);
                log.debug("Received Response:" + responseString);

            }
        } catch (DataTypeException e) {
            log.error("", e);
        } catch (HL7Exception e) {
            log.error("", e);
        } catch (IOException e) {
            log.error("", e);
        } catch (LLPException e) {
            log.error("", e);
        } finally {

        }
    }
    private static final String PTID_SYSTEM = "urn:oid:2.16.840.1.113883.3.1974";
    public void sendFHIR(Numeric data, SampleInfo sampleInfo) {
        // Device device = new Device();

        final String mrn = deviceUdiToPatientMRN.get(data.unique_device_identifier);
        if(null == mrn) {
            log.debug("No known mrn for udi="+data.unique_device_identifier);
            return;
        }
        IdDt resourceId = patientMRNtoResourceId.get(mrn);
        if(null == resourceId) {
            ca.uhn.fhir.model.api.Bundle bundle = fhirClient
                    .search()
                    .forResource(Patient.class)
                    .where(Patient.IDENTIFIER.exactly().systemAndIdentifier(PTID_SYSTEM, mrn))
                    .execute();
            List<Patient> patients = bundle.getResources(Patient.class);
            if(patients.isEmpty()) {
                log.warn("No patient in remote system with MRN="+mrn);
                return;
            } else {
                if(patients.size()>1) {
                    log.warn("Duplicate resource ids for mrn="+mrn+" using first");
                }
                resourceId = patients.get(0).getId();
                patientMRNtoResourceId.put(mrn, resourceId);
            }
            
        }
        

        // TODO Needs to exist and be referred to
         Device device = new Device();
         
        // TODO Probably needs to exist and be referred to
         DeviceMetric deviceMetric = new DeviceMetric();
         
        Observation obs = new Observation();
        obs.setValue(new QuantityDt(data.value).setUnits(data.unit_id).setCode(data.metric_id).setSystem("OpenICE"));
        obs.addIdentifier().setSystem("urn:info.openice").setValue(uuidFromSequence(sampleInfo.publication_sequence_number).toString());
        Date presentation = new Date(data.presentation_time.sec * 1000L + data.presentation_time.nanosec / 1000000L);
        obs.setApplies(new DateTimeDt(presentation, TemporalPrecisionEnum.SECOND, TimeZone.getTimeZone("UTC")));
        obs.setSubject(new ResourceReferenceDt(resourceId));
        obs.setStatus(ObservationStatusEnum.PRELIMINARY);

        IGenericClient client = fhirClient;
        if (null != client) {
            MethodOutcome outcome = client.create().resource(obs).execute();
        }

        // String xmlEncoded =
        // ctx.newXmlParser().encodeResourceToString(patient);
        String jsonEncoded = fhirContext.newJsonParser().encodeResourceToString(obs);

        listeners.fire(new DispatchLine(jsonEncoded + "\n"));

    }

    final NumericInstanceModelListener numericListener = new NumericInstanceModelListener() {

        @Override
        public void instanceSample(ReaderInstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric data, SampleInfo sampleInfo) {
            if(metricIdsForExport.contains(data.metric_id)) {
                if (Type.V26.equals(type)) {
                    sendHL7v26(data, sampleInfo);
                } else if (Type.FHIR_DSTU2.equals(type)) {
                    sendFHIR(data, sampleInfo);
                }
            }
        }

        @Override
        public void instanceNotAlive(ReaderInstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric keyHolder,
                SampleInfo sampleInfo) {
        }

        @Override
        public void instanceAlive(ReaderInstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric data, SampleInfo sampleInfo) {
        }
    };


    protected static UUID uuidFromSequence(SequenceNumber_t seq) {
        return new UUID(seq.high, seq.low);
    }

    public static void main(String[] args) {
        System.out.println(uuidFromSequence(new SequenceNumber_t(1, 1L)));
    }

    
    
    @Override
    public void handleDataSampleEvent(MDSEvent evt) {
        ice.MDSConnectivity c = (MDSConnectivity) evt.getSource();
        log.info("udi " + c.unique_device_identifier + " is " + c.partition);
        if(c.partition.startsWith("MRN=")) {
            deviceUdiToPatientMRN.put(c.unique_device_identifier, c.partition.substring(4, c.partition.length()));
        }
    }
}
