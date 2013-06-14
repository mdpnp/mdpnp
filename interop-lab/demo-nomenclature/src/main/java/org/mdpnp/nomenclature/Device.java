/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.nomenclature;

import org.mdpnp.data.identifierarray.IdentifierArray;
import org.mdpnp.data.identifierarray.IdentifierArrayImpl;
import org.mdpnp.data.image.Image;
import org.mdpnp.data.image.ImageImpl;
import org.mdpnp.data.numeric.Numeric;
import org.mdpnp.data.numeric.NumericImpl;
import org.mdpnp.data.text.Text;
import org.mdpnp.data.text.TextImpl;

public interface Device {    
	Text NAME = new TextImpl(Device.class, "NAME");
	Text GUID = new TextImpl(Device.class, "GUID");
	Image ICON = new ImageImpl(Device.class, "ICON");
	
	Numeric TIME_MSEC_SINCE_EPOCH = new NumericImpl(Device.class, "TIME_MSEC_SINCE_EPOCH");
	
	IdentifierArray REQUEST_IDENTIFIED_UPDATES = new IdentifierArrayImpl(Device.class, "REQUEST_IDENTIFIED_UPDATES");
	
	Text REQUEST_AVAILABLE_IDENTIFIERS = new TextImpl(Device.class, "REQUEST_AVAILABLE_IDENTIFIERS");
	
	IdentifierArray GET_AVAILABLE_IDENTIFIERS = new IdentifierArrayImpl(Device.class, "GET_AVAILABLE_IDENTIFIERS");
}
