package org.mdpnp.apps.testapp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;

import org.mdpnp.apps.gui.swing.DevicePanelFactory;
import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.GatewayListener;
import org.mdpnp.comms.IdentifiableUpdate;
import org.mdpnp.comms.data.identifierarray.IdentifierArrayUpdate;
import org.mdpnp.comms.data.text.MutableTextUpdate;
import org.mdpnp.comms.data.text.MutableTextUpdateImpl;
import org.mdpnp.comms.nomenclature.Association;
import org.mdpnp.comms.nomenclature.Device;

public class NetworkController {
	
	private static GatewayListener gatewayListener;
	
	public static void main(String[] args) throws Exception {
//		if(!DDS.init()) {
//			throw new RuntimeException("Unable to find DDS");
//		}

		final Gateway gateway = new Gateway();

		final JFrame frame = new JFrame("DDS Monster");
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		JMenuItem forceDiscoveryItem = new JMenuItem("Force Discovery");
		
		fileMenu.add(forceDiscoveryItem);
		JMenuItem exitItem = new JMenuItem("Exit");
		fileMenu.add(exitItem);
		
		frame.setJMenuBar(menuBar);
		
		
		final JPanel dataPanel = new JPanel(), devicePanel = new JPanel();
		dataPanel.setBackground(Color.black);
		
		GridLayout gridLayout = new GridLayout(3,3);
		gridLayout.setHgap(10);
		gridLayout.setVgap(10);
		dataPanel.setLayout(gridLayout);

		JTable table = new JTable();

		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		devicePanel.setLayout(new BorderLayout());
		devicePanel.add(new JScrollPane(table), BorderLayout.CENTER);
		
//		EngineerConsole ec = new EngineerConsole(gateway);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Data", dataPanel);
		tabbedPane.addTab("Devices", devicePanel);
//		tabbedPane.addTab("Engineer Console", new JScrollPane(ec));
		frame.getContentPane().add(tabbedPane);
				
		final Map<String, List<org.mdpnp.apps.gui.swing.DevicePanel>> panels = new HashMap<String, List<org.mdpnp.apps.gui.swing.DevicePanel>>();
		gateway.addListener(gatewayListener  = new GatewayListener() {

			@Override
			public void update(IdentifiableUpdate<?> update) {
				if(Device.GET_AVAILABLE_IDENTIFIERS.equals(update.getIdentifier())) {
//					System.out.println("MY PRECIOUS IDENTIFIERS:"+update);
					String source = update.getSource();
					if(!panels.containsKey(source)) {
						List<org.mdpnp.apps.gui.swing.DevicePanel> panelsForThisDevice = new ArrayList<org.mdpnp.apps.gui.swing.DevicePanel>();
						for(final org.mdpnp.apps.gui.swing.DevicePanel panel : DevicePanelFactory.findPanel( (IdentifierArrayUpdate) update, gateway, source)) {
							panel.setForeground(Color.green);
							panel.setBackground(Color.black);
							
							panelsForThisDevice.add(panel);
							
//							System.out.println("BUILT A DISPLAY PANEL:"+panel);
							try {
								SwingUtilities.invokeAndWait(new Runnable() {
									public void run() {
										dataPanel.add(panel);
										dataPanel.revalidate();
										dataPanel.repaint();
									}
								});
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						panels.put(source, panelsForThisDevice);
					}
				} else if(Association.ACKNOWLEDGE_ARRIVE.equals(update.getIdentifier())) {
//					System.out.println("ACK ARRIVE");
					MutableTextUpdate tu = new MutableTextUpdateImpl(Device.REQUEST_AVAILABLE_IDENTIFIERS, "");
					tu.setTarget(update.getTarget());
					tu.setValue("");
					tu.setSource(update.getSource());
					gateway.update(gatewayListener, tu);
				} else if(Association.ACKNOWLEDGE_DEPART.equals(update.getIdentifier())) {
//					System.out.println("ACK DEPART");
					String target = update.getTarget();
					final List<org.mdpnp.apps.gui.swing.DevicePanel> panelsForThisDevice = panels.get(target);
					if(null != panelsForThisDevice) {
						for(final org.mdpnp.apps.gui.swing.DevicePanel panel : panelsForThisDevice) {
							try {
								SwingUtilities.invokeAndWait(new Runnable() {
									public void run() {
										dataPanel.remove(panel);
										dataPanel.revalidate();
										dataPanel.repaint();
									}
								});
							} catch (InvocationTargetException e1) {
								e1.printStackTrace();
							} catch (InterruptedException e2) {
								e2.printStackTrace();
							}
						}
						panels.remove(target);
					}
				}
			}
		});
		
		final int domainId = 0;
		final org.mdpnp.transport.NetworkController nc = new org.mdpnp.transport.NetworkController(domainId, gateway);
		table.setModel(nc.getDeviceTableModel());
		table.getColumnModel().getColumn(0).setPreferredWidth(350);
		table.getColumnModel().getColumn(1).setPreferredWidth(300);
		
		forceDiscoveryItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				nc.solicit();
			}
			
		});
		
		exitItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
			}
			
		});
		
		// solicit only after registration or else we're bound to miss something
		nc.solicit();
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				nc.tearDown();
				super.windowClosing(e);
			}
		});

		
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
}
