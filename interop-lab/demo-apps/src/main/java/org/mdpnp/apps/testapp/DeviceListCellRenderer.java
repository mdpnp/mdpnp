package org.mdpnp.apps.testapp;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;

public class DeviceListCellRenderer extends DefaultListCellRenderer {
	@Override
	public Component getListCellRendererComponent(JList list,
			Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
	    ice.DeviceIdentity device = value == null ? null : (ice.DeviceIdentity)value;
	    
		Component c = super.getListCellRendererComponent(list, null==device?null:device.model, index, isSelected,
				cellHasFocus);
		c.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
		
		
		
		if(null != device) {
//			DeviceIcon di = device.getIcon();
		    DeviceIcon di = new DeviceIcon(device.icon);
		    
//			ice.ConnectionState connectedState = device.
//			if(null != connectedState && State.Connected.equals(connectedState)) {
//				c.setForeground(Color.black);
				
//			} else {
//				log.debug("Device connectedState:"+connectedState);
//				c.setForeground(Color.red);
				
//			}
			
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
