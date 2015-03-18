package org.mdpnp.apps.testapp;

import java.awt.Image;
import java.io.File;
import java.lang.reflect.Method;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.stage.Stage;

public class MainApplication extends javafx.application.Application {

    private Configuration runConf;
    private IceApplication app;
    
    private static final Logger log = LoggerFactory.getLogger(MainApplication.class);
   

    private final static File[] searchPath = new File [] {
        new File(".JumpStartSettings"),
        new File(System.getProperty("user.home"), ".JumpStartSettings")
    };
    
    @Override
    public void start(Stage primaryStage) throws Exception {        
        try {
            Class<?> cls = Class.forName("com.apple.eawt.Application");
            Method m1 = cls.getMethod("getApplication");
            Method m2 = cls.getMethod("setDockIconImage", Image.class);
            m2.invoke(m1.invoke(null), ImageIO.read(Main.class.getResource("icon.png")));
        } catch (Throwable t) {
            log.debug("Not able to set Mac OS X dock icon");
        }

        runConf = Configuration.searchAndLoadSettings(searchPath);
        
        ConfigurationDialog d = ConfigurationDialog.showDialog(runConf);
//        d.setIconImage(ImageIO.read(Main.class.getResource("icon.png")));
//        runConf = d.showDialog();

        // It's nice to be able to change settings even without running
        // Even if the user presses 'quit' save the state so that it can be used
        // to boot strap the dialog later.
        //
        if (d.getQuitPressed()) {
            Configuration c = d.getLastConfiguration();
            Configuration.searchAndSaveSettings(c, searchPath);
            runConf = null;
            Platform.exit();
        } else {
            runConf = d.getLastConfiguration();
            Object o = runConf.getApplication().getAppClass().newInstance();
            if(o instanceof IceApplication) {
                app = (IceApplication) o;
                app.setConfiguration(runConf);
                app.init();
                app.start(primaryStage);
            } else if(o instanceof Configuration.Command) {
                ((Configuration.Command)o).execute(runConf);
            }
        }
    }
    
    @Override
    public void stop() throws Exception {
        super.stop();
        if(null != app) {
            app.stop();
            app = null;
        }
    }

}
