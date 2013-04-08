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
import org.mdpnp.comms.data.numeric.UnitCode;

public interface BloodPressure extends Device {
	Numeric SYSTOLIC = new NumericImpl(BloodPressure.class, "SYSTOLIC", UnitCode.MM_HG, null);
	Numeric DIASTOLIC = new NumericImpl(BloodPressure.class, "DIASTOLIC", UnitCode.MM_HG, null);
	Numeric PULSE = new NumericImpl(BloodPressure.class, "PULSE", UnitCode.BEATS_PER_MINUTE, null);
	
}
