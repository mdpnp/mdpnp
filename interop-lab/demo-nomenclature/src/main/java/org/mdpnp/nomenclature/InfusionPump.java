/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.nomenclature;

import org.mdpnp.data.enumeration.Enumeration;
import org.mdpnp.data.enumeration.EnumerationImpl;

public interface InfusionPump extends Device {
	enum State {
		On,
		Off
	};
	
	Enumeration STATE = new EnumerationImpl(InfusionPump.class, "STATE");
	
	enum ControlState {
		TurnOn,
		TurnOff
	}
	Enumeration CONTROL_STATE = new EnumerationImpl(InfusionPump.class, "CONTROL_STATE");
}
