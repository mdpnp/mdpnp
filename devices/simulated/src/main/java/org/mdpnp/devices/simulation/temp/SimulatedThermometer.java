/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.simulation.temp;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class SimulatedThermometer {

        
    private final class MyTask implements Runnable {
        @Override
        public void run() {
            receiveTemp(temperature);
        }
        
    };
    
    protected void receiveTemp(float temperature) {
        
    }
    private static final long UPDATE_PERIOD = 1000L;
    protected int temperature = 37;
    
    private ScheduledFuture<?> task;
    
    public void connect(ScheduledExecutorService executor) {
        if(task != null) {
            task.cancel(false);
            task = null;
        }
        task = executor.scheduleAtFixedRate(new MyTask(), 0L, UPDATE_PERIOD, TimeUnit.MILLISECONDS);
    }
    
    public void disconnect() {
        if(task != null) {
            task.cancel(false); 
            task = null;
        }
    }
    
    public SimulatedThermometer() {
    }
    
}
