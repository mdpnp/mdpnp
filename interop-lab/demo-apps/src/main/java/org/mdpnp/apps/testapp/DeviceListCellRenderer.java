package org.mdpnp.apps.testapp;

import ice.DeviceConnectivity;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

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
        add(icon, BorderLayout.WEST);

        JPanel text = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0, 0);
        text.setOpaque(false);
//        text.setBorder(new EmptyBorder(1, 5, 1, 5));

        gbc.gridwidth = 2;
        text.add(modelName, gbc);
        gbc.gridy++;
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        text.add(connectionStatus, gbc);
        
        gbc.gridx++;
        text.add(udi, gbc);
        add(text, BorderLayout.CENTER);
        
    }
    
    private final Border selectedBorder = new LineBorder(Color.blue, 1);
    
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
		setBorder(isSelected?selectedBorder:null);
		return this;
	}
}
