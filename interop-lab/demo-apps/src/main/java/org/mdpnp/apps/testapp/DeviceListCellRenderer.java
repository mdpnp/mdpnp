package org.mdpnp.apps.testapp;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;

import org.mdpnp.messaging.Device;
import org.mdpnp.messaging.DeviceIcon;
import org.mdpnp.nomenclature.ConnectedDevice.State;

public class DeviceListCellRenderer extends DefaultListCellRenderer {
	@Override
	public Component getListCellRendererComponent(JList list,
			Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		Component c = super.getListCellRendererComponent(list, value, index, isSelected,
				cellHasFocus);
		c.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
		Device device = value == null ? null : (Device)value;
		
		
		if(null != device) {
			DeviceIcon di = device.getIcon();
			State connectedState = device.getConnectedState();
			if(null != connectedState && State.Connected.equals(connectedState)) {
				c.setForeground(Color.black);
				
			} else {
//				log.debug("Device connectedState:"+connectedState);
				c.setForeground(Color.red);
				
			}
			
			if(di != null && c instanceof JLabel) {
				((JLabel)c).setIcon(di);
			}
		} else {
			c.setForeground(Color.red);
		}

		if(c instanceof JComponent) {
//			((JComponent)c).setOpaque(false);
//			((JComponent)c).setBorder(new LineBorder(new Color(1.0f, 1.0f, 1.0f, 0.1f), 5));
		}
		return c;
	}
}
