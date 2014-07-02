package org.mdpnp.apps.safetylockapplication;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.mdpnp.apps.safetylockapplication.Resources.AlarmOption;
import org.mdpnp.apps.safetylockapplication.Resources.OperatingMode;

@SuppressWarnings("serial")
public class SetAlarmDialog extends JDialog implements SetAlarmListener, OptionsPanelListener {
	
	private int defaultWidth = 900;
	private int defaultHeight = 400;
	private static Color black = Color.BLACK;
	
	private AlarmOption decision;
	private JPanel container = new JPanel();
		
	private ArrayList<SetAlarmListener> listeners = new ArrayList<SetAlarmListener>();
	AlarmOption lastClick = AlarmOption.UNDEFINED;
	
	public static void main(String[] args)
	{
		SetAlarmDialog dialog = new SetAlarmDialog(OperatingMode.PLETHYSMOGRAPH);
		AlarmOption decided = dialog.showDialog();
	}
	
	SetAlarmDialog(OperatingMode mode)
	{		
		setSize(defaultWidth, defaultHeight);
	    final Toolkit toolkit = Toolkit.getDefaultToolkit();
	    final Dimension screenSize = toolkit.getScreenSize();
	    final int x = (screenSize.width - defaultWidth) / 2;
	    final int y = (screenSize.height - defaultHeight) / 2;
	    setLocation(x, y);
	    setUndecorated(true);
	    getRootPane().setBorder(BorderFactory.createLineBorder(Color.white, 2, true));
	    getContentPane().setBackground(black);
	    container.setBackground(black);
	    container.setLayout(new BorderLayout());
	    add(container);
	    
	    RulePanel rulePanel = new RulePanel(this, mode);
	    rulePanel.setPreferredSize(new Dimension((defaultWidth/4)*3, defaultHeight));
		
	    SetAlarmOptionsPanel optionsPanel = new SetAlarmOptionsPanel(mode);
	    optionsPanel.setPreferredSize(new Dimension(defaultWidth/4, defaultHeight));
	    
	    optionsPanel.setOptionsPanelListener(rulePanel);
	    rulePanel.setSetAlarmListener(this);
	    rulePanel.setRulePanelListener(this);
	    //optionsPanel.setOptionsPanelListener(this);
	    
	    add(rulePanel, BorderLayout.CENTER);
	    add(optionsPanel, BorderLayout.EAST);
	    
	    setModal(true);
	}
	
	SetAlarmDialog(JFrame parent, SimulatedPatient patient, OperatingMode mode, int betterWidth, int betterHeight)
	{
		this(mode);
		defaultWidth = betterWidth;
		defaultHeight = betterHeight;
		setSize(defaultWidth, defaultHeight);
	    final Toolkit toolkit = Toolkit.getDefaultToolkit();
	    final Dimension screenSize = toolkit.getScreenSize();
	    final int x = (screenSize.width - defaultWidth) / 2;
	    final int y = (screenSize.height - defaultHeight) / 2;
	    setLocation(x, y);
	}
	
	public void addSetAlarmListener(SetAlarmListener listener)
	{
		this.listeners.add(listener);
	}
	
	public AlarmOption showDialog()
	{
		setVisible(true);
		return decision;
	}
	
	public void extendDefaults()
	{
		defaultWidth = defaultWidth + 10;
		defaultHeight = defaultHeight + 25;
		repaint();
	}

	@Override
	public void actionPerformed(SetAlarmEvent event) {
		for (SetAlarmListener listener : listeners)
		{
			listener.actionPerformed(event);
		}
	}

	@Override
	public void actionPerformed(AlarmOption optionEvent) {
		if (AlarmOption.CANCEL == optionEvent || AlarmOption.OK == optionEvent)
		{
			decision = optionEvent;
			dispose();
		}
	}
}