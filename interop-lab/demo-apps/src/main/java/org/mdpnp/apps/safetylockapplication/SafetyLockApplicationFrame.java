package org.mdpnp.apps.safetylockapplication;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JFrame;

public class SafetyLockApplicationFrame extends JFrame implements ControllerFrameListener, CommandListener, SetAlarmListener {

	private static final long serialVersionUID = 1L;
	private static final int applicationDefaultWidth = 1200;
	private static final int applicationDefaultHeight = 600;
	SafetyLockScreen safetyLockScreen;

	ArrayList<CommandListener> listeners;
	ArrayList<SetAlarmListener> alarmListeners;
	
	public SafetyLockApplicationFrame(String title)
	{
		super(title);
		
		listeners = new ArrayList<CommandListener>();
		alarmListeners = new ArrayList<SetAlarmListener>();
		
		setSize(new Dimension(applicationDefaultWidth, applicationDefaultHeight));
		int useWidth = applicationDefaultWidth;
		int useHeight = applicationDefaultHeight;
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		final Toolkit toolkit = Toolkit.getDefaultToolkit();
	    final Dimension screenSize = toolkit.getScreenSize();
	    final int x = (screenSize.width - useWidth) / 2;
	    final int y = (screenSize.height - useHeight) / 2;
	    setLocation(x, y);

		setLayout(new GridLayout(1,1));
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		safetyLockScreen = new SafetyLockScreen();
		safetyLockScreen.addCommandListener(this);
		safetyLockScreen.addSetAlarmListener(this);
		
		
		add(safetyLockScreen);
	}
	
	public void addSetAlarmListener(SetAlarmListener listener)
	{
		alarmListeners.add(listener);
	}
	
	public void addCommandListener(CommandListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public void actionPerformed(DeviceEvent e) {
		safetyLockScreen.passDeviceEvent(e);
	}

	@Override
	public void actionPerformed(CommandEvent e) {
		for (CommandListener listener : listeners)
			listener.actionPerformed(e);
		
	}

	@Override
	public void actionPerformed(SetAlarmEvent event) {
		for (SetAlarmListener listener : alarmListeners)
			listener.actionPerformed(event);
		
	}
}
