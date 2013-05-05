/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.nomenclature;

import org.mdpnp.data.numeric.Numeric;
import org.mdpnp.data.numeric.NumericImpl;
import org.mdpnp.data.numeric.UnitCode;
import org.mdpnp.data.waveform.Waveform;
import org.mdpnp.data.waveform.WaveformImpl;

public interface Capnograph extends Device {
	Waveform CAPNOGRAPH = new WaveformImpl(Capnograph.class, "CAPNOGRAPH", UnitCode.NONE);
	Numeric END_TIDAL_CO2 = new NumericImpl(Capnograph.class, "END_TIDAL_CO2", UnitCode.NONE, null);
//	Numeric AIRWAY_RESPIRATORY_RATE = new NumericImpl(Capnograph.class, "AIRWAY_RESPIRATORY_RATE", UnitCode.NONE, null);
	
	
	
}
