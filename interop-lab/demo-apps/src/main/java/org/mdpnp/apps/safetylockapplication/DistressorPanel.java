package org.mdpnp.apps.safetylockapplication;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

public class DistressorPanel extends JPanel implements ActionListener {
	
	
	private ImageButton o2Distressor = new ImageButton(Resources.loadImage("o2DistressorInactive.png"));
	private ImageButton o2Roc = new ImageButton(Resources.loadImage("o2RocInactive.png"));
	private ImageButton co2Distressor = new ImageButton(Resources.loadImage("co2DistressorInactive.png"));
	private ImageButton plethysmographDistressor = new ImageButton(Resources.loadImage("plethDistressorInactive.png"));
	private ImageButton co2Roc = new ImageButton(Resources.loadImage("co2RocInactive.png"));
	private ImageButton respiratoryDistressor = new ImageButton(Resources.loadImage("RespiratoryRateDistressorInactive.png"));
	private ImageButton respiratoryRoc = new ImageButton(Resources.loadImage("RespirationRocInactive.png"));
	private ImageButton hrprDistressor = new ImageButton(Resources.loadImage("HrprDistressorInactive.png"));
	private ImageButton hrprRoc = new ImageButton(Resources.loadImage("HrprRocInactive.png"));
	
	private SimulatedPatient patient;
	
	public DistressorPanel(SimulatedPatient patient)
	{
		this.patient = patient;
		
		o2Distressor.addActionListener(this);
		o2Roc.addActionListener(this);
		co2Distressor.addActionListener(this);
		co2Roc.addActionListener(this);
		respiratoryDistressor.addActionListener(this);
		respiratoryRoc.addActionListener(this);
		hrprDistressor.addActionListener(this);
		hrprRoc.addActionListener(this);
		plethysmographDistressor.addActionListener(this);
		
		setLayout(new GridBagLayout());
		setBackground(Resources.physiologicalDisplayPanelBackgroundColor);
		setupGridLayout();
		
		o2Distressor.doClick();
	}
	
	public void setupGridLayout()
	{
		GridBagConstraints gc = new GridBagConstraints();
		gc.gridx = 0;
		gc.gridy = 0;
		gc.weightx = 0;
		gc.weighty = 0;
		
		add(o2Distressor, gc);
		gc.gridy++;
		add(co2Distressor, gc);
		gc.gridy++;
		add(respiratoryDistressor, gc);
		gc.gridy++;
		add(plethysmographDistressor, gc);
		gc.gridy++;
		add(hrprDistressor, gc);
		gc.gridy++;
		add(o2Roc, gc);
		gc.gridy++;
		add(co2Roc, gc);
		gc.gridy++;
		add(respiratoryRoc, gc);
		gc.gridy++;
		add(hrprRoc, gc);
	}
	
	private void resetAll()
	{
		o2Distressor.updateImage(Resources.loadImage("o2DistressorInactive.png"));
		patient.o2Bad = false;
		o2Roc.updateImage(Resources.loadImage("o2RocInactive.png"));
		patient.O2RocBad = false;
		co2Distressor.updateImage(Resources.loadImage("co2DistressorInactive.png"));
		patient.co2Bad = false;
		co2Roc.updateImage(Resources.loadImage("co2RocInactive.png"));
		patient.Co2RocBad = false;
		respiratoryDistressor.updateImage(Resources.loadImage("RespiratoryRateDistressorInactive.png"));
		patient.respBad = false;
		respiratoryRoc.updateImage(Resources.loadImage("RespirationRocInactive.png"));
		patient.RespRocBad = false;
		hrprDistressor.updateImage(Resources.loadImage("HrprDistressorInactive.png"));
		patient.hrprBad = false;
		hrprRoc.updateImage(Resources.loadImage("HrprRocInactive.png"));
		patient.HrprRocBad = false;
		plethysmographDistressor.updateImage(Resources.loadImage("plethDistressorInactive.png"));
		patient.plethBad = false;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (!patient.distressed)
		{
			resetAll();
			Object source = e.getSource();
			if (source == o2Distressor)
			{
				o2Distressor.updateImage(Resources.loadImage("o2DistressorActive.png"));
				patient.o2Bad = true;
			}
			else if (source == o2Roc)
			{
				o2Roc.updateImage(Resources.loadImage("o2RocActive.png"));
				patient.O2RocBad = true;
			}
			else if (source == co2Distressor)
			{
				co2Distressor.updateImage(Resources.loadImage("co2DistressorActive.png"));
				patient.co2Bad = true;
			}
			else if (source == co2Roc)
			{
				co2Roc.updateImage(Resources.loadImage("co2RocActive.png"));
				patient.Co2RocBad = true;
			}
			else if (source == respiratoryDistressor)
			{
				respiratoryDistressor.updateImage(Resources.loadImage("RespiratoryRateDistressorActive.png"));
				patient.respBad = true;
			}
			else if (source == respiratoryRoc)
			{
				respiratoryRoc.updateImage(Resources.loadImage("RespirationRocActive.png"));
				patient.RespRocBad = true;
			}
			else if (source == hrprDistressor)
			{
				hrprDistressor.updateImage(Resources.loadImage("HrprDistressorActive.png"));
				patient.hrprBad = true;
			}
			else if (source == hrprRoc)
			{
				hrprRoc.updateImage(Resources.loadImage("HrprRocActive.png"));
				patient.HrprRocBad = true;
			}
			else if (source == plethysmographDistressor)
			{
				plethysmographDistressor.updateImage(Resources.loadImage("plethDistressorActive.png"));
				patient.plethBad = true;
			}
		}
	}

}
