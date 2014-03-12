package org.mdpnp.gip.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.mdpnp.gip.ui.units.MassUnits;
import org.mdpnp.gip.ui.units.RatioUnits;
import org.mdpnp.gip.ui.units.RatioUnitsFactory;
import org.mdpnp.gip.ui.units.TimeUnits;
import org.mdpnp.gip.ui.units.Units;
import org.mdpnp.gip.ui.units.VolumeUnits;
import org.mdpnp.gip.ui.values.MassValue;
import org.mdpnp.gip.ui.values.VolumeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class DrugPanel extends javax.swing.JPanel implements DrugListener, PatientListener {
	private final DefaultComboBoxModel drugEntryModel = new DefaultComboBoxModel();
	private final JComboBox drugName = new JComboBox(drugEntryModel);
	private final ValueField<MassUnits> massField = new ValueField<MassUnits>();
	private final ValueField<VolumeUnits> volumeField = new ValueField<VolumeUnits>();
	private final JLabel concentration = new JLabel("---");
	
	private DrugModel drug;
	private PatientModel patient;
	private InfusionModel infusion;
	
	private static final JLabel rightLabel(String txt) {
		JLabel lbl = new JLabel(txt);
		lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		lbl.setHorizontalTextPosition(SwingConstants.RIGHT);
		lbl.setVerticalTextPosition(SwingConstants.CENTER);
		return lbl;
	}
	
	private static final <U extends Units> U find(U[] u, String s) {
		if(s == null) {
			return null;
		}
		for(U _u : u) {
			if(s.equals(_u.getAbbreviatedName()) || s.equals(_u.getName())) {
				return _u;
			}
		}
		return null;
	}
	
	private static final Map<String, RatioUnits> doseModes = new HashMap<String, RatioUnits>();
	static {
		doseModes.put("mL/hr", RatioUnitsFactory.mLPerHour);
		doseModes.put("mcg/min", RatioUnitsFactory.mcgPerMinute);
		doseModes.put("mg/kg/hr", RatioUnitsFactory.mgPerKgPerHour);
		doseModes.put("mg/hr", RatioUnitsFactory.mgPerHour);
		doseModes.put("mg/min", RatioUnitsFactory.mgPerMinute);
		doseModes.put("mcg/kg/hr", RatioUnitsFactory.mcgPerKgPerHour);
		doseModes.put("g/hr", RatioUnitsFactory.gPerHour);
	}
	private final Logger log = LoggerFactory.getLogger(DrugPanel.class);
	public DrugPanel() {
		super(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.BASELINE, GridBagConstraints.NONE, new Insets(5,5,5,5), 5, 5);

		drugName.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				switch(e.getStateChange()) {
				case ItemEvent.SELECTED:
					DrugEntry de = (DrugEntry) drugName.getSelectedItem();
					drug.setName(de.getName());
					MassValue m = drug.getConcentration().getMass();
					VolumeValue v = drug.getConcentration().getVolume();
					MassUnits mu = find(m.getAcceptableUnits(), de.getAmountUnits());
					if(null != mu) {
						m.setUnits(mu);
						m.setValue(de.getAmount());
					} else {
						log.warn("Invalid units " + mu + " for mass");
						m.setValue(null);
					}
					
					VolumeUnits vu = find(v.getAcceptableUnits(), de.getDiluentUnits());
					if(null != vu) {
						v.setUnits(vu);
						v.setValue(de.getDiluent());
					} else {
						log.warn("Invalid units " + vu + " for volume");
						v.setValue(null);
					}
					
					InfusionModel infusion = DrugPanel.this.infusion;
					if(infusion != null) {
						String doseMode = de.getDoseMode();
						if(doseMode != null) {
							TimeUnits timeUnits = find(infusion.getDuration().getAcceptableUnits(), doseMode.substring(doseMode.lastIndexOf('/')+1, doseMode.length()));
							infusion.getDuration().setUnits(timeUnits);
							infusion.getRate().setUnits(doseModes.get(doseMode));
						}
						infusion.getRate().setMinimum(de.getLowerHardLimit());
						infusion.getRate().setMaximum(de.getUpperHardLimit());
						infusion.getRate().setValue(de.getStartingRate());
						infusion.getRate().setSoftMinimum(de.getLowerSoftLimit());
						infusion.getRate().setSoftMaximum(de.getUpperSoftLimit());
						infusion.getRate().setStarting(de.getStartingRate());
					}
					break;
				}
			}
			
		});
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(rightLabel("Drug Name:"), gbc);
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.NONE;
		drugName.setAlignmentX(0);
		drugName.setAlignmentY(0);
		
		add(drugName, gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(rightLabel("Amount:"), gbc);
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.NONE;
		add(massField, gbc);
		gbc.gridx = 2;
		add(new JLabel("/"), gbc);
		gbc.gridx = 3;
		add(volumeField, gbc);
		gbc.gridy++;
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(rightLabel("Concentration:"), gbc);
		gbc.gridx = 1;
		gbc.fill = GridBagConstraints.NONE;
		add(concentration, gbc);
	}
	private static final NumberFormat snf = NumberFormat.getNumberInstance();
	static {
		snf.setMinimumFractionDigits(0);
		snf.setMinimumIntegerDigits(1);
		snf.setMaximumFractionDigits(4);
		snf.setGroupingUsed(false);
	}
	private void update() {
		DrugModel drug = this.drug;
		if(drug != null) {
			Double value = drug.getConcentration().getValue();
			String name = drug.getConcentration().getAbbreviatedName();
			
			concentration.setText( (null==value?"":snf.format(value))+" "+(null==name?"":name));
		}
	}
	
	public void setInfusionModel(InfusionModel infusionModel) {
		this.infusion = infusionModel;
	}
	public void setPatientModel(PatientModel patientModel) {
		if(null != this.patient) {
			this.patient.removeListener(this);
		}
		this.patient = patientModel;
		if(null != this.patient) {
			this.patient.addListener(this);
			patientChanged(this.patient);
		}
	}
	
	public void setModel(DrugModel drug) {
		if(this.drug != null) {
			this.drug.removeListener(this);
		}
		this.drug = drug;
		if(this.drug != null) {
			this.drug.addListener(this);
			this.massField.setModel(drug.getConcentration().getMass());
			this.volumeField.setModel(drug.getConcentration().getVolume());
		}
		update();
	}
	public DrugModel getModel() {
		return drug;
	}
	@Override
	public void drugChanged(DrugModel drug) {
		update();
	}
	
	
	private CareArea lastCareArea;
	
	@Override
	public void patientChanged(PatientModel p) {
		CareArea currentCareArea = p.getCareArea();
		
		if(currentCareArea.equals(lastCareArea)) {
			return;
		}
		drugEntryModel.removeAllElements();
		drugEntryModel.addElement(new DrugEntry(""));
		for(DrugEntry de : currentCareArea.getDrugEntries()) {
			drugEntryModel.addElement(de);
		}
		lastCareArea = currentCareArea;
	}
}
