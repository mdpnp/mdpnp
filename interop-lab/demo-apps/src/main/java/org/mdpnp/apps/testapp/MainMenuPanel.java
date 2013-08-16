package org.mdpnp.apps.testapp;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class MainMenuPanel extends JPanel {
	private static final Logger log = LoggerFactory.getLogger(MainMenuPanel.class);
	private final JList appList;
	private final JList deviceList;
	private final JButton spawnDeviceAdapter;
	


	public MainMenuPanel(AppType[] appTypes) {
		super(new GridBagLayout());
		
		setOpaque(false);

		Font bigFont = Font.decode("verdana-30");
		
		GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.BASELINE, GridBagConstraints.BOTH, new Insets(2,10,2,10), 0, 0);
		appList = new JList(appTypes);
		appList.setSelectionBackground(appList.getBackground());
		appList.setSelectionForeground(appList.getForeground());
		deviceList = new JList();
		appList.setFont(bigFont);
		deviceList.setFont(bigFont);
		ListCellRenderer lcr = new DefaultListCellRenderer() {
		    @Override
    		public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
    		        boolean cellHasFocus) {
		        AppType at = value instanceof AppType ? (AppType) value : null;
		        value = at != null ? at.getName() : value;
    		    Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    		    if(c instanceof JLabel) {
    		        if(at != null) {
    		            ((JLabel)c).setIcon(at.getIcon());
    		        } else {
    		            ((JLabel)c).setIcon(null);
    		        }
    		    }

    		    return c;
    		}  
//		    private Dimension myDimension = new Dimension();
//		    @Override
//		    protected void paintComponent(Graphics g) {
//		        if(isOpaque()) {
//		            g.setColor(getBackground());
//		            myDimension = getSize(myDimension);
//		            g.fillRect(0, 0, myDimension.width, myDimension.height);
//		        }
//		        ((Graphics2D)g).setBackground(getBackground());
//		        super.paintComponent(g);
//		    }
		};
//		((DefaultListCellRenderer)lcr).setBackground(new Color(1f, 1f, 1f, .5f));
		((DefaultListCellRenderer)lcr).setOpaque(false);
		((DefaultListCellRenderer)lcr).setVerticalTextPosition(SwingConstants.TOP);
//		Border b = new LineBorder(Color.black, 1);
//		BorderFactory.createCompoundBorder(outsideBorder, insideBorder)
//		((DefaultListCellRenderer)lcr).setBorder(new CompoundBorder(new LineBorder(Color.black, 5), new EmptyBorder(1, 5, 1, 5)));
		
		
		appList.setCellRenderer(lcr);
		deviceList.setCellRenderer(new DeviceListCellRenderer());

		JLabel lbl;
		add(lbl = new JLabel("Available Applications"), gbc);
		lbl.setFont(bigFont);
		gbc.gridy++;
		gbc.weighty = 100.0;
		JScrollPane scrollAppList = new JScrollPane(appList);
		scrollAppList.getViewport().setOpaque(false);
		add(scrollAppList, gbc);
		scrollAppList.setOpaque(false);
        appList.setOpaque(false);
		
		gbc.gridy++;
		gbc.weighty = 1.0;
		add(new JLabel(), gbc);

		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.gridx++;
		gbc.weighty = 1.0;
		add(lbl = new JLabel("Connected Devices"), gbc);
		lbl.setFont(bigFont);
		gbc.gridy++;
		gbc.weighty = 100.0;
		
		JScrollPane scrollDeviceList = new JScrollPane(deviceList); 
		add(scrollDeviceList, gbc);
		scrollDeviceList.setOpaque(false);
		scrollDeviceList.getViewport().setOpaque(false);
		deviceList.setOpaque(false);
		
		gbc.gridy++;
		gbc.weighty = 1.0;
		
		spawnDeviceAdapter = new JButton("Create a local ICE Device Adapter..."); 
		
		add(spawnDeviceAdapter, gbc);
	}
	
	public JList getAppList() {
		return appList;
	}
	
	public JList getDeviceList() {
		return deviceList;
	}
	public JButton getSpawnDeviceAdapter() {
		return spawnDeviceAdapter;
	}
}
