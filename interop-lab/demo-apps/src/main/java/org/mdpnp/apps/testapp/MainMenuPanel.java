package org.mdpnp.apps.testapp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
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
	


	public MainMenuPanel(String[] names) {
		super(new GridBagLayout());
		
		setOpaque(false);

		GridBagConstraints gbc = new GridBagConstraints(0,0,1,1,1.0,1.0,GridBagConstraints.BASELINE, GridBagConstraints.BOTH, new Insets(2,10,2,10), 0, 0);
		appList = new JList(names);
		appList.setSelectionBackground(appList.getBackground());
		appList.setSelectionForeground(appList.getForeground());
		deviceList = new JList();
		appList.setFont(Font.decode("verdana-30"));
		deviceList.setFont(Font.decode("verdana-30"));
		ListCellRenderer lcr = new DefaultListCellRenderer();
		((DefaultListCellRenderer)lcr).setBackground(new Color(1f, 1f, 1f, .5f));
		
		appList.setCellRenderer(lcr);
		deviceList.setCellRenderer(new DeviceListCellRenderer());

		add(new JLabel("Available Applications"), gbc);
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
		add(new JLabel("Connected Devices"), gbc);
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
