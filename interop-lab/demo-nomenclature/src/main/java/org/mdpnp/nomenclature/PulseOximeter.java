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
import org.mdpnp.data.numeric.Numeric;
import org.mdpnp.data.numeric.NumericImpl;
import org.mdpnp.data.numeric.UnitCode;
import org.mdpnp.data.waveform.Waveform;
import org.mdpnp.data.waveform.WaveformImpl;

public interface PulseOximeter extends Device {
	Numeric PULSE = new NumericImpl(PulseOximeter.class, "PULSE", UnitCode.BEATS_PER_MINUTE, null);
	Numeric SPO2  = new NumericImpl(PulseOximeter.class, "SPO2" , UnitCode.SAT_OXYGEN_PULSE_OXIMETRY, null);
	
	Numeric PULSE_LOWER = new NumericImpl(PulseOximeter.class, "PULSE_LOWER", UnitCode.BEATS_PER_MINUTE, null);
	Numeric PULSE_UPPER = new NumericImpl(PulseOximeter.class, "PULSE_UPPER", UnitCode.BEATS_PER_MINUTE, null);
	
	Numeric SPO2_LOWER = new NumericImpl(PulseOximeter.class, "SPO2_LOWER", UnitCode.SAT_OXYGEN_PULSE_OXIMETRY, null);
	Numeric SPO2_UPPER = new NumericImpl(PulseOximeter.class, "SPO2_UPPER", UnitCode.SAT_OXYGEN_PULSE_OXIMETRY, null);
	
	Waveform PLETH = new WaveformImpl(PulseOximeter.class,  "PLETH", UnitCode.NONE);
	
	Numeric RESPIRATORY_RATE = new NumericImpl(PulseOximeter.class, "RESPIRATORY_RATE", UnitCode.BEATS_PER_MINUTE, null);
	Numeric RR_APNEA = new NumericImpl(PulseOximeter.class, "RR_APNEA");
	
	enum CLock {
		On,
		Off
	}
	Enumeration C_LOCK = new EnumerationImpl(PulseOximeter.class, "C_LOCK");
}
