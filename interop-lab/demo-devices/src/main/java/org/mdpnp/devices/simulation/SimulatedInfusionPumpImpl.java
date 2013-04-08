/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.simulation;

import org.mdpnp.comms.Gateway;


public class SimulatedInfusionPumpImpl extends AbstractSimulatedDevice implements SimulatedInfusionPump {
	

	public SimulatedInfusionPumpImpl(Gateway gateway) {
		super(gateway);
		nameUpdate.setValue("Simulated Infusion Pump");
	}
}
