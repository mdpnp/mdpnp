package org.mdpnp.apps.testapp.hl7;

import java.io.IOException;
import java.util.Date;

import ice.Numeric;
import ice.NumericDataReader;

import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.InstanceModel;
import org.mdpnp.rtiapi.data.InstanceModelListener;
import org.mdpnp.rtiapi.data.ListenerList;
import org.mdpnp.rtiapi.data.NumericInstanceModel;
import org.mdpnp.rtiapi.data.NumericInstanceModelImpl;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.Subscriber;

public class HL7Emitter implements InstanceModelListener<Numeric, NumericDataReader> {
    
    protected static final Logger log = LoggerFactory.getLogger(HL7Emitter.class);
    
    private final EventLoop eventLoop;
    private final Subscriber subscriber;
    private final HapiContext context;
    
    private Connection hapiConnection;
    
    private final ListenerList<LineEmitterListener> listeners = new ListenerList<LineEmitterListener>(LineEmitterListener.class);
    private final ListenerList<StartStopListener> ssListeners = new ListenerList<StartStopListener>(StartStopListener.class);
    
    public HL7Emitter(final Subscriber subscriber, final EventLoop eventLoop) {
        this.subscriber = subscriber;
        this.eventLoop = eventLoop;
        context = new DefaultHapiContext();
        numericInstanceModel = new NumericInstanceModelImpl(ice.NumericTopic.VALUE);
//        numericInstanceTableModel = new NumericInstanceTableModel(numericInstanceModel);
    }
    
    private final NumericInstanceModel numericInstanceModel;
//    private final NumericInstanceTableModel numericInstanceTableModel;
    
    public void start(String host, int port) {
        numericInstanceModel.addListener(this);
        log.debug("Starting NumericInstanceModel");
        numericInstanceModel.start(subscriber, eventLoop, QosProfiles.ice_library, QosProfiles.numeric_data);
        log.debug("Started NumericInstanceModel");
        if(host != null && !host.isEmpty()) {
            
            try {
                
                hapiConnection = context.newClient(host, port, false);
                ssListeners.fire(started);
                
            } catch (HL7Exception e) {
                log.error("", e);
                stop();
            } catch(RuntimeException re) {
                log.error("", re);
                stop();
            }
        } else {
            // We'll make it ok to start with no external connection
            // just to demo the ability to compose HL7 messages
            ssListeners.fire(started);
        }
    }
    public void stop() {
        log.debug("Stopping NumericInstanceModel");
        numericInstanceModel.stop();
        ssListeners.fire(stopped);
        log.debug("Stopped NumericInstanceModel");
        if(hapiConnection != null) {
            hapiConnection.close();
            hapiConnection = null;
        }
    }
    
    static class DispatchStartStop implements ListenerList.Dispatcher<StartStopListener> {
        final boolean started;
        DispatchStartStop(final boolean started) {
            this.started = started;
        }
        
        @Override
        public void dispatch(StartStopListener l) {
            if(started) {
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
    
    public NumericInstanceModel getNumericInstanceModel() {
        return numericInstanceModel;
    }
    
//    public NumericInstanceTableModel getNumericInstanceTableModel() {
//        return numericInstanceTableModel;
//    }
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
    @Override
    public void instanceAlive(InstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric data, SampleInfo sampleInfo) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void instanceNotAlive(InstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric keyHolder, SampleInfo sampleInfo) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void instanceSample(InstanceModel<Numeric, NumericDataReader> model, NumericDataReader reader, Numeric data, SampleInfo sampleInfo) {
        if(rosetta.MDC_ECG_HEART_RATE.VALUE.equals(data.metric_id)) {
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
                nm.setValue(Float.toString(data.value));
        
                obx.getObservationValue(0).setData(nm);
        
                Parser parser = context.getPipeParser();

                String encodedMessage = parser.encode(r01);
                listeners.fire(new DispatchLine(encodedMessage));
                
                
                // Now, let's encode the message and look at the output
                Connection hapiConnection = this.hapiConnection;
                if(null != hapiConnection) {

        
                    Initiator initiator = hapiConnection.getInitiator();
                    Message response = initiator.sendAndReceive(r01);
                    String responseString = parser.encode(response);
                    log.debug("Received Response:"+responseString);
                    
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
    }
}
