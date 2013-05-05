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

public interface NurseCall extends Device {
	enum Status {
		Green,
		Yellow,
		Red
	}
	
	Enumeration STATUS = new EnumerationImpl(NurseCall.class, "STATUS");
	
}
