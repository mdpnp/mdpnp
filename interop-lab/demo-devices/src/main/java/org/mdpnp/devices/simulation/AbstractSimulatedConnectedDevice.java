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

import ice.AlarmSettings;
import ice.GlobalAlarmSettingsObjective;
import ice.GlobalSimulationObjective;
import ice.LocalAlarmSettingsObjective;
import ice.Numeric;

import java.util.HashMap;
import java.util.Map;

import org.mdpnp.devices.DeviceClock;
import org.mdpnp.devices.connected.AbstractConnectedDevice;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractSimulatedConnectedDevice extends AbstractConnectedDevice implements GlobalSimulationObjectiveListener {
    protected Throwable t;

    protected final GlobalSimulationObjectiveMonitor monitor;
    
    private static final Logger log = LoggerFactory.getLogger(AbstractSimulatedConnectedDevice.class);

    public AbstractSimulatedConnectedDevice(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        writeDeviceIdentity();

        monitor = new GlobalSimulationObjectiveMonitor(this);
        monitor.register(domainParticipant, eventLoop);
    }

    public Throwable getLastError() {
        return t;
    }

    @Override
    public boolean connect(String str) {
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
    
    private Map<String, InstanceHolder<ice.LocalAlarmSettingsObjective>> localAlarmSettings = new HashMap<String, InstanceHolder<ice.LocalAlarmSettingsObjective>>();
    private Map<String, InstanceHolder<ice.AlarmSettings>> alarmSettings = new HashMap<String, InstanceHolder<ice.AlarmSettings>>();
    
    @Override
    protected void unregisterAlarmSettingsObjectiveInstance(InstanceHolder<LocalAlarmSettingsObjective> holder) {
        localAlarmSettings.clear();
        super.unregisterAlarmSettingsObjectiveInstance(holder);
    }
    
    @Override
    protected void unregisterAlarmSettingsInstance(InstanceHolder<AlarmSettings> holder) {
        alarmSettings.clear();
        super.unregisterAlarmSettingsInstance(holder);
    }
    
    @Override
    public void setAlarmSettings(GlobalAlarmSettingsObjective obj) {
        super.setAlarmSettings(obj);
        localAlarmSettings.put(obj.metric_id,
                alarmSettingsObjectiveSample(localAlarmSettings.get(obj.metric_id), obj.lower, obj.upper, obj.metric_id));
        alarmSettings.put(obj.metric_id,
                alarmSettingsSample(alarmSettings.get(obj.metric_id), obj.lower, obj.upper, obj.metric_id));
        // TODO really should also check alarm violation on threshold change here but it will be tricky to get the right
        // sample of Numeric
    }

    @Override
    protected void numericSample(InstanceHolder<Numeric> holder, float newValue, DeviceClock.Reading time) {
        super.numericSample(holder, newValue, time);
        String identifier = holder.data.metric_id + "-" + holder.data.instance_id;
        InstanceHolder<ice.AlarmSettings> alarmSettings = this.alarmSettings.get(holder.data.metric_id);
        if(null != alarmSettings) {
            
            // There are threshold settings for this numeric value, let's emit an alarm!
            if(Float.compare(alarmSettings.data.lower, newValue)>0) {
                log.debug("For " + identifier + " lower bound is exceeded " + newValue + " < " + alarmSettings.data.lower);
                writePatientAlert(identifier, "LOW");
            } else if(Float.compare(alarmSettings.data.upper, newValue)<0) {
                log.debug("For " + identifier + " upper bound is exceeded " + newValue + " > " + alarmSettings.data.upper);
                writePatientAlert(identifier, "HIGH");
            } else {
                log.trace("For " + identifier + " is in range " + newValue + " in [" + alarmSettings.data.lower+"-"+alarmSettings.data.upper);
                writePatientAlert(identifier, "NORMAL");
            }
        } else {
            log.trace("For " + identifier + " no alarm settings");
        }
    }
    
    @Override
    public void unsetAlarmSettings(String metricId) {
        // TODO Really ought to unreg the local objective and alarm settings when the alarm settings are unset
        super.unsetAlarmSettings(metricId);
    }
    
    
}
