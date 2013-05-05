/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.connected;

import java.awt.Component;

import javax.swing.JOptionPane;

public class NetworkChooser {
	public static String showNetworkAddressDialog(Component parent, String _default) {
		return (String) JOptionPane.showInputDialog(parent, "Enter a network address (blank may mean wait for incoming beacon)", "Address", JOptionPane.QUESTION_MESSAGE, null, null, _default);
	}
}
