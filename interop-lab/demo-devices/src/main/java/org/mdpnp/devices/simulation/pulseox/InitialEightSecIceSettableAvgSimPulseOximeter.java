/*******************************************************************************
 * Copyright (c) 2014, MD PnP Program
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 ******************************************************************************/
package org.mdpnp.devices.simulation.pulseox;

import ice.GlobalSimulationObjective;

import ice.OximetryAveragingObjective;

import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;
import org.mdpnp.devices.simulation.GlobalSimulationObjectiveListener;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.TopicUtil;
import org.mdpnp.rtiapi.data.EventLoop.ConditionHandler;

import com.rti.dds.infrastructure.Condition;
import com.rti.dds.infrastructure.RETCODE_NO_DATA;
import com.rti.dds.infrastructure.ResourceLimitsQosPolicy;
import com.rti.dds.infrastructure.StatusKind;
import com.rti.dds.infrastructure.StringSeq;
import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.InstanceStateKind;
import com.rti.dds.subscription.QueryCondition;
import com.rti.dds.subscription.ReadCondition;
import com.rti.dds.subscription.SampleInfo;
import com.rti.dds.subscription.SampleInfoSeq;
import com.rti.dds.subscription.SampleStateKind;

import com.rti.dds.subscription.Subscriber;
import com.rti.dds.subscription.ViewStateKind;
import com.rti.dds.topic.Topic;

/**
 * @author Jeff Plourde
 * 
 * This pulse oximeter is "ICE configurable" - the averaging time can be set by ICE.
 * It is initially 8 seconds, to trigger the requirement in the app for it to be changed.
 *
 */
public class InitialEightSecIceSettableAvgSimPulseOximeter extends AbstractSimulatedConnectedDevice {
	
	//Need to do something in here about setting an alarm limit...
	//AbstractSimulatedConnectedDevice.setAlarmLimit will be a starting point.
	
    private final InstanceHolder<ice.Numeric> pulse;
    private final InstanceHolder<ice.Numeric> SpO2;
    private InstanceHolder<ice.SampleArray> pleth;
    private final SimulatedPulseOximeter pulseox;
    
    private final InstanceHolder<ice.Numeric> canGetAveragingTime;
    private final InstanceHolder<ice.Numeric> averagingTime;
    private final InstanceHolder<ice.Numeric> operCanSetAveragingTime;
    private final InstanceHolder<ice.Numeric> softCanSetAveragingTime;
    
    //Are we going to need all four of these?  I don't think so.
    private ice.OximetryAveragingObjectiveDataWriter averagingTimeWriter;
    private ice.OximetryAveragingObjectiveDataReader averagingTimeReader;
    private QueryCondition averagingTimeQueryCondition;
    private Topic averagingTimeTopic;
    
    private int currentAveragingTime;


    private class SimulatedPulseOximeterExt extends SimulatedPulseOximeter {

        public SimulatedPulseOximeterExt(DeviceClock referenceClock) {
            super(referenceClock);
        }

        @Override
        protected void receivePulseOx(DeviceClock.Reading sampleTime, int heartRate, int SpO2, Number[] plethValues, int frequency) {
            numericSample(pulse, heartRate, sampleTime);
            numericSample(InitialEightSecIceSettableAvgSimPulseOximeter.this.SpO2, SpO2, sampleTime);
            //Indicate that we cannot supply the averaging time.
            //System.err.println("Doing 0 for canGetAveragingTime");
            numericSample(canGetAveragingTime, 1, sampleTime);
            //Still need to decide how to indicate an unknown or invalid value
            //for cases where it should be gettable.  Return -1 in this case,
            //remembering that when returning 0 for "canGet", this should not
            //be used anyway.
            //System.err.println("Doing 0 for averagingTime");
            System.err.println("EightSecIceSettable doing "+currentAveragingTime+" numeric sample");
            numericSample(averagingTime, currentAveragingTime, sampleTime);
            numericSample(operCanSetAveragingTime, 1, sampleTime);
            numericSample(softCanSetAveragingTime, 1, sampleTime);
            pleth = sampleArraySample(pleth, plethValues, rosetta.MDC_PULS_OXIM_PLETH.VALUE, "", 0, 
                    rosetta.MDC_DIM_DIMLESS.VALUE, frequency, sampleTime);
        }
    }

    @Override
    public boolean connect(String str) {
        pulseox.connect(executor);
        return super.connect(str);
    }

    @Override
    public void disconnect() {
        pulseox.disconnect();
        super.disconnect();
    }

    public InitialEightSecIceSettableAvgSimPulseOximeter(final Subscriber subscriber, final Publisher publisher, EventLoop eventLoop) {
        super(subscriber, publisher, eventLoop);
        
        currentAveragingTime=8;

        DeviceClock referenceClock = super.getClockProvider();
        pulseox = new SimulatedPulseOximeterExt(referenceClock);

        pulse = createNumericInstance(rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, "");
        SpO2 = createNumericInstance(rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, "");
        canGetAveragingTime = createNumericInstance(ice.SP02_SOFT_CAN_GET_AVERAGING_RATE.VALUE, "");
        operCanSetAveragingTime = createNumericInstance(ice.SP02_OPER_CAN_SET_AVERAGING_RATE.VALUE, "");
        softCanSetAveragingTime = createNumericInstance(ice.SP02_SOFT_CAN_SET_AVERAGING_RATE.VALUE, "");
        averagingTime = createNumericInstance(ice.SP02_AVERAGING_RATE.VALUE, "");

        deviceIdentity.model = "8s Initial Average Soft Settable Pulse Ox (Simulated)";
        writeDeviceIdentity();
        
        
        //ice.InfusionObjectiveTypeSupport.register_type(getParticipant(), ice.InfusionObjectiveTypeSupport.get_type_name());
        ice.OximetryAveragingObjectiveTypeSupport.register_type(getParticipant(), ice.OximetryAveragingObjectiveTypeSupport.get_type_name());
        //infusionObjectiveTopic = TopicUtil.findOrCreateTopic(getParticipant(), ice.InfusionObjectiveTopic.VALUE, ice.InfusionObjectiveTypeSupport.class);
        averagingTimeTopic = TopicUtil.findOrCreateTopic(getParticipant(), ice.OximetryAveragingObjectiveTopic.VALUE, ice.OximetryAveragingObjectiveTypeSupport.class);

//        infusionObjectiveReader = (ice.InfusionObjectiveDataReader) subscriber.create_datareader_with_profile(infusionObjectiveTopic,
//                QosProfiles.ice_library, QosProfiles.state, null, StatusKind.STATUS_MASK_NONE);
        averagingTimeReader = (ice.OximetryAveragingObjectiveDataReader) subscriber.create_datareader_with_profile(averagingTimeTopic,
        		QosProfiles.ice_library, QosProfiles.state,  null, StatusKind.STATUS_MASK_NONE);

        StringSeq params = new StringSeq();
        params.add("'" + deviceIdentity.unique_device_identifier + "'");
//        infusionObjectiveQueryCondition = infusionObjectiveReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE,
//                ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE, "unique_device_identifier = %0", params);
        averagingTimeQueryCondition = averagingTimeReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE,
        		ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE, "unique_device_identifier = %0", params);
        eventLoop.addHandler(averagingTimeQueryCondition, new ConditionHandler() {
            private ice.OximetryAveragingObjectiveSeq data_seq = new ice.OximetryAveragingObjectiveSeq();
            private SampleInfoSeq info_seq = new SampleInfoSeq();

            @Override
            public void conditionChanged(Condition condition) {

                for (;;) {
                    try {
                        averagingTimeReader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                                (ReadCondition) condition);
                        for (int i = 0; i < info_seq.size(); i++) {
                            SampleInfo si = (SampleInfo) info_seq.get(i);
                            ice.OximetryAveragingObjective data = (OximetryAveragingObjective) data_seq.get(i);
                            if (si.valid_data) {
                                setCurrentAveragingTime(data.newAverageTime);
                            }
                        }
                    } catch (RETCODE_NO_DATA noData) {
                        break;
                    } finally {
                        averagingTimeReader.return_loan(data_seq, info_seq);
                    }
                }
            }

			private void setCurrentAveragingTime(int newAverageTime) {
				System.err.println("switched current averaging time to "+newAverageTime);
				currentAveragingTime=newAverageTime;
			}

        });
        
        
        
        
        

    }

    @Override
    protected String iconResourceName() {
        return "pulseox.png";
    }

    @Override
    public void simulatedNumeric(GlobalSimulationObjective obj) {
        // Currently the super ctor registers for this callback; so pulseox might not yet be initialized
        if (obj != null && pulseox != null) {
            Number value = GlobalSimulationObjectiveListener.toIntegerNumber(obj);
            if (rosetta.MDC_PULS_RATE.VALUE.equals(obj.metric_id) ||
                rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE.equals(obj.metric_id)) {
                pulseox.setTargetHeartRate(value);
            } else if (rosetta.MDC_PULS_OXIM_SAT_O2.VALUE.equals(obj.metric_id)) {
                pulseox.setTargetSpO2(value);
            }
        }
    }

}
