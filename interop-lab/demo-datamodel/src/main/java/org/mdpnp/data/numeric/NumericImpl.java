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
public class NumericImpl extends IdentifierImpl implements Numeric {
	private final UnitCode unitCode;
	private final NumericGroup group;
	
	public NumericImpl(Class<?> clazz, String id) {
		this(clazz, id, null, null);
	}
	
	public NumericImpl(Class<?> clazz, String id, UnitCode unitCode, NumericGroup numericGroup) {
		super(clazz, id);
		this.unitCode = unitCode;
		this.group = numericGroup;
	}
	
	@Override
	public NumericGroup getNumericGroup() {
		return group;
	}

	@Override
	public UnitCode getUnitCode() {
		return unitCode;
	}
	@Override
	public String getIdentifierClass() {
		return Numeric.class.getName();
	}
}
