/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.comms.nomenclature;

import org.mdpnp.comms.data.numeric.Numeric;
import org.mdpnp.comms.data.numeric.NumericImpl;
import org.mdpnp.comms.data.text.Text;
import org.mdpnp.comms.data.text.TextImpl;

public interface Demographics {
	Text LAST_NAME = new TextImpl(Demographics.class, "LAST_NAME");
	Text FIRST_NAME = new TextImpl(Demographics.class, "FIRST_NAME");
	Text PATIENT_ID = new TextImpl(Demographics.class, "PATIENT_ID");
	Numeric AGE = new NumericImpl(Demographics.class, "AGE", null, null);
	Numeric WEIGHT = new NumericImpl(Demographics.class, "WEIGHT", null, null);
	
}
