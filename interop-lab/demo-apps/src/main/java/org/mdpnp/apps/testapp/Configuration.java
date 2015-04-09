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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.mdpnp.devices.DeviceDriverProvider;
import org.mdpnp.devices.DeviceDriverProvider.DeviceType;
import org.mdpnp.devices.serial.SerialProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * The purpose of configuration object is to collect essential parameters either from the command line or
 * from UI dialog to define runtime configuration of the application. The following flow of control defines
 * the behavior of the component:
 *
 *  1.	If any arguments are passed via the command line, the application assumes them to define an
 *  environment and will start the application in the headless mode (i.e no gui).  If any configuration
 *  parameters are missing or undefined, the application will terminate with an error exit code.
 *
 *  2.	If there are no arguments on the command line, the system assumes a ‘gui’ mode and will
 *  present user with the configuration dialog to collect all necessary parameters.
 *
 *  The dialog is pre-populated with the configuration data from the previous runs. That data will be stored
 *  in the .JumpStartSettings file.  Current working directory is searched first and if not found, the user
 *  home directory.
 *
 *  Many system configuration parameters can be controlled/overridden via system environment variables.
 *  Some of those parameters had been exposed via command line arguments (which takes precedence over the
 *  environment). To pass these values into the application context one should use Configuration::createContext
 *  helper API when creating top-level spring application context. The api will install logic to ensure the proper
 *  search order for property resolution.
 *
 */
public class Configuration {

    enum Application {
        ICE_Supervisor(IceAppsContainer.class),
        ICE_Device_Interface(DeviceAdapterCommand.class);

        Application(Class<?> c) {
            clazz = c;
        }
        private Class<?> clazz;
        
        public Class<?> getAppClass() {
            return clazz;
        }
    }
    
    interface HeadlessCommand {
        int execute(Configuration config) throws Exception;
    }

    interface GUICommand {
        IceApplication create(Configuration config) throws Exception;
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

    public HeadlessCommand getCommand() {

        try {
            HeadlessCommand command = (HeadlessCommand) application.clazz.newInstance();
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

    /**
     * @param path
     * @return spring's application context. The point of this API is to insert a higher priority
     * property resolver into the context so that command line arguments could be used in property
     * resolution. Out of the box our spring xml configs wire property resolvers with 'order=1'
     * which functions just fine, but also allows for a 'order=0' to take over as a primary.
     */
    public AbstractApplicationContext createContext(String path)
    {
        ClassPathXmlApplicationContext ctx =
                new ClassPathXmlApplicationContext(new String[] { path }, false);

        Properties props = getCmdLineEnv();

        PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
        ppc.setIgnoreUnresolvablePlaceholders(true);
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

    @SuppressWarnings("static-access")
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

        // if mdpnp.ui is set to true, force the system to come up in the UI mode regardless of
        // command line having arguments or not. If not set, default to headless==true.
        //
        boolean headless=!Boolean.getBoolean("mdpnp.ui");
        return new Configuration(headless, app, domainId, deviceType, address);
    }

    public static Configuration searchAndLoadSettings(File[] fPath) throws IOException {

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


    public static void searchAndSaveSettings(Configuration runConf, File[] fPath) throws IOException {

        if (null == runConf)
            return;

        for(File f : fPath) {
            if(!f.exists()) {
                if(null != f.getParentFile() && f.getParentFile().exists() && f.getParentFile().canWrite()) {
                    f.createNewFile();
                }
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

    @SuppressWarnings("static-access")
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
