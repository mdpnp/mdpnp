package org.mdpnp.apps.testapp;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.domain.DomainParticipantFactory;
import com.rti.dds.domain.DomainParticipantFactoryQos;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        // TODO this should be external
        System.setProperty("java.net.preferIPv4Stack","true");
        final boolean debug = false;

        Configuration runConf = null;

        File jumpStartSettings = new File(".JumpStartSettings");
        File jumpStartSettingsHome = new File(System.getProperty("user.home"), ".JumpStartSettings");

        boolean cmdline = false;

        if(args.length > 0) {
            runConf = Configuration.read(args);
            cmdline = true;
        } else if(jumpStartSettings.exists() && jumpStartSettings.canRead()) {
            FileInputStream fis = new FileInputStream(jumpStartSettings);
            runConf = Configuration.read(fis);
            fis.close();
        } else if(jumpStartSettingsHome.exists() && jumpStartSettingsHome.canRead()) {
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

        if(!cmdline) {
            ConfigurationDialog d = new ConfigurationDialog(runConf, debug);

            d.setIconImage(ImageIO.read(Main.class.getResource("icon.png")));
            runConf = d.showDialog();
            // It's nice to be able to change settings even without running
            if(null == runConf) {
                writeConf = d.getLastConfiguration();
            }
        } else {
            // fall through to allow configuration via a file
        }

        if(null != runConf) {
            writeConf = runConf;
        }

        if(null != writeConf) {
            if(!jumpStartSettings.exists()) {
                jumpStartSettings.createNewFile();
            }

            if(jumpStartSettings.canWrite()) {
                FileOutputStream fos = new FileOutputStream(jumpStartSettings);
                writeConf.write(fos);
                fos.close();
            }

            if(!jumpStartSettingsHome.exists()) {
                jumpStartSettingsHome.createNewFile();
            }

            if(jumpStartSettingsHome.canWrite()) {
                FileOutputStream fos = new FileOutputStream(jumpStartSettingsHome);
                writeConf.write(fos);
                fos.close();
            }
        }

        if(null != runConf) {
            if(!(Boolean)Class.forName("org.mdpnp.rti.dds.DDS").getMethod("init", boolean.class).invoke(null, debug)) {
                throw new Exception("Unable to DDS.init");
            }
            {
                    DomainParticipantFactoryQos qos = new DomainParticipantFactoryQos();
                    DomainParticipantFactory.get_instance().get_qos(qos);
                    qos.resource_limits.max_objects_per_thread = 8192;
                    DomainParticipantFactory.get_instance().set_qos(qos);
            }

            switch(runConf.getApplication()) {
            case ICE_Device_Interface:
                new DeviceAdapter().start(runConf.getDeviceType(), runConf.getDomainId(), runConf.getAddress(), !cmdline);
                break;
            case ICE_Supervisor:
                DemoApp.start(runConf.getDomainId());
                break;
            }
        } else if(cmdline) {
            Configuration.help(Main.class, System.out);
        } else {

        }
        log.trace("This is the end of Main");
    }
}
