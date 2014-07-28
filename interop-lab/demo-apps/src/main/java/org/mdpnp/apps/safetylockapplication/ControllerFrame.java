package org.mdpnp.apps.safetylockapplication;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class ControllerFrame implements DevicePanelListener, CommandListener, ActionListener, SetAlarmListener {
	
	private ImageButton patientShape;
	private ArrayList<ControllerFrameListener> listeners;
	private DevicePanel devicePanel;
	private SimulatedPatient patient;
	
	public ControllerFrame()
	{
		listeners = new ArrayList<ControllerFrameListener>();
		patient = new SimulatedPatient();
		patientShape = new ImageButton(Resources.loadImage("PatientShapeHealthy.png"));
		patientShape.addActionListener(this);
		
		JFrame controllerPanel = new JFrame();
		controllerPanel.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		controllerPanel.getContentPane().setBackground(Resources.physiologicalDisplayPanelBackgroundColor);
		controllerPanel.setLayout(new GridBagLayout());
		
		devicePanel = new DevicePanel();
		patient.addPatientEventListener(devicePanel);
		devicePanel.addDevicePanelListener(this);
		
		DistressorPanel distressorPanel = new DistressorPanel(patient);
		distressorPanel.setBorder(makeBorder("Distressors"));
		
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 0;
		gc.weighty = 0;
		gc.insets = new Insets(10, 10, 10, 10);
		
		controllerPanel.add(devicePanel, gc);
		gc.gridx++;
		controllerPanel.add(patientShape, gc);
		gc.gridx++;
		controllerPanel.add(distressorPanel, gc);
		
		controllerPanel.pack();
		controllerPanel.setResizable(false);
		controllerPanel.setVisible(true);
		
		patient.start();
	}
	
	private Border makeBorder(String borderTitle)
	{
		Border inner = BorderFactory.createLineBorder(Color.GRAY);
		Font titleFont = new Font("Sans", Font.PLAIN, 15);
		Border innerBorder = BorderFactory.createTitledBorder(inner, borderTitle, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, titleFont, Color.WHITE);
		Border outerBorder = BorderFactory.createEmptyBorder(5, 5, 5, 5);
		return BorderFactory.createCompoundBorder(outerBorder, innerBorder);
	}

	public void addControllerFrameListener(ControllerFrameListener listener)
	{
		listeners.add(listener);
	}

	@Override
	public void actionPerformed(DeviceEvent event) {
		for (ControllerFrameListener listener : listeners)
			listener.actionPerformed(event);
	}

	@Override
	public void actionPerformed(CommandEvent e) {
		devicePanel.actionPerformed(e);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (!patient.distressed)
		{
			patientShape.updateImage(Resources.loadImage("PatientShapeDistressed.png"));
			patient.setDistressed(true);
		}
		else if (patient.distressed)
		{
			patientShape.updateImage(Resources.loadImage("PatientShapeHealthy.png"));
			patient.setDistressed(false);
		}
	}

	@Override
	public void actionPerformed(SetAlarmEvent event) {
		patient.setO2Minimum(event.o2Minimum);
		patient.setCo2Maximum(event.co2Maximum);
		patient.setHrprDMaximum(event.minDHrPr);
		patient.setRespRateMinimum(event.respRateMinimum);
		patient.setMaxO2RateOfChange(event.maxO2RateOfChange);
		patient.setMaxDHrPrRateOfChange(event.maxDHrPrRateOfChange);
		patient.setMaxRespRateRateOfChange(event.maxRespRateRateOfChange);
		patient.setMaxCo2RateOfChange(event.maxCo2RateOfChange);
	}

}
