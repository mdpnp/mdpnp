package org.mdpnp.apps.safetylockapplication;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;

import org.mdpnp.apps.safetylockapplication.Resources.AlarmOption;
import org.mdpnp.apps.safetylockapplication.Resources.OperatingMode;

public class SetAlarmOptionsPanel extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	private ArrayList<OptionsPanelListener> listeners;
	
	private ImageButton cancel; 
	private ImageButton use;
	private ImageButton start;
	private ImageButton clear;
	private ImageButton newRule;
	OperatingMode mode;
	
	SetAlarmOptionsPanel(OperatingMode mode)
	{
		setLayout(new GridBagLayout());
		
		listeners = new ArrayList<OptionsPanelListener>();
		this.mode = mode;
		cancel = new ImageButton(Resources.loadImage("CancelButton.png"));
		clear = new ImageButton(Resources.loadImage("ClearRulesButton.png"));
		newRule = new ImageButton(Resources.loadImage("NewRuleButton.png"));
		
		if (mode == OperatingMode.PLETHYSMOGRAPH)
			use = new ImageButton(Resources.loadImage("PlethysmographDefaults.png"));
		else if (mode == OperatingMode.HEART_RATE_VS_PULSE_RATE)
			use = new ImageButton(Resources.loadImage("HrprDefaults.png"));
		else if (mode == OperatingMode.CAPNOGRAPH)
			use = new ImageButton(Resources.loadImage("CapnographDefaults.png"));
		
		start = new ImageButton(Resources.loadImage("StartButton.png"));
		
		cancel.addActionListener(this);
		clear.addActionListener(this);
		use.addActionListener(this);
		start.addActionListener(this);
		newRule.addActionListener(this);
		
		setBackground(Resources.physiologicalDisplayPanelBackgroundColor);
		
		setLayout();
	}
	
	public void setOptionsPanelListener(OptionsPanelListener listener)
	{
		listeners.add(listener);
	}
	
	private void setLayout()
	{
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 1;
		gc.fill = GridBagConstraints.NONE;
		//gc.anchor = GridBagConstraints.LAST_LINE_START;
		add(use, gc);
		
		gc.gridy = 1;
		add(clear, gc);
		
		gc.gridy = 2;
		add(newRule, gc);
		
		gc.gridy = 3;
		add(start, gc);
		
		gc.gridy = 4;
		add(cancel, gc);
		
	}

	@Override
	public void actionPerformed(ActionEvent button) {
		
		if (button.getSource() == start)
		{
			for (OptionsPanelListener listener : listeners)
			{
				AlarmOption event = AlarmOption.OK;
				listener.actionPerformed(event);
			}
		}
		else if (button.getSource() == clear)
		{
			for (OptionsPanelListener listener : listeners)
			{
				AlarmOption event = AlarmOption.CLEAR;
				listener.actionPerformed(event);
			}
		}
		else if (button.getSource() == newRule)
		{
			for (OptionsPanelListener listener : listeners)
			{
				AlarmOption event = AlarmOption.NEW;
				listener.actionPerformed(event);
			}
		}
		else if (button.getSource() == cancel)
		{
			for (OptionsPanelListener listener : listeners)
			{
				AlarmOption event = AlarmOption.CANCEL;
				listener.actionPerformed(event);
			}
		}
		else if (button.getSource() == use)
		{
			for (OptionsPanelListener listener : listeners)
			{
				AlarmOption event = AlarmOption.DEFAULT;
				listener.actionPerformed(event);
			}
		}
	}
	
}
