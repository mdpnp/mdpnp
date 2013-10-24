/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.connected;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import ice.DeviceConnectivity;
import ice.DeviceConnectivityObjective;
import ice.DeviceConnectivityObjectiveTopic;
import ice.DeviceConnectivityTopic;

import org.mdpnp.devices.AbstractDevice;
import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.io.util.StateMachine;
import org.omg.dds.core.Condition;
import org.omg.dds.core.InstanceHandle;
import org.omg.dds.pub.DataWriter;
import org.omg.dds.sub.DataReader;
import org.omg.dds.sub.ReadCondition;
import org.omg.dds.sub.Sample;
import org.omg.dds.sub.DataReader.Selector;
import org.omg.dds.sub.SampleState;
import org.omg.dds.topic.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractConnectedDevice extends AbstractDevice {
    protected final DeviceConnectivity deviceConnectivity;
    protected final Topic<ice.DeviceConnectivity> deviceConnectivityTopic;
    private InstanceHandle deviceConnectivityHandle;
    private final DataWriter<ice.DeviceConnectivity> deviceConnectivityWriter;

    protected final DeviceConnectivityObjective deviceConnectivityObjective;
    protected final DataReader<ice.DeviceConnectivityObjective> deviceConnectivityObjectiveReader;
//	protected final InstanceHandle_t deviceConnectivityObjectiveHandle;
    protected final Topic<ice.DeviceConnectivityObjective> deviceConnectivityObjectiveTopic;
    private final Selector<ice.DeviceConnectivityObjective> rc;

    protected final StateMachine<ice.ConnectionState> stateMachine = new StateMachine<ice.ConnectionState>(legalTransitions, ice.ConnectionState.Disconnected) {
        @Override
        public void emit(ice.ConnectionState newState, ice.ConnectionState oldState) {
            stateChanging(newState, oldState);
            log.debug(oldState + "==>"+newState);
            deviceConnectivity.state = newState;
            InstanceHandle handle = deviceConnectivityHandle;
            if(handle != null) {
                try {
                    writeDeviceConnectivity();
                } catch (TimeoutException e) {
                    log.error("timed out writing device connectivity on change", e);
                }
            }
            stateChanged(newState, oldState);
        };
    };

    protected void stateChanging(ice.ConnectionState newState, ice.ConnectionState oldState) {

    }

    protected void stateChanged(ice.ConnectionState newState, ice.ConnectionState oldState) {
        if(ice.ConnectionState.Connected.equals(oldState) && !ice.ConnectionState.Connected.equals(newState)) {
            eventLoop.doLater(new Runnable() {
                public void run() {
                    try {
                        unregisterAllInstances();
                    } catch (TimeoutException e) {
                        log.error("Unable to unregister all instances where no longer in Connected state", e);
                    }
                }
            });
        }
    }

    private static final Logger log = LoggerFactory.getLogger(AbstractConnectedDevice.class);

    @Override
    public void shutdown() {
        eventLoop.removeHandler(rc.getCondition());
        rc.getCondition().close();
        deviceConnectivityObjectiveReader.close();
        deviceConnectivityObjectiveTopic.close();

        if(null != deviceConnectivityHandle) {
            InstanceHandle handle = deviceConnectivityHandle;
            deviceConnectivityHandle = null;
            try {
                deviceConnectivityWriter.dispose(handle, deviceConnectivity);
            } catch (TimeoutException e) {
                log.error("timed out disposing of deviceConnectivity", e);
            }

        }
        deviceConnectivityWriter.close();
        deviceConnectivityTopic.close();
        super.shutdown();
    }


    public AbstractConnectedDevice(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);
        deviceConnectivityTopic = domainParticipant.createTopic(ice.DeviceConnectivityTopic.value, ice.DeviceConnectivity.class);
        deviceConnectivityWriter = publisher.createDataWriter(deviceConnectivityTopic);

        if(null == deviceConnectivityWriter) {
            throw new RuntimeException("unable to create writer");
        }

        deviceConnectivity = new DeviceConnectivity();
        deviceConnectivity.type = getConnectionType();
        deviceConnectivity.state = ice.ConnectionState.Disconnected;

        deviceConnectivityObjective = new DeviceConnectivityObjective();
        deviceConnectivityObjectiveTopic = domainParticipant.createTopic(ice.DeviceConnectivityObjectiveTopic.value, ice.DeviceConnectivityObjective.class);
        deviceConnectivityObjectiveReader = subscriber.createDataReader(deviceConnectivityObjectiveTopic);

        rc = deviceConnectivityObjectiveReader.select().dataState(subscriber.createDataState().withAnyInstanceState().withAnyViewState().with(SampleState.NOT_READ));
        ReadCondition<ice.DeviceConnectivityObjective> read = deviceConnectivityObjectiveReader.createReadCondition(rc.getDataState());

        eventLoop.addHandler(read, new EventLoop.ConditionHandler() {

            @Override
            public void conditionChanged(Condition condition) {
                Sample.Iterator<ice.DeviceConnectivityObjective> itr = rc.read();
                try {
                    while(itr.hasNext()) {
                        Sample<ice.DeviceConnectivityObjective> sample = itr.next();
                        if(null != sample.getData()) {
                            if(deviceIdentity.unique_device_identifier.equals(sample.getData().unique_device_identifier)) {

                                if(sample.getData().connected) {
                                    log.info("Issuing connect for " + deviceIdentity.unique_device_identifier + " to " + sample.getData().target);
                                    connect(sample.getData().target);

                                } else {
                                    log.info("Issuing disconnect for " + deviceIdentity.unique_device_identifier);
                                    disconnect();
                                }
                            }
                        }
                    }
                } finally {
                    try {
                        itr.close();
                    } catch (IOException e) {
                        log.error("unable to close DeviceConnectivity iterator", e);
                    }
                }
            }

        });
    }

    public abstract void connect(String str);
    public abstract void disconnect();
    protected abstract ice.ConnectionType getConnectionType();

    public ice.ConnectionState getState() {
        return stateMachine.getState();
    };

    private static final ice.ConnectionState[][] legalTransitions = new ice.ConnectionState[][] {
        // Normal "flow"
        // A "connect" was requested
        {ice.ConnectionState.Disconnected, ice.ConnectionState.Connecting},
        // A "disconnect" was requested from the Connected state
        {ice.ConnectionState.Connected, ice.ConnectionState.Disconnecting},
          // A "disconnect" was requested from the Connecting state
        {ice.ConnectionState.Connecting, ice.ConnectionState.Disconnecting},
        // A "disconnect" was requested from the Negotiating state
        {ice.ConnectionState.Negotiating, ice.ConnectionState.Disconnecting},
        // Connection was established
        {ice.ConnectionState.Connecting, ice.ConnectionState.Negotiating},
        // Connection still open but no active session (silence on the RS-232 line for example)
        {ice.ConnectionState.Connected, ice.ConnectionState.Negotiating},
        // Negotiation was successful
        {ice.ConnectionState.Negotiating, ice.ConnectionState.Connected},
        // Disconnection was successful
        {ice.ConnectionState.Disconnecting, ice.ConnectionState.Disconnected},
        // Exception pathways
        // A fatal error occurred in the Negotiating state
        {ice.ConnectionState.Negotiating, ice.ConnectionState.Disconnected},
        // A fatal error occurred in the Connecting state
        {ice.ConnectionState.Connecting, ice.ConnectionState.Disconnected},
        // A fatal error occurred in the Connected state
        {ice.ConnectionState.Connected, ice.ConnectionState.Disconnected},

    };

    //Disconnected -> Connecting -> Negotiating -> Connected -> Disconnecting -> Disconnected

    public boolean awaitState(ice.ConnectionState state, long timeout) {
        return stateMachine.wait(state, timeout);
    }

    protected void setConnectionInfo(String connectionInfo) throws TimeoutException {
        if(null == connectionInfo) {
            // TODO work on nullity semantics
            log.warn("Attempt to set connectionInfo null");
            connectionInfo = "";
        }
        if(!connectionInfo.equals(deviceConnectivity.info)) {
            deviceConnectivity.info = connectionInfo;
            writeDeviceConnectivity();
        }
    }

    @Override
    protected void writeDeviceIdentity() throws TimeoutException {
        super.writeDeviceIdentity();
        if(null == deviceConnectivityHandle) {
            writeDeviceConnectivity();
        }
    }

    protected void writeDeviceConnectivity() throws TimeoutException {
        deviceConnectivity.unique_device_identifier = deviceIdentity.unique_device_identifier;
        if(null == deviceConnectivity.unique_device_identifier || "".equals(deviceConnectivity.unique_device_identifier)) {
            throw new IllegalStateException("No UDI");
        }
        if(null == deviceConnectivityHandle) {
            deviceConnectivityHandle = deviceConnectivityWriter.registerInstance(deviceConnectivity);
        }
        deviceConnectivityWriter.write(deviceConnectivity, deviceConnectivityHandle);
    }
}
