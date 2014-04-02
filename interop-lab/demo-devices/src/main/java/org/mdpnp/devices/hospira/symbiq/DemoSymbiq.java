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
package org.mdpnp.devices.hospira.symbiq;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.mdpnp.devices.simulation.pump.SimInfusionPump;
import org.mdpnp.rtiapi.data.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class DemoSymbiq extends SimInfusionPump {

    private final File pumpControlFile = new File("C:\\pump_control_1.txt");

    @Override
    protected void writeIdentity() {
        deviceIdentity.manufacturer = "Hospira";
        deviceIdentity.model = "Symbiq";
        deviceIdentity.serial_number = "xxx";
        writeDeviceIdentity();
    }

    private static final Logger log = LoggerFactory.getLogger(DemoSymbiq.class);

    private Boolean lastStopThePumpWritten = null;

    @Override
    protected void stopThePump(boolean stopThePump) {
        super.stopThePump(stopThePump);
        if (null == lastStopThePumpWritten || (stopThePump ^ lastStopThePumpWritten)) {

            Writer w;
            try {
                w = new FileWriter(pumpControlFile);
                if (stopThePump) {
                    w.write("Stop, \n");
                } else {
                    w.write("Start, 10\n");
                }
                w.close();
                lastStopThePumpWritten = stopThePump;
            } catch (IOException e) {
                log.error("can't stop", e);
            }

        }

    }

    public DemoSymbiq(int domainId, EventLoop eventLoop) {
        super(domainId, eventLoop);

    }

    @Override
    protected String iconResourceName() {
        return "symbiq.png";
    }

}
