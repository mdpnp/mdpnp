package org.mdpnp.devices.simulation.pump;

import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.mdpnp.rtiapi.data.QosProfiles;
import org.mdpnp.rtiapi.data.TopicUtil;
import org.mdpnp.rtiapi.data.EventLoop.ConditionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import ice.ConnectionState;
import ice.FlowRateObjectiveDataReader;
import ice.Numeric;

import javafx.beans.value.*;
import javafx.beans.property.*;

/**
 * A simulated pump for which the speed can be controlled.  Designed to be
 * used as part of the closed loop control demo application, so that that app
 * can be run without needing a real controllable pump attached.  But of course,
 * can be used for any other purpose.
 * 
 * Initial speed is 1.0, and can be controlled via an objective.  Publishes
 * numeric samples with the same metric ID as a physical pump that we are
 * using.
 * 
 * We use the scheduler to publish the metrics - using an initial delay of 5 seconds,
 * which roughly corresponds to the connection time to the physical pump that we are
 * basing the emulation from.
 */
public class SimControllablePump extends AbstractSimulatedConnectedDevice {
	
	private static int fakeComPortNumber=1;
	
	private float currentFlowRate=1.0f;
	
	private FlowRateObjectiveDataReader flowRateReader;
	private Topic flowRateTopic;
	private QueryCondition flowRateQueryCondition;
	
	private DeviceClock defaultClock;
	
	private static final Logger log = LoggerFactory.getLogger(SimControllablePump.class);
	
	ScheduledFuture<?> flowRateEmitter;
	
	public SimControllablePump(Subscriber subscriber, Publisher publisher, EventLoop eventLoop) {
		super(subscriber, publisher, eventLoop);
		
		writeDeviceIdentity();
		defaultClock=new TrivialClock();
		
		/**
		 * Following block of code is for receiving objectives for the flow rate
		 */
		ice.FlowRateObjectiveTypeSupport.register_type(getParticipant(), ice.FlowRateObjectiveTypeSupport.get_type_name());
		flowRateTopic = TopicUtil.findOrCreateTopic(getParticipant(), ice.FlowRateObjectiveTopic.VALUE, ice.FlowRateObjectiveTypeSupport.class);
		flowRateReader = (ice.FlowRateObjectiveDataReader) subscriber.create_datareader_with_profile(flowRateTopic,
        		QosProfiles.ice_library, QosProfiles.state,  null, StatusKind.STATUS_MASK_NONE);
		StringSeq params = new StringSeq();
        params.add("'" + deviceIdentity.unique_device_identifier + "'");
        flowRateQueryCondition = flowRateReader.create_querycondition(SampleStateKind.NOT_READ_SAMPLE_STATE,
        		ViewStateKind.ANY_VIEW_STATE, InstanceStateKind.ALIVE_INSTANCE_STATE, "unique_device_identifier = %0", params);
        eventLoop.addHandler(flowRateQueryCondition, new ConditionHandler() {
            private ice.FlowRateObjectiveSeq data_seq = new ice.FlowRateObjectiveSeq();
            private SampleInfoSeq info_seq = new SampleInfoSeq();

            @Override
            public void conditionChanged(Condition condition) {

                for (;;) {
                    try {
                        flowRateReader.read_w_condition(data_seq, info_seq, ResourceLimitsQosPolicy.LENGTH_UNLIMITED,
                                (ReadCondition) condition);
                        for (int i = 0; i < info_seq.size(); i++) {
                            SampleInfo si = (SampleInfo) info_seq.get(i);
                            ice.FlowRateObjective data = (ice.FlowRateObjective) data_seq.get(i);
                            if (si.valid_data) {
                            	try { 
                            		setSpeed(data.newFlowRate);
                            	} catch (IOException ioe) {
                            		log.error("Failed to set pump speed", ioe);
                            		ioe.printStackTrace();
                            	}
                            }
                        }
                    } catch (RETCODE_NO_DATA noData) {
                        break;
                    } finally {
                        flowRateReader.return_loan(data_seq, info_seq);
                    }
                }
            }
        });
        
	}
	
	/**
	 * Later on, we should introduce a way of controlling throwing IOException,
	 * so we can test the way a calling application handles that.
	 * @param newSpeed
	 * @throws IOException
	 */
	private void setSpeed(float newSpeed) throws IOException {
		try {
			Thread.sleep(5000);
			currentFlowRate=newSpeed;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		//Take 5 seconds
		
	}
	
	@Override
	protected void writeDeviceIdentity() {
		deviceIdentity.model="Controllable Pump";
		super.writeDeviceIdentity();
	}
	
	@Override
	public boolean connect(String str) {
		System.err.println("SCP Connect called");
		//Completely fake com port property that is useful for distinguishing instances
		deviceConnectivity.comPort="com"+String.valueOf(fakeComPortNumber++);
		doBasicStuff();
		return super.connect(str);
	}

	@Override
	public void disconnect() {
		if(flowRateEmitter!=null) {
			flowRateEmitter.cancel(true);
		}
		super.disconnect();
		
	}

	private void doBasicStuff() {
		stateMachine.transitionIfLegal(ConnectionState.Connected, "Connected");
		final InstanceHolder<Numeric> flowRateHolder=createNumericInstance(rosetta.MDC_FLOW_FLUID_PUMP.VALUE, "");
		System.err.println("executor is "+executor.getClass().getName());
		//We have access to "executor" - a scheduled executor service	 
		flowRateEmitter=executor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				numericSample(flowRateHolder, currentFlowRate , defaultClock.instant());
			}
		}, 5, 1, TimeUnit.SECONDS);
		
	}
	
	private class TrivialClock implements DeviceClock {

		@Override
		public Reading instant() {
			return new Reading() {
				
				@Override
				public Reading refineResolutionForFrequency(int hertz, int size) {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public boolean hasDeviceTime() {
					// TODO Auto-generated method stub
					return true;
				}
				
				@Override
				public Instant getTime() {
					// TODO Auto-generated method stub
					Instant i=Instant.ofEpochMilli(System.currentTimeMillis());
					//System.out.println("MSeriesScaleClock returning "+i.toString());
					return i;
				}
				
				@Override
				public Instant getDeviceTime() {
					Instant i=Instant.ofEpochMilli(System.currentTimeMillis());
					return i;
				}
			};
		}
		
	}
	
    @Override
    protected String iconResourceName() {
        return "controllablepump.png";
    }

}
