package org.mdpnp.apps.safetylockapplication;

import javax.swing.SwingUtilities;


public class Main {
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				SafetyLockApplicationFrame safetyLockAppFrame = new SafetyLockApplicationFrame("Safety Lock Application");
				ControllerFrame controlFrame = new ControllerFrame();
				controlFrame.addControllerFrameListener(safetyLockAppFrame);
				safetyLockAppFrame.addCommandListener(controlFrame);
				safetyLockAppFrame.addSetAlarmListener(controlFrame);
			}
			
		});	

	}

}
