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

import java.awt.*;
import java.io.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

import org.mdpnp.devices.serial.SerialProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;

/**
 * @author Jeff Plourde
 *
 */
public class Configuration {
    enum Application {
        ICE_Supervisor, ICE_Device_Interface, ICE_ParticipantOnly;
    }

    public enum DeviceType {
        PO_Simulator(ice.ConnectionType.Simulated, "Simulated", "Pulse Oximeter"), 
        NIBP_Simulator(ice.ConnectionType.Simulated, "Simulated", "Noninvasive Blood Pressure"), 
        ECG_Simulator(ice.ConnectionType.Simulated, "Simulated", "ElectroCardioGram"), 
        CO2_Simulator(ice.ConnectionType.Simulated, "Simulated", "Capnometer"), 
        Temp_Simulator(ice.ConnectionType.Simulated, "Simulated", "Temperature Probe"), 
        Pump_Simulator(ice.ConnectionType.Simulated, "Simulated", "Infusion Pump"),
        FlukeProsim68(ice.ConnectionType.Serial, "Fluke", "Prosim 6/8"), 
        Bernoulli(ice.ConnectionType.Network, "CardioPulmonaryCorp", "Bernoulli"), 
        Ivy450C(ice.ConnectionType.Serial, "Ivy", "450C Monitor"), 
        Nonin(ice.ConnectionType.Serial, "Nonin", "Bluetooth Pulse Oximeter"), 
        IntellivueEthernet(ice.ConnectionType.Network, "Philips", "Intellivue (LAN)"), 
        IntellivueSerial(ice.ConnectionType.Serial, "Philips", "Intellivue (MIB/RS232)"), 
        DrgerApollo(ice.ConnectionType.Serial, "Dr\u00E4ger", "Apollo"),
        DrgerEvitaXL(ice.ConnectionType.Serial, "Dr\u00E4ger", "EvitaXL"),
        DrgerV500(ice.ConnectionType.Serial, "Dr\u00E4ger", "V500"),
        DrgerV500_38400(ice.ConnectionType.Serial, "Dr\u00E4ger", "V500"),
        DrgerEvita4(ice.ConnectionType.Serial, "Dr\u00E4ger", "Evita4"),
        Capnostream20(ice.ConnectionType.Serial, "Oridion", "Capnostream20"), 
        NellcorN595(ice.ConnectionType.Serial, "Nellcor", "N-595"), 
        MasimoRadical7(ice.ConnectionType.Serial, "Masimo", "Radical-7"), 
        Symbiq(ice.ConnectionType.Simulated, "Hospira", "Symbiq"), 
        Multiparameter(ice.ConnectionType.Simulated, "Simulated", "Multiparameter Monitor"),
        DraegerApollo(ice.ConnectionType.Serial, "Dr\u00E4ger", "Apollo"),
        DraegerEvitaXL(ice.ConnectionType.Serial, "Dr\u00E4ger", "EvitaXL"), 
        DraegerV500(ice.ConnectionType.Serial, "Dr\u00E4ger", "V500"), 
        DraegerV500_38400(ice.ConnectionType.Serial, "Dr\u00E4ger", "V500"), 
        DraegerEvita4(ice.ConnectionType.Serial, "Dr\u00E4ger", "Evita4"),
        PB840(ice.ConnectionType.Serial, "Puritan Bennett", "840"),
        BioPatch(ice.ConnectionType.Serial, "Zephyr", "BioPatch");

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
    private static final String DOMAIN_ID   = "domainId";
    private static final String DEVICE_TYPE = "deviceType";
    private static final String ADDRESS     = "address";

    private final static Logger log = LoggerFactory.getLogger(Configuration.class);

    private void write(PrintWriter os) throws IOException {
        Properties p = new Properties();
        p.setProperty(APPLICATION, application.name());
        p.setProperty(DOMAIN_ID,   Integer.toString(domainId));
        if (null != deviceType)
            p.setProperty(DEVICE_TYPE, deviceType.name());
        if (null != address)
            p.setProperty(ADDRESS, address);

        p.list(os);
    }

    public static Configuration read(InputStream is) throws IOException {

        Properties p = new Properties();
        p.load(is);

        Application app = null;
        int domainId = 0;
        DeviceType deviceType = null;
        String address = null;

        if(p.containsKey(APPLICATION)) {
            String s = p.getProperty(APPLICATION);
            try {
                app = Application.valueOf(s);
            } catch (IllegalArgumentException iae) {
                log.warn("Ignoring unknown application type:" + s);
            }
        }
        if(p.containsKey(DOMAIN_ID)) {
            String s = p.getProperty(DOMAIN_ID);
            try {
                domainId = Integer.parseInt(s);
            } catch (NumberFormatException nfe) {
                log.warn("Ignoring unknown domainId:" + s);
            }
        }
        if(p.containsKey(DEVICE_TYPE)) {
            String s = p.getProperty(DEVICE_TYPE);
            try {
                deviceType = DeviceType.valueOf(s);
            } catch (IllegalArgumentException iae) {
                log.warn("Ignoring unknown device type:" + s);
            }
        }
        if(p.containsKey(ADDRESS)) {
            address = p.getProperty(ADDRESS);
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
        
        litr = args.listIterator();
        while (litr.hasNext()) {
            try {
                String x = litr.next();
                try {
                    domainId = Integer.parseInt(x);
                    litr.remove();
                    break;
                } catch (NumberFormatException nfe) {

                }

            } catch (IllegalArgumentException iae) {

            }
        }

        if (Application.ICE_Device_Interface.equals(app)) {
            litr = args.listIterator();
            while (litr.hasNext()) {
                String x = litr.next();
                String[] y = x.split("\\=");
                try {
                    deviceType = DeviceType.valueOf(y[0]);
                    litr.remove();
                    if (y.length > 1) {
                        address = y[1];
                    }
                    break;
                } catch (IllegalArgumentException iae) {
                    log.error("Unknown DeviceType:"+y[0]);
                }
            }
        }



        return new Configuration(app, domainId, deviceType, address);
    }


    public static Configuration getInstance(String[] args) throws Exception {

        Configuration runConf = null;

        File jumpStartSettings = new File(".JumpStartSettings");
        File jumpStartSettingsHome = new File(System.getProperty("user.home"), ".JumpStartSettings");

        boolean cmdline = false;

        if (args.length > 0) {
            runConf = Configuration.read(args);
            cmdline = true;
        } else if (jumpStartSettings.exists() && jumpStartSettings.canRead()) {
            FileInputStream fis = new FileInputStream(jumpStartSettings);
            runConf = Configuration.read(fis);
            fis.close();
        } else if (jumpStartSettingsHome.exists() && jumpStartSettingsHome.canRead()) {
            FileInputStream fis = new FileInputStream(jumpStartSettingsHome);
            runConf = Configuration.read(fis);
            fis.close();
        }

        Configuration writeConf = null;

        try {
            Class<?> cls = Class.forName("com.apple.eawt.Application");
            Method m1 = cls.getMethod("getApplication");
            Method m2 = cls.getMethod("setDockIconImage", Image.class);
            m2.invoke(m1.invoke(null), ImageIO.read(Main.class.getResource("icon.png")));
        } catch (Throwable t) {
            log.debug("Not able to set Mac OS X dock icon");
        }

        if (!cmdline) {
            ConfigurationDialog d = new ConfigurationDialog(runConf, null);

            d.setIconImage(ImageIO.read(Main.class.getResource("icon.png")));
            runConf = d.showDialog();
            // It's nice to be able to change settings even without running
            if (null == runConf) {
                writeConf = d.getLastConfiguration();
            }
        } else {
            // fall through to allow configuration via a file
        }

        if (null != runConf) {
            writeConf = runConf;
        }

        if (null != writeConf) {
            if (!jumpStartSettings.exists()) {
                jumpStartSettings.createNewFile();
            }

            if (jumpStartSettings.canWrite()) {
                PrintWriter fos = new PrintWriter(jumpStartSettings);
                writeConf.write(fos);
                fos.close();
            }

            if (!jumpStartSettingsHome.exists()) {
                jumpStartSettingsHome.createNewFile();
            }

            if (jumpStartSettingsHome.canWrite()) {
                PrintWriter fos = new PrintWriter(jumpStartSettingsHome);
                writeConf.write(fos);
                fos.close();
            }
        }

        return runConf;

    }
}
