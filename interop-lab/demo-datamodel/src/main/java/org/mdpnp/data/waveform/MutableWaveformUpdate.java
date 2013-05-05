/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.data.waveform;

import java.util.Date;

import org.mdpnp.data.MutableIdentifiableUpdate;

public interface MutableWaveformUpdate extends WaveformUpdate, MutableIdentifiableUpdate<Waveform> {
	void setTimestamp(Date date);
	void setValues(Number[] values);
//	void setValue(int x, Number n);
//	void setCount(Integer i);
	void setMillisecondsPerSample(Double d);
}
