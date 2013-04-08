/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.apps.testapp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.Collection;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.mdpnp.apps.gui.swing.DevicePanel;
import org.mdpnp.apps.gui.swing.DevicePanelFactory;
import org.mdpnp.comms.Gateway;
import org.mdpnp.comms.GatewayListener;
import org.mdpnp.comms.IdentifiableUpdate;
import org.mdpnp.comms.data.identifierarray.IdentifierArrayUpdate;
import org.mdpnp.comms.data.text.MutableTextUpdate;
import org.mdpnp.comms.data.text.MutableTextUpdateImpl;
import org.mdpnp.comms.nomenclature.Device;

@SuppressWarnings("serial")
public class DeviceAppFrame extends javax.swing.JFrame implements GatewayListener {
	
//	private Device model;
//	private JProgressBar progress;
	private JPanel devicePanel; 
	private GridLayout gridLayout = new GridLayout(1,1);
	private final Gateway gateway;
	
	private void init() {
		
		devicePanel = new JPanel();
		devicePanel.setLayout(gridLayout);
		
		setBackground(Color.black);
		getContentPane().setLayout(new BorderLayout());
//		getContentPane().add(progress = new JProgressBar(), BorderLayout.NORTH);
//		progress.setStringPainted(true);
//		progress.setString("");
		getContentPane().add(devicePanel, BorderLayout.CENTER);
		pack();
	}
	
	public DeviceAppFrame(String title, Gateway gateway) {
		super(title);
		this.gateway = gateway;
		gateway.addListener(this);
		MutableTextUpdate tu = new MutableTextUpdateImpl(Device.REQUEST_AVAILABLE_IDENTIFIERS);
		tu.setValue("");
		gateway.update(this, tu);
		init();
	}
	
//	public void setModel(Device model) {
//		if(this.model != null) {
//			for(Component c : devicePanel.getComponents()) {
//				if(c instanceof DevicePanel) {
//					((DevicePanel)c).setModel(null);
//				}
//			}
//			devicePanel.removeAll();
//		}
//		this.model = model;
//		if(model != null) {
//			Collection<DevicePanel> panels = DevicePanelFactory.findPanel(model.getClass());
//			gridLayout.setRows(panels.size());
//			for(DevicePanel panel : panels) {
//				panel.setOpaque(true);
//				panel.setBackground(Color.black);
//				panel.setForeground(Color.green);
//				panel.setModel(this.model);
//				devicePanel.add(panel);
//			}
//		}
//		
//		SwingUtilities.invokeLater(new Runnable() {
//
//			@Override
//			public void run() {
//				devicePanel.revalidate();
//				devicePanel.repaint();
//				
//			}
//			
//		});
//
//	}
//	
//	public JProgressBar getProgress() {
//		return progress;
//	}

	@Override
	public void update(IdentifiableUpdate<?> update) {
		if(update.getIdentifier().equals(Device.GET_AVAILABLE_IDENTIFIERS)) {
			devicePanel.removeAll();
			IdentifierArrayUpdate iau = (IdentifierArrayUpdate) update;
//			Set<Identifier> identifiers = new HashSet<Identifier>(Arrays.asList(iau.getValue()));
			Collection<DevicePanel> panels = DevicePanelFactory.findPanel(iau, gateway, update.getSource());
			gridLayout.setRows(panels.size());
			for(DevicePanel panel : panels) {
				panel.setOpaque(true);
				panel.setBackground(Color.black);
				panel.setForeground(Color.green);
//				panel.setModel(this.model);
				devicePanel.add(panel);
			}
		}
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				if(null!=devicePanel) {
				devicePanel.revalidate();
				devicePanel.repaint();
				}
			}
			
		});
	}

}
