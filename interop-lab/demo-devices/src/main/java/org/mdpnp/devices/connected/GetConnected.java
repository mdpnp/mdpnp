/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.connected;

import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.mdpnp.devices.EventLoop;

public class GetConnected extends AbstractGetConnected {
	private final JFrame frame;
	
	public GetConnected(JFrame frame, int domainId, String unique_device_identifier, EventLoop eventLoop) {
		super(domainId, unique_device_identifier, eventLoop);
		this.frame = frame;
	}
	
	@Override
	protected void abortConnect() {
		frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
	}
	
	@Override
	protected String addressFromUser() {
		return NetworkChooser.showNetworkAddressDialog(frame, "192.168.1.199");
	}
	
	@Override
	protected String addressFromUserList(String[] list) {
		return (String) JOptionPane.showInputDialog(frame, "Choose a port", "Port", JOptionPane.QUESTION_MESSAGE, null, list, null);
	}
	@Override
	protected boolean isFixedAddress() {
		return false;
	}

}
