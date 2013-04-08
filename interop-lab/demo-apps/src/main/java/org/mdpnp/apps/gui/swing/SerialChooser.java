/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.apps.gui.swing;

import java.awt.Component;

import javax.swing.JOptionPane;

import org.mdpnp.comms.serial.SerialProviderFactory;

public class SerialChooser {
	public static String showSerialPortNameDialog(Component parent) {
		return (String) JOptionPane.showInputDialog(parent, "Choose a port", "Port", JOptionPane.QUESTION_MESSAGE, null, SerialProviderFactory.getDefaultProvider().getPortNames().toArray(), null);
	}
}
