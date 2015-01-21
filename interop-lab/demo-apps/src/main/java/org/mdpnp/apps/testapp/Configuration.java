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

import org.apache.commons.cli.*;
import org.mdpnp.devices.DeviceDriverProvider;
import org.mdpnp.devices.serial.SerialProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import org.mdpnp.devices.DeviceDriverProvider.DeviceType;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class Configuration {

    enum Application {
        ICE_Supervisor(IceAppsContainer.class),
        ICE_Device_Interface(DeviceAdapter.DeviceAdapterCommand.class),
        ICE_ParticipantOnly(ParticipantOnly.class);

        Application(Class c) {
            clazz = c;
        }
        private Class clazz;
    }

    interface Command {
        int execute(Configuration config) throws Exception;
    }

    private final boolean              headless;
    private final Application          application;
    private final DeviceDriverProvider deviceFactory;
    private final String               address;
    private final int                  domainId;
    private final Properties           cmdLineEnv = new Properties();

    public Configuration(boolean headless, Application application, int domainId, DeviceDriverProvider deviceFactory, String address) {
        this.headless = headless;
        this.deviceFactory = deviceFactory;
        this.address = address;
        this.domainId = domainId;
        this.application = application;

        cmdLineEnv.put("mdpnp.domain", Integer.toString(domainId));
    }

    public boolean isHeadless()
    {
        return headless;
    }

    public Properties getCmdLineEnv() {
        return cmdLineEnv;
    }

    public Command getCommand() {

        try {
            Command command = (Command) application.clazz.newInstance();
            return command;
        }
        catch(Exception ex)
        {
            throw new IllegalStateException("Failed to create command instance " + application.clazz.getName());
        }
    }

    public Application getApplication() {
        return application;
    }

    public int getDomainId() {
        return domainId;
    }

    public DeviceDriverProvider getDeviceFactory() {
        return deviceFactory;
    }

    public String getAddress() {
        return address;
    }

    private static final String APPLICATION = "application";
    private static final String DOMAIN_ID   = "domainId";
    private static final String DEVICE_TYPE = "deviceType";
    private static final String ADDRESS     = "address";

    private final static Logger log = LoggerFactory.getLogger(Configuration.class);

    public AbstractApplicationContext createContext(String path)
    {
        ClassPathXmlApplicationContext ctx =
                new ClassPathXmlApplicationContext(new String[] { path }, false);

        Properties props = getCmdLineEnv();

        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ppc.setProperties(props);
        ppc.setOrder(0);

        ctx.addBeanFactoryPostProcessor(ppc);
        ctx.refresh();
        return ctx;
    }

    private void write(PrintWriter os) throws IOException {
        Properties p = new Properties();
        p.setProperty(APPLICATION, application.name());
        p.setProperty(DOMAIN_ID,   Integer.toString(domainId));
        if (null != deviceFactory)
            p.setProperty(DEVICE_TYPE, deviceFactory.getDeviceType().getAlias());
        if (null != address)
            p.setProperty(ADDRESS, address);

        p.list(os);
    }

    static Configuration read(InputStream is) throws IOException {

        Properties p = new Properties();
        p.load(is);

        Application app = null;
        int domainId = 0;
        DeviceDriverProvider deviceType = null;
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
                deviceType = DeviceFactory.getDeviceDriverProvider(s);
            } catch (IllegalArgumentException iae) {
                log.warn("Ignoring unknown device type:" + s);
            }
        }
        if(p.containsKey(ADDRESS)) {
            address = p.getProperty(ADDRESS);
        }

        return new Configuration(false, app, domainId, deviceType, address);
    }

    static Configuration read(String[] cmdLineArgs) throws Exception{

        StringBuilder as = new StringBuilder("Application may be one of:");
        for (Application a : Application.values()) {
            as.append("\t" + a.name());
        }
        Option appArg = OptionBuilder.withArgName("app")
                .hasArg()
                .isRequired(true)
                .withDescription(as.toString())
                .create("app");

        Option domainArg = OptionBuilder.withArgName("domain")
                .hasArg()
                .isRequired(true)
                .withDescription("DDS domain identifier")
                .create("domain");

        StringBuilder ds = new StringBuilder();
        ds.append("if Application is ").append(Application.ICE_Device_Interface.name()).append(" then DeviceType may be one of:");
        DeviceDriverProvider[] all =  DeviceFactory.getAvailableDevices();
        for (DeviceDriverProvider ddp : all) {
            DeviceType d=ddp.getDeviceType();
            ds.append("\t").append(ice.ConnectionType.Serial.equals(d.getConnectionType()) ? "*" : "").append(d.getAlias());
        }
        Option deviceArg = OptionBuilder.withArgName("device")
                .hasArg()
                .isRequired(false)
                .withDescription(ds.toString())
                .create("device");

        StringBuilder ps = new StringBuilder();
        ps.append("DeviceTypes marked with * are serial devices which require port specification.");
        List<String> l = SerialProviderFactory.getDefaultProvider().getPortNames();
        if(!l.isEmpty()) {
            ps.append(" The following address values are currently valid:");
            for (String s : l) {
                ps.append("\t" + s);
            }
        }
        Option addressArg = OptionBuilder.withArgName("address")
                .hasArg()
                .isRequired(false)
                .withDescription(ps.toString())
                .create("address");

        Options options = new Options();
        options.addOption( appArg );
        options.addOption( domainArg );
        options.addOption( deviceArg );
        options.addOption( addressArg );

        CommandLine line = parseCommandLine("ICE", cmdLineArgs, options);
        if(line == null)
            return null;

        Application app = null;
        int domainId = 0;
        DeviceDriverProvider deviceType = null;
        String address = null;

        String v = line.getOptionValue("app");
        try {
            app = Application.valueOf(v);
        }catch (Exception e) {
            throw new IllegalArgumentException("Invalid app name: " + v );
        }

        v = line.getOptionValue("domain");
        domainId = Integer.parseInt(v);
        if (Application.ICE_Device_Interface.equals(app)) {
            if(!line.hasOption("device"))
                throw new IllegalArgumentException("Missing device specification");
            v = line.getOptionValue("device");
            deviceType = DeviceFactory.getDeviceDriverProvider(v);

            if(ice.ConnectionType.Serial.equals(deviceType.getDeviceType().getConnectionType())) {
                if(!line.hasOption("address"))
                    throw new IllegalArgumentException("Missing address specification");
                address = line.getOptionValue("address");
            }
        }

        boolean headless=!Boolean.getBoolean("mdpnp.ui");
        return new Configuration(headless, app, domainId, deviceType, address);
    }


    public static Configuration getInstance(String[] args) throws Exception {

        File[] searchPath = new File [] {
                new File(".JumpStartSettings"),
                new File(System.getProperty("user.home"), ".JumpStartSettings")
        };

        Configuration runConf;

        if (args.length > 0) {
            runConf = read(args);
        }
        else {

            try {
                Class<?> cls = Class.forName("com.apple.eawt.Application");
                Method m1 = cls.getMethod("getApplication");
                Method m2 = cls.getMethod("setDockIconImage", Image.class);
                m2.invoke(m1.invoke(null), ImageIO.read(Main.class.getResource("icon.png")));
            } catch (Throwable t) {
                log.debug("Not able to set Mac OS X dock icon");
            }

            runConf = searchAndLoadSettings(searchPath);

            ConfigurationDialog d = new ConfigurationDialog(runConf, null);

            d.setIconImage(ImageIO.read(Main.class.getResource("icon.png")));
            runConf = d.showDialog();

            // It's nice to be able to change settings even without running
            // Even if the user presses 'quit' save the state so that it can be used
            // to boot strap the dialog later.
            //
            if (null == runConf) {
                Configuration c = d.getLastConfiguration();
                searchAndSaveSettings(c, searchPath);
            }
        }

        if(runConf != null)
            searchAndSaveSettings(runConf, searchPath);

        return runConf;

    }

    private static Configuration searchAndLoadSettings(File[] fPath) throws IOException {

        for(File f : fPath) {
            if(f.exists() && f.canRead()) {
                log.debug("Reading jumpStartSettings from " + f.getAbsolutePath());
                FileInputStream fis = new FileInputStream(f);
                Configuration runConf = Configuration.read(fis);
                fis.close();
                return runConf;
            }
        }
        return null;
    }


    private static void searchAndSaveSettings(Configuration runConf, File[] fPath) throws IOException {

        if (null == runConf)
            return;

        for(File f : fPath) {
            if(!f.exists() && f.getParentFile().exists() && f.getParentFile().canWrite()) {
                f.createNewFile();
            }
            if(f.exists() && f.canWrite()) {
                log.debug("Saving jumpStartSettings from " + f.getAbsolutePath());
                PrintWriter fos = new PrintWriter(f);
                runConf.write(fos);
                fos.close();
                break;
            }
        }
    }

    public static CommandLine parseCommandLine(String execName, String args[], Options options) throws ParseException
    {
        Option h = OptionBuilder.withDescription("display usage information").create("help");

        Options help = new Options();
        help.addOption( h );

        CommandLineParser parser = new GnuParser();

        CommandLine line = parser.parse( help, args, true);
        if (line.hasOption("help"))
        {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(execName, options);
            return null;
        }

        // now parse for real.
        line = parser.parse( options, args);
        return line;
    }

}
