package org.mdpnp.apps.testapp;

import ice.DeviceIdentity;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.util.Collection;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.mdpnp.devices.EventLoop;
import org.mdpnp.guis.swing.CompositeDevicePanel;
import org.mdpnp.guis.swing.DeviceMonitor;
import org.mdpnp.guis.swing.DevicePanelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rti.dds.subscription.Subscriber;

public class DevicePanel extends JPanel {
    
	
	private static final Logger log = LoggerFactory.getLogger(DevicePanel.class);
	private final CompositeDevicePanel cdp = new CompositeDevicePanel();
	
	public DevicePanel() {
		super(new BorderLayout());
		enableEvents(AWTEvent.COMPONENT_EVENT_MASK);
		add(cdp, BorderLayout.CENTER);
	}
	
	
	@Override
	protected void processComponentEvent(ComponentEvent e) {
		super.processComponentEvent(e);
		switch(e.getID()) {
		case ComponentEvent.COMPONENT_HIDDEN:
		    log.debug("Component hidden");
			setModel(null, null, null);
			break;
		}
	}

	private DeviceMonitor deviceMonitor;
	
	public void setModel(Subscriber subscriber, DeviceIdentity deviceIdentity, EventLoop eventLoop) {
	    if(null != deviceMonitor) {
	        log.debug("DeviceMonitor.shutdown");
	        deviceMonitor.shutdown();
	        deviceMonitor = null;
	    }

	    if(null != subscriber && null != deviceIdentity) {
	        log.debug("DeviceMonitor ctor");
	        deviceMonitor = new DeviceMonitor(subscriber.get_participant(), deviceIdentity.universal_device_identifier, cdp, eventLoop);
	    } else {
	        cdp.reset();
	    }
	    
	}


}
