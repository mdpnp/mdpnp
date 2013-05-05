package org.mdpnp.apps.testapp;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.util.Collection;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.mdpnp.data.IdentifiableUpdate;
import org.mdpnp.data.identifierarray.IdentifierArrayUpdate;
import org.mdpnp.data.text.MutableTextUpdate;
import org.mdpnp.data.text.MutableTextUpdateImpl;
import org.mdpnp.guis.swing.DevicePanelFactory;
import org.mdpnp.messaging.Gateway;
import org.mdpnp.messaging.GatewayListener;
import org.mdpnp.nomenclature.Device;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DevicePanel extends JPanel implements GatewayListener {
	private String source;
	private Gateway gateway;
	
	private static final Logger log = LoggerFactory.getLogger(DevicePanel.class);
	
	public DevicePanel() {
		super();
		enableEvents(AWTEvent.COMPONENT_EVENT_MASK);
	}
	
	
	@Override
	protected void processComponentEvent(ComponentEvent e) {
		super.processComponentEvent(e);
		switch(e.getID()) {
		case ComponentEvent.COMPONENT_HIDDEN:
			setModel(null, null);
			break;
		}
	}
	
	public void setModel(Gateway gateway, String source) {
		if(this.gateway != null) {
			this.gateway.removeListener(this);
			this.gateway = null;
		}
		this.source = null;
		Runnable removeAll = new Runnable() {
			public void run() {
				removeAll();
			}
		};
		for(int i = 0; i < getComponentCount(); i++) {
			Component c  = getComponent(i);
			if(c instanceof org.mdpnp.guis.swing.DevicePanel) {
				((org.mdpnp.guis.swing.DevicePanel)c).destroy();
			}
		}
		if(SwingUtilities.isEventDispatchThread()) {
			removeAll.run();
		} else {
			try {
				SwingUtilities.invokeAndWait(removeAll);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		this.gateway = gateway;
		this.source = source;
		
		if(gateway != null) {
			gateway.addListener(this);

			log.debug("Requesting identifiers for " + source);
			MutableTextUpdate tu = new MutableTextUpdateImpl(Device.REQUEST_AVAILABLE_IDENTIFIERS, "");
			tu.setTarget(source);
			tu.setValue("");
			tu.setSource("*");
			gateway.update(this, tu);
		}
	}


	@Override
	public void update(IdentifiableUpdate<?> update) {
		String source = this.source;
		if(source != null && source.equals(update.getSource())) {
			if(0 == getComponentCount() && Device.GET_AVAILABLE_IDENTIFIERS.equals(update.getIdentifier())) {
				log.debug("GET_AVAILABLE_IDENTIFIERS .. building panels for " + source);
				
				final Collection<org.mdpnp.guis.swing.DevicePanel> panels = DevicePanelFactory.findPanel( (IdentifierArrayUpdate) update, gateway, source);
				log.debug("Found " + panels.size() + " panels for " + source);
				for(org.mdpnp.guis.swing.DevicePanel panel : panels) {
					DemoPanel.setChildrenOpaque(panel, true);
					panel.setBackground(Color.black);
					panel.setForeground(Color.green);
				}
				Runnable r = new Runnable() {
					public void run() {
						setLayout(new GridLayout(panels.size(), 1));
						for(org.mdpnp.guis.swing.DevicePanel panel : panels) {
							
							add(panel);
						}
						
						revalidate();
						repaint();
					}
				};
				if(SwingUtilities.isEventDispatchThread()) {
					r.run();
				} else {
					try {
						SwingUtilities.invokeAndWait(r);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}
		}
	}
}
