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
import javax.swing.border.EmptyBorder;

public class AppListCellRenderer extends JComponent implements ListCellRenderer {
    
    private final JLabel icon = new JLabel();
    private final JLabel appName = new JLabel(" ");
    
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
    
    public AppListCellRenderer() {
        super();
        setLayout(new BorderLayout());
        setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
        setOpaque(true);
        
        add(icon, BorderLayout.WEST);
        add(appName, BorderLayout.CENTER);
    }
    
	@Override
	public Component getListCellRendererComponent(JList list,
			Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
	    AppType app = value == null ? null : (AppType)value;
	    
	    appName.setFont(list.getFont());
	    
		if(null != app) {
		    appName.setText(app.getName());
			icon.setIcon(app.getIcon());
		}
		return this;
	}
}
