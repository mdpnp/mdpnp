package org.mdpnp.gip.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.mdpnp.gip.ui.units.LengthUnits;
import org.mdpnp.gip.ui.units.MassUnits;

@SuppressWarnings("serial")
/**
 * @author Jeff Plourde
 *
 */
public class PatientPanel extends JPanel implements PatientListener {
	
	private static final JLabel rightLabel(String txt) {
		JLabel lbl = new JLabel(txt);
		lbl.setHorizontalAlignment(SwingConstants.RIGHT);
		lbl.setHorizontalTextPosition(SwingConstants.RIGHT);
		return lbl;
	}
		private PatientModel patient;
		private final JTextField idField = new JTextField("1", 6);
		//
		private final DefaultComboBoxModel careAreaModel = new DefaultComboBoxModel();
		private final JComboBox careAreaBox = new JComboBox(careAreaModel);
		private final ValueField<MassUnits> weightField = new ValueField<MassUnits>();
		private final ValueField<LengthUnits> heightField = new ValueField<LengthUnits>();
		private final JLabel bsa;
		
		public void setModel(PatientModel patient) {
			if(null != this.patient) {
				this.patient.removeListener(this);
			}
			weightField.setModel(null);
			heightField.setModel(null);
			
			this.patient = patient;
			
			if(null != this.patient) {
				this.patient.addListener(this);
				weightField.setModel(this.patient.getWeight());
				heightField.setModel(this.patient.getHeight());
				this.patient.setCareArea((CareArea) careAreaModel.getSelectedItem());
			}

			update();
		}
		public PatientModel getModel() {
			return patient;
		}
		
		public PatientPanel(DrugLibrary drugLibrary) {
			super(new GridBagLayout());
			
			careAreaBox.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent e) {
					switch(e.getStateChange()) {
					case ItemEvent.SELECTED:
						PatientModel patient = PatientPanel.this.patient;
						if(null != patient) {
							patient.setCareArea((CareArea) careAreaBox.getSelectedItem());
						}
						break;
					}
				}
				
			});
			
			careAreaModel.removeAllElements();
			careAreaModel.addElement(new CareArea(""));
			for(CareArea ca : drugLibrary.getCareAreas()) {
				careAreaModel.addElement(ca);
			}
			
			
//			GroupLayout layout = new GroupLayout(this);
//			setLayout(layout);
			
//			layout.set
			
			GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0);
			
			gbc.gridheight = 5;
			JPanel picPanel = new JPanel(new BorderLayout());
			JLabel pic = new JLabel(new ImageIcon(PatientPanel.class.getResource("placeholder.png")));
			pic.setBorder(BorderFactory.createLineBorder(Color.black, 1));
			picPanel.add(pic, BorderLayout.CENTER);
			picPanel.add(new JTextField("Randall Jones"), BorderLayout.SOUTH);
			add(picPanel, gbc);
			

			gbc.gridheight = 1;
			gbc.gridx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			add(rightLabel("Care Area:"), gbc);
			gbc.gridx = 2;
			gbc.fill = GridBagConstraints.NONE;
			careAreaBox.setAlignmentX(SwingConstants.LEFT);
			add(careAreaBox, gbc);

			gbc.gridy = 1;
			gbc.gridx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			add(rightLabel("Id:"), gbc);
			gbc.gridx = 2; 
			gbc.fill = GridBagConstraints.NONE;
			
			add(idField, gbc);

			gbc.gridy = 2;
			gbc.gridx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			add(rightLabel("Weight:"), gbc);
			gbc.gridx = 2;
			gbc.fill = GridBagConstraints.NONE;
			add(weightField, gbc);

			gbc.gridy = 3;
			gbc.gridx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			add(rightLabel("Height:"), gbc);
			gbc.gridx = 2;
			gbc.fill = GridBagConstraints.NONE;
			add(heightField, gbc);

			gbc.gridy = 4;
			gbc.gridx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			add(rightLabel("BSA:"), gbc);
			gbc.gridx = 2;
			gbc.fill = GridBagConstraints.NONE;
			add(bsa = new JLabel("--- m??"), gbc);
			bsa.setHorizontalAlignment(SwingConstants.LEFT);
			update();
			
		}
		
		private void update() {
			PatientModel patient = this.patient;
			if(null != patient) {
				bsa.setText(format(patient.getBodySurfaceArea())+" m??");
			}
		}
		private static final String format(Double d) {
			if(null == d) {
				return "---";
			} else {
				return snf.format(d);
			}
		}
		private static final NumberFormat snf = NumberFormat.getNumberInstance();
		static {
			snf.setMinimumFractionDigits(2);
			snf.setMaximumFractionDigits(2);
		}
		@Override
		public void patientChanged(PatientModel p) {
			update();
		}
	}