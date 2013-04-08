package org.mdpnp.apps.testapp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class MainMenuPanel extends JPanel {
	private static final Logger log = LoggerFactory.getLogger(MainMenuPanel.class);
	private final JList appList;
	private final JList deviceList;
	private final JButton biomedConsole;
	

	public MainMenuPanel() {
		super(new GridLayout(1,2));
		
		setOpaque(false);
		
		appList = new JList(new String[] {"Infusion Safety", "Data Fusion", "X-Ray Ventilator Sync"});
		deviceList = new JList();
		appList.setFont(Font.decode("verdana-30"));
		deviceList.setFont(Font.decode("verdana-30"));
		
		appList.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				
				Component c = super.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);
//				c.setBackground(DemoPanel.lightBlue);
				c.setBackground(new Color(1.0f, 1.0f, 1.0f, 0.5f));
				if(c instanceof JComponent) {
//					((JComponent)c).setOpaque(false);
//					((JComponent)c).setBorder(new LineBorder(new Color(1.0f, 1.0f, 1.0f, 0.1f), 5));
				}
				return c;
			}
		});
		deviceList.setCellRenderer(new DeviceListCellRenderer());
		
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		leftPanel.add(new JLabel("Available Applications"), BorderLayout.NORTH);
		leftPanel.add(new JScrollPane(appList), BorderLayout.CENTER);
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(new JLabel("Connected Devices"), BorderLayout.NORTH);
		rightPanel.add(new JScrollPane(deviceList), BorderLayout.CENTER);
		rightPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		biomedConsole = new JButton("Force Discovery"); 
		
		rightPanel.add(biomedConsole, BorderLayout.SOUTH);
		add(leftPanel);
		add(rightPanel);
		
	}
	
	public JList getAppList() {
		return appList;
	}
	
	public JList getDeviceList() {
		return deviceList;
	}
	public JButton getBiomedConsole() {
		return biomedConsole;
	}
}
