/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.comms.nomenclature;

import org.mdpnp.comms.data.enumeration.Enumeration;
import org.mdpnp.comms.data.enumeration.EnumerationImpl;
import org.mdpnp.comms.data.numeric.Numeric;
import org.mdpnp.comms.data.numeric.NumericImpl;
import org.mdpnp.comms.data.numeric.UnitCode;
import org.mdpnp.comms.data.text.Text;
import org.mdpnp.comms.data.text.TextImpl;

public interface NoninvasiveBloodPressure extends BloodPressure {
	Numeric NEXT_INFLATION_TIME_REMAINING = new NumericImpl(NoninvasiveBloodPressure.class, "NEXT_INFLATION_TIME_REMAINING", UnitCode.MILLISECONDS, null);
	Numeric INFLATION_PRESSURE = new NumericImpl(NoninvasiveBloodPressure.class, "INFLATION_PRESSURE", UnitCode.MM_HG, null);
	Enumeration STATE = new EnumerationImpl(NoninvasiveBloodPressure.class, "STATE");
	Text REQUEST_NIBP = new TextImpl(NoninvasiveBloodPressure.class, "REQUEST_NIBP");
	
	enum NBPState {
		Inflating,
		Deflating,
		Waiting
	}
	
	void doInflate();
	
}
