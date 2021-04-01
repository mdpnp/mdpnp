package org.mdpnp.devices.testdevice;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;
import org.mdpnp.devices.simulation.pump.SimControllablePump;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.QueryCondition;
import com.rti.dds.subscription.Subscriber;
import com.rti.dds.topic.Topic;

import ice.ConnectionState;
import ice.FlowRateObjectiveDataReader;
import ice.Numeric;
import ice.NumericSQI;

public class TestDevice extends AbstractSimulatedConnectedDevice {
	
	private static int fakeComPortNumber=1;
	
	private float currentFlowRate=1.0f;
	private NumericSQI currentSQI = new NumericSQI();
	
	private FlowRateObjectiveDataReader flowRateReader;
	private Topic flowRateTopic;
	private QueryCondition flowRateQueryCondition;
	
	private DeviceClock defaultClock;
	
	private static final Logger log = LoggerFactory.getLogger(SimControllablePump.class);
	
	ScheduledFuture<?> flowRateEmitter;

	public TestDevice(Subscriber subscriber, Publisher publisher, EventLoop eventLoop) {
		super(subscriber, publisher, eventLoop);
		writeDeviceIdentity();
		defaultClock= new DeviceClock.WallClock();
	}
	
	@Override
	protected void writeDeviceIdentity() {
		deviceIdentity.model = "Test Device";
		super.writeDeviceIdentity();
	}

	@Override
	public boolean connect(String str) {
		publishMetrics();
		return super.connect(str);
	}
	
	@Override
	public void disconnect() {
		if(flowRateEmitter!=null) {
			flowRateEmitter.cancel(true);
		}
		super.disconnect();
		
	}
	
	@Override
	protected String iconResourceName() {
		return "testdevice.png";
	}

	private void publishMetrics() {
		stateMachine.transitionIfLegal(ConnectionState.Connected, "Connected");
		final InstanceHolder<Numeric> flowRateHolder=createNumericInstance(rosetta.MDC_FLOW_FLUID_PUMP.VALUE, "");
		System.err.println("executor is "+executor.getClass().getName());
		//We have access to "executor" - a scheduled executor service	 
		flowRateEmitter=executor.scheduleAtFixedRate(new Runnable() {
			public void run() {
				numericSample(flowRateHolder, currentFlowRate, currentSQI, defaultClock.instant());
			}
		}, 5, 1, TimeUnit.SECONDS);
	}
}
