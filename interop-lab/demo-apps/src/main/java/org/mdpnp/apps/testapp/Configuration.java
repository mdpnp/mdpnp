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
import org.mdpnp.messaging.BindingFactory;
import org.mdpnp.messaging.BindingFactory.BindingType;
import org.mdpnp.nomenclature.ConnectedDevice;
import org.mdpnp.nomenclature.ConnectedDevice.ConnectionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Configuration {
    enum Application {
        ICE_Supervisor,
        ICE_Device_Interface;
    }
    
    public enum DeviceType {
        PO_Simulator(ConnectionType.Simulated),
        NBP_Simulator(ConnectionType.Simulated),
        Bernoulli(ConnectionType.Network),
        Nonin(ConnectionType.Serial),
        PhilipsMP70(ConnectionType.Network),
        DragerApollo(ConnectionType.Serial),
        DragerEvitaXL(ConnectionType.Serial),
        Capnostream20(ConnectionType.Serial),
        NellcorN595(ConnectionType.Serial),
        MasimoRadical7(ConnectionType.Serial),
        Symbiq(ConnectionType.Simulated);
        
        private final ConnectedDevice.ConnectionType connectionType;
        
        private DeviceType(ConnectedDevice.ConnectionType connectionType) {
            this.connectionType = connectionType;
        }
        public ConnectedDevice.ConnectionType getConnectionType() {
            return connectionType;
        }
    } 
    
    private final Application application;
    private final BindingFactory.BindingType binding;
    private final String bindingSettings;
    private final DeviceType deviceType;
    private final String address;
    
    public Configuration(Application application, BindingFactory.BindingType binding, String bindingSettings, DeviceType deviceType, String address) {
        this.application = application;
        this.binding = binding;
        this.bindingSettings = bindingSettings;
        this.deviceType = deviceType;
        this.address = address;
    }
    public Application getApplication() {
        return application;
    }
    public BindingFactory.BindingType getBinding() {
        return binding;
    }
    public String getBindingSettings() {
        return bindingSettings;
    }
    public DeviceType getDeviceType() {
        return deviceType;
    }
    public String getAddress() {
        return address;
    }
    public void write(OutputStream os) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os, "ASCII"));
        bw.write("application");
        bw.write("\t");
        bw.write(application.name());
        bw.write("\n");
        
        bw.write("binding");
        bw.write("\t");
        bw.write(binding.name());
        bw.write("\n");
        
        if(null != bindingSettings) {
	        bw.write("bindingSettings");
	        bw.write("\t");
	        bw.write(bindingSettings);
	        bw.write("\n");
        }
        if(null != deviceType) {
	        bw.write("deviceType");
	        bw.write("\t");
	        bw.write(deviceType.name());
	        bw.write("\n");
        }
        
        if(null != address) {
	        bw.write("address");
	        bw.write("\t");
	        bw.write(address);
	        bw.write("\n");
        }
        
        bw.flush();
    }
    
    private final static Logger log = LoggerFactory.getLogger(Configuration.class);
    
    public static Configuration read(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "ASCII"));
        
        String line = null;
        
        Application app = null;
        BindingFactory.BindingType binding = null;
        String bindingSettings = null;
        DeviceType deviceType = null;
        String address = null;
        
        while(null != (line = br.readLine())) {
            String[] v = line.split("\t");
            if("application".equals(v[0])) {
                try {
                    app = Application.valueOf(v[1]);
                } catch (IllegalArgumentException iae) {
                    app = null;
                    log.warn("Ignoring unknown application type:"+v[1]);
                }
            } else if("binding".equals(v[0])) {
                try {
                    binding = BindingFactory.BindingType.valueOf(v[1]);
                } catch (IllegalArgumentException iae) {
                    binding = null;
                    log.warn("Ignoring unknown binding type:"+v[1]);
                }
            } else if("bindingSettings".equals(v[0])) {
                bindingSettings = v[1];
            } else if("deviceType".equals(v[0])) {
                try {
                    deviceType = DeviceType.valueOf(v[1]);
                } catch (IllegalArgumentException iae) {
                    deviceType = null;
                    log.warn("Ignoring unknown devicetype:"+v[1]);
                }
            } else if("address".equals(v[0])) {
                if(v.length > 1) {
                    address = v[1];
                } else {
                    address = null;
                }
            }
        }
        
        return new Configuration(app, binding, bindingSettings, deviceType, address);
    }
    
    public static void help(Class<?> launchClass, PrintStream out) {
        out.println(launchClass.getName() + " [Application] [Binding[=BindingSettings]] [DeviceType[=DeviceAddress]]");
        out.println();
        out.println("For interactive graphical interface specify no command line options");
        out.println();
        out.println("Application may be one of:");
        for(Application a : Application.values()) {
            out.println("\t"+a.name());
        }
        out.println();
        out.println("Binding may be one of:");
        for(BindingType w : BindingType.values()) {
            out.println("\t"+w.name());
        }
        
        out.println("BindingOptions is an optional string configuring the selected Binding");
        out.println();
        
        out.println("if Application is " + Application.ICE_Device_Interface.name() + " then DeviceType may be one of:");
        for(DeviceType d : DeviceType.values()) {
            out.println("\t"+(ConnectionType.Serial.equals(d.getConnectionType())?"*":"")+d.name());
        }
        out.println("DeviceAddress is an optional string configuring the address of the device");
        out.println();
        out.println("DeviceTypes marked with * are serial devices for which the following DeviceAddress values are currently valid:");
        for(String s : SerialProviderFactory.getDefaultProvider().getPortNames()) {
            out.println("\t"+s);
        }
    }
    
    public static Configuration read(String[] args_) {
        Application app = null;
        BindingFactory.BindingType binding = null;
        String bindingSettings = null;
        DeviceType deviceType = null;
        String address = null;
        
        List<String> args = new ArrayList<String>(Arrays.asList(args_));
        ListIterator<String> litr = args.listIterator();
        while(litr.hasNext()) {
            try {
                app = Application.valueOf(litr.next());
                litr.remove();
                break;
            } catch (IllegalArgumentException iae) {
                
            }
        }
        if(null == app) {
            return null;
        }
        
        if(Application.ICE_Device_Interface.equals(app)) {
            litr = args.listIterator();
            while(litr.hasNext()) {
                try {
                    String x = litr.next();
                    String[] y = x.split("\\=");
                    deviceType = DeviceType.valueOf(y[0]);
                    litr.remove();
                    if(y.length > 1) {
                        address = y[1];
                    }
                    break;
                } catch (IllegalArgumentException iae) {
                    
                }
            }
        }
        
        litr = args.listIterator();
        while(litr.hasNext()) {
            try {
                String x = litr.next();
                String[] y = x.split("\\=");
                binding = BindingFactory.BindingType.valueOf(y[0]);
                litr.remove();
                if(y.length > 1) {
                    bindingSettings = y[1];
                }
                break;
            } catch (IllegalArgumentException iae) {
                
            }
        }
       
        return new Configuration(app, binding, bindingSettings, deviceType, address);
    }
}