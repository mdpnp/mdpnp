package org.mdpnp.apps.testapp;

import ice.DeviceConnectivity;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.border.EmptyBorder;

public class DeviceListCellRenderer extends JComponent implements ListCellRenderer {
    
    private final JLabel icon = new JLabel();
    private final JLabel modelName = new JLabel(" ");
    private final JLabel connectionStatus = new JLabel(" ");
    private final JLabel udi = new JLabel(" ");
    
    private Dimension myDimension = null;
    
    @Override
    protected void paintComponent(Graphics g) {
        if(isOpaque()) {
            g.setColor(getBackground());
            myDimension = getSize(myDimension);
            g.fillRect(0, 0, myDimension.width, myDimension.height);
        }
        super.paintComponent(g);
    }
    
    public DeviceListCellRenderer() {
        super();
        setLayout(new BorderLayout());
        setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
        setOpaque(true);
        
        udi.setFont(Font.decode("fixed-12"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;

        gbc.weightx = 0.5;
        gbc.gridheight = 3;
        add(icon, BorderLayout.WEST);
        
        JPanel text = new JPanel(new GridLayout(3, 1));
        text.setOpaque(false);
        text.setBorder(new EmptyBorder(1, 5, 1, 5));
        
        gbc.weightx = 1.5;
        gbc.gridheight = 1;
        gbc.gridx++;
        text.add(modelName);
        gbc.gridy++;
        text.add(udi);
        gbc.gridy++;
        text.add(connectionStatus);
        add(text, BorderLayout.CENTER);
        
    }
    
	@Override
	public Component getListCellRendererComponent(JList list,
			Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
	    Device device = value == null ? null : (Device)value;
	    
	    modelName.setFont(list.getFont());
	    connectionStatus.setFont(list.getFont());
	    
		if(null != device) {
		    modelName.setText(device.getDeviceIdentity().model);
		    udi.setText(device.getShortUDI());
		    DeviceIcon di = device.getIcon();
		    ListModel m = list.getModel();
		    if(m instanceof DeviceListModel) {
		        DeviceListModel dilm = (DeviceListModel) m;
		        DeviceConnectivity dc = device.getDeviceConnectivity();
		        // NOT A CONNECTED DEVICE (DONGLE)
		        if(null == dc) {
		            di.setConnected(true);
		            connectionStatus.setText("");
		        } else {
		            di.setConnected(ice.ConnectionState.Connected.equals(dc.state));
		            connectionStatus.setText(dc.state.toString());
		        }
		    }
			icon.setIcon(di);
		} else {
			connectionStatus.setForeground(Color.red);
		}
		return this;
	}
}
