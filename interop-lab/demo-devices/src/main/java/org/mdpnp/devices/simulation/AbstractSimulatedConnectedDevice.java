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
package org.mdpnp.devices.simulation;

import ice.AlarmLimit;
import ice.GlobalAlarmLimitObjective;
import ice.GlobalSimulationObjective;
import ice.LocalAlarmLimitObjective;
import ice.Numeric;
import ice.NumericSQI;

import java.util.HashMap;
import java.util.Map;

import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.connected.AbstractConnectedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.publication.Publisher;
import com.rti.dds.subscription.Subscriber;

public abstract class AbstractSimulatedConnectedDevice extends AbstractConnectedDevice implements GlobalSimulationObjectiveListener {
	private static final double DEFAULT_JITTER_CEILING = 100.0;
	private static final double DEFAULT_JITTER_FLOOR = 90.0;
	private static final double DEFAULT_JITTER_STEP_AMT = 0.25;
	private static final double DEFAULT_JITTER_START = 95.0;
	
	// Used to simulate accuracy SQI across all simulated connected devices
	protected NumberWithJitter<Float> accuracyJitter = new NumberWithJitter<Float>(DEFAULT_JITTER_START, DEFAULT_JITTER_STEP_AMT,
			DEFAULT_JITTER_FLOOR, DEFAULT_JITTER_CEILING);
	
	private NumericSQI defaultSQI = new NumericSQI();
	
    protected Throwable t;

    protected final GlobalSimulationObjectiveMonitor monitor;
    
    private static final Logger log = LoggerFactory.getLogger(AbstractSimulatedConnectedDevice.class);

    public AbstractSimulatedConnectedDevice(final Subscriber subscriber, final Publisher publisher, EventLoop eventLoop) {
        super(subscriber, publisher, eventLoop);
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        writeDeviceIdentity();

        monitor = new GlobalSimulationObjectiveMonitor(this);
        
    }

    public Throwable getLastError() {
        return t;
    }

    @Override
    public boolean connect(String str) {
        monitor.register(subscriber, eventLoop);
        ice.ConnectionState state = getState();
        if (ice.ConnectionState.Connected.equals(state) || ice.ConnectionState.Connecting.equals(state)
                || ice.ConnectionState.Negotiating.equals(state)) {
        } else {
            if (!stateMachine.transitionWhenLegal(ice.ConnectionState.Connecting, 1000L, "connect requested to "+str)) {
                throw new RuntimeException("Unable to enter Connecting State");
            }
            if (!stateMachine.transitionWhenLegal(ice.ConnectionState.Negotiating, 1000L, "connect requested")) {
                throw new RuntimeException("Unable to enter Negotiating State");
            }
            if (!stateMachine.transitionWhenLegal(ice.ConnectionState.Connected, 1000L, "connect requested")) {
                throw new RuntimeException("Unable to enter Connected State");
            }
        }
        return true;
    }

    @Override
    public void disconnect() {
        monitor.unregister();
        if(!ice.ConnectionState.Terminal.equals(getState())) {
            if (!stateMachine.transitionWhenLegal(ice.ConnectionState.Terminal, 2000L, "disconnect requested")) {
                throw new RuntimeException("Unable to enter Terminal State");
            }
        }
    }
    
    @Override
    protected ice.ConnectionType getConnectionType() {
        return ice.ConnectionType.Simulated;
    }

    public String getConnectionInfo() {
        return null;
    }

    @Override
    public void simulatedNumeric(GlobalSimulationObjective obj) {
        // TODO remove this default implementation to check that inheritors are
        // properly implementing this
    }
    
    private Map<String, InstanceHolder<ice.LocalAlarmLimitObjective>> localAlarmLimit = new HashMap<String, InstanceHolder<ice.LocalAlarmLimitObjective>>();
    private Map<String, InstanceHolder<ice.AlarmLimit>> alarmLimit = new HashMap<String, InstanceHolder<ice.AlarmLimit>>();
       
    @Override
    protected void unregisterAlarmLimitObjectiveInstance(InstanceHolder<LocalAlarmLimitObjective> holder) {
        localAlarmLimit.clear();
        super.unregisterAlarmLimitObjectiveInstance(holder);
    }
    
    @Override
    protected void unregisterAlarmLimitInstance(InstanceHolder<AlarmLimit> holder) {
        alarmLimit.clear();
        super.unregisterAlarmLimitInstance(holder);
    }
    
    private static final String alarmLimitKey(final ice.GlobalAlarmLimitObjective alarmLimit) {
        return alarmLimit.metric_id + "-" + alarmLimit.limit_type;
    }
    
    private static final String alarmLimitKey(final String metric_id, final ice.LimitType limitType) {
        return metric_id + "-" + limitType;
    }    
    
    @Override
    public void setAlarmLimit(GlobalAlarmLimitObjective obj) {
        super.setAlarmLimit(obj);
        localAlarmLimit.put(obj.metric_id+"_"+obj.limit_type,
                alarmLimitObjectiveSample(localAlarmLimit.get(obj.metric_id), obj.value, obj.unit_identifier, obj.metric_id, obj.limit_type));
        alarmLimit.put(alarmLimitKey(obj),
                alarmLimitSample(alarmLimit.get(obj.metric_id), obj.unit_identifier, obj.value, obj.metric_id, obj.limit_type));
        // TODO really should also check alarm violation on threshold change here but it will be tricky to get the right
        // sample of Numeric
    }

    @Override
    protected void numericSample(InstanceHolder<Numeric> holder, float newValue, NumericSQI sqi, DeviceClock.Reading time) {
        super.numericSample(holder, newValue, sqi, time);
        processPatientAlert(holder, newValue);
    }

	private void processPatientAlert(InstanceHolder<Numeric> holder, float newValue) {
		String identifier = holder.data.metric_id + "-" + holder.data.instance_id;
        InstanceHolder<ice.AlarmLimit> lowAlarmLimit = this.alarmLimit.get(alarmLimitKey(holder.data.metric_id, ice.LimitType.low_limit));
        InstanceHolder<ice.AlarmLimit> highAlarmLimit = this.alarmLimit.get(alarmLimitKey(holder.data.metric_id, ice.LimitType.high_limit));
        
        if(lowAlarmLimit == null && highAlarmLimit == null) {
            // This is so imperfect, what if limits had previously existed?
            return;
        }
        
        // I'm trusting here that we cannot simultaneously violate the upper and lower bound although this might not be true forever.
        //LOW LIMIT
        if(null != lowAlarmLimit && Float.compare(lowAlarmLimit.data.value, newValue) > 0) {
            // TODO ought the units be checked?
            log.debug("For " + identifier + " lower limit is exceeded " + newValue + " < " + lowAlarmLimit.data.value);
            writePatientAlert(identifier, "LOW");
            //HIGH LIMIT
        } else if(null != highAlarmLimit && Float.compare(highAlarmLimit.data.value, newValue) < 0) {
            log.debug("For " + identifier + " upper limit is exceeded " + newValue + " < " + highAlarmLimit.data.value);
            writePatientAlert(identifier, "HIGH");
        } else {
            log.trace("For " + identifier + " is in range " + newValue + " in [" + (null==lowAlarmLimit?"?":""+lowAlarmLimit.data.value)+"-"+(null==highAlarmLimit?"?":highAlarmLimit.data.value));
            writePatientAlert(identifier, "NORMAL");
        }
	}
    
    @Override
    public void unsetAlarmLimit(String metricId, ice.LimitType limit_type) {
        // TODO Really ought to unread the local objective and alarm settings when the alarm settings are unset
        super.unsetAlarmLimit(metricId, limit_type);
    }
    
    public NumericSQI getSQI() {
    	defaultSQI.accuracy = accuracyJitter.floatValue();
    	return defaultSQI;
    }
}
