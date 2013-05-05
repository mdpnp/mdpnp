/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.data.waveform;

import java.util.Date;

import org.mdpnp.data.IdentifiableUpdate;
import org.mdpnp.data.Persistent;

@Persistent
public interface WaveformUpdate extends IdentifiableUpdate<Waveform> {
	@Persistent
	Number[] getValues();
//	Number getValue(int x);
//	Integer getCount();
	@Persistent
	Date getTimestamp();
	@Persistent
	Double getMillisecondsPerSample();
}
