/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.data.waveform;

import org.mdpnp.data.IdentifierImpl;
import org.mdpnp.data.numeric.UnitCode;

@SuppressWarnings("serial")
public class WaveformImpl extends IdentifierImpl implements Waveform {
	private final UnitCode unitCode;
	
	public WaveformImpl(Class<?> clazz, String id, UnitCode unitCode) {
		super(clazz, id);
		this.unitCode = unitCode;
	}
	
	@Override
	public UnitCode getUnitCode() {
		return unitCode;
	}
	
	@Override
	public String getIdentifierClass() {
		return Waveform.class.getName();
	}


}
