package org.mdpnp.apps.safetylockapplication;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.mdpnp.apps.safetylockapplication.Resources.AlarmOption;
import org.mdpnp.apps.safetylockapplication.Resources.Algorithm;
import org.mdpnp.apps.safetylockapplication.Resources.OperatingMode;

public class RulePanel extends JPanel implements OptionsPanelListener {
	
	///MODIFY RANGES HERE
	private static final int o2RangeHigh = 98;
	private static final int o2RangeLow = 85;
	private static final int co2Range = 4;
	private static final int respiratoryRangeHigh = 20;
	private static final int respiratoryRangeLow = 8;
	private static final int hrprRange = 8;
	private static final int o2RocRange = 10;
	private static final int co2RocRange = 4;
	private static final int respiratoryRocRange = 5;
	private static final int hrprRocRange = 9;
	///

	SetAlarmDialog dialog;
	SetAlarmListener alarmListener;
	private OptionsPanelListener optionsListener;
	OperatingMode mode;
	
	String rulePrefix = "Rule: ";
	String o2Part = "";
	String co2Part = "";
	String respPart = "";
	String plethPart = "";
	String hrprPart = "";
	String rampPart1 = "";
	String rampPart2 = "";
	JLabel rule1;
	JLabel rule2;
	JLabel rule3;
	JLabel rule4;
	JPanel bunk;
	
	JPanel ruleStringPanel = new JPanel();
	JPanel ruleListPanel = new JPanel();
	JLabel extraAttribute1;
	JLabel extraAttribute2;
	JLabel extraAttribute3;
	JLabel extraAttribute4;
	
	JComboBox<Integer> selection1;
	JComboBox<Algorithm> selection2;
	JComboBox<Integer> selection3;
	JComboBox<Integer> selection4;
	JComboBox<Integer> selection5;
	
	boolean newIsOut = false;
	
	RulePanel(SetAlarmDialog inputDialog2, OperatingMode mode) {
		dialog = inputDialog2;
		this.mode = mode;
		
		if (mode == OperatingMode.PLETHYSMOGRAPH)
			plethModeLayout();
		if (mode == OperatingMode.HEART_RATE_VS_PULSE_RATE)
			hrprModeLayout();
		if (mode == OperatingMode.CAPNOGRAPH)
			respCo2Layout();
	}
	
	private void respCo2Layout() {
		
		ruleStringPanel.setBackground(Color.BLACK);
		ruleStringPanel.setPreferredSize(new Dimension(100, 150));
		ruleListPanel.setBackground(Color.BLACK);
		
		setLayout(new BorderLayout());
		add(ruleStringPanel, BorderLayout.NORTH);
		add(ruleListPanel, BorderLayout.CENTER);
		
		rule1 = new JLabel(rulePrefix + "None Specified");
		fixLabel(rule1, 15);
		rule2 = new JLabel("");
		fixLabel(rule2, 15);
		rule3 = new JLabel("");
		fixLabel(rule3, 15);
		rule4 = new JLabel("");
		fixLabel(rule4, 15);
		
		ruleStringPanel.setLayout(new GridBagLayout());
		GridBagConstraints rc = new GridBagConstraints();
		rc.gridx = 0;
		rc.gridy = 0;
		rc.weightx = 0;
		rc.weighty = 0;
		rc.insets = new Insets(5, 5, 5, 5);
		
		ruleStringPanel.add(rule1,rc);
		rc.gridy = 1;
		ruleStringPanel.add(rule2,rc);
		rc.gridy = 2;
		ruleStringPanel.add(rule3, rc);
		rc.gridy = 3;
		ruleStringPanel.add(rule4, rc);
		
		/////////////////////////////////////
		
		ruleListPanel.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 0.1;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.insets = new Insets(10, 10, 10, 10);
		JLabel attribute = new JLabel("CO2 Saturation");
		fixLabel(attribute, 18);
		ruleListPanel.add(attribute, gc);
		
		
		JLabel min = new JLabel("Maximum (%): ");
		fixLabel(min, 18);
		gc.gridx = 1;
		gc.gridy = 0;
		ruleListPanel.add(min, gc);
		
		selection1 = new JComboBox<Integer>();
		DefaultComboBoxModel<Integer> co2Model = new DefaultComboBoxModel<Integer>();
		co2Model.addElement(null);
		for (int i = 1; i <= co2Range; i++)
			co2Model.addElement(i);
		selection1.setModel(co2Model);
		selection1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (selection1.getSelectedItem() == null)
					return;
				int item = (int) selection1.getSelectedItem();
				
				if (selection4 != null)
				{
					DefaultComboBoxModel<Integer> rateModel1 = new DefaultComboBoxModel<Integer>();
					rateModel1.addElement(null);
					for (int i = 1; i < item; i++)
						rateModel1.addElement(i);
					selection4.setModel(rateModel1);
				}
			}
			
		});
		selection1.setPrototypeDisplayValue(100000);
		gc.gridx = 2;
		gc.gridy = 0;
		ruleListPanel.add(selection1, gc);
		selection1.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					Object item = event.getItem();
					
					String part1 = "";
					String part2 = "";
					String part3 = "";
					String part4 = "";
					co2Part = item.toString();
					if (co2Part.length() > 0)
						part1 = "if CO2 Saturation > " + co2Part + "%";
					if (respPart.length() > 0)
						part2 = "\n OR if Respiration Rate < " + respPart;
					if (rampPart1.length() > 0)
						part3 = "\n OR if CO2 Saturation rate of change > " + rampPart1;
					if (rampPart2.length() > 0)
						part4 = "\n OR if Resp. Rate rate of change > " + rampPart2 ;
					rule1.setText(rulePrefix + "Turn off the pump " + part1);
					rule2.setText(part2);
					rule3.setText(part3);
					rule4.setText(part4);
				}
			}
			
		});
		
		JLabel attribute2 = new JLabel("Respiration Rate");
		fixLabel(attribute2, 18);
		gc.gridx = 0;
		gc.gridy = 1;
		ruleListPanel.add(attribute2, gc);
		
		JLabel attribute3 = new JLabel("Minimum (bpm): ");
		fixLabel(attribute3, 18);
		gc.gridx = 1;
		gc.gridy = 1;
		ruleListPanel.add(attribute3, gc);
		
		selection3 = new JComboBox<Integer>();
		DefaultComboBoxModel<Integer> respRateModel = new DefaultComboBoxModel<Integer>();
		respRateModel.addElement(null);
		for (int i = respiratoryRangeLow; i <= respiratoryRangeHigh; i++) 
		{
			respRateModel.addElement(i);
		}
		selection3.setModel(respRateModel);
/*		selection3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int item = (int) selection3.getSelectedItem();
				
				if (selection5 != null)
				{
					DefaultComboBoxModel<Integer> rateModel1 = new DefaultComboBoxModel<Integer>();
					rateModel1.addElement(null);
					for (int i = 1; i < 100-item; i++)
						rateModel1.addElement(i);
					selection5.setModel(rateModel1);
				}
			}
			
		});*/
		selection3.setPrototypeDisplayValue(100000);
		gc.gridx = 2;
		gc.gridy = 1;
		ruleListPanel.add(selection3, gc);
		selection3.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					Object item = event.getItem();
					
					String part1 = "";
					String part2 = "";
					String part3 = "";
					String part4 = "";
					respPart = item.toString();
					if (co2Part.length() > 0)
						part1 = "if CO2 Saturation > " + co2Part + "%";
					if (respPart.length() > 0)
						part2 = "\n OR if Respiration Rate < " + respPart + " bpm";
					if (rampPart1.length() > 0)
						part3 = "\n OR if CO2 Saturation rate of change > " + rampPart1 ;
					if (rampPart2.length() > 0)
						part4 = "\n OR if Resp. Rate rate of change > " + rampPart2 ;
					rule1.setText(rulePrefix + "Turn off the pump " + part1);
					rule2.setText(part2);
					rule3.setText(part3);
					rule4.setText(part4);
				}
			}
			
		});
		
		selection4 = new JComboBox<Integer>();
		DefaultComboBoxModel<Integer> rateModel1 = new DefaultComboBoxModel<Integer>();
		rateModel1.addElement(null);
		for (int i = 1; i <= co2RocRange; i++) //resource co2 roc range
			rateModel1.addElement(i);
		selection4.setModel(rateModel1);
		selection4.setPrototypeDisplayValue(100000);
		selection4.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					Object item = event.getItem();
					
					String part1 = "";
					String part2 = "";
					String part3 = "";
					String part4 = "";
					rampPart1 = item.toString();
					if (co2Part.length() > 0)
						part1 = "if CO2 Saturation > " + co2Part + "%";
					if (respPart.length() > 0)
						part2 = "\n OR if Respiration Rate < " + respPart ;
					if (rampPart1.length() > 0)
						part3 = "\n OR if CO2 Saturation rate of change > " + rampPart1 ;
					if (rampPart2.length() > 0)
						part4 = "\n OR if Resp. Rate rate of change > " + rampPart2 ;
					rule1.setText(rulePrefix + "Turn off the pump " + part1);
					rule2.setText(part2);
					rule3.setText(part3);
					rule4.setText(part4);
				}
			}
			
		});
		
		selection5 = new JComboBox<Integer>();
		DefaultComboBoxModel<Integer> rateModel2 = new DefaultComboBoxModel<Integer>();
		rateModel2.addElement(null);
		for (int i = 1; i <= respiratoryRocRange; i++)  //resource : respiration roc range
			rateModel2.addElement(i);
		selection5.setModel(rateModel2);
		selection5.setPrototypeDisplayValue(100000);
		selection5.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					Object item = event.getItem();
					
					String part1 = "";
					String part2 = "";
					String part3 = "";
					String part4 = "";
					rampPart2 = item.toString();
					if (co2Part.length() > 0)
						part1 = "if CO2 Saturation > " + co2Part + "%";
					if (respPart.length() > 0)
						part2 = "\n OR if Respiration Rate < " + respPart ;
					if (rampPart1.length() > 0)
						part3 = "\n OR if CO2 Saturation rate of change > " + rampPart1 ;
					if (rampPart2.length() > 0)
						part4 = "\n OR if Resp. Rate rate of change > " + rampPart2 ;
					rule1.setText(rulePrefix + "Turn off the pump " + part1);
					rule2.setText(part2);
					rule3.setText(part3);
					rule4.setText(part4);
				}
			}
			
		});
		
		extraAttribute1 = new JLabel("CO2 Saturation ROC");
		extraAttribute2 = new JLabel("Maximum: ");
		fixLabel(extraAttribute1, 18);
		fixLabel(extraAttribute2, 18);
		
		extraAttribute3 = new JLabel("Resp. Rate ROC");
		fixLabel(extraAttribute3, 18);
		extraAttribute4 = new JLabel("Maximum: ");
		fixLabel(extraAttribute4, 18);
		
		bunk = new JPanel();
		bunk.setBackground(Color.BLACK);
		gc.gridx = 0;
		gc.gridy = 2;
		gc.weighty = 100;
		ruleListPanel.add(bunk, gc);
		
	}

	public void plethModeLayout()
	{
		ruleStringPanel.setBackground(Color.BLACK);
		ruleStringPanel.setPreferredSize(new Dimension(100, 100));
		ruleListPanel.setBackground(Color.BLACK);
		
		setLayout(new BorderLayout());
		add(ruleStringPanel, BorderLayout.NORTH);
		add(ruleListPanel, BorderLayout.CENTER);
		
		rule1 = new JLabel(rulePrefix + "None Specified");
		fixLabel(rule1, 15);
		rule2 = new JLabel("");
		fixLabel(rule2, 15);
		rule3 = new JLabel("");
		fixLabel(rule3, 15);
		
		ruleStringPanel.setLayout(new GridBagLayout());
		GridBagConstraints rc = new GridBagConstraints();
		rc.gridx = 0;
		rc.gridy = 0;
		rc.weightx = 0;
		rc.weighty = 0;
		rc.insets = new Insets(5, 5, 5, 5);
		
		ruleStringPanel.add(rule1,rc);
		
		rc.gridy = 1;
		ruleStringPanel.add(rule2,rc);
		rc.gridy = 2;
		ruleStringPanel.add(rule3, rc);
		
		/////////////////////////////////////
		
		ruleListPanel.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 0.1;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.insets = new Insets(10, 10, 10, 10);
		JLabel attribute = new JLabel("O2 Saturation");
		fixLabel(attribute, 18);
		ruleListPanel.add(attribute, gc);
		
		
		JLabel min = new JLabel("Minimum (%): ");
		fixLabel(min, 18);
		gc.gridx = 1;
		gc.gridy = 0;
		ruleListPanel.add(min, gc);
		
		selection1 = new JComboBox<Integer>();
		DefaultComboBoxModel<Integer> o2Model = new DefaultComboBoxModel<Integer>();
		o2Model.addElement(null);
		for (int i = o2RangeLow; i <= o2RangeHigh; i++)
			o2Model.addElement(i);
		selection1.setModel(o2Model);
		selection1.setPrototypeDisplayValue(100000);
		selection1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (selection1.getSelectedItem() == null)
					return;
					
				int item = (int) selection1.getSelectedItem();
				
				if (selection3 != null)
				{
					DefaultComboBoxModel<Integer> rateModel1 = new DefaultComboBoxModel<Integer>();
					rateModel1.addElement(null);
					for (int i = 1; i < 100-item; i++)
						rateModel1.addElement(i);
					selection3.setModel(rateModel1);
				}
				
			}
			
		});
		gc.gridx = 2;
		gc.gridy = 0;
		ruleListPanel.add(selection1, gc);
		selection1.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					Object item = event.getItem();
					
					String part1 = "";
					String part2 = "";
					String part3 = "";
					o2Part = item.toString();
					if (o2Part.length() > 0)
						part1 = "if O2 Saturation < " + o2Part + "%";
					if (plethPart.length() > 0)
						part2 = "\n OR if " + plethPart + " Plethysmograph Algorithm Indicates Bad Incoming";
					if (rampPart1.length() > 0)
						part3 = "\n OR if O2 Saturation rate of change > " + rampPart1 ;
					String prefixer = rulePrefix + "Turn off the pump ";
					rule1.setText(prefixer + part1);
					rule2.setText(part2);
					rule3.setText(part3);
				}
			}
			
		});
		
		JLabel attribute2 = new JLabel("Plethysmograph");
		fixLabel(attribute2, 18);
		gc.gridx = 0;
		gc.gridy = 1;
		ruleListPanel.add(attribute2, gc);
		
		JLabel algorithm = new JLabel("Algorithm: ");
		fixLabel(algorithm, 18);
		gc.gridx = 1;
		gc.gridy = 1;
		ruleListPanel.add(algorithm, gc);
		
		selection2 = new JComboBox<Algorithm>();
		DefaultComboBoxModel<Algorithm> plethModel = new DefaultComboBoxModel<Algorithm>();
		plethModel.addElement(null);
		plethModel.addElement(Algorithm.ALPHA);
		plethModel.addElement(Algorithm.BETA);
		plethModel.addElement(Algorithm.GAMMA);
		plethModel.addElement(Algorithm.DELTA);
		plethModel.addElement(Algorithm.EPSILON);
		selection2.setModel(plethModel);
		selection2.setPrototypeDisplayValue(Algorithm.EPSILON);
		gc.gridx = 2;
		gc.gridy = 1;
		ruleListPanel.add(selection2, gc);
		selection2.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					Object item = event.getItem();
					
					String part1 = "";
					String part2 = "";
					String part3 = "";
					plethPart = item.toString();
					if (o2Part.length() > 0)
						part1 = "if O2 Saturation < " + o2Part + "%";
					if (plethPart.length() > 0)
						part2 = "\n OR if " + plethPart + " Plethysmograph Algorithm indicates bad incoming";
					if (rampPart1.length() > 0)
						part3 = "\n OR if O2 Saturation rate of change > " + rampPart1 + " % ";
					rule1.setText(rulePrefix + "Turn off the pump " + part1);
					rule2.setText(part2);
					rule3.setText(part3);
				}
			}
			
		});
		
		selection3 = new JComboBox<Integer>();
		DefaultComboBoxModel<Integer> rateModel1 = new DefaultComboBoxModel<Integer>();
		rateModel1.addElement(null);
		for (int i = 1; i <= o2RocRange; i++)
			rateModel1.addElement(i);
		selection3.setModel(rateModel1);
		selection3.setPrototypeDisplayValue(100000);
		selection3.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					Object item = event.getItem();
					
					String part1 = "";
					String part2 = "";
					String part3 = "";
					rampPart1 = item.toString();
					if (o2Part.length() > 0)
						part1 = "if O2 Saturation < " + o2Part + "%";
					if (plethPart.length() > 0)
						part2 = "\n OR if " + plethPart + " Plethysmograph Algorithm indicates bad incoming";
					if (rampPart1.length() > 0)
						part3 = "\n OR if O2 Saturation rate of change > " + rampPart1 + "% ";
					rule1.setText(rulePrefix + "Turn off the pump " + part1);
					rule2.setText(part2);
					rule3.setText(part3);
				}
			}
			
		});
		
		extraAttribute1 = new JLabel("O2 Saturation ROC");
		extraAttribute2 = new JLabel("Maximum: ");
		fixLabel(extraAttribute1, 18);
		fixLabel(extraAttribute2, 18);
		
		bunk = new JPanel();
		bunk.setBackground(Color.BLACK);
		gc.gridx = 0;
		gc.gridy = 2;
		gc.weighty = 100;
		ruleListPanel.add(bunk, gc);
	}
	
	private void hrprModeLayout()
	{
		ruleStringPanel.setBackground(Color.BLACK);
		ruleStringPanel.setPreferredSize(new Dimension(100, 150));
		ruleListPanel.setBackground(Color.BLACK);
		
		setLayout(new BorderLayout());
		add(ruleStringPanel, BorderLayout.NORTH);
		add(ruleListPanel, BorderLayout.CENTER);
		
		rule1 = new JLabel(rulePrefix + "None Specified");
		fixLabel(rule1, 15);
		rule2 = new JLabel("");
		fixLabel(rule2, 15);
		rule3 = new JLabel("");
		fixLabel(rule3, 15);
		rule4 = new JLabel("");
		fixLabel(rule4, 15);
		
		ruleStringPanel.setLayout(new GridBagLayout());
		GridBagConstraints rc = new GridBagConstraints();
		rc.gridx = 0;
		rc.gridy = 0;
		rc.weightx = 0;
		rc.weighty = 0;
		rc.insets = new Insets(5, 5, 5, 5);
		
		ruleStringPanel.add(rule1,rc);
		rc.gridy = 1;
		ruleStringPanel.add(rule2,rc);
		rc.gridy = 2;
		ruleStringPanel.add(rule3, rc);
		rc.gridy = 3;
		ruleStringPanel.add(rule4, rc);
		
		/////////////////////////////////////
		
		ruleListPanel.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 1;
		gc.weighty = 0.1;
		gc.fill = GridBagConstraints.NONE;
		gc.anchor = GridBagConstraints.FIRST_LINE_START;
		gc.insets = new Insets(10, 10, 10, 10);
		JLabel attribute = new JLabel("O2 Saturation");
		fixLabel(attribute, 18);
		ruleListPanel.add(attribute, gc);
		
		
		JLabel min = new JLabel("Minimum (%): ");
		fixLabel(min, 18);
		gc.gridx = 1;
		gc.gridy = 0;
		ruleListPanel.add(min, gc);
		
		selection1 = new JComboBox<Integer>();
		DefaultComboBoxModel<Integer> o2Model = new DefaultComboBoxModel<Integer>();
		o2Model.addElement(null);
		for (int i = o2RangeLow; i <= o2RangeHigh; i++)
			o2Model.addElement(i);
		selection1.setModel(o2Model);
		selection1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (selection1.getSelectedItem() == null)
					return;
				int item = (int) selection1.getSelectedItem();
				
				if (selection4 != null)
				{
					DefaultComboBoxModel<Integer> rateModel1 = new DefaultComboBoxModel<Integer>();
					rateModel1.addElement(null);
					for (int i = 1; i < 100-item; i++)
						rateModel1.addElement(i);
					selection4.setModel(rateModel1);
				}
				
			}
			
		});
		
		selection1.setPrototypeDisplayValue(100000);
		gc.gridx = 2;
		gc.gridy = 0;
		ruleListPanel.add(selection1, gc);
		selection1.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					Object item = event.getItem();
					
					String part1 = "";
					String part2 = "";
					String part3 = "";
					String part4 = "";
					o2Part = item.toString();
					if (o2Part.length() > 0)
						part1 = "if O2 Saturation < " + o2Part + "%";
					if (hrprPart.length() > 0)
						part2 = "\n OR if |HR - PR| > " + hrprPart ;
					if (rampPart1.length() > 0)
						part3 = "\n OR if O2 Saturation rate of change > " + rampPart1 ;
					if (rampPart2.length() > 0)
						part4 = "\n OR if |HR - PR| rate of change > " + rampPart2 ;
					rule1.setText(rulePrefix + "Turn off the pump " + part1);
					rule2.setText(part2);
					rule3.setText(part3);
					rule4.setText(part4);
				}
			}
			
		});
		
		JLabel attribute2 = new JLabel("|Heart Rate - Pulse Rate|");
		fixLabel(attribute2, 18);
		gc.gridx = 0;
		gc.gridy = 1;
		ruleListPanel.add(attribute2, gc);
		
		JLabel attribute3 = new JLabel("Maximum: ");
		fixLabel(attribute3, 18);
		gc.gridx = 1;
		gc.gridy = 1;
		ruleListPanel.add(attribute3, gc);
		
		selection3 = new JComboBox<Integer>();
		DefaultComboBoxModel<Integer> hrprModel = new DefaultComboBoxModel<Integer>();
		hrprModel.addElement(null);
		for (int i = 1; i <= hrprRange; i++)
		{
			hrprModel.addElement(i);
		}
		selection3.setModel(hrprModel);
		selection3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (selection3.getSelectedItem() == null)
					return;
				int item = (int) selection3.getSelectedItem();
				
				if (selection5 != null)
				{
					DefaultComboBoxModel<Integer> rateModel1 = new DefaultComboBoxModel<Integer>();
					rateModel1.addElement(null);
					for (int i = 1; i < item; i++)
						rateModel1.addElement(i);
					selection5.setModel(rateModel1);
				}
			}
		});
		
		selection3.setPrototypeDisplayValue(100000);
		gc.gridx = 2;
		gc.gridy = 1;
		ruleListPanel.add(selection3, gc);
		selection3.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					Object item = event.getItem();
					
					String part1 = "";
					String part2 = "";
					String part3 = "";
					String part4 = "";
					hrprPart = item.toString();
					if (o2Part.length() > 0)
						part1 = "if O2 Saturation < " + o2Part + "%";
					if (hrprPart.length() > 0)
						part2 = "\n OR if |HR - PR| > " + hrprPart ;
					if (rampPart1.length() > 0)
						part3 = "\n OR if O2 Saturation rate of change > " + rampPart1 ;
					if (rampPart2.length() > 0)
						part4 = "\n OR if |HR - PR| rate of change > " + rampPart2 ;
					
					rule1.setText(rulePrefix + "Turn off the pump " + part1);
					rule2.setText(part2);
					rule3.setText(part3);
				}
			}
			
		});
		
		selection4 = new JComboBox<Integer>();
		DefaultComboBoxModel<Integer> rateModel1 = new DefaultComboBoxModel<Integer>();
		rateModel1.addElement(null);
		for (int i = 1; i <= o2RocRange; i++)
			rateModel1.addElement(i);
		selection4.setModel(rateModel1);
		selection4.setPrototypeDisplayValue(100000);
		selection4.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					Object item = event.getItem();
					
					String part1 = "";
					String part2 = "";
					String part3 = "";
					String part4 = "";
					rampPart1 = item.toString();
					if (o2Part.length() > 0)
						part1 = "if O2 Saturation < " + o2Part + "%";
					if (hrprPart.length() > 0)
						part2 = "\n OR if |HR - PR| > " + hrprPart ;
					if (rampPart1.length() > 0)
						part3 = "\n OR if O2 Saturation rate of change > " + rampPart1 ;
					if (rampPart2.length() > 0)
						part4 = "\n OR if |HR - PR| rate of change > " + rampPart2 ;
					rule1.setText(rulePrefix + "Turn off the pump " + part1);
					rule2.setText(part2);
					rule3.setText(part3);
					rule4.setText(part4);
				}
			}
			
		});
		
		extraAttribute1 = new JLabel("O2 Saturation ROC");
		extraAttribute2 = new JLabel("Maximum: ");
		fixLabel(extraAttribute1, 18);
		fixLabel(extraAttribute2, 18);
		
		selection5 = new JComboBox<Integer>();
		DefaultComboBoxModel<Integer> rateModel2 = new DefaultComboBoxModel<Integer>();
		rateModel2.addElement(null);
		for (int i = 1; i <= hrprRocRange; i++)
			rateModel2.addElement(i);
		selection5.setModel(rateModel2);
		selection5.setPrototypeDisplayValue(100000);
		selection5.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					Object item = event.getItem();
					
					String part1 = "";
					String part2 = "";
					String part3 = "";
					String part4 = "";
					rampPart2 = item.toString();
					if (o2Part.length() > 0)
						part1 = "if O2 Saturation < " + o2Part + "%";
					if (hrprPart.length() > 0)
						part2 = "\n OR if |HR - PR| > " + hrprPart ;
					if (rampPart1.length() > 0)
						part3 = "\n OR if O2 Saturation rate of change > " + rampPart1 ;
					if (rampPart2.length() > 0)
						part4 = "\n OR if |HR - PR| rate of change > " + rampPart2 ;
					rule1.setText(rulePrefix + "Turn off the pump " + part1);
					rule2.setText(part2);
					rule3.setText(part3);
					rule4.setText(part4);
				}
			}
			
		});
		
		extraAttribute1 = new JLabel("O2 Saturation ROC");
		extraAttribute2 = new JLabel("Maximum: ");
		fixLabel(extraAttribute1, 18);
		fixLabel(extraAttribute2, 18);
		
		extraAttribute3 = new JLabel("|Heart Rate - Pulse Rate| ROC");
		fixLabel(extraAttribute3, 18);
		extraAttribute4 = new JLabel("Maximum: ");
		fixLabel(extraAttribute4, 18);
		
		bunk = new JPanel();
		bunk.setBackground(Color.BLACK);
		gc.gridx = 0;
		gc.gridy = 2;
		gc.weighty = 100;
		ruleListPanel.add(bunk, gc);
	}

	private void fixLabel(JLabel label, int size)
	{
		label.setForeground(Color.PINK);
		label.setFont(new Font("Sans", Font.BOLD, size));
	}
	
	public void setSetAlarmListener(SetAlarmListener listener)
	{
		this.alarmListener = listener;
	}
	
	@Override
	public void actionPerformed(AlarmOption optionEvent) {
		
		if (optionEvent == AlarmOption.CANCEL)
		{
			optionsListener.actionPerformed(optionEvent);
		}
		if (optionEvent == AlarmOption.CLEAR)
		{
			if (mode == OperatingMode.PLETHYSMOGRAPH)
			{
				selection1.setSelectedIndex(0);
				selection2.setSelectedIndex(0);
				rule1.setText(rulePrefix + "None Specified");
				o2Part = "";
				plethPart = "";
				rampPart1 = "";
				rule2.setText(plethPart);
				rule3.setText(rampPart1);
				
				if (newIsOut == true)
				{
					ruleListPanel.remove(bunk);
					ruleListPanel.remove(selection3);
					ruleListPanel.remove(extraAttribute1);
					ruleListPanel.remove(extraAttribute2);
					selection3.setSelectedIndex(0);
					//ruleListPanel.validate();
					newIsOut = false;
					
					GridBagConstraints gc = new GridBagConstraints();
					gc.gridx = 0;
					gc.gridy = 2;
					gc.weightx = 1;
					gc.weighty = 100;
					gc.fill = GridBagConstraints.NONE;
					gc.anchor = GridBagConstraints.FIRST_LINE_START;
					gc.insets = new Insets(10, 10, 10, 10);
					ruleListPanel.add(bunk, gc);
					ruleListPanel.validate();
				}
			}
			
			if (mode == OperatingMode.HEART_RATE_VS_PULSE_RATE)
			{
				selection1.setSelectedIndex(0);
				selection3.setSelectedIndex(0);
				rule1.setText(rulePrefix + "None Specified");
				o2Part = "";
				hrprPart = "";
				rampPart1 = "";
				rampPart2 = "";
				rule2.setText(hrprPart);
				rule3.setText(rampPart1);
				rule4.setText(rampPart2);
				
				if (newIsOut == true)
				{
					ruleListPanel.remove(bunk);
					ruleListPanel.remove(selection5);
					ruleListPanel.remove(selection4);
					ruleListPanel.remove(extraAttribute4);
					ruleListPanel.remove(extraAttribute3);
					ruleListPanel.remove(extraAttribute2);
					ruleListPanel.remove(extraAttribute1);
					selection5.setSelectedIndex(0);
					selection4.setSelectedIndex(0);
					//ruleListPanel.validate();
					newIsOut = false;
					
					ruleListPanel.validate();
					
					GridBagConstraints gc = new GridBagConstraints();
					gc.gridx = 0;
					gc.gridy = 2;
					gc.weightx = 1;
					gc.weighty = 100;
					gc.fill = GridBagConstraints.NONE;
					gc.anchor = GridBagConstraints.FIRST_LINE_START;
					gc.insets = new Insets(10, 10, 10, 10);
					ruleListPanel.add(bunk, gc);
					ruleListPanel.validate();
				}
			}
			
			if (mode == OperatingMode.CAPNOGRAPH)
			{
				selection1.setSelectedIndex(0);
				selection3.setSelectedIndex(0);
				rule1.setText(rulePrefix + "None Specified");
				co2Part = "";
				respPart = "";
				rampPart1 = "";
				rampPart2 = "";
				rule2.setText(respPart);
				rule3.setText(rampPart1);
				rule4.setText(rampPart2);
				
				if (newIsOut == true)
				{
					ruleListPanel.remove(bunk);
					ruleListPanel.remove(selection5);
					ruleListPanel.remove(selection4);
					ruleListPanel.remove(extraAttribute4);
					ruleListPanel.remove(extraAttribute3);
					ruleListPanel.remove(extraAttribute2);
					ruleListPanel.remove(extraAttribute1);
					selection5.setSelectedIndex(0);
					selection4.setSelectedIndex(0);
					//ruleListPanel.validate();
					newIsOut = false;
					
					ruleListPanel.validate();
					
					GridBagConstraints gc = new GridBagConstraints();
					gc.gridx = 0;
					gc.gridy = 2;
					gc.weightx = 1;
					gc.weighty = 100;
					gc.fill = GridBagConstraints.NONE;
					gc.anchor = GridBagConstraints.FIRST_LINE_START;
					gc.insets = new Insets(10, 10, 10, 10);
					ruleListPanel.add(bunk, gc);
					ruleListPanel.validate();
				}
			}
		}
		if (optionEvent == AlarmOption.NEW)
		{
			if (mode == OperatingMode.PLETHYSMOGRAPH && newIsOut == false)
			{
				ruleListPanel.remove(bunk);

				GridBagConstraints gc = new GridBagConstraints();
				gc.gridx = 0;
				gc.gridy = 2;
				gc.weightx = 1;
				gc.weighty = 0.1;
				gc.fill = GridBagConstraints.NONE;
				gc.anchor = GridBagConstraints.FIRST_LINE_START;
				gc.insets = new Insets(10, 10, 10, 10);
				
				ruleListPanel.add(extraAttribute1, gc);
				
				gc.gridx = 1;
				
				ruleListPanel.add(extraAttribute2, gc);
				
				gc.gridx = 2;
				
				ruleListPanel.add(selection3, gc);
				
				newIsOut = true;
				
				ruleListPanel.validate();
				
				gc.gridy = 3;
				gc.gridx = 0;
				gc.weightx = 1;
				gc.weighty = 100;
				
				ruleListPanel.add(bunk, gc);
				
				ruleListPanel.validate();
			}
			
			if (mode == OperatingMode.HEART_RATE_VS_PULSE_RATE && newIsOut == false)
			{
				ruleListPanel.remove(bunk);

				GridBagConstraints gc = new GridBagConstraints();
				gc.gridx = 0;
				gc.gridy = 2;
				gc.weightx = 1;
				gc.weighty = 0.1;
				gc.fill = GridBagConstraints.NONE;
				gc.anchor = GridBagConstraints.FIRST_LINE_START;
				gc.insets = new Insets(10, 10, 10, 10);
				
				ruleListPanel.add(extraAttribute1, gc);
				
				gc.gridx = 1;
				
				ruleListPanel.add(extraAttribute2, gc);
				
				gc.gridx = 2;
				
				ruleListPanel.add(selection4, gc);
				
				gc.gridy = 3;
				gc.gridx = 0;
				
				ruleListPanel.add(extraAttribute3, gc);
				
				gc.gridx = 1;
				ruleListPanel.add(extraAttribute4,gc);
				gc.gridx = 2;
				ruleListPanel.add(selection5, gc);
				
				ruleListPanel.validate();
				newIsOut = true;
				
				gc.gridy = 4;
				gc.gridx = 0;
				gc.weightx = 1;
				gc.weighty = 100;
				
				ruleListPanel.add(bunk, gc);
				
				ruleListPanel.validate();
			}
			
			if (mode == OperatingMode.CAPNOGRAPH && newIsOut == false)
			{
				ruleListPanel.remove(bunk);

				GridBagConstraints gc = new GridBagConstraints();
				gc.gridx = 0;
				gc.gridy = 2;
				gc.weightx = 1;
				gc.weighty = 0.1;
				gc.fill = GridBagConstraints.NONE;
				gc.anchor = GridBagConstraints.FIRST_LINE_START;
				gc.insets = new Insets(10, 10, 10, 10);
				
				ruleListPanel.add(extraAttribute1, gc);
				
				gc.gridx = 1;
				
				ruleListPanel.add(extraAttribute2, gc);
				
				gc.gridx = 2;
				
				ruleListPanel.add(selection4, gc);
				
				gc.gridy = 3;
				gc.gridx = 0;
				
				ruleListPanel.add(extraAttribute3, gc);
				
				gc.gridx = 1;
				ruleListPanel.add(extraAttribute4,gc);
				gc.gridx = 2;
				ruleListPanel.add(selection5, gc);
				
				ruleListPanel.validate();
				newIsOut = true;
				
				gc.gridy = 4;
				gc.gridx = 0;
				gc.weightx = 1;
				gc.weighty = 100;
				
				ruleListPanel.add(bunk, gc);
				
				ruleListPanel.validate();
			}
		}
		else if (optionEvent == AlarmOption.DEFAULT)
		{
			if (mode == OperatingMode.PLETHYSMOGRAPH)
			{
				selection1.setSelectedIndex(0);
				selection2.setSelectedIndex(0);
				rule1.setText(rulePrefix + "None Specified");
				o2Part = "";
				plethPart = "";
				rampPart1 = "";
				rule2.setText(plethPart);
				rule3.setText(rampPart1);
				
				if (newIsOut == true)
				{
					ruleListPanel.remove(bunk);
					ruleListPanel.remove(selection3);
					ruleListPanel.remove(extraAttribute1);
					ruleListPanel.remove(extraAttribute2);
					selection3.setSelectedIndex(0);
					//ruleListPanel.validate();
					newIsOut = false;
					
					GridBagConstraints gc = new GridBagConstraints();
					gc.gridx = 0;
					gc.gridy = 2;
					gc.weightx = 1;
					gc.weighty = 100;
					gc.fill = GridBagConstraints.NONE;
					gc.anchor = GridBagConstraints.FIRST_LINE_START;
					gc.insets = new Insets(10, 10, 10, 10);
					ruleListPanel.add(bunk, gc);
				}
				selection1.setSelectedIndex(10);
				selection2.setSelectedIndex(3);
			}
			
			if (mode == OperatingMode.HEART_RATE_VS_PULSE_RATE)
			{
				selection1.setSelectedIndex(0);
				selection3.setSelectedIndex(0);
				rule1.setText(rulePrefix + "None Specified");
				o2Part = "";
				hrprPart = "";
				rampPart1 = "";
				rampPart2 = "";
				rule2.setText(hrprPart);
				rule3.setText(rampPart1);
				rule4.setText(rampPart2);
				
				if (newIsOut == true)
				{
					ruleListPanel.remove(bunk);
					ruleListPanel.remove(selection5);
					ruleListPanel.remove(selection4);
					ruleListPanel.remove(extraAttribute4);
					ruleListPanel.remove(extraAttribute3);
					ruleListPanel.remove(extraAttribute2);
					ruleListPanel.remove(extraAttribute1);
					selection5.setSelectedIndex(0);
					selection4.setSelectedIndex(0);
					//ruleListPanel.validate();
					newIsOut = false;
					
					ruleListPanel.validate();
					
					GridBagConstraints gc = new GridBagConstraints();
					gc.gridx = 0;
					gc.gridy = 2;
					gc.weightx = 1;
					gc.weighty = 100;
					gc.fill = GridBagConstraints.NONE;
					gc.anchor = GridBagConstraints.FIRST_LINE_START;
					gc.insets = new Insets(10, 10, 10, 10);
					ruleListPanel.add(bunk, gc);
					ruleListPanel.validate();
				}
				selection1.setSelectedIndex(10);
				selection3.setSelectedIndex(4);
			}
			
			if (mode == OperatingMode.CAPNOGRAPH)
			{
				selection1.setSelectedIndex(0);
				selection3.setSelectedIndex(0);
				rule1.setText(rulePrefix + "None Specified");
				co2Part = "";
				respPart = "";
				rampPart1 = "";
				rampPart2 = "";
				rule2.setText(respPart);
				rule3.setText(rampPart1);
				rule4.setText(rampPart2);
				
				if (newIsOut == true)
				{
					ruleListPanel.remove(bunk);
					ruleListPanel.remove(selection5);
					ruleListPanel.remove(selection4);
					ruleListPanel.remove(extraAttribute4);
					ruleListPanel.remove(extraAttribute3);
					ruleListPanel.remove(extraAttribute2);
					ruleListPanel.remove(extraAttribute1);
					selection5.setSelectedIndex(0);
					selection4.setSelectedIndex(0);
					//ruleListPanel.validate();
					newIsOut = false;
					
					ruleListPanel.validate();
					
					GridBagConstraints gc = new GridBagConstraints();
					gc.gridx = 0;
					gc.gridy = 2;
					gc.weightx = 1;
					gc.weighty = 100;
					gc.fill = GridBagConstraints.NONE;
					gc.anchor = GridBagConstraints.FIRST_LINE_START;
					gc.insets = new Insets(10, 10, 10, 10);
					ruleListPanel.add(bunk, gc);
					ruleListPanel.validate();
				}
				selection1.setSelectedIndex(2);
				selection3.setSelectedIndex(3);
			}
			
		}
		else if (optionEvent == AlarmOption.OK)
		{
			if (mode == OperatingMode.PLETHYSMOGRAPH)
			{
				SetAlarmEvent event = new SetAlarmEvent(this);
				if (selection1.getSelectedItem() != null && selection2.getSelectedItem() != null)
				{
					event.o2Minimum = ((int) selection1.getSelectedItem());
					event.plethAlg = ((Algorithm) selection2.getSelectedItem());
					if (selection3.getSelectedItem() != null)
						event.maxO2RateOfChange = ((int) selection3.getSelectedItem());
					if (alarmListener != null)
						alarmListener.actionPerformed(event);
					optionsListener.actionPerformed(optionEvent);
				}
				else
				{
					JFrame frame = new JFrame();
					JOptionPane.showMessageDialog(frame, "You must specify an O2 Saturation Mininum \nAND Plethysmograph Algorithm to start the lock in Pleth Mode.");
				}
			}
			
			else if (mode == OperatingMode.HEART_RATE_VS_PULSE_RATE)
			{
				SetAlarmEvent event = new SetAlarmEvent(this);
				if (selection1.getSelectedItem() != null && selection3.getSelectedItem() != null)
				{
					event.o2Minimum = ((int) selection1.getSelectedItem());
					event.minDHrPr = ((int) selection3.getSelectedItem());
					
					if (selection4.getSelectedItem() != null)
						event.maxO2RateOfChange = ((int) selection4.getSelectedItem());
					if (selection5.getSelectedItem() != null)
						event.maxDHrPrRateOfChange = ((int) selection5.getSelectedItem());
					if (alarmListener != null)
						alarmListener.actionPerformed(event);
					optionsListener.actionPerformed(optionEvent);
				}
				else
				{
					JFrame frame = new JFrame();
					JOptionPane.showMessageDialog(frame, "You must specify an O2 Saturation Mininum "
							+ "\nAND Minimum Heart Rate vs. Pulse Rate differential\nto start the lock in Heart Rate vs. Pulse Rate Mode.");
				}
			}
			
			else if (mode == OperatingMode.CAPNOGRAPH)
			{
				SetAlarmEvent event = new SetAlarmEvent(this);
				if (selection1.getSelectedItem() != null && selection3.getSelectedItem() != null)
				{
					event.co2Maximum = ((int) selection1.getSelectedItem());
					event.respRateMinimum = ((int) selection3.getSelectedItem());
					if (selection4.getSelectedItem() != null)
						event.maxCo2RateOfChange = ((int) selection4.getSelectedItem());
					if (selection5.getSelectedItem() != null)
						event.maxRespRateRateOfChange = ((int) selection5.getSelectedItem());
					if (alarmListener != null)
						alarmListener.actionPerformed(event);
					optionsListener.actionPerformed(optionEvent);
				}
				else
				{
					JFrame frame = new JFrame();
					JOptionPane.showMessageDialog(frame, "You must specify a CO2 Saturation Maximum "
							+ "\nAND Minimum Respiration Rate to start the lock in Capnograph Mode.");
				}
			}
		}
		
	}

	public void setRulePanelListener(OptionsPanelListener listener) {
		this.optionsListener = listener;
		
	}
	
	

}
