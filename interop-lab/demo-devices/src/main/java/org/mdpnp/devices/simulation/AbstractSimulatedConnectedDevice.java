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

import ice.GlobalSimulationObjective;

import org.mdpnp.devices.connected.AbstractConnectedDevice;
import org.mdpnp.rtiapi.data.EventLoop;

public abstract class AbstractSimulatedConnectedDevice extends AbstractConnectedDevice implements GlobalSimulationObjectiveListener {
    protected Throwable t;

    protected final GlobalSimulationObjectiveMonitor monitor;

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
        ice.ConnectionState state = getState();
        if (ice.ConnectionState.Disconnected.equals(state) || ice.ConnectionState.Disconnecting.equals(state)) {
        } else {
            if (!stateMachine.transitionWhenLegal(ice.ConnectionState.Disconnecting, 1000L, "disconnect requested")) {
                throw new RuntimeException("Unable to enter Disconnecting State");
            }
            if (!stateMachine.transitionWhenLegal(ice.ConnectionState.Disconnected, 1000L, "disconnect requested")) {
                throw new RuntimeException("Unable to enter Disconnected State");
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
}
