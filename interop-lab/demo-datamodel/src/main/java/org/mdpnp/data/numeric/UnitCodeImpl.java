/*******************************************************************************
 * Copyright (c) 2012 MD PnP Program.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package org.mdpnp.data.numeric;

import org.mdpnp.data.IdentifierImpl;

@SuppressWarnings("serial")
public class UnitCodeImpl extends IdentifierImpl implements UnitCode {
	private final String displayName;
	private final String displaySymbol;
	
	public UnitCodeImpl(Class<?> cls, String name, String displayName, String displaySymbol) {
		super(cls, name);
		this.displayName = displayName;
		this.displaySymbol = displaySymbol;
	}
	
	@Override
	public String getDisplayName() {
		return displayName;
	}
	
	@Override
	public String toString() {
		return displayName;
	}
	
	public String getDisplaySymbol() {
		return displaySymbol;
	}
	@Override
	public String getIdentifierClass() {
		return UnitCode.class.getName();
	}
}
