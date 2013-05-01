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
import org.mdpnp.comms.data.waveform.Waveform;
import org.mdpnp.comms.data.waveform.WaveformImpl;

public interface InvasiveBloodPressure {
    Waveform PRESSURE1 = new WaveformImpl(InvasiveBloodPressure.class, "PRESSURE1", UnitCode.NONE);
    Waveform PRESSURE2 = new WaveformImpl(InvasiveBloodPressure.class, "PRESSURE2", UnitCode.NONE);
    
    Numeric SYSTOLIC1 = new NumericImpl(InvasiveBloodPressure.class, "SYSTOLIC1", UnitCode.MM_HG, null);
    Numeric DIASTOLIC1 = new NumericImpl(InvasiveBloodPressure.class, "DIASTOLIC1", UnitCode.MM_HG, null);
    Numeric PULSE1 = new NumericImpl(InvasiveBloodPressure.class, "PULSE1", UnitCode.BEATS_PER_MINUTE, null);
    Numeric MEAN1 = new NumericImpl(InvasiveBloodPressure.class, "MEAN1", UnitCode.MM_HG, null);
    
    Numeric SYSTOLIC2 = new NumericImpl(InvasiveBloodPressure.class, "SYSTOLIC2", UnitCode.MM_HG, null);
    Numeric DIASTOLIC2 = new NumericImpl(InvasiveBloodPressure.class, "DIASTOLIC2", UnitCode.MM_HG, null);
    Numeric PULSE2 = new NumericImpl(InvasiveBloodPressure.class, "PULSE2", UnitCode.BEATS_PER_MINUTE, null);
    Numeric MEAN2 = new NumericImpl(InvasiveBloodPressure.class, "MEAN2", UnitCode.MM_HG, null);
}
