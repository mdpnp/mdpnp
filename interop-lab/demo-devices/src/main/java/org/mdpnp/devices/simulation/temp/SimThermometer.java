/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.simulation.temp;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.simulation.AbstractSimulatedConnectedDevice;

public class SimThermometer extends AbstractSimulatedConnectedDevice {

    protected final InstanceHolder<ice.Numeric> temperature; 
    
    private class MySimulatedThermometer extends SimulatedThermometer {
        @Override
        protected void receiveTemp(float temperature) {
            numericSample(SimThermometer.this.temperature, temperature);
            
        }
    }
    
    private final MySimulatedThermometer thermometer = new MySimulatedThermometer();
    
    
    @Override
    public void connect(String str) {
        thermometer.connect(executor);
        super.connect(str);
    }
    
    @Override
    public void disconnect() {
        thermometer.disconnect();
        super.disconnect();
    }
    
    public SimThermometer(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);
        temperature = createNumericInstance(ice.MDC_TEMP_BLD.VALUE);
        
        deviceIdentity.model = "Thermometer (Simulated)";
        deviceIdentityWriter.write(deviceIdentity, deviceIdentityHandle);
    }
    
    @Override
    protected String iconResourceName() {
        return null;
    }
}
