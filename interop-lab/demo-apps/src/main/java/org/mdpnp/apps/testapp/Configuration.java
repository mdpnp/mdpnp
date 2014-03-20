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
package org.mdpnp.apps.testapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.mdpnp.devices.serial.SerialProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jeff Plourde
 *
 */
public class Configuration {
    enum Application {
        ICE_Supervisor, ICE_Device_Interface, ICE_ParticipantOnly;
    }

    public enum DeviceType {
        PO_Simulator(ice.ConnectionType.Simulated, "Simulated", "Pulse Oximeter"), NIBP_Simulator(ice.ConnectionType.Simulated, "Simulated",
                "Noninvasive Blood Pressure"), ECG_Simulator(ice.ConnectionType.Simulated, "Simulated", "ElectroCardioGram"), CO2_Simulator(
                ice.ConnectionType.Simulated, "Simulated", "Capnometer"), Temp_Simulator(ice.ConnectionType.Simulated, "Simulated",
                "Temperature Probe"), Pump_Simulator(ice.ConnectionType.Simulated, "Simulated", "Infusion Pump"), FlukeProsim68(
                ice.ConnectionType.Serial, "Fluke", "Prosim 6/8"), Bernoulli(ice.ConnectionType.Network, "CardioPulmonaryCorp", "Bernoulli"), Ivy450C(
                ice.ConnectionType.Serial, "Ivy", "450C Monitor"), Nonin(ice.ConnectionType.Serial, "Nonin", "Bluetooth Pulse Oximeter"), IntellivueEthernet(
                ice.ConnectionType.Network, "Philips", "Intellivue (LAN)"), IntellivueSerial(ice.ConnectionType.Serial, "Philips",
                "Intellivue (MIB/RS232)"), Dr\u00E4gerApollo(ice.ConnectionType.Serial, "Dr\u00E4ger", "Apollo"), Dr\u00E4gerEvitaXL(
                ice.ConnectionType.Serial, "Dr\u00E4ger", "EvitaXL"), Dr\u00E4gerV500(ice.ConnectionType.Serial, "Dr\u00E4ger", "V500"), Dr\u00E4gerEvita4(
                ice.ConnectionType.Serial, "Dr\u00E4ger", "Evita4"), Capnostream20(
                ice.ConnectionType.Serial, "Oridion", "Capnostream20"), NellcorN595(ice.ConnectionType.Serial, "Nellcor", "N-595"), MasimoRadical7(
                ice.ConnectionType.Serial, "Masimo", "Radical-7"), Symbiq(ice.ConnectionType.Simulated, "Hospira", "Symbiq"), MultiPO_Simulator(
                ice.ConnectionType.Simulated, "Simulated", "Multiple Pulse Oximeter"),
                DraegerApollo(ice.ConnectionType.Serial, "Dr\u00E4ger", "Apollo"),
                DraegerEvitaXL(
                        ice.ConnectionType.Serial, "Dr\u00E4ger", "EvitaXL"), 
                        DraegerV500(ice.ConnectionType.Serial, "Dr\u00E4ger", "V500"), DraegerEvita4(
                        ice.ConnectionType.Serial, "Dr\u00E4ger", "Evita4");

        private final ice.ConnectionType connectionType;
        private final String manufacturer, model;

        private DeviceType(ice.ConnectionType connectionType, String manufacturer, String model) {
            this.connectionType = connectionType;
            this.manufacturer = manufacturer;
            this.model = model;
        }

        public ice.ConnectionType getConnectionType() {
            return connectionType;
        }

        public String getManufacturer() {
            return manufacturer;
        }

        public String getModel() {
            return model;
        }

        @Override
        public String toString() {
            return manufacturer + " " + model;
        }
    }

    private final Application application;
    private final DeviceType deviceType;
    private final String address;
    private final int domainId;

    public Configuration(Application application, int domainId, DeviceType deviceType, String address) {
        this.application = application;
        this.deviceType = deviceType;
        this.address = address;
        this.domainId = domainId;
    }

    public Application getApplication() {
        return application;
    }

    public int getDomainId() {
        return domainId;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public String getAddress() {
        return address;
    }

    private static final String APPLICATION = "application";
    private static final String DOMAIN_ID = "domainId";
    private static final String DEVICE_TYPE = "deviceType";
    private static final String ADDRESS = "address";

    public void write(OutputStream os) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, CHARACTER_ENCODING));
        bw.write(APPLICATION);
        bw.write("\t");
        bw.write(application.name());
        bw.write("\n");

        bw.write(DOMAIN_ID);
        bw.write("\t");
        bw.write(Integer.toString(domainId));
        bw.write("\n");

        if (null != deviceType) {
            bw.write(DEVICE_TYPE);
            bw.write("\t");
            bw.write(deviceType.name());
            bw.write("\n");
        }

        if (null != address) {
            bw.write(ADDRESS);
            bw.write("\t");
            bw.write(address);
            bw.write("\n");
        }

        bw.flush();
    }

    private final static Logger log = LoggerFactory.getLogger(Configuration.class);
    private static final String CHARACTER_ENCODING = "ISO8859_1";

    public static Configuration read(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, CHARACTER_ENCODING));

        String line = null;

        Application app = null;
        int domainId = 0;
        DeviceType deviceType = null;
        String address = null;

        while (null != (line = br.readLine())) {
            String[] v = line.split("\t");
            if (APPLICATION.equals(v[0])) {
                try {
                    app = Application.valueOf(v[1]);
                } catch (IllegalArgumentException iae) {
                    app = null;
                    log.warn("Ignoring unknown application type:" + v[1]);
                }
            } else if (DOMAIN_ID.equals(v[0])) {
                try {
                    domainId = Integer.parseInt(v[1]);
                } catch (NumberFormatException nfe) {
                    log.warn("Ignoring unknown domainId:" + v[1]);
                }
            } else if (DEVICE_TYPE.equals(v[0])) {
                try {
                    deviceType = DeviceType.valueOf(v[1]);
                } catch (IllegalArgumentException iae) {
                    deviceType = null;
                    log.warn("Ignoring unknown devicetype:" + v[1]);
                }
            } else if (ADDRESS.equals(v[0])) {
                if (v.length > 1) {
                    address = v[1];
                } else {
                    address = null;
                }
            }
        }

        return new Configuration(app, domainId, deviceType, address);
    }

    public static void help(Class<?> launchClass, PrintStream out) {
        out.println(launchClass.getName() + " [Application] [domainId] [DeviceType[=DeviceAddress]]");
        out.println();
        out.println("For interactive graphical interface specify no command line options");
        out.println();
        out.println("Application may be one of:");
        for (Application a : Application.values()) {
            out.println("\t" + a.name());
        }
        out.println();
        out.println("domainId must be a DDS domain identifier");
        out.println();

        out.println("if Application is " + Application.ICE_Device_Interface.name() + " then DeviceType may be one of:");
        for (DeviceType d : DeviceType.values()) {
            out.println("\t" + (ice.ConnectionType.Serial.equals(d.getConnectionType()) ? "*" : "") + d.name());
        }
        out.println("DeviceAddress is an optional string configuring the address of the device");
        out.println();
        out.println("DeviceTypes marked with * are serial devices for which the following DeviceAddress values are currently valid:");
        for (String s : SerialProviderFactory.getDefaultProvider().getPortNames()) {
            out.println("\t" + s);
        }
    }

    public static Configuration read(String[] args_) {
        Application app = null;
        int domainId = 0;
        DeviceType deviceType = null;
        String address = null;

        List<String> args = new ArrayList<String>(Arrays.asList(args_));
        ListIterator<String> litr = args.listIterator();
        while (litr.hasNext()) {
            try {
                app = Application.valueOf(litr.next());
                litr.remove();
                break;
            } catch (IllegalArgumentException iae) {

            }
        }
        if (null == app) {
            return null;
        }

        if (Application.ICE_Device_Interface.equals(app)) {
            litr = args.listIterator();
            while (litr.hasNext()) {
                try {
                    String x = litr.next();
                    String[] y = x.split("\\=");
                    deviceType = DeviceType.valueOf(y[0]);
                    litr.remove();
                    if (y.length > 1) {
                        address = y[1];
                    }
                    break;
                } catch (IllegalArgumentException iae) {

                }
            }
        }

        litr = args.listIterator();
        while (litr.hasNext()) {
            try {
                String x = litr.next();
                try {
                    domainId = Integer.parseInt(x);
                    break;
                } catch (NumberFormatException nfe) {

                }

            } catch (IllegalArgumentException iae) {

            }
        }

        return new Configuration(app, domainId, deviceType, address);
    }
}
