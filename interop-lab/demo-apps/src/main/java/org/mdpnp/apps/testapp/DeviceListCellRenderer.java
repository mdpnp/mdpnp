package org.mdpnp.apps.testapp;

import ice.DeviceConnectivity;

import java.awt.Color;
import java.awt.Component;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListModel;

public class DeviceListCellRenderer extends DefaultListCellRenderer {
    
	@Override
	public Component getListCellRendererComponent(JList list,
			Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
	    Device device = value == null ? null : (Device)value;
	    
		Component c = super.getListCellRendererComponent(list, null==device?null:device.getDeviceIdentity().model, index, isSelected,
				cellHasFocus);
		c.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
		
		
		
		if(null != device) {
		    DeviceIcon di = device.getIcon();
		    ListModel m = list.getModel();
		    if(m instanceof DeviceListModel) {
		        DeviceListModel dilm = (DeviceListModel) m;
		        DeviceConnectivity dc = device.getDeviceConnectivity();
		        // NOT A CONNECTED DEVICE (DONGLE)
		        if(null == dc) {
		            di.setConnected(true);
		        }
		        
		        if(null != dc) {
		            di.setConnected(ice.ConnectionState.Connected.equals(dc.state));
		        }
		    }
//			DeviceIcon di = device.getIcon();
//		    DeviceIcon di = new DeviceIcon(device.icon);
		    
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
