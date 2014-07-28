package org.mdpnp.apps.safetylockapplication;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.mdpnp.apps.safetylockapplication.Resources.OperatingMode;

public class SafetyLockScreen extends JPanel implements ActionListener, CommandListener, SetAlarmListener {
	private static final long serialVersionUID = 1L;
	
	private ResizeableLabelPanel safetyLockScreenTitlePanel;
	private SafetyLockScreenMainPanel safetyLockScreenMainPanel;
	
	private JPanel safetyLockScreenModeSelectionPanel;
	//the buttons to be added to safetyLockScreenModeSelectionPanel ...
	private ImageButton plethysmographButton;
	//private JButton heartRateVsPulseRateButton;
	private ImageButton heartRateVsPulseRateButton;
	private ImageButton manualButton;
	private ImageButton capnographButton;

	private final Dimension safetyLockScreenTitlePanelSize = new Dimension(40, 40);
	private final Dimension safetyLockScreenModeSelectionPanelSize = new Dimension(50, 50);
	private final Dimension modeButtonSize = new Dimension(200, 50);
	private final Dimension modeSelectLabelSize = new Dimension(75, 50);
	private final Font modeButtonFont = new Font("Sans", Font.PLAIN, 20);
	private final int buttonHorizontalSeparation = 10;
	
	private OperatingMode modeOfOperation;
	ArrayList<CommandListener> listeners;
	ArrayList<SetAlarmListener> AlarmListeners;

	public SafetyLockScreen()
	{
		listeners = new ArrayList<CommandListener>();
		AlarmListeners = new ArrayList<SetAlarmListener>();
		
		modeOfOperation = OperatingMode.UNSPECIFIED;
		
		//TITLE PANEL SETUP
		safetyLockScreenTitlePanel = new ResizeableLabelPanel("MDPnP Safety Lock Screen", null);
		safetyLockScreenTitlePanel.setSize(safetyLockScreenTitlePanelSize);
		safetyLockScreenTitlePanel.setBackground(Resources.physiologicalDisplayPanelBackgroundColor);
		//... reuse of this color promotes consistent look and feel
		safetyLockScreenTitlePanel.setTextColor(Resources.standardTextColor);
		
		//MAIN PANEL SETUP
		safetyLockScreenMainPanel = new SafetyLockScreenMainPanel();
		safetyLockScreenMainPanel.addCommandListener(this);
		safetyLockScreenMainPanel.addSetAlarmListener(this);
		
		//SELECTION PANEL SETUP
		safetyLockScreenModeSelectionPanel = new JPanel();
		safetyLockScreenModeSelectionPanel.setSize(safetyLockScreenModeSelectionPanelSize);
		safetyLockScreenModeSelectionPanel.setPreferredSize(safetyLockScreenModeSelectionPanelSize);
		safetyLockScreenModeSelectionPanel.setBackground(Resources.physiologicalDisplayPanelBackgroundColor);
		safetyLockScreenModeSelectionPanel.setLayout(new FlowLayout(FlowLayout.CENTER, buttonHorizontalSeparation, 0));
		
		//SELECTION PANEL CONTENT SETUP
		ResizeableLabelPanel modeSelectLabel = new ResizeableLabelPanel("Mode : ", modeSelectLabelSize);
		modeSelectLabel.setBackground(Resources.physiologicalDisplayPanelBackgroundColor);
		modeSelectLabel.setTextColor(Resources.standardTextColor);
		
		manualButton = new ImageButton(Resources.loadImage("ManualButtonInactive.png"));
		plethysmographButton = new ImageButton(Resources.loadImage("PlethysmographButtonInactive.png"));
		heartRateVsPulseRateButton = new ImageButton(Resources.loadImage("HrprButtonInactive.png"));
		capnographButton = new ImageButton(Resources.loadImage("CapnographButtonInactive.png"));
		
		safetyLockScreenModeSelectionPanel.add(modeSelectLabel);
		safetyLockScreenModeSelectionPanel.add(manualButton);
		safetyLockScreenModeSelectionPanel.add(plethysmographButton);
		safetyLockScreenModeSelectionPanel.add(heartRateVsPulseRateButton);
		safetyLockScreenModeSelectionPanel.add(capnographButton);
		
		capnographButton.addActionListener(this);
		heartRateVsPulseRateButton.addActionListener(this);
		plethysmographButton.addActionListener(this);
		manualButton.addActionListener(this);
		
		setLayout(new GridBagLayout());
		setupGridDisplay();
	}
	
	public void addCommandListener(CommandListener listener)
	{
		listeners.add(listener);
	}
	
	public void addSetAlarmListener(SetAlarmListener listener)
	{
		AlarmListeners.add(listener);
	}
	
	public void setupGridDisplay()
	{
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.anchor = GridBagConstraints.PAGE_START;
		
		add(safetyLockScreenTitlePanel, gc);
		
		gc.weighty = 1;
		gc.fill = GridBagConstraints.BOTH;
		gc.gridy = 1;
		
		add(safetyLockScreenMainPanel, gc);
		
		gc.weighty = 0;
		gc.gridy = 2;
		
		add(safetyLockScreenModeSelectionPanel, gc);
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object object = e.getSource();
		if (!safetyLockScreenMainPanel.isLockActive())
		{
			if (object == plethysmographButton)
			{
				safetyLockScreenMainPanel.updateModeOfOperation(OperatingMode.PLETHYSMOGRAPH);
				manualButton.updateImage(Resources.loadImage("ManualButtonInactive.png"));
				plethysmographButton.updateImage(Resources.loadImage("PlethysmographButtonActive.png"));
				heartRateVsPulseRateButton.updateImage(Resources.loadImage("HrprButtonInactive.png"));
				capnographButton.updateImage(Resources.loadImage("CapnographButtonInactive.png"));
			}
			if (object == manualButton)
			{
				safetyLockScreenMainPanel.updateModeOfOperation(OperatingMode.MANUAL);
				manualButton.updateImage(Resources.loadImage("ManualButtonActive.png"));
				plethysmographButton.updateImage(Resources.loadImage("PlethysmographButtonInactive.png"));
				heartRateVsPulseRateButton.updateImage(Resources.loadImage("HrprButtonInactive.png"));
				capnographButton.updateImage(Resources.loadImage("CapnographButtonInactive.png"));
			}
			if (object == heartRateVsPulseRateButton)
			{
				safetyLockScreenMainPanel.updateModeOfOperation(OperatingMode.HEART_RATE_VS_PULSE_RATE);
				manualButton.updateImage(Resources.loadImage("ManualButtonInactive.png"));
				plethysmographButton.updateImage(Resources.loadImage("PlethysmographButtonInactive.png"));
				heartRateVsPulseRateButton.updateImage(Resources.loadImage("HrprButtonActive.png"));
				capnographButton.updateImage(Resources.loadImage("CapnographButtonInactive.png"));
			}
			if (object == capnographButton)
			{
				safetyLockScreenMainPanel.updateModeOfOperation(OperatingMode.CAPNOGRAPH);
				manualButton.updateImage(Resources.loadImage("ManualButtonInactive.png"));
				plethysmographButton.updateImage(Resources.loadImage("PlethysmographButtonInactive.png"));
				heartRateVsPulseRateButton.updateImage(Resources.loadImage("HrprButtonInactive.png"));
				capnographButton.updateImage(Resources.loadImage("CapnographButtonActive.png"));
			}
		}
	}

	public void passDeviceEvent(DeviceEvent deviceEvent) {
		safetyLockScreenMainPanel.handleDeviceEvent(deviceEvent);
	}

	@Override
	public void actionPerformed(CommandEvent e) {
		for (CommandListener listener : listeners)
			listener.actionPerformed(e);
		
	}

	@Override
	public void actionPerformed(SetAlarmEvent event) {
		
		for (SetAlarmListener listener : AlarmListeners)
			listener.actionPerformed(event);
		
	}
}
