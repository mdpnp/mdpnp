/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.comms.nomenclature;

import org.mdpnp.comms.GatewayListener;
import org.mdpnp.comms.data.identifierarray.IdentifierArray;
import org.mdpnp.comms.data.identifierarray.IdentifierArrayImpl;
import org.mdpnp.comms.data.image.Image;
import org.mdpnp.comms.data.image.ImageImpl;
import org.mdpnp.comms.data.numeric.Numeric;
import org.mdpnp.comms.data.numeric.NumericImpl;
import org.mdpnp.comms.data.text.Text;
import org.mdpnp.comms.data.text.TextImpl;

public interface Device extends GatewayListener {
	Text NAME = new TextImpl(Device.class, "NAME");
	Text GUID = new TextImpl(Device.class, "GUID");
	Image ICON = new ImageImpl(Device.class, "ICON");
	
	Numeric TIME_MSEC_SINCE_EPOCH = new NumericImpl(Device.class, "TIME_MSEC_SINCE_EPOCH");
	
	IdentifierArray REQUEST_IDENTIFIED_UPDATES = new IdentifierArrayImpl(Device.class, "REQUEST_IDENTIFIED_UPDATES");
	
	Text REQUEST_AVAILABLE_IDENTIFIERS = new TextImpl(Device.class, "REQUEST_AVAILABLE_IDENTIFIERS");
	
	IdentifierArray GET_AVAILABLE_IDENTIFIERS = new IdentifierArrayImpl(Device.class, "GET_AVAILABLE_IDENTIFIERS");
}
