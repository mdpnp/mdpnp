package org.mdpnp.apps.safetylockapplication;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.mdpnp.apps.safetylockapplication.Resources.AlarmOption;
import org.mdpnp.apps.safetylockapplication.Resources.Algorithm;
import org.mdpnp.apps.safetylockapplication.Resources.Command;
import org.mdpnp.apps.safetylockapplication.Resources.DisplayMode;
import org.mdpnp.apps.safetylockapplication.Resources.Hand;
import org.mdpnp.apps.safetylockapplication.Resources.OperatingMode;
import org.mdpnp.apps.safetylockapplication.Resources.YesNoResponse;

public class SafetyLockScreenMainPanel extends JPanel implements MouseListener, SetAlarmListener {
	
	private static final long serialVersionUID = 1L;
	
	private PhysiologicalDisplayPanel o2Saturation;
	private PhysiologicalDisplayPanel pulseRate;
	private PhysiologicalDisplayPanel plethysmograph;
	private PhysiologicalDisplayPanel heartRate;
	private PhysiologicalDisplayPanel co2Saturation;
	private PhysiologicalDisplayPanel respirationRate;
	private PhysiologicalDisplayPanel pumpStatus;
	private PhysiologicalDisplayPanel pumpCommand;

	private ImageButton lockButton;
	//private boolean lockPress = false;
	private boolean lockIsActive = false;
	
	private OperatingMode modeOfOperation;
	private static final int PIXEL_SEPARATION = 10;
	
	private ArrayList<CommandListener> CommandListeners = new ArrayList<CommandListener>();
	private ArrayList<SetAlarmListener> AlarmListeners = new ArrayList<SetAlarmListener>();
	
	private Command lastCommand;
	private SetAlarmEvent alarmSetting;
	private DeviceEvent lastDeviceEvent;
	
	public SafetyLockScreenMainPanel()
	{	
		o2Saturation = new PhysiologicalDisplayPanel("O2 Saturation");
		pulseRate = new PhysiologicalDisplayPanel("Pulse Rate");
		
		plethysmograph = new PhysiologicalDisplayPanel("Plethysmograph", DisplayMode.QUALITATIVE);
		ArrayList<String> plethGood = new ArrayList<String>();
		plethGood.add("good");
		ArrayList<String> plethMediocre = new ArrayList<String>();
		plethMediocre.add("bad");
		plethysmograph.setGoodQualityDescriptors(plethGood);
		plethysmograph.setMediocreQualityDescriptors(plethMediocre);
		
		heartRate = new PhysiologicalDisplayPanel("Heart Rate");
		co2Saturation = new PhysiologicalDisplayPanel("CO2 Saturation");
		respirationRate = new PhysiologicalDisplayPanel("Respiration Rate");
		
		pumpStatus = new PhysiologicalDisplayPanel("Pump Status", DisplayMode.QUALITATIVE);
		ArrayList<String> pumpGood = new ArrayList<String>();
		pumpGood.add("interlock");
		pumpStatus.setGoodQualityDescriptors(pumpGood);
		
		pumpCommand = new PhysiologicalDisplayPanel("Pump Command", DisplayMode.QUALITATIVE);
		ArrayList<String> commandGood = new ArrayList<String>();
		commandGood.add("Stop");
		commandGood.add("Start");
		commandGood.add("Ready");
		pumpCommand.setGoodQualityDescriptors(commandGood);
		
		lockButton = new ImageButton(Resources.loadImage("InitialInactiveLock.png"));
		lockButton.addMouseListener(this);
		
		modeOfOperation = OperatingMode.UNSPECIFIED;
		setBackground(Color.BLACK);
		setLayout(new GridLayout(1, 3));
		setupGridDisplay();
		
		alarmSetting = new SetAlarmEvent(this);
		alarmSetting.plethAlg = Algorithm.ALPHA;
	}
	
	public boolean isLockActive()
	{
		return lockIsActive;
	}
	
	public void addCommandListener(CommandListener listener)
	{
		CommandListeners.add(listener);
	}
	
	public void addSetAlarmListener(SetAlarmListener listener)
	{
		AlarmListeners.add(listener);
	}
	
	public void updateModeOfOperation(OperatingMode mode)
	{
		modeOfOperation = mode;
		
		if (modeOfOperation == OperatingMode.MANUAL)
			lockButton.updateImage(Resources.loadImage("InactiveLockManual.png"));
		else if (modeOfOperation == OperatingMode.PLETHYSMOGRAPH)
			lockButton.updateImage(Resources.loadImage("InactiveLockPlethysmograph.png"));
		else if (modeOfOperation == OperatingMode.HEART_RATE_VS_PULSE_RATE)
			lockButton.updateImage(Resources.loadImage("InactiveLockHeartRateVsPulseRate.png"));
		else if (modeOfOperation == OperatingMode.CAPNOGRAPH)
			lockButton.updateImage(Resources.loadImage("InactiveLockCapnograph.png"));
		
		repaint();
	}
	
	private void setupGridDisplay()
	{
		JPanel leftSidePanel = new JPanel();
		leftSidePanel.setBackground(Color.BLACK);
		leftSidePanel.setLayout(new GridBagLayout());
		
		GridBagConstraints leftSide = new GridBagConstraints();
		
		leftSide.gridx = 0;
		leftSide.gridy = 0;
		leftSide.weightx = 1;
		leftSide.weighty = 1;
		
		leftSidePanel.add(o2Saturation, leftSide);
		
		leftSide.gridy++;
		leftSidePanel.add(pulseRate, leftSide);
		
		leftSide.gridy++;
		leftSidePanel.add(plethysmograph, leftSide);
		
		leftSide.gridy++;
		leftSidePanel.add(heartRate, leftSide);
		
		leftSide.gridy++;
		leftSidePanel.add(co2Saturation, leftSide);
		
		leftSide.gridy++;
		leftSidePanel.add(respirationRate,leftSide);
		
		leftSide.gridy++;
		leftSidePanel.add(pumpStatus, leftSide);
		
		//////////////////////////////////////////////
		
		JPanel middlePanel = new JPanel();
		middlePanel.setBackground(Color.BLACK);
		middlePanel.setLayout(new GridBagLayout());
		
		GridBagConstraints middle = new GridBagConstraints();
		
		middle.gridx = 0;
		middle.gridy = 0;
		middle.weightx = 1;
		middle.weighty = 1;
		
		middlePanel.add(lockButton, middle);
		
		//////////////////////////////////////////////
		
		JPanel rightSidePanel = new JPanel();
		rightSidePanel.setBackground(Color.BLACK);
		rightSidePanel.setLayout(new GridBagLayout());
		
		GridBagConstraints right = new GridBagConstraints();
		
		middle.gridx = 0;
		middle.gridy = 0;
		middle.weightx = 1;
		middle.weighty = 1;
		
		rightSidePanel.add(pumpCommand, right);
		
		add(leftSidePanel);
		add(middlePanel);
		add(rightSidePanel);
	}
	
	
	//ARROW VISUALS
	
	public void paint(Graphics g)
	{
		super.paint(g);
        Graphics2D pen = (Graphics2D) g;
        pen.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        pen.setStroke(new BasicStroke(2));
        
        if (modeOfOperation == OperatingMode.PLETHYSMOGRAPH)
        {
        	Point rightDrawPoint = getConnectionPoint(lockButton, Hand.LEFT);
        	
        	Point leftDrawPoint = getConnectionPoint(o2Saturation, Hand.RIGHT);
        	rightDrawPoint.y -= PIXEL_SEPARATION;
            Color arrowColor = o2Saturation.isAvailable() ? Resources.valueAvailableTextColor : Resources.valueMissingTextColor;
            drawColoredArrow(pen, arrowColor, leftDrawPoint, rightDrawPoint);
            
            leftDrawPoint = getConnectionPoint(plethysmograph, Hand.RIGHT);
            rightDrawPoint.y += PIXEL_SEPARATION;
            arrowColor = plethysmograph.isAvailable() ? Resources.valueAvailableTextColor : Resources.valueMissingTextColor;
            drawColoredArrow(pen, arrowColor, leftDrawPoint, rightDrawPoint);
            
            leftDrawPoint = getConnectionPoint(pumpStatus, Hand.RIGHT);
            rightDrawPoint.y += PIXEL_SEPARATION;
            arrowColor = pumpStatus.isAvailable() ? Resources.valueAvailableTextColor : Resources.valueMissingTextColor;
            drawColoredArrow(pen, arrowColor, leftDrawPoint, rightDrawPoint);
            
            leftDrawPoint = getConnectionPoint(lockButton, Hand.RIGHT);
            rightDrawPoint = getConnectionPoint(pumpCommand, Hand.LEFT);
            arrowColor = lockIsActive ? Resources.valueAvailableTextColor : Resources.valueMissingTextColor;
            drawColoredArrow(pen, arrowColor, leftDrawPoint, rightDrawPoint);
            
        }
        
        else if (modeOfOperation == OperatingMode.HEART_RATE_VS_PULSE_RATE)
        {
        	Point rightDrawPoint = getConnectionPoint(lockButton, Hand.LEFT);
        	
        	Point leftDrawPoint = getConnectionPoint(o2Saturation, Hand.RIGHT);
        	rightDrawPoint.y -= PIXEL_SEPARATION;
            Color arrowColor = o2Saturation.isAvailable() ? Resources.valueAvailableTextColor : Resources.valueMissingTextColor;
            drawColoredArrow(pen, arrowColor, leftDrawPoint, rightDrawPoint);
            
            leftDrawPoint = getConnectionPoint(pulseRate, Hand.RIGHT);
            rightDrawPoint.y += PIXEL_SEPARATION;
            arrowColor = pulseRate.isAvailable() ? Resources.valueAvailableTextColor : Resources.valueMissingTextColor;
            drawColoredArrow(pen, arrowColor, leftDrawPoint, rightDrawPoint);
            
            leftDrawPoint = getConnectionPoint(heartRate, Hand.RIGHT);
            rightDrawPoint.y += PIXEL_SEPARATION;
            arrowColor = heartRate.isAvailable() ? Resources.valueAvailableTextColor : Resources.valueMissingTextColor;
            drawColoredArrow(pen, arrowColor, leftDrawPoint, rightDrawPoint);
            
            leftDrawPoint = getConnectionPoint(pumpStatus, Hand.RIGHT);
            rightDrawPoint.y += PIXEL_SEPARATION;
            arrowColor = pumpStatus.isAvailable() ? Resources.valueAvailableTextColor : Resources.valueMissingTextColor;
            drawColoredArrow(pen, arrowColor, leftDrawPoint, rightDrawPoint);
            
            leftDrawPoint = getConnectionPoint(lockButton, Hand.RIGHT);
            rightDrawPoint = getConnectionPoint(pumpCommand, Hand.LEFT);
            arrowColor = lockIsActive ? Resources.valueAvailableTextColor : Resources.valueMissingTextColor;
            drawColoredArrow(pen, arrowColor, leftDrawPoint, rightDrawPoint);
            
        }
        
        else if (modeOfOperation == OperatingMode.CAPNOGRAPH)
        {
        	Point rightDrawPoint = getConnectionPoint(lockButton, Hand.LEFT);
        	
        	Point leftDrawPoint = getConnectionPoint(co2Saturation, Hand.RIGHT);
        	rightDrawPoint.y -= PIXEL_SEPARATION*1.5;
            Color arrowColor = co2Saturation.isAvailable() ? Resources.valueAvailableTextColor : Resources.valueMissingTextColor;
            drawColoredArrow(pen, arrowColor, leftDrawPoint, rightDrawPoint);
            
            leftDrawPoint = getConnectionPoint(respirationRate, Hand.RIGHT);
            rightDrawPoint.y += PIXEL_SEPARATION*1.5;
            arrowColor = respirationRate.isAvailable() ? Resources.valueAvailableTextColor : Resources.valueMissingTextColor;
            drawColoredArrow(pen, arrowColor, leftDrawPoint, rightDrawPoint);
            
            leftDrawPoint = getConnectionPoint(pumpStatus, Hand.RIGHT);
            rightDrawPoint.y += PIXEL_SEPARATION*1.5;
            arrowColor = pumpStatus.isAvailable() ? Resources.valueAvailableTextColor : Resources.valueMissingTextColor;
            drawColoredArrow(pen, arrowColor, leftDrawPoint, rightDrawPoint);
            
            leftDrawPoint = getConnectionPoint(lockButton, Hand.RIGHT);
            rightDrawPoint = getConnectionPoint(pumpCommand, Hand.LEFT);
            arrowColor = lockIsActive ? Resources.valueAvailableTextColor : Resources.valueMissingTextColor;
            drawColoredArrow(pen, arrowColor, leftDrawPoint, rightDrawPoint);
            
        }
        else if (modeOfOperation == OperatingMode.MANUAL)
        {
        	Point rightDrawPoint = getConnectionPoint(lockButton, Hand.LEFT);
        	
        	Point leftDrawPoint = getConnectionPoint(pumpStatus, Hand.RIGHT);
            Color arrowColor = pumpStatus.isAvailable() ? Resources.valueAvailableTextColor : Resources.valueMissingTextColor;
            drawColoredArrow(pen, arrowColor, leftDrawPoint, rightDrawPoint);
        	
            leftDrawPoint = getConnectionPoint(lockButton, Hand.RIGHT);
            rightDrawPoint = getConnectionPoint(pumpCommand, Hand.LEFT);
            arrowColor = lockIsActive ? Resources.valueAvailableTextColor : Resources.valueMissingTextColor;
            drawColoredArrow(pen, arrowColor, leftDrawPoint, rightDrawPoint);
        }
	}
	
	private Point getConnectionPoint(JComponent component, Hand hand)
	{
        Point componentLocation = component.getLocation();
        int componentWidth = component.getWidth();
        Point locationOfParent = component.getParent().getLocation();
        int parentXOffset = (int) locationOfParent.getX();
        int componentHeight = component.getHeight();
        
        int xcoordinate = 0;
        int ycoordinate = 0;
        switch (hand)
        {
        	case LEFT:
        		xcoordinate = componentLocation.x + parentXOffset - 5;
        		ycoordinate = componentLocation.y + (componentHeight/2);
        		return new Point(xcoordinate, ycoordinate);
        	case RIGHT:
        		xcoordinate = componentLocation.x + componentWidth + parentXOffset;
                ycoordinate = componentLocation.y + (componentHeight/2);
                return new Point(xcoordinate, ycoordinate);
        }
        return null;
	}
	
	private void drawColoredArrow(Graphics2D pen, Color color, Point begin, Point end)
	{
		Color originalPenColor = pen.getColor();
		pen.setColor(color);
		
		pen.drawLine(begin.x, begin.y, end.x, end.y);

		int xDifferential = end.x - begin.x;
		int yDifferential = end.y - begin.y;
		double lengthOfBeginEndLine = Math.sqrt(xDifferential*xDifferential + yDifferential*yDifferential);
		int arrowHeadDegreesFromBeginEndLine = 25;
		int ArrowHeadSize = 12;
		double degreesInARadian = 57.2957795;
		
		double angleOfInclineOfBeginEndLine = Math.asin(yDifferential/lengthOfBeginEndLine) * degreesInARadian;
		
		double yVectorComponentFromEnd = Math.sin((angleOfInclineOfBeginEndLine-arrowHeadDegreesFromBeginEndLine+180)/degreesInARadian);
		double xVectorComponentFromEnd = Math.cos((angleOfInclineOfBeginEndLine-arrowHeadDegreesFromBeginEndLine+180)/degreesInARadian);
		int xVectorComponentFromEndScaled = (int) (xVectorComponentFromEnd*ArrowHeadSize);
		int yVectorComponentFromEndScaled = (int) (yVectorComponentFromEnd*ArrowHeadSize);
		pen.drawLine(end.x, end.y, end.x+xVectorComponentFromEndScaled, end.y+yVectorComponentFromEndScaled);
		
		yVectorComponentFromEnd = Math.sin((angleOfInclineOfBeginEndLine+arrowHeadDegreesFromBeginEndLine+180)/degreesInARadian);
		xVectorComponentFromEnd = Math.cos((angleOfInclineOfBeginEndLine+arrowHeadDegreesFromBeginEndLine+180)/degreesInARadian);
		xVectorComponentFromEndScaled = (int) (xVectorComponentFromEnd*ArrowHeadSize);
		yVectorComponentFromEndScaled = (int) (yVectorComponentFromEnd*ArrowHeadSize);
		pen.drawLine(end.x, end.y, end.x+xVectorComponentFromEndScaled, end.y+yVectorComponentFromEndScaled);
		
		pen.setColor(originalPenColor);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		if (x > 0 && x <= 278 && y > 84 && y <= 165)
		{
			if (!lockIsActive)
			{
				if (modeConditionsMet()) //initiate lock sequence TODO
				{
					if (modeOfOperation == OperatingMode.MANUAL)
					{
						lockIsActive = true;
						sendStart();
						updateLockImage();
					}
					
					else {
						SetAlarmDialog dialog = new SetAlarmDialog(modeOfOperation);
						dialog.addSetAlarmListener(this);
						AlarmOption decided = dialog.showDialog();
					
						if (decided == AlarmOption.OK)
						{
							lockIsActive = true;
							sendStart();
							updateLockImage();
						}
						else ; //DO NOTHING
					}
				}
				
				else
				{
					if (modeOfOperation != OperatingMode.UNSPECIFIED)
					{
						MessageDialog dialog = new MessageDialog("Mode Requirements Not Met", new Dimension(350, 100));
						dialog.showDialog();
					}
					else
					{
						MessageDialog dialog = new MessageDialog("Select a Mode", new Dimension(350, 100));
						dialog.showDialog();
					}
				}
			}
			else
			{
				String lowerCaseMode = modeOfOperation.toString().toLowerCase();
				String newString = "";
				boolean capitalizeNext = false;
				for (int i = 0; i < lowerCaseMode.length(); i++)
				{
					char c = lowerCaseMode.charAt(i);
					if (i == 0 || capitalizeNext == true)
					{
						c = Character.toUpperCase(c);
						newString = newString + Character.toString(c);
						capitalizeNext = false;
					}
					else if (c == '_')
					{
						newString = newString + Character.toString(' ');
						capitalizeNext = true;
					}
					else newString = newString + Character.toString(c);
				}
				boolean stopFirst = (lastCommand == Command.START) ? true : false;
				YesNoDialog dialog = new YesNoDialog((stopFirst ? "Send Stop Command and " : "") + "End " + newString + " Mode Session?", new Dimension(750, 100));
				YesNoResponse response = dialog.showDialog();
				if (response == YesNoResponse.YES)
				{
					if (stopFirst)
						sendStop();
					lockIsActive = false;
					updateLockImage();
				}
			}
		}
		else if (x > 40 && x < 240 && y > 50 && y < 84)
		{
			if (modeOfOperation == OperatingMode.MANUAL)
			{
				if (lockIsActive)
				{
					if (lastCommand == Command.START)
					{
						sendStop();
						updateLockImage();
					}
					else if (lastCommand == Command.STOP)
					{
						sendStart();
						updateLockImage();
					}
				}
			}
			else if (lockIsActive)
			{
				if (lastCommand == Command.STOP)
				{
					if (!modeConditionsMet())
					{
						MessageDialog dialog = new MessageDialog("Mode Requirements Not Met", new Dimension(350, 100));
						dialog.showDialog();
					}
					else if (!lockConditionsMet())
					{
						MessageDialog dialog = new MessageDialog("Lock Rule Is Violated", new Dimension(350, 100));
						dialog.showDialog();
					}
					else if (lockConditionsMet())
					{
						sendStart();
						updateLockImage();
					}
				}
			}
		}
	}
	
	private void sendStart()
	{
		for (CommandListener listener : CommandListeners)
			listener.actionPerformed(new CommandEvent(Command.START));
		lastCommand = Command.START;
		pumpCommand.updateQuality("Start Sent");
	}
	
	private void sendStop()
	{
		for (CommandListener listener : CommandListeners)
			listener.actionPerformed(new CommandEvent(Command.STOP));
		lastCommand = Command.STOP;
		pumpCommand.updateQuality("Stop Sent");
	}

	private void updateLockImage()
	{
		if (lockIsActive && lastCommand == Command.START)
		{
			if (modeOfOperation == OperatingMode.PLETHYSMOGRAPH)
				lockButton.updateImage(Resources.loadImage("ActiveLockPlethysmograph.png"));
			else if (modeOfOperation == OperatingMode.CAPNOGRAPH)
				lockButton.updateImage(Resources.loadImage("ActiveLockCapnograph.png"));
			else if (modeOfOperation == OperatingMode.HEART_RATE_VS_PULSE_RATE)
				lockButton.updateImage(Resources.loadImage("ActiveLockHeartRateVsPulseRate.png"));
			else if (modeOfOperation == OperatingMode.MANUAL)
					lockButton.updateImage(Resources.loadImage("LockManualPressStop.png"));
		}
		else if (lockIsActive && lastCommand == Command.STOP)
		{
			if (modeOfOperation == OperatingMode.PLETHYSMOGRAPH)
				lockButton.updateImage(Resources.loadImage("ActiveLockPlethysmographPressStart.png"));
			else if (modeOfOperation == OperatingMode.CAPNOGRAPH)
				lockButton.updateImage(Resources.loadImage("ActiveLockCapnographPressStart.png"));
			else if (modeOfOperation == OperatingMode.HEART_RATE_VS_PULSE_RATE)
				lockButton.updateImage(Resources.loadImage("ActiveLockHeartRateVsPulseRatePressStart.png"));
			else if (modeOfOperation == OperatingMode.MANUAL)
					lockButton.updateImage(Resources.loadImage("LockManualPressStart.png"));
		}
		else if (!lockIsActive)
		{
			if (modeOfOperation == OperatingMode.PLETHYSMOGRAPH)
				lockButton.updateImage(Resources.loadImage("InactiveLockPlethysmograph.png"));
			else if (modeOfOperation == OperatingMode.CAPNOGRAPH)
				lockButton.updateImage(Resources.loadImage("InactiveLockCapnograph.png"));
			else if (modeOfOperation == OperatingMode.HEART_RATE_VS_PULSE_RATE)
				lockButton.updateImage(Resources.loadImage("InactiveLockHeartRateVsPulseRate.png"));
			else if (modeOfOperation == OperatingMode.MANUAL)
				lockButton.updateImage(Resources.loadImage("InactiveLockManual.png"));
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	public void handleDeviceEvent(DeviceEvent deviceEvent) {
		o2Saturation.updateValue(deviceEvent.o2Saturation);
		pulseRate.updateValue(deviceEvent.pulseRate);
		
		if (deviceEvent.plethysmographSet.isEmpty())
			plethysmograph.updateQuality("NULL");
		else if (deviceEvent.plethIsBad)
			plethysmograph.updateQuality(alarmSetting.plethAlg + " : Bad");
		else plethysmograph.updateQuality(alarmSetting.plethAlg + " : Good");
		
		heartRate.updateValue(deviceEvent.heartRate);
		co2Saturation.updateValue(deviceEvent.co2Saturation);
		respirationRate.updateValue(deviceEvent.respiratoryRate);
		
		pumpStatus.updateQuality(deviceEvent.pumpMessage);
		if (pumpStatus.isAvailable() && !lockIsActive)
			pumpCommand.updateQuality("Ready");
		else if (!pumpStatus.isAvailable())
			pumpCommand.updateQuality("");
		
		if (lockIsActive && lastCommand == Command.START)
		{
			if (!pumpStatus.isAvailable())
			{
				lockIsActive = false;
				updateLockImage();
				( (JFrame) SwingUtilities.getRoot(this)).toFront();
				MessageDialog dialog = new MessageDialog("Pump Was Lost!", new Dimension(350, 100));
				dialog.showDialog();
			}
			else if (modeOfOperation == OperatingMode.PLETHYSMOGRAPH)
			{
				if (deviceEvent.o2Saturation < alarmSetting.o2Minimum || deviceEvent.plethIsBad)
				{
					sendStop();
					updateLockImage();
					( (JFrame) SwingUtilities.getRoot(this)).toFront();
					MessageDialog dialog = new MessageDialog("Stop Sent!", new Dimension(350, 100));
					dialog.showDialog();
				}
				
				if (alarmSetting.maxO2RateOfChange > 0)
				{
					if (Math.abs(deviceEvent.o2Saturation - lastDeviceEvent.o2Saturation) > alarmSetting.maxO2RateOfChange)
					{
						sendStop();
						updateLockImage();
						( (JFrame) SwingUtilities.getRoot(this)).toFront();
						MessageDialog dialog = new MessageDialog("Stop Sent!", new Dimension(350, 100));
						dialog.showDialog();
					}
				}
			}
			else if (modeOfOperation == OperatingMode.HEART_RATE_VS_PULSE_RATE)
			{
				if (Math.abs(deviceEvent.heartRate - deviceEvent.pulseRate) > alarmSetting.minDHrPr
						|| deviceEvent.o2Saturation < alarmSetting.o2Minimum)
				{
					sendStop();
					updateLockImage();
					( (JFrame) SwingUtilities.getRoot(this)).toFront();
					MessageDialog dialog = new MessageDialog("Stop Sent!", new Dimension(350, 100));
					dialog.showDialog();
				}
				
				if (alarmSetting.maxO2RateOfChange > 0)
				{
					if (Math.abs(deviceEvent.o2Saturation - lastDeviceEvent.o2Saturation) > alarmSetting.maxO2RateOfChange)
					{
						sendStop();
						updateLockImage();
						( (JFrame) SwingUtilities.getRoot(this)).toFront();
						MessageDialog dialog = new MessageDialog("Stop Sent!", new Dimension(350, 100));
						dialog.showDialog();
					}
				}
				if (alarmSetting.maxDHrPrRateOfChange > 0)
				{
					int alpha = Math.abs(lastDeviceEvent.pulseRate - lastDeviceEvent.heartRate);
					int beta = Math.abs(deviceEvent.pulseRate - deviceEvent.heartRate);
					int delta = Math.abs(alpha - beta);
					if (delta > alarmSetting.maxDHrPrRateOfChange)
					{
						sendStop();
						updateLockImage();
						( (JFrame) SwingUtilities.getRoot(this)).toFront();
						MessageDialog dialog = new MessageDialog("Stop Sent!", new Dimension(350, 100));
						dialog.showDialog();
					}
				}
			}
			else if (modeOfOperation == OperatingMode.CAPNOGRAPH)
			{
				if (deviceEvent.co2Saturation > alarmSetting.co2Maximum || 
						deviceEvent.respiratoryRate < alarmSetting.respRateMinimum)
				{
					sendStop();
					updateLockImage();
					( (JFrame) SwingUtilities.getRoot(this)).toFront();
					MessageDialog dialog = new MessageDialog("Stop Sent!", new Dimension(350, 100));
					dialog.showDialog();
				}
				if (alarmSetting.maxCo2RateOfChange > 0)
				{
					if (Math.abs(deviceEvent.co2Saturation - lastDeviceEvent.co2Saturation) > alarmSetting.maxCo2RateOfChange)
					{
						sendStop();
						updateLockImage();
						( (JFrame) SwingUtilities.getRoot(this)).toFront();
						MessageDialog dialog = new MessageDialog("Stop Sent!", new Dimension(350, 100));
						dialog.showDialog();
					}
				}
				if (alarmSetting.maxRespRateRateOfChange > 0)
				{
					if (Math.abs(deviceEvent.respiratoryRate - lastDeviceEvent.respiratoryRate) > alarmSetting.maxRespRateRateOfChange)
					{
						sendStop();
						updateLockImage();
						( (JFrame) SwingUtilities.getRoot(this)).toFront();
						MessageDialog dialog = new MessageDialog("Stop Sent!", new Dimension(350, 100));
						dialog.showDialog();
					}
				}
			}
		}
		lastDeviceEvent = deviceEvent;
		
		repaint();
	}
	
	//check roc
	private boolean lockConditionsMet()
	{
		if (modeOfOperation == OperatingMode.PLETHYSMOGRAPH)
		{
			if (lastDeviceEvent.o2Saturation < alarmSetting.o2Minimum || lastDeviceEvent.plethIsBad)
				return false;
		}
		else if (modeOfOperation == OperatingMode.HEART_RATE_VS_PULSE_RATE)
		{
			if (Math.abs(lastDeviceEvent.heartRate - lastDeviceEvent.pulseRate) > alarmSetting.minDHrPr
					|| lastDeviceEvent.o2Saturation < alarmSetting.o2Minimum)
				return false;
		}
		else if (modeOfOperation == OperatingMode.CAPNOGRAPH)
		{
			if (lastDeviceEvent.co2Saturation > alarmSetting.co2Maximum || 
					lastDeviceEvent.respiratoryRate < alarmSetting.respRateMinimum)
				return false;
		}
		return true;
	}
	
	private boolean modeConditionsMet()
	{
		if (!pumpStatus.isAvailable()) return false;
		if (modeOfOperation.equals(OperatingMode.MANUAL))
			return true;
		if (modeOfOperation.equals(OperatingMode.PLETHYSMOGRAPH))
			if (plethysmograph.isAvailable() && pulseRate.isAvailable()) return true;
		if (modeOfOperation.equals(OperatingMode.HEART_RATE_VS_PULSE_RATE))
			if (heartRate.isAvailable() && pulseRate.isAvailable() && o2Saturation.isAvailable()) return true;
		if (modeOfOperation.equals(OperatingMode.CAPNOGRAPH))
			if (co2Saturation.isAvailable() && respirationRate.isAvailable()) return true;
		
		return false;
	}

	@Override
	public void actionPerformed(SetAlarmEvent event) {
		for (SetAlarmListener listener : AlarmListeners)
		{
			listener.actionPerformed(event);
			alarmSetting = event;
		}
	}
}
