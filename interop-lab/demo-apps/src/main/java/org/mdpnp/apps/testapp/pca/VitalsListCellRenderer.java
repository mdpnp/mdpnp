package org.mdpnp.apps.testapp.pca;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import org.mdpnp.apps.testapp.DeviceIcon;
import org.mdpnp.apps.testapp.VitalsModel;
import org.mdpnp.apps.testapp.VitalsModel.Vitals;

public class VitalsListCellRenderer extends JPanel implements ListCellRenderer {

		private final JLabel name = new JLabel(" ");
		private final JLabel deviceName = new JLabel(" ");
		private final JLabel value = new JLabel(" ");
		private final JLabel icon = new JLabel(" ");
		private final JLabel udi = new JLabel(" ");
		public VitalsListCellRenderer() {
			super(new BorderLayout());
			setBorder(BorderFactory.createLineBorder(Color.gray, 1));
			setOpaque(false);
			name.setFont(name.getFont().deriveFont(24f));
			value.setFont(value.getFont().deriveFont(24f));
			deviceName.setFont(deviceName.getFont().deriveFont(14f));
			udi.setFont(Font.decode("fixed-10"));
//			value.setFont(value.getFont().deriveFont(14f));
			JPanel pan = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0);
			pan.setOpaque(false);
			
			gbc.weightx = 0.9;
			pan.add(name, gbc);
			gbc.gridy++;
			pan.add(deviceName, gbc);
			
			gbc.gridx = 1;
			gbc.weightx = 1.1;
			gbc.gridy = 0;
			pan.add(value, gbc);
			gbc.gridy++;
			pan.add(udi, gbc);
			
			add(icon, BorderLayout.WEST);
			add(pan, BorderLayout.CENTER);
			
		}
		
		@Override
		public Component getListCellRendererComponent(JList list, Object val,
				int index, boolean isSelected, boolean cellHasFocus) {
			VitalsModel.Vitals v = (Vitals) val;
			// strongly thinking about making these identifiers into strings
			String name = Integer.toString(v.getNumeric().name);
			String units = "";
			switch(v.getNumeric().name) {
			case ice.MDC_PULS_OXIM_SAT_O2.VALUE:
			    name = "SpO\u2082";
                units = "%";
                break;
			case ice.MDC_CO2_RESP_RATE.VALUE:
			case ice.MDC_RESP_RATE.VALUE:
			    name = "Respiratory Rate";
                units = "bpm";
                break;
			case ice.MDC_PULS_OXIM_PULS_RATE.VALUE:
			    name = "Heart Rate";
                units = "bpm";
                break;
			case ice.MDC_CONC_AWAY_CO2.VALUE:
			case ice.MDC_AWAY_CO2_EXP.VALUE:
			    name = "etCO\u2082";
                units = "mmHg";
                break;
			}

			this.name.setText(name);
			
			value.setText(""+v.getNumeric().value+" "+units);

			DeviceIcon di = v.getDevice().getIcon();
			if(null != di) {
			    icon.setIcon(new ImageIcon(di.getImage()));
			}
			deviceName.setText(v.getDevice().getMakeAndModel());
			udi.setText(v.getDevice().getShortUDI());
			return this;
		}
		
	}