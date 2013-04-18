package org.mdpnp.apps.testapp;

import java.awt.GraphicsEnvironment;

import javax.swing.JOptionPane;

import org.mdpnp.rti.dds.DDS;

public class JumpStart {
	private enum Application {
	    DemoApp,
		DeviceAdapter,
		NetworkController
	}
	
	public static void main(String[] args) throws Exception {
		

//		Pointer logger = RTICLibrary.INSTANCE.NDDS_Config_Logger_get_instance();
//		RTICLibrary.INSTANCE.NDDS_Config_Logger_set_verbosity(logger, RTICLibrary.NDDS_CONFIG_LOG_VERBOSITY_STATUS_ALL);
//		RTICLibrary.INSTANCE.NDDS_Config_Logger_set_print_format(logger, RTICLibrary.NDDS_CONFIG_LOG_PRINT_FORMAT_MAXIMAL);
		
		
//		new Thread(new Runnable() {
//			public void run() {
//				while(true) {
//					Thread[] t = new Thread[Thread.currentThread().getThreadGroup().activeCount()*2];
//					int n = Thread.currentThread().getThreadGroup().enumerate(t, true);
//					try {
//						Thread.sleep(2000L);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}).start();
		if(!DDS.init()) {
			throw new RuntimeException();

		}
		Application app;
		if(args.length > 1) {
			app = Application.valueOf(args[0]);
			String[] new_args = new String[args.length - 1];
			System.arraycopy(args, 1, new_args, 0, args.length - 1);
			args = new_args;
		} else {
			app = (Application) JOptionPane.showInputDialog(null, "Application Type", "Choose an application type", JOptionPane.QUESTION_MESSAGE, null, Application.values(), null);
		}
		if(null != app) {
			switch(app) {
			case NetworkController:
				NetworkController.main(args);
				break;
			case DeviceAdapter:
				if(GraphicsEnvironment.isHeadless()) {
					VersionedDeviceAdapter.main(args);
				} else {
					DeviceAdapter.main(args);
				}
				break;
			case DemoApp:
			    DemoApp.main(args);
			    break;
			}
		} 
		
	}
}
