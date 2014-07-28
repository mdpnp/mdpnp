package org.mdpnp.apps.safetylockapplication;

import javax.swing.SwingUtilities;

import org.mdpnp.apps.testapp.DemoFrame;


public class Main {
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
			    DemoFrame.setQuitStrategy("CLOSE_ALL_WINDOWS");
				SafetyLockApplicationFrame safetyLockAppFrame = new SafetyLockApplicationFrame("Safety Lock Application");
				DemoFrame.setWindowCanFullScreen(true, safetyLockAppFrame);
				ControllerFrame controlFrame = new ControllerFrame();
				controlFrame.addControllerFrameListener(safetyLockAppFrame);
				safetyLockAppFrame.addCommandListener(controlFrame);
				safetyLockAppFrame.addSetAlarmListener(controlFrame);
			}
			
		});	

	}

}
