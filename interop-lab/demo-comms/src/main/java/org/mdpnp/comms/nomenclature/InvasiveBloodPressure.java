/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.comms.nomenclature;

import org.mdpnp.comms.data.numeric.UnitCode;
import org.mdpnp.comms.data.waveform.Waveform;
import org.mdpnp.comms.data.waveform.WaveformImpl;

public interface InvasiveBloodPressure extends BloodPressure {
    Waveform PRESSURE = new WaveformImpl(InvasiveBloodPressure.class, "PRESSURE", UnitCode.NONE);
}
