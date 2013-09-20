/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.masimo.radical;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.devices.serial.AbstractSerialDevice;
import org.mdpnp.devices.serial.SerialProvider;
import org.mdpnp.devices.serial.SerialSocket;
import org.mdpnp.devices.simulation.AbstractSimulatedDevice;

public class DemoRadical7 extends AbstractSerialDevice {
    private InstanceHolder<ice.Numeric> pulseUpdate;
    private InstanceHolder<ice.Numeric> spo2Update;

    private static final String MANUFACTURER_NAME = "Masimo";
    private static final String MODEL_NAME = "Radical-7";

    private class MyMasimoRadical7 extends MasimoRadical7 {

        public MyMasimoRadical7() throws NoSuchFieldException, SecurityException, IOException {
            super();
        }
        @Override
        public void firePulseOximeter() {
            super.firePulseOximeter();
            reportConnected();
            pulseUpdate = numericSample(pulseUpdate, getHeartRate(), ice.Physio._MDC_PULS_OXIM_PULS_RATE);
            spo2Update = numericSample(spo2Update, getSpO2(), ice.Physio._MDC_PULS_OXIM_SAT_O2);
            String guid = getUniqueId();
            if(guid != null && !guid.equals(deviceIdentity.serial_number)) {
                deviceIdentity.serial_number = guid;
                writeDeviceIdentity();
            }
        }
    }

    private final MyMasimoRadical7 fieldDelegate;

    @Override
    protected void process(InputStream inputStream, OutputStream outputStream) throws IOException {
        fieldDelegate.setInputStream(inputStream);
        fieldDelegate.run();
    }
    @Override
    protected long getMaximumQuietTime() {
        return 1100L;
    }
    @Override
    protected void doInitCommands() throws IOException {
    }


    @Override
    public SerialProvider getSerialProvider() {
        SerialProvider serialProvider =  super.getSerialProvider();
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
