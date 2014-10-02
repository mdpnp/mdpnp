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
package org.mdpnp.devices.masimo.radical;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.mdpnp.devices.serial.AbstractSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;
import org.mdpnp.rtiapi.data.EventLoop;

import com.rti.dds.infrastructure.Time_t;

/**
 * @author Jeff Plourde
 *
 */
public class DemoRadical7 extends AbstractSerialDevice {
    private InstanceHolder<ice.Numeric> pulseUpdate;
    private InstanceHolder<ice.Numeric> spo2Update;

    private static final String MANUFACTURER_NAME = "Masimo";
    private static final String MODEL_NAME = "Radical-7";

    private class MyMasimoRadical7 extends MasimoRadical7 {

        public MyMasimoRadical7() throws NoSuchFieldException, SecurityException, IOException {
            super();
        }

        private final Time_t sampleTime = new Time_t(0, 0);

        @Override
        public void firePulseOximeter() {
            super.firePulseOximeter();
            reportConnected("message received");
            long tm = getTimestamp().getTime();
            sampleTime.sec = (int) (tm / 1000L);
            sampleTime.nanosec = (int) (tm % 1000L * 1000000L);
            pulseUpdate = numericSample(pulseUpdate, getHeartRate(), rosetta.MDC_PULS_OXIM_PULS_RATE.VALUE, 
                    rosetta.MDC_DIM_BEAT_PER_MIN.VALUE, sampleTime);
            spo2Update = numericSample(spo2Update, getSpO2(), rosetta.MDC_PULS_OXIM_SAT_O2.VALUE, 
                    rosetta.MDC_DIM_PERCENT.VALUE, sampleTime);
            String guid = getUniqueId();
            if (guid != null && !guid.equals(deviceIdentity.serial_number)) {
                deviceIdentity.serial_number = guid;
                writeDeviceIdentity();
            }
            if(getAlarm() != null && !"".equals(getAlarm())) {
                writeDeviceAlert(getAlarm());
            } else {
                writeDeviceAlert("");
            }
        }
    }

    private final MyMasimoRadical7 fieldDelegate;

    @Override
    protected void process(int idx, InputStream inputStream, OutputStream outputStream) throws IOException {
        fieldDelegate.setInputStream(inputStream);
        fieldDelegate.run();
    }

    @Override
    protected long getMaximumQuietTime(int idx) {
        return 1100L;
    }

    @Override
    protected void doInitCommands(int idx) throws IOException {
    }

    @Override
    public SerialProvider getSerialProvider(int idx) {
        SerialProvider serialProvider = super.getSerialProvider(idx);
        serialProvider.setDefaultSerialSettings(9600, SerialSocket.DataBits.Eight, SerialSocket.Parity.None, SerialSocket.StopBits.One);
        return serialProvider;
    }

    public DemoRadical7(int domainId, EventLoop eventLoop) throws NoSuchFieldException, SecurityException, IOException {
        super(domainId, eventLoop);
        AbstractSimulatedDevice.randomUDI(deviceIdentity);
        deviceIdentity.manufacturer = MANUFACTURER_NAME;
        deviceIdentity.model = MODEL_NAME;
        writeDeviceIdentity();

        this.fieldDelegate = new MyMasimoRadical7();
    }

    @Override
    protected String iconResourceName() {
        return "radical7.png";
    }

}
