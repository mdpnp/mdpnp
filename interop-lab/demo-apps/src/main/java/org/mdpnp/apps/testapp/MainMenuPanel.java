package org.mdpnp.apps.testapp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class MainMenuPanel extends JPanel {
	private static final Logger log = LoggerFactory.getLogger(MainMenuPanel.class);
	private final JList appList;
	private final JList deviceList;
	private final JButton spawnDeviceAdapter;
	
	private static final boolean isTrue(String property) {
	    String s = System.getProperty(property);
	    if(null != s && "true".equals(s)) {
	        return true;
	    } else {
	        return false;
	    }
	}

	public MainMenuPanel() {
		super(new GridLayout(1,2));
		
		setOpaque(false);
		List<String> names = new ArrayList<String>();
		if(!isTrue("NOPCA")) {
		    names.add("Infusion Safety");
		}
		if(!isTrue("NOXRAYVENT")) {
		    names.add("X-Ray Ventilator Sync");
		}

		appList = new JList(names.toArray(new String[0]));
		appList.setSelectionBackground(appList.getBackground());
		appList.setSelectionForeground(appList.getForeground());
		deviceList = new JList();
		appList.setFont(Font.decode("verdana-30"));
		deviceList.setFont(Font.decode("verdana-30"));
		ListCellRenderer lcr = new DefaultListCellRenderer();
		((DefaultListCellRenderer)lcr).setBackground(new Color(1f, 1f, 1f, .5f));
		
		appList.setCellRenderer(lcr);
		deviceList.setCellRenderer(new DeviceListCellRenderer());
		

		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		leftPanel.add(new JLabel("Available Applications"), BorderLayout.NORTH);
		leftPanel.add(new JScrollPane(appList), BorderLayout.CENTER);
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(new JLabel("Connected Devices"), BorderLayout.NORTH);
//		JScrollPane p;
		rightPanel.add(new JScrollPane(deviceList), BorderLayout.CENTER);
//		p.setBorder(BorderFactory.createEmptyBorder());
		rightPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
//		deviceList.setBorder(new EmptyBorder(0,0,0,0));
		
		spawnDeviceAdapter = new JButton("Create a local ICE Device Adapter..."); 
		
		
		rightPanel.add(spawnDeviceAdapter, BorderLayout.SOUTH);
		add(leftPanel);
		add(rightPanel);
		
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
