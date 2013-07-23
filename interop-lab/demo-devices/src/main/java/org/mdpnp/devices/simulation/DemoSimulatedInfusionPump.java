/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.devices.simulation;

import org.mdpnp.devices.EventLoop;



public class DemoSimulatedInfusionPump extends AbstractSimulatedDevice {
	

	public DemoSimulatedInfusionPump(int domainId, EventLoop eventLoop) {
		super(domainId, eventLoop);
		deviceIdentity.model = "Simulated Infusion Pump";
	}
}
