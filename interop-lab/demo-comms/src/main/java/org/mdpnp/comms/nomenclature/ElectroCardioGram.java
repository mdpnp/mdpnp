/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.comms.nomenclature;

import org.mdpnp.comms.data.numeric.UnitCode;
import org.mdpnp.comms.data.text.Text;
import org.mdpnp.comms.data.text.TextImpl;
import org.mdpnp.comms.data.waveform.Waveform;
import org.mdpnp.comms.data.waveform.WaveformImpl;

public interface ElectroCardioGram extends Device {
	Waveform I = new WaveformImpl(ElectroCardioGram.class, "I", UnitCode.NONE);
	Waveform II = new WaveformImpl(ElectroCardioGram.class, "II", UnitCode.NONE);
	Waveform III = new WaveformImpl(ElectroCardioGram.class, "III", UnitCode.NONE);
	Waveform A_VR = new WaveformImpl(ElectroCardioGram.class, "A_VR", UnitCode.NONE);
	Waveform A_VL = new WaveformImpl(ElectroCardioGram.class, "A_VL", UnitCode.NONE);
	Waveform A_VF = new WaveformImpl(ElectroCardioGram.class, "A_VF", UnitCode.NONE);
	Waveform V2 = new WaveformImpl(ElectroCardioGram.class, "V2", UnitCode.NONE);
	Waveform V5 = new WaveformImpl(ElectroCardioGram.class, "V5", UnitCode.NONE);
	
	Text HR_ECG_MODE = new TextImpl(ElectroCardioGram.class, "HR_ECG_MODE");
}
