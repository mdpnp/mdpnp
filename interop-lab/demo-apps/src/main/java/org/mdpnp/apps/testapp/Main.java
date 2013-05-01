package org.mdpnp.apps.testapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.mdpnp.messaging.Binding;
import org.mdpnp.messaging.BindingFactory;
import org.mdpnp.messaging.BindingFactory.BindingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	
	
	private static final Logger log = LoggerFactory.getLogger(Main.class);
	public static void main(String[] args) throws Exception {
	    System.setProperty("java.net.preferIPv4Stack","true");

	    Configuration conf = null;
	    
	    File jumpStartSettings = new File(".JumpStartSettings");
	    
	    boolean cmdline = false;
	    
	    if(args.length > 0) {
	        conf = Configuration.read(args);
	        cmdline = true;
	    } else if(jumpStartSettings.exists() && jumpStartSettings.canRead()) {
	        FileInputStream fis = new FileInputStream(jumpStartSettings);
	        conf = Configuration.read(fis);
	        fis.close();
	    }


		if(!cmdline) {
		    ConfigurationDialog d = new ConfigurationDialog(conf);
		    conf = d.showDialog();
		    d.dispose();
		} else {
		    // fall through to allow configuration via a file
		}
		if(null != conf) {
		    if(!jumpStartSettings.exists()) {
		        jumpStartSettings.createNewFile();
		    }
		    
		    
		    if(jumpStartSettings.canWrite()) {
		        FileOutputStream fos = new FileOutputStream(jumpStartSettings);
		        conf.write(fos);
		        fos.close();
		    }
		    
		    
		    BindingType binding = conf.getBinding();
		    String bindingSettings = conf.getBindingSettings();
		    
		    switch(binding) {
		    case RTI_DDS:
		        try {
        	        if(!(Boolean)Class.forName("org.mdpnp.rti.dds.DDS").getMethod("init").invoke(null)) {
        	            throw new Exception("Unable to init");
        	        }
		        } catch (Throwable t) {
		            log.warn("Unable to initialize RTI DDS, falling back to JGroups transport", t);
		            binding = BindingType.JGROUPS;
		            bindingSettings = "";
		        }
		        break;
		        
		    }

			switch(conf.getApplication()) {
			case DeviceAdapter:
			    DeviceAdapter.start(conf.getDeviceType(), binding, bindingSettings, conf.getAddress(), !cmdline);
				break;
			case DemoApp:
			    DemoApp.start(binding, bindingSettings);
			    break;
			}
		} else if(cmdline) {
		    Configuration.help(Main.class, System.out);
		}
		
	}
}
