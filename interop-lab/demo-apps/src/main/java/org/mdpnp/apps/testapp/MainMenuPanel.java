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
		appList.setBorder(null);
		
		deviceList = new JList();
		appList.setFont(bigFont);
		deviceList.setFont(bigFont);
		deviceList.setBorder(null);
		
		AppListCellRenderer lcr = new AppListCellRenderer();
		DeviceListCellRenderer dlcr = new DeviceListCellRenderer();
		
		CompoundBorder cb = new CompoundBorder(new EmptyBorder(5,5,5,5), new LineBorder(Color.black, 1, true));
		CompoundBorder cb1 = new CompoundBorder(new EmptyBorder(5,5,5,5), new LineBorder(Color.black, 1, true));

		lcr.setBorder(cb);
		dlcr.setBorder(cb1);
		
		appList.setCellRenderer(lcr);
		deviceList.setCellRenderer(dlcr);
		
		

		JLabel lbl;
		add(lbl = new JLabel("Available Applications"), gbc);
		lbl.setFont(bigFont);
		gbc.gridy++;
		gbc.weighty = 100.0;
		JScrollPane scrollAppList = new JScrollPane(appList);
		scrollAppList.setBorder(null);
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
		scrollDeviceList.setBorder(null);
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
